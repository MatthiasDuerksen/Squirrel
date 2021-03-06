package org.aksw.simba.squirrel.components;

import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import org.aksw.simba.squirrel.collect.SqlBasedUriCollector;
import org.aksw.simba.squirrel.configurator.WorkerConfiguration;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.serialize.Serializer;
import org.aksw.simba.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.WorkerGuard;
import org.aksw.simba.squirrel.rabbit.msgs.CrawlingResult;
import org.aksw.simba.squirrel.rabbit.msgs.UriSet;
import org.aksw.simba.squirrel.rabbit.msgs.UriSetRequest;
import org.aksw.simba.squirrel.robots.RobotsManagerImpl;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.file.FileBasedSink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.aksw.simba.squirrel.worker.impl.AliveMessage;
import org.aksw.simba.squirrel.worker.impl.WorkerImpl;
import org.apache.commons.io.IOUtils;
import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.rabbit.DataSender;
import org.hobbit.core.rabbit.DataSenderImpl;
import org.hobbit.core.rabbit.RabbitRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Qualifier("workerComponent")
public class WorkerComponent extends AbstractComponent implements Frontier {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerComponent.class);

    public static final String OUTPUT_FOLDER_KEY = "OUTPUT_FOLDER";

    /**
     * Indicates whether deduplication is active. If it is not active, this component will send data to the deduplicator.
     */
    private static boolean deduplicationActive;

    @Qualifier("workerBean")
    @Autowired
    private Worker worker;
    private DataSender senderFrontier, senderDeduplicator;
    private RabbitRpcClient clientFrontier;
    @Qualifier("sender")
    @Autowired
    private DataSender sender;
    @Qualifier("client")
    @Autowired
    private RabbitRpcClient client;
    private byte[] uriSetRequest;
    @Qualifier("serializerBean")
    @Autowired
    private Serializer serializer;
    private Timer timerAliveMessages = new Timer();

    @Override
    public void init() throws Exception {
        super.init();
        if (worker == null || sender == null || client == null || serializer == null) {
            LOGGER.warn("The SPRING-config autowire service was not (totally) working. We must do the instantiation in the WorkerComponent!");
            initWithoutSpring();
        }
        uriSetRequest = serializer.serialize(new UriSetRequest());

        Map<String, String> env = System.getenv();
        if (env.containsKey(DeduplicatorComponent.DEDUPLICATION_ACTIVE_KEY)) {
            deduplicationActive = Boolean.parseBoolean(env.get(DeduplicatorComponent.DEDUPLICATION_ACTIVE_KEY));
        } else {
            LOGGER.warn("Couldn't get {} from the environment. The default value will be used.", DeduplicatorComponent.DEDUPLICATION_ACTIVE_KEY);
            deduplicationActive = DeduplicatorComponent.DEFAULT_DEDUPLICATION_ACTIVE;
        }

        senderFrontier = DataSenderImpl.builder().queue(outgoingDataQueuefactory, FrontierComponent.FRONTIER_QUEUE_NAME)
            .build();

        if (deduplicationActive) {
            senderDeduplicator = DataSenderImpl.builder().queue(outgoingDataQueuefactory, DeduplicatorComponent.DEDUPLICATOR_QUEUE_NAME)
                .build();
        }
        clientFrontier = RabbitRpcClient.create(outgoingDataQueuefactory.getConnection(),
            FrontierComponent.FRONTIER_QUEUE_NAME);

        if (worker.sendsAliveMessages()) {
            timerAliveMessages.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        senderFrontier.sendData(serializer.serialize(new AliveMessage(worker.getId())));
                    } catch (IOException e) {
                        LOGGER.warn(e.toString());
                    }
                }
            }, 0, TimeUnit.SECONDS.toMillis(WorkerGuard.TIME_WORKER_DEAD) / 2);

        }
        LOGGER.info("Worker initialized.");

    }

    private void initWithoutSpring() throws Exception {
        super.init();

        WorkerConfiguration workerConfiguration = WorkerConfiguration.getWorkerConfiguration();

        Sink sink;
        if (workerConfiguration.getSparqlHost() == null || workerConfiguration.getSqarqlPort() == null) {
            sink = new FileBasedSink(new File(workerConfiguration.getOutputFolder()), true);
        } else {
            String httpPrefix = "http://" + workerConfiguration.getSparqlHost() + ":" + workerConfiguration.getSqarqlPort() + "/";
            sink = new SparqlBasedSink(workerConfiguration.getSparqlHost().toString(), workerConfiguration.getSqarqlPort().toString(), "contentset/update", "contentset/query", "MetaData/update", "MetaData/query");
        }

        serializer = new GzipJavaUriSerializer();

        worker = new WorkerImpl(this, sink, new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))), serializer, SqlBasedUriCollector.create(serializer), 2000, workerConfiguration.getOutputFolder() + File.separator + "log", true);
        sender = DataSenderImpl.builder().queue(outgoingDataQueuefactory, FrontierComponent.FRONTIER_QUEUE_NAME).build();
        client = RabbitRpcClient.create(outgoingDataQueuefactory.getConnection(), FrontierComponent.FRONTIER_QUEUE_NAME);
    }

    @Override
    public void run() {
        worker.run();
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(senderFrontier);
        if (deduplicationActive) {
            IOUtils.closeQuietly(senderDeduplicator);
        }
        IOUtils.closeQuietly(clientFrontier);
        timerAliveMessages.cancel();
        super.close();
    }

    @Override
    public List<CrawleableUri> getNextUris() {
        UriSet set = null;
        try {
            byte[] response = clientFrontier.request(uriSetRequest);
            if (response != null) {
                set = serializer.deserialize(response);
            }
        } catch (IOException e) {
            LOGGER.error("Error while requesting the next set of URIs.", e);
        }
        if ((set == null) || (set.uris == null) || (set.uris.size() == 0)) {
            return null;
        } else {
            return set.uris;
        }
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }


    @Override
    public void addNewUri(CrawleableUri uri) {
        addNewUris(Collections.singletonList(uri));
    }

    @Override
    public void addNewUris(List<CrawleableUri> uris) {
        try {
            senderFrontier.sendData(serializer.serialize(new UriSet(uris)));
        } catch (Exception e) {
            LOGGER.error("Exception while sending URIs to the frontier.", e);
        }
    }

    @Override
    public void crawlingDone(Dictionary<CrawleableUri, List<CrawleableUri>> uriMap) {
        try {
            Hashtable<CrawleableUri, List<CrawleableUri>> uriMapHashtable;
            if (uriMap instanceof Hashtable) {
                uriMapHashtable = (Hashtable<CrawleableUri, List<CrawleableUri>>) uriMap;
            } else {
                uriMapHashtable = new Hashtable<>(uriMap.size(), 1);
                Enumeration<CrawleableUri> keys = uriMap.keys();
                while (keys.hasMoreElements()) {
                    CrawleableUri key = keys.nextElement();
                    uriMapHashtable.put(key, uriMap.get(key));
                }
            }
            senderFrontier.sendData(serializer.serialize(new CrawlingResult(uriMapHashtable, worker.getId())));

            if (deduplicationActive) {
                UriSet uriSet = new UriSet();
                uriSet.uris = Collections.list(uriMapHashtable.keys());
                senderDeduplicator.sendData(serializer.serialize(uriSet));
            }
        } catch (Exception e) {
            LOGGER.error("Exception while sending crawl result to the frontier.", e);
        }
    }

    @Override
    public int getNumberOfPendingUris() {
        return 0;
    }

    @Override
    public boolean doesRecrawling() {
        return false;
    }

}

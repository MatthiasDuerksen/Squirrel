package org.aksw.simba.squirrel.metadata;

import org.aksw.simba.squirrel.components.WorkerComponent;
import org.aksw.simba.squirrel.configurator.WorkerConfiguration;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.TripleBasedSink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.aksw.simba.squirrel.worker.Worker;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Representation of Crawling activity. A crawling activity is started by a single worker. So, it contains a bunch of Uris
 * and some meta data, like timestamps for the start and end of the crawling activity.
 */
public class CrawlingActivity {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlingActivity.class);
    /**
     * A unique id.
     */
    private String id;
    /**
     * When the activity has started.
     */
    private Date dateStarted;
    /**
     * When the activity has ended.
     */
    private Date dateEnded;

    /**
     * The uri for the crawling activity.
     */
    private CrawleableUri uri;

    /**
     * The graph where the uri is stored.
     */
    private String graphId;

    /**
     * The crawling state of the uri.
     */
    private CrawlingURIState state;

    /**
     * The worker that has been assigned the activity.
     */
    private Worker worker;

    /**
     * Number of triples crawled by this activity.
     */
    private int numTriples;

    /**
     * The sink used for the activity.
     */
    private TripleBasedSink sink;
    /**
     * date format for start_date and end_date
     */
    public SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss");

    /**
     * Constructor
     *
     * @param uri
     * @param worker
     * @param sink
     */
    public CrawlingActivity(CrawleableUri uri, Worker worker, TripleBasedSink sink) {
        this.worker = worker;
        this.dateStarted = new Date();
        this.uri = uri;
        this.state = CrawlingURIState.UNKNOWN;
        if (sink instanceof SparqlBasedSink) {
            graphId = ((SparqlBasedSink) sink).getGraphId(uri);
        }
        id = "crawlingActivity:" + graphId;
        this.sink = sink;
    }

    public void setState(CrawlingURIState state) {
        this.state = state;
    }

    public void finishActivity() {
        countTriples();
        dateEnded = new Date();

    }

    /**
     * count the triples of the activity.
     */
    private void countTriples() {
        int sum = 0;
        if (sink instanceof SparqlBasedSink) {

            //sum += ((SparqlBasedSink) sink).getNumberOfTriplesForGraph(uri);

            numTriples = sum;
        } else {
            numTriples = -1;
        }
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        try {
            WorkerConfiguration workerConfiguration = WorkerConfiguration.getWorkerConfiguration();
            String httpPrefix = "http://" + workerConfiguration.getSparqlHost() + ":" + workerConfiguration.getSqarqlPort() + "/";
            return httpPrefix;
        } catch (Exception e) {
            return null;
        }
    }

    public String getDateStarted() {

        String dateString = ft.format(dateStarted).toString();
        return dateString;
    }

    public String getDateEnded() {

        String dateString = ft.format(dateEnded).toString();
        return dateString;
    }

    public int getNumTriples() {
        return numTriples;
    }

    public Worker getWorker() {
        return worker;
    }

    public enum CrawlingURIState {SUCCESSFUL, UNKNOWN, FAILED;}

    public CrawlingURIState getState() {
        return state;
    }

    public CrawleableUri getCrawleableUri() {
        return uri;
    }

    public URI getUri()
    {
        return URI.create(getUri().toString());

    }
}

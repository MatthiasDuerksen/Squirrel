package org.aksw.simba.squirrel.metadata;

import com.sun.xml.internal.bind.v2.TODO;
import org.aksw.jena_sparql_api.mapper.annotation.RdfType;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;

import org.aksw.simba.squirrel.sink.Sink;
import org.aksw.simba.squirrel.sink.impl.sparql.SparqlBasedSink;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.graph.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class MetaDataHandler {

    private static final String GRAPH_NAME_FOR_METADATA = "MetaData";
    private CrawleableUri dummyUri;
    private Sink sink;

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedSink.class);


    public MetaDataHandler(String updateDatasetURI, String queryDatasetURI) {
        sink = new SparqlBasedSink(updateDatasetURI, queryDatasetURI);
        try {
            dummyUri = new CrawleableUri(new URI("MetaData:DummyUri"));
            dummyUri.addData(CrawleableUri.UUID_KEY, GRAPH_NAME_FOR_METADATA);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void addMetadata(final CrawlingActivity crawlingActivity) {
        List<Triple> lstTriples = new LinkedList<>();
        Model model  = ModelFactory.createDefaultModel();
        CrawleableUri uri = crawlingActivity.getCrawleableUri();
        Node nodeResultGraph = NodeFactory.createURI(String.valueOf(crawlingActivity.geturl("ResultGraph_"+crawlingActivity.getGraphId(uri))));
        Node nodeCrawlingActivity = NodeFactory.createURI(String.valueOf(crawlingActivity.geturl("CrawlingActivity_"+crawlingActivity.getId())));
        Node Association = NodeFactory.createBlankNode(Prov.Association.toString());

        lstTriples.add(new Triple(nodeCrawlingActivity, Prov.startedAtTime.asNode(), model.createTypedLiteral(crawlingActivity.getDateStarted(),XSDDatatype.XSDdateTime).asNode()));
        lstTriples.add(new Triple(nodeCrawlingActivity, Prov.endedAtTime.asNode(), model.createTypedLiteral(crawlingActivity.getDateEnded(),XSDDatatype.XSDdateTime).asNode()));
        lstTriples.add(new Triple(nodeCrawlingActivity, Sq.status.asNode(), NodeFactory.createLiteral(crawlingActivity.getState().toString())));
        lstTriples.add(new Triple(nodeCrawlingActivity, Sq.numberOfTriples.asNode(), model.createTypedLiteral(crawlingActivity.getNumTriples(),XSDDatatype.XSDint).asNode()));
        lstTriples.add(new Triple(nodeCrawlingActivity, Prov.wasAssociatedWith.asNode(), NodeFactory.createURI(String.valueOf(crawlingActivity.geturl("Worker_"+ String.valueOf(crawlingActivity.getWorker().getId()))))));
        lstTriples.add(new Triple(nodeResultGraph, Prov.wasGeneratedBy.asNode(),nodeCrawlingActivity ));
        lstTriples.add(new Triple(nodeCrawlingActivity, RDFS.comment.asNode(),NodeFactory.createLiteral("GraphID where the content is stored")));
        lstTriples.add(new Triple(nodeCrawlingActivity, Sq.hostedOn.asNode(),NodeFactory.createURI(crawlingActivity.getHost())));
        lstTriples.add(new Triple(nodeCrawlingActivity, Prov.qualifiedAssociation.asNode(),Association));
        lstTriples.add(new Triple(Association,Prov.agent.asNode(),NodeFactory.createURI(String.valueOf(crawlingActivity.geturl("Worker_"+ String.valueOf(crawlingActivity.getWorker().getId()))))));
        lstTriples.add(new Triple(Association,Prov.hadPlan.asNode(),NodeFactory.createLiteral(crawlingActivity.getHadPlan())));

        sink.openSinkForUri(dummyUri);
        for (Triple triple : lstTriples) {
            sink.addTriple(dummyUri, triple);
        }
        sink.closeSinkForUri(dummyUri);
        LOGGER.info("MetaData successfully added for crawling activity: " + crawlingActivity.getId());
    }


}
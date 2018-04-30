package org.aksw.simba.squirrel.sink.impl.sparql;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is used to provides templates for basic SPARQL commands needed in this project.
 */
public class QueryGenerator {

    /**
     * The instance of the class QueryGenerator.
     */
    private static final QueryGenerator instance = new QueryGenerator();
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryGenerator.class);

    private QueryGenerator() {
    }

    /**
     * Getter for {@link #instance}.
     *
     * @return instannce of the class.
     */
    public static QueryGenerator getInstance() {
        return instance;
    }

    /**
     * Return an Add Query for the given graph id and triple.
     *
     * @param mapBufferedTriples the given map from uri to the List of triples.
     * @return The generated query.
     */
    public String getAddQuery(ConcurrentHashMap<CrawleableUri, ConcurrentLinkedQueue<Triple>> mapBufferedTriples) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT DATA {");
        for (CrawleableUri uri : mapBufferedTriples.keySet()) {
            stringBuilder.append(" Graph <");
            stringBuilder.append(uri.getUri());
            stringBuilder.append("> { ");

            for (Triple triple : mapBufferedTriples.get(uri)) {
                stringBuilder.append("<");
                stringBuilder.append(triple.getSubject());
                stringBuilder.append("> <");
                stringBuilder.append(triple.getPredicate());
                stringBuilder.append("> <");
                stringBuilder.append(triple.getObject());
                stringBuilder.append("> . ");
            }
            stringBuilder.append("} ");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    /**
     * Return a select all query for a given uri.
     *
     * @param uri The given uri.
     * @return The generated query.
     */
    @SuppressWarnings("unused")
    public Query getSelectAllQuery(CrawleableUri uri) {
        return getSelectQuery(uri, null, true);
    }

    /**
     * Return a select query for a given uri and triple.
     *
     * @param uri    The given uri.
     * @param triple The given triple.
     * @return The generated query.
     */
    @SuppressWarnings("unused")
    public Query getSelectQuery(CrawleableUri uri, Triple triple) {
        return getSelectQuery(uri, triple, false);
    }

    /**
     * Return a select query for the given uri and triple.
     *
     * @param uri        The given uri.
     * @param triple     The given triple.
     * @param bSelectAll Indicates whether the query should be a select all query or not.
     * @return
     */
    public Query getSelectQuery(CrawleableUri uri, Triple triple, boolean bSelectAll) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ?subject ?predicate ?object WHERE { GRAPH <");
        stringBuilder.append(uri.getUri());
        stringBuilder.append("> { ");
        if (bSelectAll) {
            stringBuilder.append("?subject ?predicate ?object ");
        } else {
            stringBuilder.append(triple.getSubject().getName());
            stringBuilder.append(" ");
            stringBuilder.append(triple.getPredicate().getName());
            stringBuilder.append(" ");
            stringBuilder.append(triple.getObject().getName());
            stringBuilder.append(" ; ");
        }
        stringBuilder.append("} } ");
        Query query = QueryFactory.create(stringBuilder.toString());
        return query;
    }

}

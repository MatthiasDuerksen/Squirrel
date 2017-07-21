package org.aksw.simba.squirrel.queue;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Cursor;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.data.uri.UriType;
import org.aksw.simba.squirrel.data.uri.UriUtils;
import org.aksw.simba.squirrel.model.RDBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;

@SuppressWarnings("rawtypes")
public class RDBQueue extends AbstractIpAddressBasedQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDBQueue.class);

    protected RDBConnector connector;
    private RethinkDB r = RethinkDB.r;

    public RDBQueue(String hostname, Integer port) {
        connector = new RDBConnector(hostname, port);
    }

    public void open() {
        this.connector.open();
        if(!squirrelDatabaseExists()) {
            r.dbCreate("squirrel").run(this.connector.connection);
        }
        if(!queueTableExists()) {
            r.db("squirrel").tableCreate("queue").run(this.connector.connection);
            r.db("squirrel").table("queue").indexCreate("ipAddressType",
                row -> r.array(row.g("ipAddress"), row.g("type"))).run(this.connector.connection);
            r.db("squirrel").table("queue").indexWait("ipAddressType").run(this.connector.connection);
        }
    }

    public void close() {
        r.db("squirrel").tableDrop("queue").run(this.connector.connection);
        this.connector.close();
    }

    public boolean squirrelDatabaseExists() {
        return this.connector.squirrelDatabaseExists();
    }

    public boolean queueTableExists() {
        return this.connector.tableExists("squirrel", "queue");
    }


    public void purge() {
        r.db("squirrel")
            .table("queue")
            .delete()
            .run(connector.connection);
    }

    public long length() {
        return r.db("squirrel")
            .table("queue")
            .count()
            .run(connector.connection);
    }

    @Override
    protected void addToQueue(CrawleableUri uri) {
        List ipAddressTypeKey = getIpAddressTypeKey(uri);
        // if URI exists update the uris list
        if(queueContainsIpAddressTypeKey(ipAddressTypeKey)) {
            LOGGER.debug("TypeKey is in the queue already");
            addCrawleableUri(uri, ipAddressTypeKey);
        } else {
            LOGGER.debug("TypeKey is not in the queue, creating a new one");
            addCrawleableUri(uri);
        }
    }

    public boolean queueContainsIpAddressTypeKey(List ipAddressTypeKey) {
        Cursor cursor = r.db("squirrel")
            .table("queue")
            .getAll(ipAddressTypeKey)
            .optArg("index", "ipAddressType")
            .run(connector.connection);
        if(cursor.hasNext()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getIpAddressTypeKey(CrawleableUri uri) {
        return packTuple(uri.getIpAddress().getHostAddress(), uri.getType().toString());
    }

    public List packTuple(String str_1, String str_2) {
        return r.array(str_1, str_2);
    }

    public void addCrawleableUri(CrawleableUri uri, List ipAddressTypeKey) {
        r.db("squirrel")
                .table("queue")
                .getAll(ipAddressTypeKey)
                .optArg("index", "ipAddressType")
                .update(queueItem -> r.hashMap("uris", queueItem.g("uris").append(uri.getUri().toString())))
                .run(connector.connection);
        LOGGER.debug("Inserted existing UriTypePair");
    }

    public void addCrawleableUri(CrawleableUri uri) {
        r.db("squirrel")
                .table("queue")
                .insert(crawleableUriToRDBHashMap(uri))
                .run(connector.connection);
        LOGGER.debug("Inserted new UriTypePair");
    }

    public MapObject crawleableUriToRDBHashMap(CrawleableUri uri) {
        InetAddress ipAddress = uri.getIpAddress();
        URI uriPath = uri.getUri();
        UriType uriType = uri.getType();
        return r.hashMap("uris", r.array(uriPath.toString()))
                .with("ipAddress", ipAddress.getHostAddress())
                .with("type", uriType.toString());
    }

    @Override
    protected Iterator<IpUriTypePair> getIterator() {
        Cursor cursor = r.db("squirrel")
                .table("queue")
                .orderBy()
                .optArg("index", "ipAddressType")
                .run(connector.connection);
        Iterator<IpUriTypePair> ipUriTypePairIterator = new Iterator<IpUriTypePair>() {
            @Override
            public boolean hasNext() {
                return cursor.hasNext();
            }

            @Override
            public IpUriTypePair next() {
                HashMap map = (HashMap) cursor.next();
                try {
                    InetAddress ipAddress = InetAddress.getByName(map.get("ipAddress").toString());
                    UriType uriType = UriType.valueOf(map.get("type").toString());
                    IpUriTypePair pair = new IpUriTypePair(ipAddress, uriType);
                    return pair;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        return ipUriTypePairIterator;
    }

    @Override
    protected List<CrawleableUri> getUris(IpUriTypePair pair) {
        List<CrawleableUri> uris = null;

        List ipAddressTypeKey = packTuple(pair.ip.getHostAddress(), pair.type.toString());
        Cursor cursor = r.db("squirrel")
                .table("queue")
                .getAll(ipAddressTypeKey)
                .optArg("index", "ipAddressType")
                .run(connector.connection);

        if (cursor.hasNext()) {
            //remove all URIs for the pair
            HashMap result = (HashMap) cursor.next();
            ArrayList uriStringList = (ArrayList) result.get("uris");
            LOGGER.debug("query result {}",result.toString());
            UriType type = UriType.valueOf(result.get("type").toString());
            uris = UriUtils.createCrawleableUriList(uriStringList, type);
            //remove from the queue
            r.db("squirrel")
                    .table("queue")
                    .getAll(ipAddressTypeKey)
                    .optArg("index", "ipAddressType")
                    .delete()
                    .run(connector.connection);
        }
        // return the URIs
        return uris;
    }

}
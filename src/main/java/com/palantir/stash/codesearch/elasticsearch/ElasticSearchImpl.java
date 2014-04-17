/**
 * Contains the elasticsearch Node and Client objects used by all other classes.
 *
 * These variables are all static since ElasticSearch best practice is apparently to
 * use 1 client per JVM. Also, we can clean up using a shutdown hook instead of relying on
 * finalize() or creating our own cleanup glue.
 *
 * Config is loaded from elasticsearch.yml in the resources path.
 */

package com.palantir.stash.codesearch.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.DisposableBean;

public class ElasticSearchImpl implements ElasticSearch, DisposableBean {

    private final Node node;

    private final Client client;

    public static final String ES_UPDATEALIAS = "scs-update";

    public static final String ES_SEARCHALIAS = "scs-search";

    public ElasticSearchImpl () {
        ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        // Hack to get names.txt from the elasticsearch CP
        Thread.currentThread().setContextClassLoader(Node.class.getClassLoader());
        node = NodeBuilder.nodeBuilder()
            .client(true)
            .node();
        client = node.client();
        Thread.currentThread().setContextClassLoader(currentLoader);
    }

    @Override
    public Node getNode () {
        return node;
    }

    @Override
    public Client getClient () {
        return client;
    }

    @Override
    public void destroy () {
        node.close();
    }

}

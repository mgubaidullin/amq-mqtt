package org.example.activemq;

import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ActiveMqRoute extends EndpointRouteBuilder {

    @ConfigProperty(name = "trustStore", defaultValue = "../../client.ts")
    String trustStore;

    @ConfigProperty(name = "trustStorePassword", defaultValue = "mypassword")
    String trustStorePassword;

    @ConfigProperty(name = "keyStore", defaultValue = "../../client.ks")
    String keyStore;

    @ConfigProperty(name = "keyStorePassword", defaultValue = "mypassword")
    String keyStorePassword;

    @Override
    public void configure() throws Exception {

        ActiveMQSslConnectionFactory cf = new ActiveMQSslConnectionFactory();
        cf.setTrustStore(trustStore);
        cf.setTrustStorePassword(trustStorePassword);
        cf.setKeyStore(keyStore);
        cf.setKeyStorePassword(keyStorePassword);

        from(activemq("queue:goc_cpc.std.apps.dia.events.newstop.item.wc.*.route.*").connectionFactory(cf))
                .routeId("ActiveMQ")
                .log("${body}")
                .process(e -> {
                    ActiveMQQueue queue = e.getIn().getHeader("JMSDestination", ActiveMQQueue.class);
                    e.getIn().setHeader("pdt", queue.getQueueName().split("\\.")[8]);
                })
                .setBody(simple("${body} ' + response'"))
                .toD(activemq("queue:reply.wc.${header.pdt}.route.${header.pdt}").connectionFactory(cf));
    }

}
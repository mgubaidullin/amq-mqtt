package org.example.activemq;

import org.apache.activemq.ActiveMQSslConnectionFactory;
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

        from(activemq("topic:goc_cpc.std.apps.dia.events.newstop.item.wc.*.route.*").connectionFactory(cf))
                .routeId("ActiveMQ")
                .log("${body}")
                .process(e -> {
                    ActiveMQTopic topic = e.getIn().getHeader("JMSDestination", ActiveMQTopic.class);
                    e.getIn().setHeader("pdt", topic.getTopicName().split("\\.")[8]);
                })
                .setBody(simple("${body} ' + response'"))
                .toD(activemq("topic:reply.wc.${header.pdt}.route.${header.pdt}").connectionFactory(cf))
        ;
    }

}
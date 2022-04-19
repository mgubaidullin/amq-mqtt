package org.example.mqtt;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.util.Properties;

@ApplicationScoped
public class MqttConsumerRoute extends EndpointRouteBuilder {

    @Override
    public void configure() throws Exception {

        Properties properties = new Properties();
        properties.setProperty("com.ibm.ssl.trustStore", "../../client.ts");
        properties.setProperty("com.ibm.ssl.trustStorePassword", "mypassword");
        properties.setProperty("com.ibm.ssl.keyStore", "../../client.ks");
        properties.setProperty("com.ibm.ssl.keyStorePassword", "mypassword");

        from(paho("reply.wc.{{pdt}}.route.{{pdt}}").cleanSession(true).sslClientProps(properties)).routeId("Consumer")
                .log("${body}");
    }

}
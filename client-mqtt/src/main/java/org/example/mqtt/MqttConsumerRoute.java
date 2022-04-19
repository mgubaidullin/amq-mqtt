package org.example.mqtt;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.Properties;

@ApplicationScoped
public class MqttConsumerRoute extends EndpointRouteBuilder {

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

        Properties properties = new Properties();
        properties.setProperty("com.ibm.ssl.trustStore", trustStore);
        properties.setProperty("com.ibm.ssl.trustStorePassword", trustStorePassword);
        properties.setProperty("com.ibm.ssl.keyStore", keyStore);
        properties.setProperty("com.ibm.ssl.keyStorePassword", keyStorePassword);

        from(paho("reply.wc.{{pdt}}.route.{{pdt}}").cleanSession(true).sslClientProps(properties)).routeId("Consumer")
                .log("${body}");
    }

}
package com.fistein.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class HttpsConfig {

    @Value("${server.ssl.key-store:}")
    private String keyStore;

    @Value("${server.ssl.key-store-password:}")
    private String keyStorePassword;

    @Value("${server.ssl.key-store-type:PKCS12}")
    private String keyStoreType;

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        
        if (keyStore != null && !keyStore.isEmpty()) {
            tomcat.addConnectorCustomizers(connector -> {
                connector.setScheme("https");
                connector.setSecure(true);
                connector.setProperty("keystoreFile", keyStore);
                connector.setProperty("keystorePass", keyStorePassword);
                connector.setProperty("keystoreType", keyStoreType);
                connector.setProperty("clientAuth", "false");
                connector.setProperty("sslProtocol", "TLS");
            });
        }
        
        return tomcat;
    }
}
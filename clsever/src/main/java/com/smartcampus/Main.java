package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main entry point. Starts an embedded Grizzly HTTP server.
 * No external Tomcat or GlassFish required — just run this class.
 *
 * Run with:  java -jar target/smart-campus-api.jar
 * API at:    http://localhost:8080/api/v1/
 */
public class Main {

    public static final String BASE_URI = "http://localhost:8080/api/v1/";
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static HttpServer startServer() {
        final ResourceConfig config = new ResourceConfig()
                .packages("com.smartcampus")
                .register(JacksonFeature.class);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        LOGGER.log(Level.INFO, "====================================================");
        LOGGER.log(Level.INFO, " Smart Campus Sensor API — RUNNING");
        LOGGER.log(Level.INFO, " API Base: {0}", BASE_URI);
        LOGGER.log(Level.INFO, "====================================================");
        LOGGER.log(Level.INFO, "Press ENTER to stop the server...");

        System.in.read();
        server.shutdownNow();
        LOGGER.log(Level.INFO, "Server stopped.");
    }
}

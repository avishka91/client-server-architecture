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
 * Main class to bootstrap the Grizzly HTTP server with Jersey JAX-RS resources.
 * 
 * Note: When using Grizzly as an embedded server, the @ApplicationPath annotation
 * on the Application subclass is not used. Instead, the base URI is specified
 * directly when creating the Grizzly server. We set the base URI to include
 * "/api/v1/" to match the required API versioning path.
 */
public class Main {

    // Base URI includes the API version path since Grizzly doesn't use @ApplicationPath
    public static final String BASE_URI = "http://localhost:8080/api/v1/";
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Creates and configures the Grizzly HTTP server with Jersey resource configuration.
     *
     * @return the configured HttpServer instance
     */
    public static HttpServer startServer() {
        // Create a ResourceConfig that scans the com.smartcampus package for JAX-RS components
        final ResourceConfig config = new ResourceConfig()
                .packages("com.smartcampus")
                .register(JacksonFeature.class);  // Explicitly register Jackson JSON provider

        // Create and start a new Grizzly HTTP server
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }

    /**
     * Main entry point for the application.
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        LOGGER.log(Level.INFO, "============================================================");
        LOGGER.log(Level.INFO, " Smart Campus Sensor API");
        LOGGER.log(Level.INFO, " Server started at: http://localhost:8080/");
        LOGGER.log(Level.INFO, " API Base Path:     {0}", BASE_URI);
        LOGGER.log(Level.INFO, " Discovery:         {0}", BASE_URI);
        LOGGER.log(Level.INFO, "============================================================");
        LOGGER.log(Level.INFO, "Press Enter to stop the server...");

        System.in.read();
        server.shutdownNow();
        LOGGER.log(Level.INFO, "Server stopped.");
    }
}

package com.smartcampus;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import jakarta.ws.rs.ApplicationPath;

/**
 * Main JAX-RS Application configuration class.
 *
 * Extends ResourceConfig (which itself extends Application) so that Jersey
 * can automatically scan and register all resource classes, exception mappers,
 * and filters found in the "com.smartcampus" package.
 *
 * Deployment modes:
 *  - Tomcat / GlassFish (WAR): This class is the active entry point.
 *    The @ApplicationPath("/api/v1") annotation sets the base URL, so all
 *    endpoints are accessible at: http://localhost:8080/<context-root>/api/v1/
 *
 *  - Standalone Grizzly (JAR): Main.java bootstraps the server directly and
 *    constructs its own ResourceConfig, so this class is bypassed in that mode.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {

    public SmartCampusApplication() {
        // Scan the entire com.smartcampus package for:
        //   - Resource classes (@Path)
        //   - Exception mappers (@Provider + ExceptionMapper)
        //   - Filters (ContainerRequestFilter / ContainerResponseFilter)
        packages("com.smartcampus");

        // Register Jackson so Jersey serialises POJOs to/from JSON automatically
        register(JacksonFeature.class);
    }
}

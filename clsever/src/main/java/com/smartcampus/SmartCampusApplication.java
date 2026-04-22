package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application subclass — present for specification completeness only.
 *
 * NOTE: This class is NOT active at runtime. The application is bootstrapped
 * programmatically in Main.java using a Grizzly HTTP server and a ResourceConfig
 * with package scanning (.packages("com.smartcampus")). When using the Grizzly
 * embedded server directly, JAX-RS does NOT use the @ApplicationPath annotation
 * or this Application subclass — the base URI is set explicitly in Main.java.
 *
 * This class is retained as documentation of intent and for potential future
 * deployment in a servlet container (e.g., Tomcat/WildFly) where it would
 * become the active entry point.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // No additional configuration required.
}

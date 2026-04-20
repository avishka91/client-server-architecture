package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application subclass that sets the base API path.
 * 
 * The @ApplicationPath annotation establishes the versioned entry point
 * for all API resources. All resource paths are relative to "/api/v1".
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // JAX-RS will automatically discover and register all resource classes
    // and providers in the scanned packages.
}

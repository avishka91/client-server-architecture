package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Root discovery endpoint for the Smart Campus Sensor API.
 * 
 * Provides essential API metadata including versioning information,
 * administrative contact details, and a map of primary resource collections
 * following HATEOAS principles.
 */
@Path("/")
public class DiscoveryResource {

    /**
     * GET /api/v1
     * Returns a JSON object with API metadata and navigable resource links.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiDiscovery() {
        Map<String, Object> discovery = new LinkedHashMap<>();

        // API Metadata
        discovery.put("apiName", "Smart Campus Sensor API");
        discovery.put("version", "1.0");
        discovery.put("description", "RESTful API for managing campus sensor infrastructure including rooms, sensors, and sensor readings.");

        // Resource Links (HATEOAS-style navigation)
        Map<String, Object> resources = new LinkedHashMap<>();

        Map<String, String> roomsLink = new LinkedHashMap<>();
        roomsLink.put("href", "/api/v1/rooms");
        roomsLink.put("method", "GET, POST");
        roomsLink.put("description", "Manage campus sensor rooms");
        resources.put("rooms", roomsLink);

        Map<String, String> sensorsLink = new LinkedHashMap<>();
        sensorsLink.put("href", "/api/v1/sensors");
        sensorsLink.put("method", "GET, POST");
        sensorsLink.put("description", "Manage sensors and their assignments");
        resources.put("sensors", sensorsLink);

        discovery.put("resources", resources);

        // Server Information
        Map<String, String> server = new LinkedHashMap<>();
        server.put("baseUri", "http://localhost:8080/api/v1");
        server.put("status", "running");
        discovery.put("server", server);

        return Response.ok(discovery).build();
    }
}

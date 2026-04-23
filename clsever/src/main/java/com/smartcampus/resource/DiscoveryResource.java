package com.smartcampus.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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

        // Administrative Contact
        Map<String, String> contact = new LinkedHashMap<>();
        contact.put("name", "Smart Campus Admin");
        contact.put("email", "admin@smartcampus.edu");
        contact.put("department", "IT Infrastructure & IoT");
        discovery.put("contact", contact);

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

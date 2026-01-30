package lab.soa.config;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    @Override
    public void filter(
        ContainerRequestContext requestContext,
        ContainerResponseContext responseContext
    ) throws IOException {
        String targetBaseUrl = System.getenv().getOrDefault(
            "TARGET_SERVICE_BASE_URL",
            "https://localhost:33511"
        );
        responseContext.getHeaders().add(
            "Access-Control-Allow-Origin", targetBaseUrl
        );
        responseContext.getHeaders().add(
            "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH"
        );
        responseContext.getHeaders().add(
            "Access-Control-Allow-Headers",
            "Origin, X-Requested-With, Content-Type, Accept, Authorization"
        );
        responseContext.getHeaders().add(
            "Access-Control-Allow-Credentials", "true"
        );
        responseContext.getHeaders().add(
            "Access-Control-Max-Age", "3600"
        );
    }
}

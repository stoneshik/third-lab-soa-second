package lab.soa.service;

import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

@ApplicationScoped
public class FlatProxyService {
    private static final Logger log = LoggerFactory.getLogger(FlatProxyService.class);
    private Client client;
    private String targetBaseUrl;

    @PostConstruct
    public void init() {
        targetBaseUrl = System.getenv().getOrDefault(
            "TARGET_SERVICE_BASE_URL",
            "https://localhost:33511"
        );
        try {
            client = ClientBuilder.newBuilder().build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to init HTTPS client", e);
        }
        log.info("FlatProxyService initialized, target={}", targetBaseUrl);
    }

    @PreDestroy
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }

    public Response proxyFindWithBalcony(
        String priceType,
        String balconyType,
        HttpServletRequest request
    ) {
        String path = String.format(
            "/api/v1/flats/agency/find-with-balcony/%s/%s",
            priceType,
            balconyType
        );
        String targetUrl = buildUrl(path);
        log.info("Proxy -> {}", targetUrl);
        WebTarget target = client.target(targetUrl);
        Invocation.Builder builder = target.request(MediaType.APPLICATION_XML);
        copyHeaders(request, builder);
        return executeGet(builder);
    }

    public Response proxyGetOrderedByTimeToMetro(
        String transportType,
        String sortType,
        Integer page,
        Integer size,
        HttpServletRequest request
    ) {
        UriBuilder uriBuilder = UriBuilder.fromUri(
            buildUrl(String.format(
                "/api/v1/flats/agency/get-ordered-by-time-to-metro/%s/%s",
                transportType,
                sortType
            ))
        );
        if (page != null) {
            uriBuilder.queryParam("page", page);
        }
        if (size != null) {
            uriBuilder.queryParam("size", size);
        }
        request.getParameterMap().forEach((k, values) -> {
            if ("page".equals(k) || "size".equals(k)) {
                return;
            }
            for (String v : values) {
                uriBuilder.queryParam(k, v);
            }
        });
        String targetUrl = uriBuilder.build().toString();
        log.info("Proxy → {}", targetUrl);
        WebTarget target = client.target(targetUrl);
        Invocation.Builder builder = target.request(MediaType.APPLICATION_XML);
        copyHeaders(request, builder);
        return executeGet(builder);
    }

    private Response executeGet(Invocation.Builder builder) {
        Response upstream = null;
        try {
            upstream = builder.get();
            String body = upstream.readEntity(String.class);
            return Response.status(upstream.getStatus())
                .type(upstream.getMediaType())
                .entity(body)
                .build();
        } catch (Exception e) {
            log.error("Upstream error", e);
            return Response.status(Response.Status.BAD_GATEWAY)
                .entity(e.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
        } finally {
            if (upstream != null) {
                upstream.close();
            }
        }
    }

    private void copyHeaders(HttpServletRequest from, Invocation.Builder to) {
        Enumeration<String> names = from.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if ("Host".equalsIgnoreCase(name)) {
                continue;
            }
            Enumeration<String> values = from.getHeaders(name);
            while (values.hasMoreElements()) {
                to.header(name, values.nextElement());
            }
        }
    }

    private String buildUrl(String path) {
        if (targetBaseUrl.endsWith("/") && path.startsWith("/")) {
            return targetBaseUrl.substring(0, targetBaseUrl.length() - 1) + path;
        }
        if (!targetBaseUrl.endsWith("/") && !path.startsWith("/")) {
            return targetBaseUrl + "/" + path;
        }
        return targetBaseUrl + path;
    }
}

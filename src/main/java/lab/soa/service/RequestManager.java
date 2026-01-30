package lab.soa.service;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

@ApplicationScoped
public class RequestManager {
    private static final Logger log = LoggerFactory.getLogger(RequestManager.class);
    private Client client;
    private String targetBaseUrl;

    @PostConstruct
    public void init() throws Exception {
        targetBaseUrl = System.getenv().getOrDefault(
            "TARGET_SERVICE_BASE_URL",
            "https://localhost:33511"
        );
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
        };
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        client = ClientBuilder.newBuilder()
            .sslContext(sslContext)
            .hostnameVerifier((hostname, session) -> true)
            .build();
        log.info("Request Manager initialized target={}", targetBaseUrl);
    }

    @PreDestroy
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }

    public Response executeGetRequest(
        String targetUrl,
        HttpServletRequest request
    ) {
        log.info("Proxy -> {}", targetUrl);
        WebTarget target = client.target(targetUrl);
        Invocation.Builder builder = target.request(MediaType.APPLICATION_XML);
        copyHeaders(request, builder);
        return executeGet(builder);
    }

    public String buildUrl(String path) {
        if (targetBaseUrl.endsWith("/") && path.startsWith("/")) {
            return targetBaseUrl.substring(0, targetBaseUrl.length() - 1) + path;
        }
        if (!targetBaseUrl.endsWith("/") && !path.startsWith("/")) {
            return targetBaseUrl + "/" + path;
        }
        return targetBaseUrl + path;
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
}

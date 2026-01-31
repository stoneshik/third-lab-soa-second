package lab.soa.ejb;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lab.soa.presentation.dto.responses.ProxyResponseDto;

@Singleton
@Startup
public class RequestManager {
    private static final Logger log = LoggerFactory.getLogger(RequestManager.class);
    private Client client;
    private String targetBaseUrl;

    @PostConstruct
    public void init() {
        try {
            targetBaseUrl = System.getenv().getOrDefault(
                "TARGET_SERVICE_BASE_URL",
                "https://localhost:33510"
            );
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] c, String a) {}
                    public void checkServerTrusted(X509Certificate[] c, String a) {}
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            client = ClientBuilder.newBuilder()
                .sslContext(sslContext)
                .hostnameVerifier((h, s) -> true)
                .build();
            log.info("RequestManager initialized, target={}", targetBaseUrl);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize RequestManager", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }

    public ProxyResponseDto executeGet(
        String path,
        Map<String, String[]> headers,
        Map<String, String[]> queryParams
    ) {
        String url = buildUrl(path);
        log.info("Proxy GET -> {}", url);
        WebTarget target = client.target(url);
        target = addQueryParams(target, queryParams);
        Invocation.Builder builder = target.request(MediaType.APPLICATION_XML);
        copyHeaders(headers, builder);
        try (Response upstream = builder.get()) {
            return ProxyResponseDto.builder()
                .status(upstream.getStatus())
                .mediaType(
                    upstream.getMediaType() != null
                        ? upstream.getMediaType().toString()
                        : MediaType.APPLICATION_XML
                )
                .body(upstream.readEntity(String.class))
                .build();
        } catch (Exception e) {
            log.error("Upstream request failed", e);
            return ProxyResponseDto.builder()
                .status(Response.Status.BAD_GATEWAY.getStatusCode())
                .mediaType(MediaType.TEXT_PLAIN)
                .body(e.getMessage())
                .build();
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

    private WebTarget addQueryParams(
        WebTarget target,
        Map<String, String[]> queryParams
    ) {
        if (queryParams != null) {
            for (Map.Entry<String,String[]> entry: queryParams.entrySet()) {
                for (String v: entry.getValue()) {
                    target = target.queryParam(entry.getKey(), v);
                }
            }
        }
        return target;
    }

    private void copyHeaders(Map<String, String[]> from, Invocation.Builder to) {
        if (from == null) return;
        for (var e : from.entrySet()) {
            for (String v : e.getValue()) {
                to.header(e.getKey(), v);
            }
        }
    }
}

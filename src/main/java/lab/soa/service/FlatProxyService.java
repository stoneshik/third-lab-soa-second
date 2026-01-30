package lab.soa.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

@ApplicationScoped
public class FlatProxyService {
    @Inject
    private RequestManager requestManager;

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
        String targetUrl = requestManager.buildUrl(path);
        return requestManager.executeGetRequest(
            targetUrl,
            request
        );
    }

    public Response proxyGetOrderedByTimeToMetro(
        String transportType,
        String sortType,
        Integer page,
        Integer size,
        HttpServletRequest request
    ) {
        UriBuilder uriBuilder = UriBuilder.fromUri(
            requestManager.buildUrl(
                String.format(
                    "/api/v1/flats/agency/get-ordered-by-time-to-metro/%s/%s",
                    transportType,
                    sortType
                )
            )
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
        return requestManager.executeGetRequest(
            targetUrl,
            request
        );
    }
}

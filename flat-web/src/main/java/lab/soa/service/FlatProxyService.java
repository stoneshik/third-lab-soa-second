package lab.soa.service;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;
import lab.soa.ejb.FlatProxyRemote;
import lab.soa.presentation.dto.responses.ProxyResponseDto;

@ApplicationScoped
public class FlatProxyService {
    @EJB(lookup = "java:global/flat-ejb-1.0.0/FlatProxyBean!lab.soa.ejb.FlatProxyRemote")
    private FlatProxyRemote proxyEjb;

    public Response proxyFindWithBalcony(
        String priceType,
        String balconyType,
        HttpServletRequest request
    ) {
        ProxyResponseDto dto = proxyEjb.proxyFindWithBalcony(
            priceType,
            balconyType,
            extractHeaders(request)
        );

        return Response.status(dto.getStatus())
            .type(dto.getMediaType())
            .entity(dto.getBody())
            .build();
    }

    public Response proxyGetOrderedByTimeToMetro(
        String transportType,
        String sortType,
        Integer page,
        Integer size,
        HttpServletRequest request
    ) {
        ProxyResponseDto dto = proxyEjb.proxyGetOrderedByTimeToMetro(
            transportType,
            sortType,
            page,
            size,
            extractHeaders(request)
        );

        return Response.status(dto.getStatus())
            .type(dto.getMediaType())
            .entity(dto.getBody())
            .build();
    }

    private Map<String, String[]> extractHeaders(HttpServletRequest request) {
        if (request == null) {
            return Collections.emptyMap();
        }
        Map<String, String[]> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if ("host".equalsIgnoreCase(name)) {
                continue;
            }
            Enumeration<String> values = request.getHeaders(name);
            headers.put(name, toArray(values));
        }
        return headers;
    }

    private String[] toArray(Enumeration<String> values) {
        return values == null
            ? new String[0]
            : Collections.list(values).toArray(String[]::new);
    }
}

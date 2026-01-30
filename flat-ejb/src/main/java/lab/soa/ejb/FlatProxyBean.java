package lab.soa.ejb;

import java.util.HashMap;
import java.util.Map;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import lab.soa.presentation.dto.responses.ProxyResponseDto;

@Stateless
public class FlatProxyBean implements FlatProxyRemote {
    @EJB
    private RequestManager requestManager;

    @Override
    public ProxyResponseDto proxyFindWithBalcony(
        String priceType,
        String balconyType,
        Map<String, String[]> headers
    ) {
        String path = String.format(
            "/api/v1/flats/agency/find-with-balcony/%s/%s",
            priceType,
            balconyType
        );
        return requestManager.executeGet(path, headers, null);
    }

    @Override
    public ProxyResponseDto proxyGetOrderedByTimeToMetro(
        String transportType,
        String sortType,
        Integer page,
        Integer size,
        Map<String, String[]> headers
    ) {
        String path = String.format(
            "/api/v1/flats/agency/get-ordered-by-time-to-metro/%s/%s",
            transportType,
            sortType
        );
        Map<String, String[]> queryParams = new HashMap<>();
        if (page != null) {
            queryParams.put("page", new String[]{page.toString()});
        }
        if (size != null) {
            queryParams.put("size", new String[]{size.toString()});
        }
        return requestManager.executeGet(path, headers, queryParams);
    }
}

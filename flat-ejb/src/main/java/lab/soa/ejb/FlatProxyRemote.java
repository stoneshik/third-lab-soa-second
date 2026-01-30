package lab.soa.ejb;

import java.util.Map;

import jakarta.ejb.Remote;
import lab.soa.presentation.dto.responses.ProxyResponseDto;

@Remote
public interface FlatProxyRemote {
    ProxyResponseDto proxyFindWithBalcony(
        String priceType,
        String balconyType,
        Map<String, String[]> headers
    );
    ProxyResponseDto proxyGetOrderedByTimeToMetro(
        String transportType,
        String sortType,
        Integer page,
        Integer size,
        Map<String, String[]> headers
    );
}

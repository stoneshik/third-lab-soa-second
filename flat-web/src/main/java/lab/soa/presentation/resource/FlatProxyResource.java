package lab.soa.presentation.resource;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lab.soa.service.FlatProxyService;

@Path("/flats/agency")
@Produces(MediaType.APPLICATION_XML)
public class FlatProxyResource {
    @Inject
    private FlatProxyService proxyService;

    @GET
    @Path("/find-with-balcony/{priceType}/{balconyType}")
    public Response findWithBalcony(
        @PathParam("priceType") String priceType,
        @PathParam("balconyType") String balconyType,
        @Context HttpServletRequest request
    ) {
        return proxyService.proxyFindWithBalcony(
            priceType,
            balconyType,
            request
        );
    }

    @GET
    @Path("/get-ordered-by-time-to-metro/{transportType}/{sortType}")
    public Response getOrderedByTimeToMetro(
        @PathParam("transportType") String transportType,
        @PathParam("sortType") String sortType,
        @QueryParam("page") Integer page,
        @QueryParam("size") Integer size,
        @Context HttpServletRequest request
    ) {
        return proxyService.proxyGetOrderedByTimeToMetro(
            transportType,
            sortType,
            page,
            size,
            request
        );
    }
}

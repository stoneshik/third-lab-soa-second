package lab.soa.presentation.exception.mapper;

import java.time.LocalDateTime;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lab.soa.presentation.dto.responses.ErrorMessageResponseDto;
import lab.soa.presentation.dto.responses.MyMediaType;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        ErrorMessageResponseDto dto = ErrorMessageResponseDto.builder()
            .message("Internal server error")
            .time(LocalDateTime.now().toString())
            .build();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(dto)
            .type(MyMediaType.xml.getMediaType())
            .build();
    }
}

package lab.soa.presentation.dto.responses;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProxyResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "status")
    private int status;

    @XmlElement(name = "mediaType")
    private String mediaType;

    @XmlElement(name = "body")
    private String body;
}

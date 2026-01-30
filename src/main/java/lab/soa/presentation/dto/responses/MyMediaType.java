package lab.soa.presentation.dto.responses;

public enum MyMediaType {
    xml("application/xml;charset=UTF-8");

    private final String mediaType;

    private MyMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return mediaType;
    }
}

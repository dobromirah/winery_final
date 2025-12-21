package bg.tu.varna.si.dto;

public class NotificationResponseDTO {
    public Long id;

    public String type;         // GRAPE_LOW, BOTTLE_LOW, SHORTAGE
    public String resourceType; // GRAPE, BOTTLE
    public Long resourceId;
    public String level;        // WARNING, CRITICAL
    public String message;
    public String createdAt;
    public boolean isRead;
}

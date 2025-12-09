package bg.tu.varna.si.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Notification extends PanacheEntity {

    public String type; // GRAPE_LOW, BOTTLE_LOW, SHORTAGE
    public String resourceType; // GRAPE, BOTTLE
    public Long resourceId;

    public String level; // WARNING, CRITICAL
    public String message;

    public LocalDateTime createdAt;
    public boolean isRead;
}


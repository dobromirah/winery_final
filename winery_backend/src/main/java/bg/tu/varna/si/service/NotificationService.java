package bg.tu.varna.si.service;

import bg.tu.varna.si.model.*;
import bg.tu.varna.si.repository.NotificationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class NotificationService {

    @Inject
    NotificationRepository notificationRepository;

    public Notification createNotification(
            String type,
            String resourceType,
            Long resourceId,
            String level,
            String message
    ) {
        Notification n = new Notification();
        n.type = type;
        n.resourceType = resourceType;
        n.resourceId = resourceId;
        n.level = level;
        n.message = message;
        n.createdAt = LocalDateTime.now();
        n.isRead = false;

        notificationRepository.persist(n);
        return n;
    }

    public List<Notification> checkGrapeLevels(GrapeVariety variety, double totalKg) {
        List<Notification> out = new ArrayList<>();

        if (totalKg < 0) {
            out.add(createNotification(
                    "SHORTAGE", "GRAPE", variety.id, "CRITICAL",
                    "Stock of grape " + variety.name + " is NEGATIVE!"
            ));
        }

        if (totalKg <= variety.criticalMinKg) {
            out.add(createNotification(
                    "GRAPE IS LOW", "GRAPE", variety.id, "WARNING",
                    "Grape variety " + variety.name + " is below minimum level."
            ));
        }

        return out;
    }

    public List<Notification> checkBottleLevels(BottleType bottleType, int totalQty) {
        List<Notification> out = new ArrayList<>();

        if (totalQty < 0) {
            out.add(createNotification(
                    "SHORTAGE", "BOTTLE", bottleType.id, "CRITICAL",
                    "Stock of bottle type " + bottleType.description + " is NEGATIVE!"
            ));
        }

        if (totalQty <= bottleType.criticalMinQty) {
            out.add(createNotification(
                    "BOTTLE IS LOW", "BOTTLE", bottleType.id, "WARNING",
                    "Bottle type " + bottleType.description + " is below minimum stock."
            ));
        }

        return out;
    }
}

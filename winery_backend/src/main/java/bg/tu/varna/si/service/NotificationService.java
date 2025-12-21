package bg.tu.varna.si.service;

import bg.tu.varna.si.model.*;
import bg.tu.varna.si.repository.NotificationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

@ApplicationScoped
public class NotificationService {

    @Inject
    NotificationRepository notificationRepository;

    public void createNotification(
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
    }


    // ====== GRAPE CHECK ======
    public void checkGrapeLevels(GrapeVariety variety, double totalKg) {

        if (totalKg < 0) {
            createNotification(
                    "SHORTAGE", "GRAPE", variety.id, "CRITICAL",
                    "Stock of grape " + variety.name + " is NEGATIVE!"
            );
        }

        if (totalKg <= variety.criticalMinKg) {
            createNotification(
                    "GRAPE_LOW", "GRAPE", variety.id, "WARNING",
                    "Grape variety " + variety.name + " is below minimum level."
            );
        }
    }


    // ====== BOTTLE CHECK ======
    public void checkBottleLevels(BottleType bottleType, int totalQty) {

        if (totalQty < 0) {
            createNotification(
                    "SHORTAGE", "BOTTLE", bottleType.id, "CRITICAL",
                    "Stock of bottle type " + bottleType.description + " is NEGATIVE!"
            );
        }

        if (totalQty <= bottleType.criticalMinQty) {
            createNotification(
                    "BOTTLE_LOW", "BOTTLE", bottleType.id, "WARNING",
                    "Bottle type " + bottleType.description + " is below minimum stock."
            );
        }
    }

}

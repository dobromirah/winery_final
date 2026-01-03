package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.*;
import bg.tu.varna.si.model.*;
import bg.tu.varna.si.repository.*;
import bg.tu.varna.si.service.BottleFillingService;
import bg.tu.varna.si.service.CurrentUserService;
import bg.tu.varna.si.service.NotificationService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Path("/bottled-wines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BottledWineResource {

    @Inject BottledWineRepository bottledRepo;
    @Inject WineBatchRepository batchRepo;
    @Inject BottleTypeRepository bottleTypeRepo;
    @Inject BottleStockMovementRepository stockRepo;

    @Inject BottleFillingService fillingService;
    @Inject NotificationService notificationService;
    @Inject CurrentUserService currentUserService;

    // ------------------- PLAN (NO DB WRITE) -------------------
    @POST
    @Path("/plan")
    @RolesAllowed({"OPERATOR", "WAREHOUSE_MANAGER"})
    public AutoBottlePlanResponseDTO plan(AutoBottlePlanRequestDTO dto) {
        if (dto == null || dto.batchId == null) throw new WebApplicationException("batchId is required", 400);

        WineBatch batch = batchRepo.findById(dto.batchId);
        if (batch == null) throw new NotFoundException("Batch not found");

        if (batch.producedLiters <= 0) throw new WebApplicationException("Batch has no produced liters.", 400);

        double alreadyBottled = batch.bottledLiters; // ⚠️ трябва да имаш това поле (default 0)
        double remaining = batch.producedLiters - alreadyBottled;
        if (remaining <= 0) throw new WebApplicationException("Nothing left to bottle.", 400);

        List<BottleType> types = bottleTypeRepo.listAll();
        var result = fillingService.plan(remaining, types, dto.preferredBottleTypeId, dto.allowedBottleTypeIds);

        AutoBottlePlanResponseDTO res = new AutoBottlePlanResponseDTO();
        res.items = result.bottleCounts.entrySet().stream().map(e -> {
            BottleType t = e.getKey();
            Integer count = e.getValue();
            BottlePlanItemDTO it = new BottlePlanItemDTO();
            it.bottleTypeId = t.id;
            it.volumeMl = t.volumeMl;
            it.description = t.description;
            it.count = count;
            return it;
        }).collect(Collectors.toList());
        res.leftoverLiters = result.leftoverLiters;
        res.plannedBottledLiters = result.plannedBottledLiters;
        return res;
    }

    // ------------------- APPLY (DB WRITE) -------------------
    @POST
    @RolesAllowed({"OPERATOR", "WAREHOUSE_MANAGER"})
    @Transactional
    public BottleApplyResponseDTO apply(BottleApplyRequestDTO dto) {
        if (dto == null || dto.batchId == null) throw new WebApplicationException("batchId is required", 400);
        if (dto.items == null || dto.items.isEmpty()) throw new WebApplicationException("items is required", 400);

        WineBatch batch = batchRepo.findById(dto.batchId);
        if (batch == null) throw new NotFoundException("Batch not found");

        if ("CANCELLED".equals(batch.status)) throw new WebApplicationException("Batch is cancelled.", 400);
        if (batch.producedLiters <= 0) throw new WebApplicationException("Batch has no produced liters.", 400);

        AppUser user = currentUserService.getCurrentUser();

        double alreadyBottled = batch.bottledLiters;
        double remainingLiters = batch.producedLiters - alreadyBottled;
        if (remainingLiters <= 0) throw new WebApplicationException("Nothing left to bottle.", 400);

        // Load bottle types + validate counts
        Map<Long, BottleType> typeById = bottleTypeRepo.listAll().stream()
                .collect(Collectors.toMap(t -> t.id, t -> t));

        // validate stock & compute liters
        double bottledNowLiters = 0.0;
        List<BottlePlanItemDTO> responseItems = new ArrayList<>();

        for (BottleApplyRequestDTO.Item it : dto.items) {
            if (it == null) continue;
            if (it.count <= 0) continue;

            BottleType t = typeById.get(it.bottleTypeId);
            if (t == null) throw new NotFoundException("BottleType not found: " + it.bottleTypeId);

            int available = stockRepo.getTotalQuantityForBottle(t.id);
            if (available < it.count) {
                throw new WebApplicationException(
                        "Not enough bottles: " + t.description + " (needed " + it.count + ", available " + available + ")",
                        400
                );
            }

            bottledNowLiters += (it.count * t.volumeMl) / 1000.0;

            BottlePlanItemDTO out = new BottlePlanItemDTO();
            out.bottleTypeId = t.id;
            out.volumeMl = t.volumeMl;
            out.description = t.description;
            out.count = it.count;
            responseItems.add(out);
        }

        if (bottledNowLiters <= 0) throw new WebApplicationException("Nothing to bottle (all counts are 0).", 400);
        if (bottledNowLiters > remainingLiters + 1e-9) {
            throw new WebApplicationException("Planned bottling exceeds remaining produced liters.", 400);
        }

        // Persist for each line
        for (BottlePlanItemDTO it : responseItems) {
            BottleType t = typeById.get(it.bottleTypeId);

            BottledWine bw = new BottledWine();
            bw.batch = batch;
            bw.bottleType = t;
            bw.quantityBottles = it.count;
            bottledRepo.persist(bw);

            BottleStockMovement m = new BottleStockMovement();
            m.bottleType = t;
            m.quantity = -it.count;
            m.movementType = "OUT";
            m.createdAt = LocalDateTime.now();
            m.createdBy = user;
            stockRepo.persist(m);

            int totalQty = stockRepo.getTotalQuantityForBottle(t.id);
            notificationService.checkBottleLevels(t, totalQty);
        }

        // Update batch
        batch.bottledLiters = alreadyBottled + bottledNowLiters;

        BottleApplyResponseDTO res = new BottleApplyResponseDTO();
        res.batchId = batch.id;
        res.bottledNowLiters = bottledNowLiters;
        res.totalBottledLiters = batch.bottledLiters;
        res.leftoverLiters = batch.producedLiters - batch.bottledLiters;
        res.items = responseItems;
        return res;
    }
}

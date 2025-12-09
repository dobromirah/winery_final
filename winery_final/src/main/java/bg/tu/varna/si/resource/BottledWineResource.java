package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.*;
import bg.tu.varna.si.mapper.BottledWineMapper;
import bg.tu.varna.si.model.*;
import bg.tu.varna.si.repository.*;
import bg.tu.varna.si.service.BottleFillingService;
import bg.tu.varna.si.service.NotificationService;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/bottled-wines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BottledWineResource {

    @Inject
    BottledWineRepository bottledRepo;

    @Inject
    WineBatchRepository batchRepo;

    @Inject
    BottleTypeRepository bottleTypeRepo;

    @Inject
    BottleStockMovementRepository stockRepo;

    @Inject
    AppUserRepository userRepo;

    @Inject
    BottleFillingService fillingService;

    @Inject
    NotificationService notificationService;

    // ------------------- LIST -------------------
    @GET
    public List<BottledWineResponseDTO> listAll() {
        return bottledRepo.listAll()
                .stream()
                .map(BottledWineMapper::toDTO)
                .collect(Collectors.toList());
    }


    // ------------------- AUTO BOTTLING -------------------
    @POST
    @Path("/auto")
    @Transactional
    public AutoBottleResponseDTO autoFill(AutoBottleRequestDTO dto) {

        WineBatch batch = batchRepo.findById(dto.batchId);
        if (batch == null)
            throw new NotFoundException("Batch not found");

        AppUser user = userRepo.findById(dto.createdById);
        if (user == null)
            throw new NotFoundException("User not found");

        List<BottleType> types = bottleTypeRepo.listAll();

        var result = fillingService.fillOptimally(batch.producedLiters, types);

        AutoBottleResponseDTO response = new AutoBottleResponseDTO();
        response.items = new ArrayList<>();
        response.leftoverLiters = result.leftoverLiters;

        for (var entry : result.bottleCounts.entrySet()) {
            BottleType type = entry.getKey();
            int count = entry.getValue();

            // SAVE BOTTLED WINE
            BottledWine bw = new BottledWine();
            bw.batch = batch;
            bw.bottleType = type;
            bw.quantityBottles = count;
            bottledRepo.persist(bw);

            // STOCK MOVEMENT OUT
            BottleStockMovement m = new BottleStockMovement();
            m.bottleType = type;
            m.quantity = -count;
            m.movementType = "OUT";
            m.createdAt = java.time.LocalDateTime.now();
            m.createdBy = user;
            stockRepo.persist(m);

            // NOTIFICATION CHECK
            int totalQty = stockRepo.getTotalQuantityForBottle(type.id);
            notificationService.checkBottleLevels(type, totalQty);

            // RESPONSE ENTRY
            AutoBottleResponseDTO.Item item = new AutoBottleResponseDTO.Item();
            item.bottleTypeId = type.id;
            item.description = type.description;
            item.count = count;

            response.items.add(item);
        }

        return response;
    }
}

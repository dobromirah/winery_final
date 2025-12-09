package bg.tu.varna.si.resource;

import bg.tu.varna.si.dto.*;
import bg.tu.varna.si.mapper.WineBatchMapper;
import bg.tu.varna.si.model.*;
import bg.tu.varna.si.repository.*;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
public class ReportResource {

    @Inject GrapeVarietyRepository varietyRepo;
    @Inject GrapeStockMovementRepository grapeStockRepo;

    @Inject BottleTypeRepository bottleTypeRepo;
    @Inject BottleStockMovementRepository bottleStockRepo;

    @Inject WineBatchRepository batchRepo;
    @Inject WineBatchGrapeUsageRepository usageRepo;

    @Inject BottledWineRepository bottledWineRepo;

    // -------------------------------------------------------
    // GRAPE STOCK REPORT
    // -------------------------------------------------------
    @GET
    @Path("/grapes")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER", "OPERATOR"})
    public List<GrapeStockReportDTO> grapeStockReport() {
        return varietyRepo.listAll()
                .stream()
                .map(v -> {
                    double totalKg = grapeStockRepo.getTotalKgForVariety(v.id);

                    GrapeStockReportDTO dto = new GrapeStockReportDTO();
                    dto.varietyId = v.id;
                    dto.varietyName = v.name;
                    dto.category = v.category;
                    dto.currentKg = totalKg;
                    dto.criticalMinKg = v.criticalMinKg;
                    dto.belowMinimum = totalKg <= v.criticalMinKg;

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // BOTTLE STOCK REPORT
    // -------------------------------------------------------
    @GET
    @Path("/bottles")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER", "OPERATOR"})
    public List<BottleStockReportDTO> bottleStockReport() {
        return bottleTypeRepo.listAll()
                .stream()
                .map(bt -> {
                    int totalQty = bottleStockRepo.getTotalQuantityForBottle(bt.id);

                    BottleStockReportDTO dto = new BottleStockReportDTO();
                    dto.bottleTypeId = bt.id;
                    dto.volumeMl = bt.volumeMl;
                    dto.description = bt.description;
                    dto.currentQty = totalQty;
                    dto.criticalMinQty = bt.criticalMinQty;
                    dto.belowMinimum = totalQty <= bt.criticalMinQty;

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // WINE BATCH PRODUCTION REPORT (OPTIONAL DATE RANGE)
    // -------------------------------------------------------
    @GET
    @Path("/batches")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER", "OPERATOR"})
    public List<WineBatchResponseDTO> batchReport(
            @QueryParam("from") String from,
            @QueryParam("to") String to
    ) {
        LocalDateTime fromDate = parseDate(from);
        LocalDateTime toDate = parseDate(to);

        List<WineBatch> batches = batchRepo.listAll();

        if (fromDate != null)
            batches = batches.stream().filter(b -> b.createdAt.isAfter(fromDate)).toList();

        if (toDate != null)
            batches = batches.stream().filter(b -> b.createdAt.isBefore(toDate)).toList();

        return batches.stream()
                .map(batch -> {
                    List<WineBatchGrapeUsage> usages =
                            usageRepo.list("batch.id", batch.id);
                    return WineBatchMapper.toDTO(batch, usages);
                })
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // BOTTLED WINE REPORT
    // -------------------------------------------------------
    @GET
    @Path("/bottled-wine")
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER", "OPERATOR"})
    public List<BottledWineReportDTO> bottledWineReport() {

        return bottledWineRepo.listAll()
                .stream()
                .map(b -> {
                    BottledWineReportDTO dto = new BottledWineReportDTO();

                    dto.id = b.id;
                    dto.batchId = b.batch.id;
                    dto.wineTypeName = b.batch.wineType.name;

                    dto.bottleTypeId = b.bottleType.id;
                    dto.volumeMl = b.bottleType.volumeMl;
                    dto.bottleDescription = b.bottleType.description;

                    dto.quantityBottles = b.quantityBottles;

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Utility to parse date safely
    private LocalDateTime parseDate(String date) {
        if (date == null) return null;
        try {
            return LocalDateTime.parse(date);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}

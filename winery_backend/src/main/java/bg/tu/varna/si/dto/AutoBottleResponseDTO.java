package bg.tu.varna.si.dto;

import java.util.List;

public class AutoBottleResponseDTO {

    public double leftoverLiters;
    public List<Item> items;

    public static class Item {
        public Long bottleTypeId;
        public String description;
        public int count;
    }
}

package bg.tu.varna.si.dto;

import java.util.List;

public class BottleApplyRequestDTO {
    public Long batchId;
    public List<Item> items;

    public static class Item {
        public Long bottleTypeId;
        public int count;
    }
}

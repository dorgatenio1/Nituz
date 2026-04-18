package presentation;

import service.InventoryService;

public class Main {
    public static void main(String[] args) {
        InputReader reader = new InputReaderScanner();
        InventoryService service = new InventoryService();
        new InventoryMenu(reader, service).start();
    }
}

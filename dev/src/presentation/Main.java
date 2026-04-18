package presentation;

import service.InventoryService;

public class Main {
    public static void main(String[] args) {
        InputReader reader = new InputReaderScanner();
        InventoryService service = new InventoryService();

        int mode = 0;
        while (mode != 1 && mode != 2) {
            System.out.println("\n=== INVENTORY MANAGEMENT SYSTEM ===");
            System.out.println("Select startup mode:");
            System.out.println("  1. Live Mode");
            System.out.println("  2. Test Mode  (pre-loaded sample data)");
            System.out.print("Choose: ");
            mode = reader.readInt();
            if (mode != 1 && mode != 2)
                System.out.println("Invalid option. Please enter 1 or 2.");
        }

        if (mode == 2)
            TestDataLoader.load(service);

        new InventoryMenu(reader, service).start();
    }
}

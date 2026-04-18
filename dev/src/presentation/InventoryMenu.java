package presentation;

import service.InventoryService;

public class InventoryMenu {

    private final InputReader reader;
    private final InventoryService service;
    private final ProductMenu productMenu;
    private final ReportMenu reportMenu;
    private final CategoryMenu categoryMenu;
    private final InventoryCountMenu inventoryCountMenu;

    public InventoryMenu(InputReader reader, InventoryService service) {
        this.reader = reader;
        this.service = service;
        this.productMenu = new ProductMenu(reader, service);
        this.reportMenu = new ReportMenu(reader, service);
        this.categoryMenu = new CategoryMenu(reader, service);
        this.inventoryCountMenu = new InventoryCountMenu(reader, service);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = reader.readInt();
            switch (choice) {
                case 1: productMenu.start();       break;
                case 2: reportMenu.start();        break;
                case 3: categoryMenu.start();      break;
                case 4: inventoryCountMenu.start(); break;
                case 5: running = false;            break;
                default: System.out.println("Invalid option. Please try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    private void printMenu() {
        System.out.println("\n=== INVENTORY MANAGEMENT SYSTEM ===");
        System.out.println(" 1. Product Management");
        System.out.println(" 2. Reports");
        System.out.println(" 3. Category Management");
        System.out.println(" 4. Inventory Count");
        System.out.println(" 5. Exit");
        System.out.print("Choose: ");
    }
}

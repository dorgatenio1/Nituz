package Presentation_Layer;
import Service_Layer.InventoryService;

import java.util.*;

public class InventoryMenu {
    private InputReader reader;
    private InventoryService inventoryService;
    
    public InventoryMenu(){
        reader = new InputReaderScanner(new Scanner(System.in));
        inventoryService = new InventoryService();
    }

    public InventoryMenu(InputReader reader, InventoryService inventoryService){
        this.reader = reader;
        this.inventoryService = inventoryService;
    }

    public void startMain() {
        System.out.println("\n===================================");
        System.out.println("   Super-Lee Inventory System   ");
        System.out.println("===================================");
        
        boolean exitSystem = false;
        
        while (!exitSystem) {
            displayMainMenu();
            int mainChoice = reader.readInt();

            switch (mainChoice) {
                case 1:
                    ProductMenu p = new ProductMenu(reader, inventoryService);
                    p.start();
                    break;
                case 2:
                    ReportMenu r = new ReportMenu(reader, inventoryService);
                    r.start();
                    break;
                case 3:
                    CategoryMenu c = new CategoryMenu(reader, inventoryService);
                    c.start();
                    break;
                case 4:
                    InventoryCountMenu ic = new InventoryCountMenu(reader, inventoryService);
                    ic.start();
                    break;
                case 0:
                    exitSystem = true;
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n===================================");
        System.out.println("           Main Menu              ");
        System.out.println("===================================");
        System.out.println("  1. Product Management");
        System.out.println("  2. Reports");
        System.out.println("  3. Category Management");
        System.out.println("  4. Inventory Count");
        System.out.println("  0. Exit");
        System.out.println("-----------------------------------");
        System.out.print("Your choice: ");
    }
}
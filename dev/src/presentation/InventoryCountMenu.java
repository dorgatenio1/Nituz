package Presentation_Layer;

import Service_Layer.InventoryService;
import Service_Layer.Response;

import java.util.ArrayList;
import java.util.List;

public class InventoryCountMenu {
    private InputReader reader;
    private InventoryService inventoryService;

    public InventoryCountMenu(InputReader inputReader, InventoryService inventoryService) {
        this.reader = inputReader;
        this.inventoryService = inventoryService;
    }

    public void start(){
        handleCountMenu();
    }

    private void handleCountMenu(){
        boolean exitMenu = false;

        while (!exitMenu) {
            displayCountMenu();
            int choice = reader.readInt();

            switch (choice) {
                case 1:
                    regularCount();
                    break;
                case 2:
                    updateInventory();
                    break;
                case 3:
                    showProductsToRestock();
                    break;
                case 0:
                    exitMenu = true;
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    private void regularCount() {
        boolean exitMenu = false;
        System.out.println("\n===== Regular Inventory Count =====");
        while(!exitMenu) {
            int productId = -1;
            while (productId == -1 && !exitMenu) {
                System.out.println("Enter the product ID for inventory count (or type '0' to return to menu):");
                int choice = reader.readInt();
                switch (choice) {
                    case 0:
                        exitMenu = true;
                        System.out.println("Returning to main menu...");
                        break;
                    default:
                        if (choice > 0) {
                            productId = choice;
                        } else {
                            System.out.println("Invalid product ID. Please try again.");
                        }
                }
            }
            if (exitMenu) return;
            int itemsInStock = -1;
            while (itemsInStock < 0) {
                System.out.println("Enter the number of items in stock for product ID " + productId + ":");
                itemsInStock = reader.readInt();
                if (itemsInStock < 0) {
                    System.out.println("Invalid input. The number of items in stock cannot be negative. Please try again.");
                }
            }

            int itemsOnShelf = -1;
            while (itemsOnShelf < 0) {
                System.out.println("Enter the number of items on the shelf for product ID " + productId + ":");
                itemsOnShelf = reader.readInt();
                if (itemsOnShelf < 0) {
                    System.out.println("Invalid input. The number of items on the shelf cannot be negative. Please try again.");
                }
            }
            Response<Boolean> r = inventoryService.inventoryCount(productId, itemsInStock, itemsOnShelf);
            if (r.getValue() != null && r.getValue())
                System.out.println("Inventory count recorded successfully for product ID " + productId + ":");
            else
                System.out.println("Failed to record inventory count. Error: " + r.getMessage());
        }
        System.out.println("Returning to main menu...");
    }

    private void displayCountMenu() {
        System.out.println("\n===================================");
        System.out.println("          Inventory Count         ");
        System.out.println("===================================");
        System.out.println("  1. Regular Count");
        System.out.println("  2. Update Product Inventory");
        System.out.println("  3. Show Products to Restock");
        System.out.println("  0. Back to Main Menu");
        System.out.println("-----------------------------------");
        System.out.print("Your choice: ");
    }

    private void showProductsToRestock(){
        Response<List<String>> response = inventoryService.showProductToRestock();

        // Check the response and display appropriate messages
        if (response.getValue()!=null) {
            if(response.getValue().isEmpty())
                System.out.println("There are no products to restock.");
            else
                System.out.println(response.getValue());
        }else{
            System.out.println("Failed to show product to restock. Error: " + response.getMessage());
        }
    }

    public void updateInventory() {
        boolean exitMenu = false;
        System.out.println("===== Update Inventory =====");
        int productId=-1;
        while (!exitMenu) {
            int choice;
            while(productId==-1 && !exitMenu) {
                System.out.println("Enter the product ID to update the inventory(or type '0' to return to menu): ");
                choice = reader.readInt();
                switch (choice) {
                    case 0:
                        exitMenu = true;
                        System.out.println("Returning to main menu...");
                        break;
                    default:
                        if (choice > 0) {
                            productId = choice;
                        } else
                            System.out.println("Invalid option. Please try again.");
                }
            }
            List<Integer> idInShelf=new ArrayList<>();
            while (idInShelf.isEmpty() && !exitMenu) { // Keep asking until valid input is given
                try {
                    // Prompt user to enter item IDs (comma-separated)
                    System.out.println("Enter item IDs of the product that are in the shelf (comma-separated, e.g., 1,2,3) or type '0' to exit ");
                    System.out.println("If there are no items in the shelf, please enter '-' (minus sign)");
                    System.out.print("your choice: ");
                    String input = reader.readString();

                    // Allow user to exit
                    if (input.equals("0")) {
                        System.out.println("Exiting to product menu...");
                        exitMenu = true;
                    }

                    // Check if the input is empty
                    if (input.isEmpty()) {
                        System.out.println("Input cannot be empty. Please enter valid item IDs.");
                    }
                    else {
                        if(!input.equals("-")) {
                            // Split input by commas and parse into integers
                            String[] idStrings = input.split(",");
                            for (String idString : idStrings) {
                                int id = Integer.parseInt(idString.trim());
                                if (id <= 0) {
                                    throw new NumberFormatException("Item IDs must be positive numbers.");
                                }
                                idInShelf.add(id);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please ensure you enter valid positive integers separated by commas.");
                    idInShelf.clear(); // Clear the list and prompt again
                }
            }
            List<Integer> idInStock=new ArrayList<>();
            while (idInStock.isEmpty() && !exitMenu) { // Keep asking until valid input is given
                try {
                    // Prompt user to enter item IDs (comma-separated)
                    System.out.println("Enter item IDs of the product that are in stock (comma-separated, e.g., 1,2,3) or type '0' to exit ");
                    System.out.println("If there are no items in the shelf, please enter '-' (minus sign)");
                    System.out.print("your choice: ");
                    String input = reader.readString();

                    // Allow user to exit
                    if (input.equals("0")) {
                        System.out.println("Exiting to product menu...");
                        exitMenu = true;
                    }

                    // Check if the input is empty
                    if (input.isEmpty()) {
                        System.out.println("Input cannot be empty. Please enter valid item IDs.");
                    }
                    else {
                        if(!input.equals("-")) {
                            // Split input by commas and parse into integers
                            String[] idStringsInStock = input.split(",");
                            for (String idString : idStringsInStock) {
                                int id = Integer.parseInt(idString.trim());
                                if (id <= 0) {
                                    throw new NumberFormatException("Item IDs must be positive numbers.");
                                }
                                idInStock.add(id);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please ensure you enter valid positive integers separated by commas.");
                    idInStock.clear(); // Clear the list and prompt again
                }
            }

            Response<List<Integer>> r=inventoryService.updateShelfAndStorageContents(productId,idInShelf,idInStock);
            if(r.getValue()!=null){
                System.out.println("Product Inventory updated successfully");
                if(r.getValue().size()>0) {
                    System.out.println("There is a gap in a few items in stock. Please check the inventory.");
                    System.out.println("These are the missing items: ");
                    System.out.println(r.getValue());
                }
                if(r.getMessage().length()>0)
                    System.out.println(r.getMessage());
            }
            else {
                System.out.println("Failed to update product inventory. Error: " + r.getMessage());
            }
            exitMenu = true;
        }
    }
}

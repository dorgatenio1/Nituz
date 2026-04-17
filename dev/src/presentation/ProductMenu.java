package Presentation_Layer;

import Service_Layer.InventoryService;
import Service_Layer.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductMenu {
    private InputReader reader;
    private InventoryService inventoryService;

    public ProductMenu(InputReader reader,InventoryService inventoryService){
        this.reader=reader;
        this.inventoryService=inventoryService;
    }

    public void start(){
        handleProductMenu();
    }

    private void handleProductMenu() {
        boolean exitMenu = false;

        while (!exitMenu) {
            displayProductMenu();
            int choice = reader.readInt();

            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    addItem();
                    break;
                case 3:
                    updateMinToRestockFromUser();
                    break;
                case 4:
                    moveItemsToShelf();
                    break;
                case 5:
                    updateDefectiveItems();
                    break;
                case 6:
                    addDiscountToProduct();
                    break;
                case 7:
                    sellItem();
                    break;
                case 8:
                    getProductPrice();
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
    private void displayProductMenu() {
        System.out.println("\n===================================");
        System.out.println("       Product Management         ");
        System.out.println("===================================");
        System.out.println("  1. Add Product");
        System.out.println("  2. Add Item");
        System.out.println("  3. Update Min Restock Level");
        System.out.println("  4. Move Items to Shelf");
        System.out.println("  5. Mark Item as Defective");
        System.out.println("  6. Add Product Discount");
        System.out.println("  7. Sell Item");
        System.out.println("  8. Get Product Price");
        System.out.println("  0. Back to Main Menu");
        System.out.println("-----------------------------------");
        System.out.print("Your choice: ");
    }


    private void getProductPrice() {
        System.out.println("\n===== Get Product Price =====");

        while (true) {
            try {
                // Prompt the user to enter a product ID
                int productId = -1;
                while (productId <= 0) {
                    try {
                        System.out.print("Enter product ID to get the price (or type '0' to return to menu): ");
                        productId = Integer.parseInt(reader.readString());

                        if (productId == 0) {
                            System.out.println("Returning to product menu...");
                            return; // Exit to the menu
                        } else if (productId < 0) {
                            System.out.println("Product ID must be a positive number. Please try again.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                }

                // Call the getPriceForProduct method
                Response<Double> response = inventoryService.getPriceForProduct(productId);

                // Check the response and display the price or an error message
                if (response.getValue() != null) {
                    System.out.println("The price of the product is: " + response.getValue() + " units.");
                } else {
                    System.out.println("Failed to retrieve price. Error: " + response.getMessage());
                }

                // Exit after processing
                return;

            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private void addDiscountToProduct() {
        System.out.println("\n===== Add Discount to Product =====");

        while (true) {
            try {
                // Prompt for product ID until valid or the user chooses to exit
                int productId = -1;
                while (productId <= 0) {
                    try {
                        System.out.print("Enter product ID for the discount (or type '0' to exit to menu): ");
                        productId = Integer.parseInt(reader.readString());

                        if (productId == 0) {
                            System.out.println("Exiting to product menu...");
                            return; // Exit to the menu
                        } else if (productId < 0) {
                            System.out.println("Product ID must be a positive number. Please try again.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                }

                // Prompt for discount percentage (must be a positive number)
                int discount = -1;
                while (discount <= 0 || discount > 100) {
                    try {
                        System.out.print("Enter the discount percentage (1-100) (or type '0' to exit to menu): ");
                        discount = Integer.parseInt(reader.readString());

                        if (discount == 0) {
                            System.out.println("Exiting to product menu...");
                            return; // Exit to the menu
                        } else if (discount < 0 || discount > 100) {
                            System.out.println("Discount must be between 1 and 100. Please try again.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                }

                // Prompt for start date
                Date startDate = null;
                while (startDate == null) {
                    try {
                        System.out.print("Enter the start date for the discount (YYYY-MM-DD) (or type 'exit' to go back): ");
                        String startDateInput = reader.readString();

                        if (startDateInput.equalsIgnoreCase("exit")) {
                            System.out.println("Exiting to product menu...");
                            return; // Exit to the menu
                        }

                        startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateInput);
                    } catch (ParseException e) {
                        System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                    }
                }

                // Prompt for end date
                Date endDate = null;
                while (endDate == null) {
                    try {
                        System.out.print("Enter the end date for the discount (YYYY-MM-DD) (or type 'exit' to go back): ");
                        String endDateInput = reader.readString();

                        if (endDateInput.equalsIgnoreCase("exit")) {
                            System.out.println("Exiting to product menu...");
                            return; // Exit to the menu
                        }

                        endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateInput);

                        // Check that the end date is after the start date
                        if (startDate != null && endDate.before(startDate)) {
                            System.out.println("End date must be after the start date. Please try again.");
                            endDate = null; // Reset to re-prompt
                        }
                    } catch (ParseException e) {
                        System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                    }
                }

                // Call the addDiscountForProduct method in InventoryService
                Response<Boolean> response = inventoryService.addDiscountForProduct(productId, discount, startDate, endDate);

                // Check the response and display appropriate messages
                if (response.getValue() != null && response.getValue()) {
                    System.out.println("Discount successfully added to the product!");
                } else {
                    System.out.println("Failed to add discount to the product. Error: " + response.getMessage());
                }

                // Exit after successful processing
                return;

            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private void sellItem() {
        System.out.println("\n===== Sell Item =====");

        while (true) {
            try {
                // Prompt for item ID until valid input or the user chooses to exit
                int itemId = -1;
                while (itemId <= 0) {
                    try {
                        System.out.print("Enter item ID to purchase (or type '0' to exit to menu): ");
                        itemId = Integer.parseInt(reader.readString());

                        if (itemId == 0) {
                            System.out.println("Exiting to product menu...");
                            return; // Return to the menu
                        } else if (itemId < 0) {
                            System.out.println("Item ID must be a positive number. Please try again.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                }

                // Call the itemSell method in InventoryService
                Response<Boolean> response = inventoryService.itemSell(itemId);

                // Check the response and display appropriate messages
                if (response.getValue() != null && response.getValue()) {
                    System.out.println("Item successfully purchased!");
                } else {
                    System.out.println("Failed to purchase the item. Error: " + response.getMessage());
                }

            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private void updateDefectiveItems() {
        System.out.println("\n===== Update Defective Items =====");

        while (true) {
            try {
                // Prompt for item ID until valid or user chooses to exit (entering 0)
                int itemId = -1; // Initialize with an invalid value
                while (itemId <= 0) {
                    try {
                        System.out.print("Enter item ID to mark as defective (or type '0' to go back to the menu): ");
                        itemId = Integer.parseInt(reader.readString());

                        if (itemId == 0) {
                            System.out.println("Returning to product menu...");
                            return; // Exit the method
                        } else if (itemId < 0) {
                            System.out.println("Item ID must be a positive number. Please try again.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                }

                // Prompt for reason until a valid (non-empty) reason is provided
                String reason = "";
                while (reason.isEmpty()) {
                    System.out.print("Enter reason for marking the item as defective: ");
                    reason = reader.readString();

                    if (reason.isEmpty()) {
                        System.out.println("Reason cannot be empty. Please provide a valid reason.");
                    }
                }

                // Call the setDefectiveItem method of InventoryService
                Response<Boolean> response = inventoryService.setDefectiveItem(itemId, reason);

                // Check response and print appropriate message
                if (response.getValue() != null && response.getValue()) {
                    System.out.println("Item successfully updated as defective.");
                } else {
                    System.out.println("Failed to mark the item as defective. Error: " + response.getMessage());
                }

            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private void moveItemsToShelf() {
        System.out.println("\n===== Move Items to Shelf =====");

        while (true) {
            try {
                // List to store valid item IDs
                List<Integer> itemIds = new ArrayList<>();

                while (itemIds.isEmpty()) { // Keep asking until valid input is given
                    try {
                        // Prompt user to enter item IDs (comma-separated)
                        System.out.print("Enter item IDs to move to shelf (comma-separated, e.g., 1,2,3) or type '0' to exit: ");
                        String input = reader.readString();

                        // Allow user to exit
                        if (input.equals("0")) {
                            System.out.println("Exiting to product menu...");
                            return;
                        }

                        // Check if the input is empty
                        if (input.isEmpty()) {
                            System.out.println("Input cannot be empty. Please enter valid item IDs.");
                            continue;
                        }

                        // Split input by commas and parse into integers
                        String[] idStrings = input.split(",");
                        for (String idString : idStrings) {
                            int id = Integer.parseInt(idString.trim());
                            if (id <= 0) {
                                throw new NumberFormatException("Item IDs must be positive numbers.");
                            }
                            itemIds.add(id);
                        }

                        // If successfully parsed, break out of the loop
                        if (!itemIds.isEmpty()) {
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please ensure you enter valid positive integers separated by commas.");
                        itemIds.clear(); // Clear the list and prompt again
                    }
                }

                // Call the moveToShelf method with the list of item IDs
                Response<Boolean> response = inventoryService.moveToShelf(itemIds);

                // Check the response and display appropriate messages
                if (response.getValue() != null && response.getValue()) {
                    System.out.println("Items successfully moved to shelf!");
                } else {
                    System.out.println("Failed to move items to shelf. Error: " + response.getMessage());
                }

            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private void addItem() {
        System.out.println("\n===== Add New Item =====");
        try {
            // Set item_id to 0 as required
            int item_id;
            while (true) {
                try {
                    System.out.print("Enter item ID: ");
                    item_id = Integer.parseInt(reader.readString());
                    if (item_id > 0) break; // Valid input
                    System.out.println("Item ID must be a positive number.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }

            // Collect valid supplier ID
            int supplier_id = 0;
            while (true) {
                try {
                    System.out.print("Enter supplier ID: ");
                    supplier_id = Integer.parseInt(reader.readString());
                    if (supplier_id > 0) break; // Valid input
                    System.out.println("Supplier ID must be a positive number.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }

            // Collect valid base price
            int basePrice = 0;
            while (true) {
                try {
                    System.out.print("Enter base price: ");
                    basePrice = Integer.parseInt(reader.readString());
                    if (basePrice > 0) break; // Valid input
                    System.out.println("Base price must be greater than zero.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }

            // Collect valid expiration date
            Date expirationDate = null;
            while (true) {
                try {
                    System.out.print("Enter expiration date (YYYY-MM-DD): ");
                    String expirationDateInput = reader.readString();
                    expirationDate = new SimpleDateFormat("yyyy-MM-dd").parse(expirationDateInput);
                    break; // Valid date, exit loop
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                }
            }

            // Collect valid product ID
            int product_id = 0;
            while (true) {
                try {
                    System.out.print("Enter product ID: ");
                    product_id = Integer.parseInt(reader.readString());
                    if (product_id > 0) break; // Valid input
                    System.out.println("Product ID must be a positive number.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }

            // Call the addNewItem method in InventoryService
            Response<Boolean> response = inventoryService.addNewItem(
                    item_id,
                    supplier_id,
                    basePrice,
                    expirationDate,
                    product_id
            );

            // Check response and print appropriate message
            if (response.getValue()!=null && response.getValue()) {
                System.out.println("Item successfully added!");
            } else {
                System.out.println("Failed to add the item. Error: " + response.getMessage());
            }
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

   

    private void updateMinToRestockFromUser() {
        System.out.println("\n===== Update Minimum Restock Level =====");

        try {
            // Collect valid product ID
            int productId = 0;
            while (true) {
                try {
                    System.out.print("Enter product ID: ");
                    productId = Integer.parseInt(reader.readString());
                    if (productId > 0) break; // Valid input
                    System.out.println("Product ID must be a positive number.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }

            // Collect valid minimum restock level
            int minToRestock = 0;
            while (true) {
                try {
                    System.out.print("Enter new minimum restock level: ");
                    minToRestock = Integer.parseInt(reader.readString());
                    if (minToRestock > 0) break; // Valid input
                    System.out.println("Minimum restock level must be greater than zero.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }

            // Call the updateMinToRestock method in InventoryService
            Response<Boolean> response = inventoryService.updateMinToRestock(productId, minToRestock);

            // Check response and print appropriate message
            if (response.getValue()!=null && response.getValue()) {
                System.out.println("Minimum restock level successfully updated!");
            } else {
                System.out.println("Failed to update the restock level. Error: " + response.getMessage());
            }
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void addProduct() {
        System.out.println("\n===== Add New Product =====");

        try {
            // Set product_id to 0 as per the requirement

            System.out.print("Enter product ID: ");
            int product_id = Integer.parseInt(reader.readString());
            if (product_id<0) {
                System.out.println("Product ID cannot be less than zero.");
                return;
            }

            // Collect product name
            System.out.print("Enter product name: ");
            String productName = reader.readString();
            if (productName.isEmpty()) {
                System.out.println("Product name cannot be empty.");
                return;
            }

            // Collect manufacturer
            System.out.print("Enter manufacturer: ");
            String manufacturer = reader.readString();
            if (manufacturer.isEmpty()) {
                System.out.println("Manufacturer cannot be empty.");
                return;
            }

            // Collect category name
            System.out.print("Enter category name: ");
            String categoryName = reader.readString();
            if (categoryName.isEmpty()) {
                System.out.println("Category name cannot be empty.");
                return;
            }

            // Collect sub-category
            System.out.print("Enter sub-category: ");
            String subCategory = reader.readString();
            if (subCategory.isEmpty()) {
                System.out.println("Sub-category cannot be empty.");
                return;
            }

            // Collect sub-sub-category
            System.out.print("Enter sub-sub-category: ");
            String subSubCategory = reader.readString();
            if (subSubCategory.isEmpty()) {
                System.out.println("Sub-sub-category cannot be empty.");
                return;
            }

            // Collect minimum restock level
            System.out.print("Enter minimum restock level: ");
            int minToRestock = Integer.parseInt(reader.readString());
            if (minToRestock < 1) {
                System.out.println("Minimum restock level must be at least 1.");
                return;
            }

            // Collect shelf location
            System.out.print("Enter shelf location: ");
            String shelfLocation = reader.readString();
            if (shelfLocation.isEmpty()) {
                System.out.println("Shelf location cannot be empty.");
                return;
            }

            // Collect price without discount
            System.out.print("Enter price without discount: ");
            int priceWithoutDiscount = Integer.parseInt(reader.readString());
            if (priceWithoutDiscount <= 0) {
                System.out.println("Price must be greater than zero.");
                return;
            }

            // Call the addNewProduct method in InventoryService
            Response<Boolean> response = inventoryService.addNewProduct(
                    product_id,
                    productName,
                    manufacturer,
                    categoryName,
                    subCategory,
                    subSubCategory,
                    minToRestock,
                    shelfLocation,
                    priceWithoutDiscount
            );

            // Check response and print appropriate message
            if (response.getValue()!=null && response.getValue()) {
                System.out.println("Product successfully added!");
            } else {
                System.out.println("Failed to add the product. Error: " + response.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please ensure numbers are entered in the correct format.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }


}

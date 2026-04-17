package Presentation_Layer;

import Service_Layer.InventoryService;
import Service_Layer.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CategoryMenu {
    private InputReader reader;
    private InventoryService inventoryService;

    public CategoryMenu(InputReader reader,InventoryService inventoryService){
        this.reader=reader;
        this.inventoryService=inventoryService;
    }
    public void start(){
        handleCategoryMenu();
    }

    private void handleCategoryMenu() {
        boolean exitMenu = false;

        while (!exitMenu) {
            displayCategoryMenu();
            int choice = reader.readInt();

            switch (choice) {
                case 1:
                    addDiscountToCategory();
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

    private void addDiscountToCategory() {
        System.out.println("\n===== Add Discount to Category =====");

        while (true) {
            try {
                // Prompt for category name until valid input or user chooses to exit
                String categoryName = null;
                while (categoryName == null || categoryName.trim().isEmpty()) {
                    System.out.print("Enter the category name for the discount (or type 'exit' to return to menu): ");
                    categoryName = reader.readString();

                    if (categoryName.equalsIgnoreCase("exit")) {
                        System.out.println("Exiting to menu...");
                        return; // Exit to the menu
                    }

                    if (categoryName.isEmpty()) {
                        System.out.println("Category name cannot be empty. Please enter a valid category name.");
                    }
                }

                // Validate discount percentage (must be between 1 and 100 inclusive)
                int discount = -1;
                while (discount < 1 || discount > 100) {
                    try {
                        System.out.print("Enter the discount percentage (1-100) (or type '0' to exit to menu): ");
                        discount = Integer.parseInt(reader.readString());

                        if (discount == 0) {
                            System.out.println("Exiting to menu...");
                            return; // Exit to the menu
                        }

                        if (discount < 1 || discount > 100) {
                            System.out.println("Discount must be between 1 and 100. Please try again.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                }

                // Validate start date input
                Date startDate = null;
                while (startDate == null) {
                    try {
                        System.out.print("Enter the start date for the discount (YYYY-MM-DD) (or type 'exit' to return to menu): ");
                        String startDateInput = reader.readString();

                        if (startDateInput.equalsIgnoreCase("exit")) {
                            System.out.println("Exiting to menu...");
                            return; // Exit to the menu
                        }

                        startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateInput);
                    } catch (ParseException e) {
                        System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                    }
                }

                // Validate end date input
                Date endDate = null;
                while (endDate == null) {
                    try {
                        System.out.print("Enter the end date for the discount (YYYY-MM-DD) (or type 'exit' to return to menu): ");
                        String endDateInput = reader.readString();

                        if (endDateInput.equalsIgnoreCase("exit")) {
                            System.out.println("Exiting to menu...");
                            return; // Exit to the menu
                        }

                        endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateInput);

                        // Ensure end date is after start date
                        if (startDate != null && endDate.before(startDate)) {
                            System.out.println("End date must be after the start date. Please try again.");
                            endDate = null; // Reset to re-enter
                        }
                    } catch (ParseException e) {
                        System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                    }
                }

                // Call the addDiscountForCategory method in InventoryService
                Response<Boolean> response = inventoryService.addDiscountForCategory(categoryName, discount, startDate, endDate);

                // Check the response and display appropriate messages
                if (response.getValue() != null && response.getValue()) {
                    System.out.println("Discount successfully added to category: " + categoryName);
                } else {
                    System.out.println("Failed to add discount to category. Error: " + response.getMessage());
                }

            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private void displayCategoryMenu() {
        System.out.println("\n===================================");
        System.out.println("      Category Management         ");
        System.out.println("===================================");
        System.out.println("  1. Add Category Discount");
        System.out.println("  0. Back to Main Menu");
        System.out.println("-----------------------------------");
        System.out.print("Your choice: ");
    }
}

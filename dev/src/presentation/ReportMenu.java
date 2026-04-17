package Presentation_Layer;

import Business_Layer.Category;
import Business_Layer.Report;
import Service_Layer.InventoryService;
import Service_Layer.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class ReportMenu {
    private InputReader reader;
    private InventoryService inventoryService;
    public ReportMenu(InputReader reader, InventoryService inventory){
        this.reader=reader;
        this.inventoryService=inventory;

    }
    public void start(){
        handleReportMenu();
    }
    private void handleReportMenu() {
        boolean exitMenu = false;

        while (!exitMenu) {
            displayReportMenu();
            int choice = reader.readInt();

            switch (choice) {
                case 1:
                    handleInventoryReport();
                    break;
                case 2:
                    handleDefectiveReport();
                    break;
                case 3:
                    handlePeriodicReport();
                    break;
                case 4:
                    handleExpiredReport();
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

    private void displayReportMenu() {
        System.out.println("\n===================================");
        System.out.println("             Reports              ");
        System.out.println("===================================");
        System.out.println("  1. Inventory Report");
        System.out.println("  2. Defective Items Report");
        System.out.println("  3. Periodic Report");
        System.out.println("  4. Expired Items Report");
        System.out.println("  0. Back to Main Menu");
        System.out.println("-----------------------------------");
        System.out.print("Your choice: ");
    }

    private void handleExpiredReport(){
        Response<Report> res=inventoryService.getExpiredReport();
        if(res.getValue()!=null)
            System.out.println(res.getValue().toString());
        else
            System.out.println(res.getMessage());
    }

    private void handleInventoryReport() {
        boolean exitMenu = false;
        Response r=inventoryService.getCategories();
        List<String> allCategories =null;
        if(r.getValue()==null){
            exitMenu = true;
            System.out.println(r.getMessage());
            System.out.println("Returning to main menu...");
        }
        else
            allCategories=(List<String>) r.getValue();
        List<String> categoriesForReport=new ArrayList<>();
        while (!exitMenu) {
            displayInventoryReportMenu(!categoriesForReport.isEmpty(), allCategories);
            int choice = reader.readInt();
            switch (choice) {
                case 0:
                    exitMenu = true;
                    System.out.println("Returning to main menu...");
                    break;
                case 1:
                    if (categoriesForReport.isEmpty()) {
                        categoriesForReport.add(allCategories.get(choice - 1));
                        allCategories.remove(choice - 1);
                    } else {
                        System.out.println("\n"+inventoryService.getInventoryReport(categoriesForReport).getValue());
                        exitMenu=true;
                    }
                    break;
                default:
                    if (choice > 1) {
                        if(choice==allCategories.size()+1 && categoriesForReport.isEmpty()
                        || choice==allCategories.size()+2 && !categoriesForReport.isEmpty()) {
                            categoriesForReport.addAll(allCategories);
                            System.out.println("\n"+inventoryService.getInventoryReport(categoriesForReport).getValue());
                            exitMenu=true;
                        }
                        else {
                            if (categoriesForReport.isEmpty()) {
                                categoriesForReport.add(allCategories.get(choice - 1));
                                allCategories.remove(choice - 1);
                            }else {
                                categoriesForReport.add(allCategories.get(choice - 2));
                                allCategories.remove(choice - 2);
                            }
                        }
                    } else
                        System.out.println("Invalid option. Please try again.");
                    break;
            }

        }
    }
    private void displayInventoryReportMenu(Boolean isSelectedCategories, List<String> categories) {
            System.out.println("\n===== inventoryReport =====");
            if (isSelectedCategories) {
                System.out.println("1 print report");
                if(categories.size()>0) {
                    System.out.println("\n add category:");
                    for (int i = 1; i <= categories.size(); i++) {
                        System.out.println((i + 1) + " " + categories.get(i - 1));
                    }
                    System.out.println((categories.size() + 2) + " all categories");
                }
            } else {
                if(categories.size()>0) {
                    System.out.println("choose category:");
                    for (int i = 1; i <= categories.size(); i++) {
                        System.out.println(i + " " + categories.get(i - 1));
                    }
                    System.out.println((categories.size() + 1) + " all categories");
                }
                else
                    System.out.println("There are no categories to choose from. Please add categories in the product menu first.");
            }
            System.out.println("0 Return to Report Menu");
            System.out.print("Enter your choice: ");
    }

    private void handleDefectiveReport() {
        boolean exitMenu = false;
        Response<Report> res=null;
        while (!exitMenu) {
            displayDefectiveReportMenu();
            String choice = reader.readString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Standard date format
            sdf.setLenient(false); // Strict date parsing

            switch (choice.toLowerCase()) {
                case "0":
                    exitMenu = true;
                    System.out.println("Returning to report menu...");
                    break;
                case "last week":
                    System.out.println("Fetching defective report for the last week...");
                    Date lastWeekStart = getDateOffset(new Date(), -7); // 7 days ago
                    Date lastWeekEnd = new Date(); // Today
                    res=inventoryService.getDefectReport(lastWeekStart, lastWeekEnd);
                    break;
                case "last month":
                    System.out.println("Fetching defective report for the last month...");
                    Date lastMonthStart = getDateOffset(new Date(), -30); // 30 days ago
                    Date lastMonthEnd = new Date(); // Today
                    res=inventoryService.getDefectReport(lastMonthStart, lastMonthEnd);
                    break;
                case "last 3 months":
                    System.out.println("Fetching defective report for the last 3 months...");
                    Date last3MonthsStart = getDateOffset(new Date(), -90); // 90 days ago
                    Date last3MonthsEnd = new Date(); // Today
                    res=inventoryService.getDefectReport(last3MonthsStart, last3MonthsEnd);
                    break;
                case "custom":
                    try {
                        System.out.print("Enter the start date (YYYY-MM-DD): ");
                        String startDateInput = reader.readString();
                        System.out.print("Enter the end date (YYYY-MM-DD): ");
                        String endDateInput = reader.readString();
                        Date startDate = sdf.parse(startDateInput); // Parse into Date object
                        Date endDate = sdf.parse(endDateInput); // Parse into Date object

                        if (!startDate.after(endDate)) { // Ensure startDate is before or equal to endDate
                            System.out.println("Fetching defective report for the period: " + sdf.format(startDate) + " to " + sdf.format(endDate));
                            res=inventoryService.getDefectReport(startDate, endDate);
                        } else {
                            System.out.println("Invalid date range. Start date cannot be after the end date.");
                        }
                    } catch (ParseException e) {
                        System.out.println("Invalid date format. Please enter dates in the format YYYY-MM-DD.");
                    }
                    break;
                default:
                    System.out.println("Invalid option. Please enter a valid choice.");
            }
            if(res!=null){
                if(res.getValue()!=null)
                    System.out.println(res.getValue().toString());
                else
                    System.out.println(res.getMessage());
            }
        }
    }

    private static Date getDateOffset(Date currentDate, int offsetDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, offsetDays); // Add offset days
        return calendar.getTime();
    }

    private static void displayDefectiveReportMenu() {
        System.out.println("\n===== Defective Report =====");
        System.out.println("Choose an option:");
        System.out.println("'last week' - View defective reports for the last week.");
        System.out.println("'last month' - View defective reports for the last month.");
        System.out.println("'last 3 months' - View defective reports for the last 3 months.");
        System.out.println("'custom' - Specify a custom date range (start date and end date).");
        System.out.println("0 - Return to Report Menu.");
        System.out.print("Enter your choice: ");
    }
    private void handlePeriodicReport() {
        boolean exitMenu = false;
        Response r;
        while (!exitMenu) {
            displayPeriodicReportMenu();
            String choice = reader.readString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Standard date format
            sdf.setLenient(false); // Strict date parsing

            switch (choice.toLowerCase()) {
                case "0":
                    exitMenu = true;
                    System.out.println("Returning to report menu...");
                    break;
                case "last week":
                    System.out.println("Fetching periodic report for the last week...");
                    Date lastWeekStart = getDateOffset(new Date(), -7); // 7 days ago
                    Date lastWeekEnd = new Date(); // Today
                    r=inventoryService.getPeriodicReport(lastWeekStart, lastWeekEnd);
                    if(r.getValue()!=null)
                        System.out.println(r.getValue());
                    else {
                        System.out.println(r.getMessage());
                        exitMenu = true;
                    }
                    break;
                case "last month":
                    System.out.println("Fetching periodic report for the last month...");
                    Date lastMonthStart = getDateOffset(new Date(), -30); // 30 days ago
                    Date lastMonthEnd = new Date(); // Today
                    r=inventoryService.getPeriodicReport(lastMonthStart, lastMonthEnd);
                    if(r.getValue()!=null)
                        System.out.println(r.getValue());
                    else {
                        System.out.println(r.getMessage());
                        exitMenu = true;
                    }
                    break;
                case "last 3 months":
                    System.out.println("Fetching periodic report for the last 3 months...");
                    Date last3MonthsStart = getDateOffset(new Date(), -90); // 90 days ago
                    Date last3MonthsEnd = new Date(); // Today
                    r=inventoryService.getPeriodicReport(last3MonthsStart, last3MonthsEnd);
                    if(r.getValue()!=null)
                        System.out.println(r.getValue());
                    else {
                        System.out.println(r.getMessage());
                        exitMenu = true;
                    }
                    break;
                case "custom":
                    try {
                        System.out.print("Enter the start date (YYYY-MM-DD): ");
                        String startDateInput = reader.readString();
                        System.out.print("Enter the end date (YYYY-MM-DD): ");
                        String endDateInput = reader.readString();
                        Date startDate = sdf.parse(startDateInput); // Parse into Date object
                        Date endDate = sdf.parse(endDateInput); // Parse into Date object

                        if (!startDate.after(endDate)) { // Ensure startDate is before or equal to endDate
                            System.out.println("Fetching periodic report for the period: " + sdf.format(startDate) + " to " + sdf.format(endDate));
                            r=inventoryService.getPeriodicReport(startDate, endDate);
                            if(r.getValue()!=null)
                                System.out.println(r.getValue());
                            else {
                                System.out.println(r.getMessage());
                                exitMenu = true;
                            }
                        } else {
                            System.out.println("Invalid date range. Start date cannot be after the end date.");
                        }
                    } catch (ParseException e) {
                        System.out.println("Invalid date format. Please enter dates in the format YYYY-MM-DD.");
                    }
                    break;
                default:
                    System.out.println("Invalid option. Please enter a valid choice.");
            }
        }
    }

    private void displayPeriodicReportMenu() {
        System.out.println("\n===== Periodic Report =====");
        System.out.println("Choose an option:");
        System.out.println("'last week' - View periodic reports for the last week.");
        System.out.println("'last month' - View periodic reports for the last month.");
        System.out.println("'last 3 months' - View periodic reports for the last 3 months.");
        System.out.println("'custom' - Specify a custom date range (start date and end date).");
        System.out.println("0 - Return to Report Menu.");
        System.out.print("Enter your choice: ");
    }
}

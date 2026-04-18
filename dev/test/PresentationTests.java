package test;

import domain.Inventorycontroller;
import domain.Reportcontroller;
import service.InventoryService;
import presentation.InputReader;
import presentation.InventoryMenu;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PresentationTests {

    private InventoryService service;
    private ByteArrayOutputStream captured;
    private PrintStream originalOut;

    @Before
    public void setUp() {
        Inventorycontroller.getInstance().resetAll();
        Reportcontroller.getInstance().resetAll();
        service = new InventoryService();
        originalOut = System.out;
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Test
    public void invalidMenuChoice_showsInvalidMessage() {
        run("99", "5");
        assertContains("Invalid");
    }

    @Test
    public void exitChoice_exitsCleanly() {
        run("5");
        assertContains("Goodbye");
    }

    // ── Product menu ──────────────────────────────────────────────────────────

    @Test
    public void addProduct_validInput_showsSuccess() {
        run(
            "1",                          // Product Management
            "1",                          // Add New Product
            "42", "Milk", "Tnuva",
            "dairy", "milk", "whole",
            "5", "A1", "8",
            "0",                          // back
            "5"                           // exit
        );
        assertContains("successfully");
    }

    @Test
    public void addProduct_duplicate_showsError() {
        run(
            "1",                                                          // InventoryMenu → ProductMenu
            "1", "1", "Milk", "Tnuva", "dairy", "milk", "whole", "5", "A1", "8",  // Add product 1
            "1", "1", "Milk", "Tnuva", "dairy", "milk", "whole", "5", "A1", "8",  // Add duplicate
            "0",                                                          // back
            "5"                                                           // exit
        );
        assertContains("Error");
    }

    @Test
    public void getProductInfo_existingProduct_showsProductId() {
        service.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 5, "A1", 8);
        run(
            "1",   // Product Management
            "9",   // Get Product Info
            "1",   // product ID
            "0",   // back
            "5"    // exit
        );
        assertContains("1"); // product ID appears in toString()
    }

    @Test
    public void sellItem_onShelf_showsSuccess() {
        service.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 5, "A1", 8);
        service.addNewItem(101, 99, 3, futureDate(), 1);
        service.moveToShelf(Arrays.asList(101));
        run(
            "1",   // Product Management
            "7",   // Sell Item
            "101",
            "0",   // back
            "5"    // exit
        );
        assertContains("sold");
    }

    @Test
    public void sellItem_inStorage_showsError() {
        service.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 5, "A1", 8);
        service.addNewItem(101, 99, 3, futureDate(), 1);
        // NOT moved to shelf
        run("1", "7", "101", "0", "5");
        assertContains("Error");
    }

    @Test
    public void getProductPrice_withDiscount_showsDiscountedPrice() {
        service.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 5, "A1", 10);
        service.addDiscountForProduct(1, 20, pastDate(), futureDate()); // 20% off → 8.0
        run("1", "8", "1", "0", "5");
        assertContains("8.0");
    }

    // ── Category menu ─────────────────────────────────────────────────────────

    @Test
    public void viewCategories_showsCategoryName() {
        service.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 5, "A1", 8);
        run(
            "3",   // Category Management
            "1",   // View All Categories
            "0",   // back
            "5"    // exit
        );
        assertContains("dairy");
    }

    // ── Inventory Count menu ──────────────────────────────────────────────────

    @Test
    public void inventoryCount_correctCounts_showsSuccess() {
        service.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 5, "A1", 8);
        service.addNewItem(101, 99, 3, futureDate(), 1);
        run(
            "4",   // Inventory Count
            "1",   // Regular Count
            "1",   // product ID
            "1",   // in storage = 1
            "0",   // on shelf = 0
            "0",   // back
            "5"    // exit
        );
        assertContains("verified");
    }

    @Test
    public void showProductsToRestock_lowStockProduct_showsName() {
        service.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 10, "A1", 8); // 0 items, min=10
        run(
            "4",   // Inventory Count
            "3",   // Show Products to Restock
            "0",   // back
            "5"    // exit
        );
        assertContains("milk");
    }

    // ── Report menu ───────────────────────────────────────────────────────────

    @Test
    public void inventoryReport_allCategories_showsProductName() {
        service.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 5, "A1", 8);
        run(
            "2",   // Reports
            "1",   // Inventory Report
            "",    // Enter = all categories
            "0",   // back
            "5"    // exit
        );
        assertContains("milk");
    }

    @Test
    public void orderReport_lowStockProduct_appearsInOutput() {
        service.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 10, "A1", 8);
        run("2", "4", "0", "5");
        assertContains("milk");
    }

    @Test
    public void defectReport_allTime_showsReason() {
        service.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 5, "A1", 8);
        service.addNewItem(101, 99, 3, futureDate(), 1);
        service.moveToShelf(Arrays.asList(101));
        service.setDefectiveItem(101, "dented can");
        run(
            "2",   // Reports
            "2",   // Defective Items Report
            "1",   // All time
            "0",   // back
            "5"    // exit
        );
        assertContains("dented can");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Run InventoryMenu with the given sequence of inputs. */
    private void run(String... inputs) {
        new InventoryMenu(listReader(inputs), service).start();
    }

    private void assertContains(String expected) {
        String out = captured.toString();
        assertTrue("Expected output to contain \"" + expected + "\" but was:\n" + out,
            out.toLowerCase().contains(expected.toLowerCase()));
    }

    private InputReader listReader(String... inputs) {
        final List<String> list = new ArrayList<>(Arrays.asList(inputs));
        return new InputReader() {
            int index = 0;
            public String readString() {
                return index < list.size() ? list.get(index++) : "0";
            }
            public int readInt() {
                try { return Integer.parseInt(readString()); }
                catch (NumberFormatException e) { return -1; }
            }
        };
    }

    private Date futureDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 1);
        return c.getTime();
    }

    private Date pastDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        return c.getTime();
    }
}

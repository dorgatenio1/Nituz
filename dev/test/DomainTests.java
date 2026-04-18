package test;

import domain.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Calendar;
import java.util.Date;

public class DomainTests {

    @Before
    public void setUp() {
        Inventorycontroller.getInstance().resetAll();
        Reportcontroller.getInstance().resetAll();
    }

    // ── Item ──────────────────────────────────────────────────────────────────

    @Test(expected = IllegalArgumentException.class)
    public void item_nullExpirationDate_throws() {
        new Item(1, 5, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void item_negativePrice_throws() {
        new Item(1, -1, futureDate());
    }

    @Test
    public void item_futureDate_isNotExpired() {
        assertFalse(new Item(1, 5, futureDate()).isExpired());
    }

    @Test
    public void item_pastDate_isExpired() {
        assertTrue(new Item(1, 5, pastDate()).isExpired());
    }

    @Test(expected = IllegalStateException.class)
    public void item_sellExpired_throws() {
        new Item(1, 5, pastDate()).sell(new Date(), 10.0);
    }

    // ── Product ───────────────────────────────────────────────────────────────

    @Test(expected = IllegalArgumentException.class)
    public void product_negativePrice_throws() {
        new Product(1, "Milk", "Tnuva", "dairy", "milk", "A1", -1, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void product_negativeMinRestock_throws() {
        new Product(1, "Milk", "Tnuva", "dairy", "milk", "A1", 8, -1);
    }

    @Test
    public void product_addFutureItem_goesToStorage() {
        Product p = product();
        p.addNewItem(101, 99, 3, futureDate());
        assertEquals(1, p.getStorageItemsQuantity());
        assertEquals(0, p.getShelfQuantity());
    }

    @Test
    public void product_addExpiredItem_doesNotGoToStorage() {
        Product p = product();
        p.addNewItem(101, 99, 3, pastDate());
        assertEquals(0, p.getStorageItemsQuantity());
    }

    @Test
    public void product_moveToShelf_movesFromStorage() {
        Product p = product();
        p.addNewItem(101, 99, 3, futureDate());
        p.moveToShelf(101);
        assertEquals(0, p.getStorageItemsQuantity());
        assertEquals(1, p.getShelfQuantity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void product_moveToShelf_itemNotInStorage_throws() {
        product().moveToShelf(999);
    }

    @Test
    public void product_sellFromShelf_succeeds() {
        Product p = product();
        p.addNewItem(101, 99, 3, futureDate());
        p.moveToShelf(101);
        SoldItem sold = p.sellItem(101, new Date(), 10.0);
        assertNotNull(sold);
        assertEquals(0, p.getShelfQuantity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void product_sellFromStorage_throws() {
        Product p = product();
        p.addNewItem(101, 99, 3, futureDate());
        p.sellItem(101, new Date(), 10.0); // still in storage
    }

    @Test
    public void product_needsRestock_whenBelowMin() {
        Product p = product(); // minRestock = 10, 0 items
        assertTrue(p.needToRestock());
    }

    @Test
    public void product_noRestock_whenAboveMin() {
        Product p = new Product(1, "Milk", "Tnuva", "dairy", "milk", "A1", 8, 1);
        p.addNewItem(101, 99, 3, futureDate());
        assertFalse(p.needToRestock());
    }

    // ── Discounts ─────────────────────────────────────────────────────────────

    @Test(expected = IllegalArgumentException.class)
    public void discountInfo_percentageOver100_throws() {
        new DiscountInfo(101, pastDate(), futureDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void discountInfo_negativePercentage_throws() {
        new DiscountInfo(-1, pastDate(), futureDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void discountInfo_startAfterEnd_throws() {
        new DiscountInfo(10, futureDate(), pastDate());
    }

    @Test
    public void product_activeDiscount_reducesPrice() {
        Product p = product(); // price = 10
        p.addDiscount(new DiscountInfo(50, pastDate(), futureDate())); // 50% off
        assertEquals(5.0, p.getPrice(), 0.001);
    }

    @Test
    public void product_expiredDiscount_doesNotReducePrice() {
        Product p = product(); // price = 10
        Date start = daysAgo(30);
        Date end   = daysAgo(10);
        p.addDiscount(new DiscountInfo(50, start, end)); // already expired
        assertEquals(10.0, p.getPrice(), 0.001);
    }

    // ── Inventorycontroller ───────────────────────────────────────────────────

    @Test
    public void controller_addProduct_canBeRetrieved() {
        Inventorycontroller ctrl = Inventorycontroller.getInstance();
        ctrl.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 5, "A1", 8);
        assertNotNull(ctrl.getProduct(1));
        assertEquals("milk", ctrl.getProduct(1).getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void controller_addDuplicateProduct_throws() {
        Inventorycontroller ctrl = Inventorycontroller.getInstance();
        ctrl.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 5, "A1", 8);
        ctrl.addNewProduct(1, "Milk2", "Tnuva", "dairy", "milk", "whole", 5, "A1", 8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controller_addItemToUnknownProduct_throws() {
        Inventorycontroller.getInstance().addNewItem(101, 999, 99, 3, futureDate());
    }

    @Test
    public void controller_sellItem_recordsInSoldList() {
        Inventorycontroller ctrl = Inventorycontroller.getInstance();
        ctrl.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 5, "A1", 8);
        ctrl.addNewItem(101, 1, 99, 3, futureDate());
        ctrl.moveToShelf(101);
        assertTrue(ctrl.sellItem(101));
        assertNotNull(ctrl.getSoldItemsById(101));
    }

    @Test
    public void controller_markDefective_succeeds() {
        Inventorycontroller ctrl = Inventorycontroller.getInstance();
        ctrl.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 5, "A1", 8);
        ctrl.addNewItem(101, 1, 99, 3, futureDate());
        ctrl.moveToShelf(101);
        assertTrue(ctrl.markItemAsDefective(101, "broken"));
    }

    @Test
    public void controller_productBelowMin_appearsInRestockList() {
        Inventorycontroller ctrl = Inventorycontroller.getInstance();
        ctrl.addNewProduct(1, "Milk", "Tnuva", "dairy", "milk", "whole", 10, "A1", 8); // min=10, 0 items
        assertFalse(ctrl.getProductsNeedingRestock().isEmpty());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Product product() {
        return new Product(1, "Milk", "Tnuva", "dairy", "milk", "A1", 10, 10);
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

    private Date daysAgo(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -days);
        return c.getTime();
    }
}

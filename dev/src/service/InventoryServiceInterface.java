package service;

import domain.*;
import domain.reports.*;
import java.util.Date;
import java.util.List;

public interface InventoryServiceInterface {

    // --- Product Management ---
    Response<Boolean> addNewProduct(int productId, String name, String manufacturer,
        String categoryName, String subCategory, String subSubCategory,
        int minToRestock, String shelfLocation, int priceWithoutDiscount);

    Response<Boolean> addNewItem(int itemId, int supplierId, int basePrice,
        Date expirationDate, int productId);

    Response<Boolean> updateMinToRestock(int productId, int minToRestock);

    Response<Product> getProduct(int productId);

    Response<Boolean> removeItem(int itemId);

    Response<Boolean> moveToShelf(List<Integer> itemIds);

    Response<Boolean> setDefectiveItem(int itemId, String reason);

    Response<Boolean> addDiscountForProduct(int productId, int discount,
        Date startDate, Date endDate);

    Response<Boolean> itemSell(int itemId);

    Response<Double> getPriceForProduct(int productId);

    // --- Category Management ---
    Response<List<String>> getCategories();

    Response<Category> getCategory(String categoryName);

    Response<Boolean> addDiscountForCategory(String categoryName, int discount,
        Date startDate, Date endDate);

    // --- Inventory Count ---
    Response<Boolean> inventoryCount(int productId, int inStorage, int onShelf);

    Response<List<Integer>> updateShelfAndStorageContents(int productId,
        List<Integer> itemsOnShelf, List<Integer> itemsInStock);

    Response<List<String>> showProductToRestock();

    Response<Boolean> checkMinToRestock(int productId);

    Response<Boolean> checkRestockByItemId(int itemId);

    // --- Sold Items ---
    Response<List<SoldItem>> getSoldItemsByDate(Date startDate, Date endDate);

    Response<SoldItem> getSoldItemById(int itemId);

    // --- Reports ---
    Response<String> getInventoryReport(List<String> categoryNames);

    Response<String> getDefectReport();
    Response<String> getDefectReport(Date startDate, Date endDate);

    Response<String> getExpiredReport();
    Response<String> getExpiredReport(Date startDate, Date endDate);

    Response<String> getOrderReport();

    // --- Report History (via Reportcontroller) ---
    Response<Report<?>> getReportById(int reportId);
    Response<List<Report<?>>> getAllReports();
}

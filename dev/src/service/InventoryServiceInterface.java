package Service_Layer;

import Business_Layer.Report;

import java.util.Date;
import java.util.List;

public interface InventoryServiceInterface {

    Response<Boolean> addNewItem(int item_id, int supplier_id, int basePrice, Date expirationDate, int product_id);

    Response<Boolean> addNewProduct(int product_id, String ProdactName, String Manufacturer, String CategoryName,
                                    String subCategory, String subSubCategory, int minToRestock,
                                    String shelfLocation, int priceWithoutDiscount);

    Response<Boolean> updateMinToRestock(int productId, int minToRestock);

    Response<Boolean> moveToShelf(List<Integer> itemIds);

    Response<Boolean> addDiscountForProduct(int product_id, int Discount, Date startDate, Date endDate);

    Response<Boolean> addDiscountForCategory(String categoryName, int discount, Date startDate, Date endDate);

    Response<Boolean> itemSell(int item_id);

    Response<Report> getDefectReport(Date startDate, Date endDate);

    Response<Report> getInventoryReport(List<String> categories);

    Response<Boolean> setDefectiveItem(int item_id, String reason);

    Response<List<String>> getCategories();

    Response<Report> getPeriodicReport(Date startDate, Date endDate);

    Response<Boolean> inventoryCount(int product_id, int inStorage, int onShelf);

    Response<List<String>> showProductToRestock();

    Response<Double> getPriceForProduct(int product_id);

    Response<List<Integer>> updateShelfAndStorageContents(int product_id, List<Integer> itemOnSelfIDs, List<Integer> itemOnStockIDs);

    Response<Report> getExpiredReport();
}

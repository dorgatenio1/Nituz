package Service_Layer;

import Business_Layer.*;
import domain.Reportcontroller;

import java.util.Date;
import java.util.List;

public class InventoryService {
    private Inventorycontroller Inventorycontroller;
    private Reportcontroller Reportcontroller;

    public InventoryService() {
        this.Inventorycontroller = Inventorycontroller.getInstance();
        this.Reportcontroller = Reportcontroller.getInstance();
    }
    //ID 2
    public Response<Boolean> addNewItem(int item_id, int supplier_id, int basePrice, Date expirationDate, int product_id) {
        try {
            Inventorycontroller.addNewItem(item_id, product_id, supplier_id, basePrice, expirationDate);
        } catch (Exception e) {
            return new Response<>(false, e.getMessage());
        }
        return new Response<>(true, "");
    }
    //ID 1 + 5
    public Response<Boolean> addNewProduct(int product_id, String ProdactName, String Manufacturer, String CategoryName, String subCategory, String subSubCategory, int minToRestock, String shelfLocation, int priceWithoutDiscount) {
        try {
            Inventorycontroller.addNewProduct(product_id, ProdactName, Manufacturer, CategoryName, subCategory, subSubCategory, minToRestock, shelfLocation, priceWithoutDiscount);
        } catch (Exception e) {
            return new Response<>(false, e.getMessage());
        }
        return new Response<>(true, "");
    }
    //ID 3
    public Response<Boolean> updateMinToRestock(int productId, int minToRestock) {
        try {
            Inventorycontroller.updateMinToRestock(productId, minToRestock);
        } catch (Exception e) {
            return new Response<>(false, e.getMessage());
        }
        return new Response<>(true, "");
    }
    //ID 7
    public Response<Boolean> moveToShelf(List<Integer> itemIds) {
        try {
            Inventorycontroller.moveToShelf(itemIds);
        } catch (Exception e) {
            return new Response<>(false, e.getMessage());
        }
        return new Response<>(true, "");
    }
    //ID 9
    public Response<Boolean> addDiscountForProduct(int product_id, int Discount, Date startDate, Date endDate) {
        try {
            Inventorycontroller.addDiscountForProduct(product_id, Discount, startDate, endDate);
        } catch (Exception e) {
            return new Response<>(false, e.getMessage());
        }
        return new Response<>(true, "");
    }
    //ID 9
    public Response<Boolean> addDiscountForCategory(String categoryName, int discount, Date startDate, Date endDate) {
        try {
            Inventorycontroller.addDiscountForCategory(categoryName, discount, startDate, endDate);
        } catch (Exception e) {
            return new Response<>(false, e.getMessage());
        }
        return new Response<>(true, "");
    }
    //ID 6 + 5
    public Response<Boolean> itemSell(int item_id) {
        try {
            Inventorycontroller.sellItem(item_id);
            return checkRestockStatus(item_id);
        } catch (Exception e) {
            return new Response<>(false, e.getMessage());
        }
    }
    //ID 16
    public Response<Report> getDefectReport(Date startDate, Date endDate) {
        try {
            return new Response<Report>(Reportcontroller.getDefectReport(startDate, endDate), "");
        } catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }
    //ID 10 + 13
    public Response<Report> getInventoryReport(List<String> categories) {
        try {
            return new Response<>(Reportcontroller.getInventoryReport(categories), "");
        } catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }
    // ID 5 + 14
    public Response<Boolean> setDefectiveItem(int item_id, String reason) {
        try {
            Inventorycontroller.markItemAsDefective(item_id, reason);
        } catch (Exception e) {
            return new Response<>(false, e.getMessage());
        }
        return new Response<>(true, "");
    }

    public Response<List<String>> getCategories() {
        try {
            return new Response<>(Inventorycontroller.getAllCategories(), "");
        } catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }
    //ID 18
    public Response<Report> getPeriodicReport(Date startDate, Date endDate) {
        try {
            return new Response<>(Reportcontroller.getPeriodicReport(startDate, endDate), "");
        } catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }
    //ID 21
    public Response<Boolean> inventoryCount(int product_id, int inStorage, int onShelf) {
        try {
            Inventorycontroller.inventoryCount(product_id, inStorage, onShelf);
            return new Response<>(true, "");
        } catch (Exception e) {
            return new Response<>(false, e.getMessage());
        }
    }
    //ID 22
    public Response<List<String>> showProductToRestock() {
        try {
            return new Response<>(Inventorycontroller.getProductsNeedingRestock(), "");
        } catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }
    //ID 6
    public Response<Double> getPriceForProduct(int product_id) {
        try {
            return new Response<>(Inventorycontroller.getPriceForProduct(product_id), "");
        } catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }

    private Response<Boolean> checkRestockStatus(int item_id){
        try{
            if(Inventorycontroller.checkRestockNeededByItemId(item_id))
                return new Response<>(true,"");
            else
                return new Response<>(false,"the sold item is below minToRestock");
        }
        catch(Exception e){
            return new Response<>(false,e.getMessage());
        }
    }
    //ID 8
    public Response<List<Integer>> updateShelfAndStorageContents(int product_id,List<Integer> itemOnSelfIDs,List<Integer> itemOnStockIDs){
        try{
            Response<List <Integer>> res= new Response<>(Inventorycontroller.updateShelfAndStorageContents(product_id,itemOnSelfIDs,itemOnStockIDs),"");
            if(!Inventorycontroller.checkMinToRestock(product_id))
                return res;
            res.setMessage("NEW ALERT - product is below minToRestock");
            return res;
        }
        catch(Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }

    public Response<Report> getExpiredReport(){
        try{
            return new Response<>(Reportcontroller.getExpiredReport(),"");
        }
        catch(Exception e){
            return new Response<>(null,e.getMessage());
        }
    }
}


package domain;

import java.util.*;

public class Inventorycontroller {
    private LinkedList<DefectItem> defectiveItems;
    private Map<Integer,Integer> allItems;//<ItemID,productId>
    private Map<Integer,String> allProducts;//<product_id,categoryName>
    private Map<String, Category> allCategories;//<CategoryName,Category>;
    private static Inventorycontroller Inventorycontroller = null;
    private List<SoldItem> soldItems;
    private List<Integer> ProductsToRestock;
    private Inventorycontroller() {
        defectiveItems = new LinkedList<>();
        allItems = new HashMap<>();
        allCategories=new HashMap<>();
        soldItems=new ArrayList<>();
        allProducts=new HashMap<>();
        ProductsToRestock=new ArrayList<>();
    }
    public static Inventorycontroller getInstance() {
        if(Inventorycontroller==null) {
        Inventorycontroller=new Inventorycontroller();
        }
        return Inventorycontroller;
    }
    public void addNewItem(int item_id,int product_id,int supplier_id,int basePrice, Date expirationDate) {
        if(allItems.containsKey(item_id))
            throw new IllegalArgumentException("Item ID " + item_id + " already exists");
        if(!allProducts.containsKey(product_id))
            throw new IllegalArgumentException("Product ID " + product_id + " doesn't exists");
        allCategories.get(allProducts.get(product_id)).addNewItem(item_id, supplier_id, basePrice, expirationDate, product_id);
        allItems.put(item_id, product_id);
        if(!checkMinToRestock(product_id)&ProductsToRestock.contains(product_id))
        ProductsToRestock.remove(ProductsToRestock.indexOf(product_id));
    }
    public void addNewProduct(int product_id,String ProdactName,String Manufacturer,String CategoryName,String subCategory,String subSubCategory, int minToRestock,String shelfLocation,int priceWithoutDiscount){
        if(allProducts.containsKey(product_id))
            throw new IllegalArgumentException("Product ID " + product_id + " already exists");
        if(ProdactName==null||Manufacturer==null||CategoryName==null||subCategory==null||subSubCategory==null||shelfLocation==null)
            throw new IllegalArgumentException("Some of the parameters are null");
        if(!allCategories.containsKey(CategoryName.toLowerCase()))
            allCategories.put(CategoryName.toLowerCase(),new Category(CategoryName.toLowerCase()));
        allCategories.get(CategoryName.toLowerCase()).addProduct(product_id,ProdactName.toLowerCase(),Manufacturer,subCategory,subSubCategory,minToRestock,shelfLocation,priceWithoutDiscount);
        allProducts.put(product_id,CategoryName.toLowerCase());
        if(minToRestock>0)
        ProductsToRestock.add(product_id);
    }

    public LinkedList<DefectItem> getDefectiveItems() {
        return defectiveItems;
    }

    public Category getCategory(String caregoryName) {
        return allCategories.get(caregoryName.toLowerCase());
    }
    protected Category getCategory(int item_id) {
        if(!allItems.containsKey(item_id))
            throw new IllegalArgumentException("Item ID " + item_id + " doesn't exists");
        if(!allProducts.containsKey(allItems.get(item_id)))
            throw new IllegalArgumentException("Product ID " + allItems.get(item_id) + " doesn't exists");
        return allCategories.get(allProducts.get(allItems.get(item_id)).toLowerCase());
    }
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        for(Category category : allCategories.values()) {
            categories.add(category.getName());
        }
        return categories;
    }
    protected List<Category> getAllCategoriesObjects() {
        List<Category> categories = new ArrayList<>();
        for(Category category : allCategories.values()) {
            categories.add(category);
        }
        return categories;
    }

    public void updateMinToRestock(int productId,int minToRestock) {
        if(minToRestock<=0)
            throw new IllegalArgumentException("Minimum to restock cannot be negative");
        if(!allProducts.containsKey(productId))
            throw new IllegalArgumentException("Product ID " + productId + " doesn't exists");
        getProduct(productId).updateMinToRestock(minToRestock);
        if(ProductsToRestock.contains(productId)&&checkMinToRestock(productId))
        ProductsToRestock.add(productId);
        if(ProductsToRestock.contains(productId)&&!checkMinToRestock(productId))
        ProductsToRestock.remove(ProductsToRestock.indexOf(productId));
    }

    //public for tests
    public Product getProduct(int productId) {
        if(!allProducts.containsKey(productId))
            throw new IllegalArgumentException("Product ID " + productId + " doesn't exists");
        if(!allCategories.containsKey(allProducts.get(productId)))
            throw new IllegalArgumentException("Product " + productId + " doesn't exists in any category");
        return this.allCategories.get(this.allProducts.get(productId).toLowerCase()).getProduct(productId);
    }
    private Product getProductByItemId(int item_id) {
        if(!allItems.containsKey(item_id))
            throw new IllegalArgumentException("Item ID " + item_id + " doesn't exists");
        return allCategories.get(allProducts.get(allItems.get(item_id))).getProduct(allItems.get(item_id));
    }

    public void moveToShelf(int itemId) {
        checkIfItemExists(itemId);
        getCategory(itemId).moveItemToShelf(allItems.get(itemId),itemId);
    }
    
    private void checkIfItemExists(int itemId) {
        if(!allItems.containsKey(itemId))
            throw new IllegalArgumentException("Item ID " + itemId + " doesn't exists");
    }
    public boolean sellItem(int itemId) {
        checkIfItemExists(itemId);
        SoldItem soldItem = getCategory(itemId).sellItem(allItems.get(itemId),new Date(),itemId);
        if(soldItem!=null){
            this.soldItems.add(soldItem);
            if(checkMinToRestock(allItems.get(itemId)))
            ProductsToRestock.add(allItems.get(itemId));
            return true;
        }
        return false;
    }

    public boolean markItemAsDefective(int itemId,String reason) {
        checkIfItemExists(itemId);
        DefectItem defectItem = getCategory(itemId).markItemDefective(allItems.get(itemId),itemId, reason);
        if(defectItem!=null){
            this.defectiveItems.add(defectItem);
            if(checkMinToRestock(allItems.get(itemId)))
            ProductsToRestock.add(allItems.get(itemId));
            return true;
        }
        return false;
    }

    public void removeProduct(int productId) {
        if(!allProducts.containsKey(productId))
            throw new IllegalArgumentException("Product ID " + productId + " doesn't exists");
        allCategories.get(allProducts.get(productId)).removeProduct(productId);
        allProducts.remove(productId);
        if(ProductsToRestock.contains(productId))
        ProductsToRestock.remove(ProductsToRestock.indexOf(productId));
    }

    public List<String> getProductsNeedingRestock() {
        List<String> productsToRestock = new ArrayList<>();
        for(Category category : allCategories.values()) {
            for(Product product : category.getProducts()) {
                if(product.needToRestock())
                    productsToRestock.add(product.toString());
            }
        }
        return productsToRestock;
    }

    public List<Item> getExpiredItems() {
        List<Item> expiredItems = new ArrayList<>();
        for(Category category : allCategories.values()) {
            expiredItems.addAll(category.getExpiredItems());
        }
        return expiredItems;
    }

    public List<DefectItem> getAllDefectiveItems() {
        return (List<DefectItem>)this.defectiveItems.clone();
    }

    public void inventoryCount(int product_id,int inStorage,int onShelf){
        if(!allProducts.containsKey(product_id))
            throw new IllegalArgumentException("Product ID " + product_id + " doesn't exists");
        if(!getProduct(product_id).inventoryCountStorage(inStorage))
            throw new IllegalArgumentException("Product ID " + product_id + " count in storage is not equal to the given count");
        if(!getProduct(product_id).inventoryCountShelf(onShelf))
            throw new IllegalArgumentException("Product ID " + product_id + " count on shelf is not equal to the given count");
    }

    public void addDiscountForProduct(int product_id,int discount,Date startDate,Date endDate){
        if(!allProducts.containsKey(product_id))
            throw new IllegalArgumentException("Product ID " + product_id + " doesn't exists");
        allCategories.get(allProducts.get(product_id)).addDiscountForProduct(product_id,discount,startDate,endDate);
    }
    public void addDiscountForCategory(String categoryName,int discount,Date startDate,Date endDate){
        if(!allCategories.containsKey(categoryName.toLowerCase()))
            throw new IllegalArgumentException("Category Name " + categoryName.toLowerCase() + " doesn't exists");
        allCategories.get(categoryName.toLowerCase()).addDiscountForCategory(discount,startDate,endDate);
    }
    public List<SoldItem> getSoldItemsByDate(Date startDate,Date endDate) {
        List<SoldItem> soldItemsInDate = new ArrayList<>();
        for(SoldItem sell : this.soldItems) {
            if(sell.inRangeDates(startDate, endDate))
                soldItemsInDate.add(sell);
        }
        return soldItemsInDate;
    }

    public List<DefectItem> getDefectiveItemsByDateRange(Date startDate,Date endDate) {
        List<DefectItem> result = new ArrayList<>();
        for(DefectItem item : this.defectiveItems) {
            if(item.inRangeDates(startDate, endDate))
                result.add(item);
        }
        return result;
    }
    public double getPriceForProduct(int product_id){
        if (!allProducts.containsKey(product_id))
            throw new IllegalArgumentException("Product ID " + product_id + " doesn't exists");
        return getCategoryByProductId(product_id).getFinalPrice(product_id);
    }

    protected Category getCategoryByProductId(int product_id) {
        if (!allProducts.containsKey(product_id))
            throw new IllegalArgumentException("Product ID " + product_id + " doesn't exists");
        return allCategories.get(allProducts.get(product_id));
    }
    
    public void resetAll(){
        this.defectiveItems = new LinkedList<>();
        this.allItems = new HashMap<>();
        this.allProducts = new HashMap<>();
        this.allCategories = new HashMap<>();
        this.soldItems = new ArrayList<>();
        this.ProductsToRestock = new ArrayList<>();
    }
    public boolean checkRestockByItemId(int item_id){
        Product product = getProductByItemId(item_id);
        return product.needToRestock();
    }
    public boolean checkMinToRestock(int product_id){
        Product product = getProduct(product_id);
        return product.needToRestock();
    }
    public List<Integer> updateShelfAndStorageContents(int product_id,List<Integer> itemOnSelf,List<Integer> itemOnStock){
        Product product = getProduct(product_id);
        List<Integer> res= product.updateShelfAndStorageContents(itemOnSelf,itemOnStock);
        if(checkMinToRestock(product_id))
        ProductsToRestock.add(product_id);
        return res;
    }
    protected List<Integer> getProductsToRestock() {
        return new ArrayList<>(ProductsToRestock);
    }
    public SoldItem getSoldItemsById(int itemId){
        for(SoldItem sell : soldItems) {
            if(sell.getItemId()==itemId)
                return sell;
        }
        return null;
    }
    public void removeItem(int item_id){
        if(!allItems.containsKey(item_id))
            throw new IllegalArgumentException("Item ID " + item_id + " doesn't exists");
        int productId=allItems.get(item_id);
        Product product=getProduct(productId);
        product.removeItem(item_id);
        if(product.needToRestock())
        ProductsToRestock.add(productId);
    }

    public int getProductId(int itemId) {
        if (!allItems.containsKey(itemId))
            throw new IllegalArgumentException("Item ID " + itemId + " doesn't exist");
        return allItems.get(itemId);
    }
}

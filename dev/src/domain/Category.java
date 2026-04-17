package domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Category {
    private String categoryName;
    private Map<Integer, Product> products = new HashMap<>(); // product_id → Product
    private Discounts discounts = new Discounts();

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    // Getters
    public String getName() {
        return categoryName;
    }

    public double getPrice() {return (((100 - discounts.getActiveDiscount(new Date()) / 100)) * getProduct(productId).getPrice(); }

    public LinkedList<Product> getProducts() {
        return new LinkedList<>(products.values());
    }

    public void addNewItem(int item_id, int supplier_id, int basePrice, Date expirationDate, int product_id) {
        if (!products.containsKey(product_id))
            throw new IllegalArgumentException("Prodact Name " + product_id + " doesn't exists");
        products.get(product_id).addNewItem(item_id, supplier_id, basePrice, expirationDate);
    }

    public void addProduct(int product_id, String productName, String manufacturer,
            String subCategory, String subSubCategory,
            int minToRestock, String shelfLocation, int priceWithoutDiscount) {
        if (products.containsKey(product_id))
            throw new IllegalArgumentException("Product " + product_id + " already exists");
        products.put(product_id,
                new Product(product_id, productName, manufacturer, subCategory, subSubCategory,
                        shelfLocation, priceWithoutDiscount, minToRestock));
    }

    public Product getProduct(int productId) {
        if (!products.containsKey(productId))
            throw new IllegalArgumentException("Product ID " + productId + " doesn't exists");
        return products.get(productId);
    }

    public void removeProduct(int productId) {
        products.remove(productId);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public void addDiscountForCategory(int percentage, Date startDate, Date endDate) {
        discounts.addDiscount(percentage, startDate, endDate);
    }

    public void addDiscountForProduct(int product_id, int discount, Date startDate, Date endDate) {
        if (!products.containsKey(product_id))
            throw new IllegalArgumentException("Product ID " + product_id + " doesn't exists");
        products.get(product_id).addDiscount(new DiscountInfo(discount, startDate, endDate));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Category: ").append(categoryName).append("\n");
        sb.append("Products:\n");
        for (Product product : products.values()) {
            sb.append(product.toString()).append("\n");
        }
        return sb.toString();
    }

    public void moveItemToShelf(int productId, int item_id) {
        if (!products.containsKey(productId))
            throw new IllegalArgumentException("Product ID " + productId + " doesn't exists");
        products.get(productId).moveToShelf(item_id);
    }

    public Discounts getDiscounts() {
        return discounts;
    }
}

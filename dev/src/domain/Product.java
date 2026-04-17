    package domain;

    import java.util.ArrayList;
    import java.util.Date;
    import java.util.LinkedList;
    import java.util.List;
    import java.util.Set;

    public class Product {
        private int productId;
        private int supplierId;
        private String name;
        private int priceWithoutDiscount;
        private String manufacturer;
        private String subCategory;
        private String subSubCategory;
        private String shelfLocation;
        private int minToRestock;
        private Discounts discounts;
        private List<Item> shelfItems;
        private List<Item> storageItems;
        public List<Item> expiredItems;

        public Product(int productId, int supplierId, String name, String manufacturer, String subCategory,
                String subSubCategory,
                String shelfLocation, int priceWithoutDiscount, int minToRestock) {
            this.productId = productId;
            this.supplierId = supplierId;
            this.name = name;
            this.manufacturer = manufacturer;
            this.subCategory = subCategory;
            this.subSubCategory = subSubCategory;
            this.shelfLocation = shelfLocation;
            this.priceWithoutDiscount = priceWithoutDiscount;
            this.minToRestock = minToRestock;
            this.discounts = new Discounts();
            this.shelfItems = new LinkedList<>();
            this.storageItems = new LinkedList<>();
            this.expiredItems = new LinkedList<>();
        }

        // Getters
        public int getProductId() {
            return productId;
        }

        public String getName() {+
            return name;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public String getSubCategory() {
            return subCategory;
        }

        public String getSubSubCategory() {
            return subSubCategory;
        }

        public String getShelfLocation() {
            return shelfLocation;
        }

        public int getPriceWithoutDiscount() {
            return priceWithoutDiscount;
        }

        public int getTotalAmount() {
            return getShelfQuantity() + getWarehouseQuantity();
        }

        public int getSupplierId() {
            return supplierId;
        }

        public int getShelfQuantity() {
            return shelfItems.size();
        }

        public int getStorageItemsQuantity() {
            return storageItems.size();
        }

        public int getTotalQuantity() {
            return (getShelfQuantity() + getStorageItemsQuantity());
        }

        public Discounts getDiscounts() {
            return discounts;
        }

        public int getMinToRestock() {
            return minToRestock;
        }

        public int getId() {
            return productId;
        }

        public double getPrice() {
            return priceWithoutDiscount * (((100 - discounts.getBestActiveDiscount(new Date())) / 100));
        }

        public List<Item> getExpiredItems() {
            return expiredItems;
        }   

        public void addNewItem(int item_id, int supplier_id, int basePrice, Date expirationDate) {
            if (storageItems.contains(item_id) || shelfItems.contains(item_id))
                throw new IllegalArgumentException("Item " + item_id + " already exists");
            Item newItem = new Item(item_id, basePrice, expirationDate);
            if (expirationDate.before(new Date()))
                expiredItems.add(newItem);
            else 
                storageItems.add(newItem);
        }

        public Item removeItem(int item_id) {
            for (Item item : shelfItems)
                if (item.getItemId() == item_id) {
                    shelfItems.remove(item);
                    return item;
                }
            for (Item item : storageItems)
                if (item.getItemId() == item_id) {
                    storageItems.remove(item);
                    return item;
                }
            return null;
        }

        private boolean isItemInStorage(int item_id) {
            for (Item item : storageItems)
                if (item.getItemId() == item_id)
                    return true;
            return false;
        }

        public boolean isItemOnShelf(int item_id) {
            for (Item item : shelfItems)
                if (item.getItemId() == item_id)
                    return true;
            return false;
        }

        public void moveToShelf(int item_id) {
            if (!isItemInStorage(item_id))
                throw new IllegalArgumentException("Item " + item_id + " doesn't exists in storage");
            shelfItems.add(removeItem(item_id));
        }

        // Discount
        public void addDiscount(DiscountInfo discountInfo) {
            this.discounts.addDiscount(discountInfo.getPercentage(), discountInfo.getStartDate(),
                    discountInfo.getEndDate());
        }

        public Item getItem(int itemId) {
            for (Item item : shelfItems)
                if (item.getItemId() == itemId)
                    return item;
            for (Item item : storageItems)
                if (item.getItemId() == itemId)
                    return item;
            return null;
        }

        public boolean needToRestock() {
            updateOutOfDateItems();
            return (storageItems.size() + shelfItems.size()) - expiredItems.size() < minToRestock;
        }

        private void updateOutOfDateItems() {
            for (Item item : storageItems) {
                if (item.isExpired()) {
                    expiredItems.add(item);
                    storageItems.remove(item);
                }
            }
            for (Item item : shelfItems) {
                if (item.isExpired()) {
                    expiredItems.add(item);
                    shelfItems.remove(item);
                }
            }
        }

        public List<Integer> updateShelfAndStorageContents(List<Integer> itemOnShelfIDs, List<Integer> itemInStockIDs) {
            List<Integer> removeItems = new ArrayList<>();
            for (Item item : shelfItems)
                if (!itemOnShelfIDs.contains(item.getItemId()) && !itemInStockIDs.contains(item.getItemId()))
                    removeItems.add(item.getItemId());
            for (Item item : storageItems)
                if (!itemOnShelfIDs.contains(item.getItemId()) && !itemInStockIDs.contains(item.getItemId()))
                    removeItems.add(item.getItemId());
            shelfItems = getItemList(itemOnShelfIDs);
            storageItems = getItemList(itemInStockIDs);
            removeItemsById(removeItems);
            return removeItems;
        }

        public List<Item> getItemList(List<Integer> itemIDs) {
            for (int itemID : itemIDs)
                if (!isItemOnShelf(itemID) && !isItemInStorage(itemID))
                    throw new IllegalArgumentException("Item " + itemID + " doesn't exists");
            List<Item> items = new ArrayList<>();
            for (int itemID : itemIDs)
                items.add(getItem(itemID));
            return items;
        }

        private void removeItemsById(List<Integer> items) {
            for (int itemID : items)
                removeItem(itemID);
        }

        public List<Item> getItemsOutOfDate() {
            updateOutOfDateItems();
            List<Item> items = new ArrayList<>();
            for (Item item : expiredItems) {
                items.add(item);
            }
            return items;
        }

        public void updateMinToRestock(int minQuantity) {
            this.minToRestock = minQuantity;
        }

        public SoldItem sellItem(int itemId, Date sellDate) {
            if (!isItemOnShelf(itemId))
                throw new IllegalArgumentException("Item " + itemId + " doesn't exists on shelf");
            Item item = removeItem(itemId);
            return item.sell(sellDate, getPrice());
        }

        public DefectItem markItemDefective(int itemId, String reason) {
            if (!isItemOnShelf(itemId) && !isItemInStorage(itemId))
                throw new IllegalArgumentException("Item " + itemId + " doesn't exists");
            Item item = removeItem(itemId);
            return item.markDefective(reason);
        }

        // Restock
        public boolean needsRestock() {
            return getTotalQuantity() < minToRestock;
        }

        // public for tests
        public boolean inventoryCountStorage(int inStorage) {
            return inStorage == storageItems.size();
        }

        public boolean inventoryCountShelf(int inSelf) {
            return inSelf == shelfItems.size();
        }

        @Override
        public String toString() {
            return "=== Product " + productId + " ==="
                    + "\n Product Name  = " + name
                    + "\n Manufacturer  = " + manufacturer
                    + "\n Sub-Category  = " + subCategory
                    + "\n Sub-Sub-Cat   = " + subSubCategory
                    + "\n Min Quantity  = " + minToRestock
                    + "\n Shelf Location= " + shelfLocation
                    + "\n Base Price    = " + priceWithoutDiscount
                    + "\n On Shelf      = " + shelfItems.size()
                    + "\n In Storage    = " + storageItems.size();
        }

    }

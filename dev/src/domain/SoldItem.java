package domain;

import java.util.Date;

public class SoldItem {
    private double sellPrice;
    private int costPrice;
    private int itemId;
    private Date sellDate;

    public SoldItem(int itemId, Date sellDate, double sellPrice, int costPrice) {
        this.itemId = itemId;
        this.sellDate = sellDate;
        this.sellPrice = sellPrice;
        this.costPrice = costPrice;
    }

    public int getItemId() { return itemId; }
    public double getSellPrice() { return sellPrice; }
    public int getCostPrice() { return costPrice; }
    public Date getSellDate() { return sellDate; }
}

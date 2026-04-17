package domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Discounts {
    private List<DiscountInfo> discounts;

    public Discounts() {
        this.discounts = new ArrayList<>();
    }
    private boolean hasOverlappingDiscount(DiscountInfo newDiscount) {
            for (DiscountInfo existingDiscount : discounts) {
                if (newDiscount.getStartDate().before(existingDiscount.getEndDate()) &&
                        existingDiscount.getStartDate().before(newDiscount.getEndDate())) {
                    return true;
                }
            }
            return false;
        }

        public void addDiscount(DiscountInfo discountInfo) {
            if (hasOverlappingDiscount(discountInfo))
                throw new IllegalArgumentException("Cannot add overlapping discount periods");
            this.discounts.add(discountInfo);
        }
    public double getBestActiveDiscount(Date date) {
        double best = 0;
        for (DiscountInfo discount : discounts) {
            if (discount.isActiveOn(date) && discount.getPercentage() > best) {
                best = discount.getPercentage();
            }
        }
        return best;
    }

    public List<DiscountInfo> getAllDiscounts() {
        return discounts;
    }
}

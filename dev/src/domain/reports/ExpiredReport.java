package domain.reports;

import domain.Item;
import java.util.Date;
import java.util.List;

public class ExpiredReport extends Report<Item> {
    private Date startDate;
    private Date endDate;

    public ExpiredReport(int reportId, List<Item> data, Date startDate, Date endDate) {
        super(reportId, data, new Date());
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }

    @Override
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Expired Items Report - ").append(getPublishDate()).append("\n");
        sb.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        sb.append("Expired items:\n");
        for (Item item : getData()) {
            sb.append("- Barcode: ").append(item.getItemId())        
              .append(" | Expired: ").append(item.getExpirationDate())
              .append("\n");
        }
        return sb.toString();
    }
}
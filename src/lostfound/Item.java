package lostfound;

import java.time.LocalDate;

public class Item {
    private final int id;
    private final String type;
    private final String title;
    private final String description;
    private final String category;
    private final String location;
    private final LocalDate eventDate;
    private final String status;
    private final int reportedBy;
    private final Integer claimedBy;
    private final String claimNote;

    public Item(int id, String type, String title, String description, String category,
                String location, LocalDate eventDate, String status, int reportedBy,
                Integer claimedBy, String claimNote) {
        this.id = id; this.type = type; this.title = title; this.description = description;
        this.category = category; this.location = location; this.eventDate = eventDate;
        this.status = status; this.reportedBy = reportedBy; this.claimedBy = claimedBy;
        this.claimNote = claimNote;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public LocalDate getEventDate() { return eventDate; }
    public String getStatus() { return status; }
    public int getReportedBy() { return reportedBy; }
    public Integer getClaimedBy() { return claimedBy; }
    public String getClaimNote() { return claimNote; }
}

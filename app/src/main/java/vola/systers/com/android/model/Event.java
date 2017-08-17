package vola.systers.com.android.model;

public class Event {
    private String id;
    private String name;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String locationName;

    public Event(String id, String name, String startDate, String endDate, String startTime,String endTime,String locationName ) {
        this.id=id;
        this.name=name;
        this.startDate=startDate;
        this.endDate=endDate;
        this.startTime=startTime;
        this.endTime=endTime;
        this.locationName=locationName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLocationName() { return locationName; }
}
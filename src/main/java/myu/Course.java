package myu;
public class Course {
    private String name;
    private String days;
    private String startTime;
    private String endTime;
    private String location;

    public Course(String name, String days, String startTime,
                    String endTime, String location) {
        
        this.name = name;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }
    public String toString() {
        String temp = "Class name: " + name + "\n";
        temp += "Location: " + location + "\n";
        temp += "Days: " + days + "\n";
        temp += "Time: " + startTime + "-" + endTime + "\n";
        return temp;
    }
    public String getName() {
        return name;
    }
    public String getDays() {
        return days;
    }
    public String getStart() {
        return startTime;
    }
    public String getEnd() {
        return endTime;
    }
    public String getLocation() {
        return location;
    }
}

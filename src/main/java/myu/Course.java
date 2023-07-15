package myu;

/*
 * This class represents a university course (at UW Seattle) with only
 *  the necessary fields to help create a visual schedule like the one
 *  registration provides us
 */
public class Course {
    private String name;
    private String days;
    private String startTime;
    private String endTime;
    private String location;

    /*
     * Constructor 
     */
    public Course(String name, String days, String startTime,
                    String endTime, String location) {
        
        this.name = name;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    /*
     * @purpose: creates a String of the properties of 
     *              current Course obj with each property
     *              on a new line
     * @param: none
     * @returns: see @purpose
     */
    public String toString() {
        String temp = "Class name: " + name + "\n";
        temp += "Location: " + location + "\n";
        temp += "Days: " + days + "\n";
        temp += "Time: " + startTime + "-" + endTime + "\n";
        return temp;
    }

    /*
     * following are all get methods 
     */
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

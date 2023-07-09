package myu;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.awt.Color;
public class Schedule {
    private String studentName;
    private List<Course> courses;
    private Color color;

    public static void main(String[] args) {
        new Schedule("test", 
            Arrays.asList("17913 M E 123 AB LB 0.0 VISUALIZATION & CAD TTh  930-1020 MEB 231 Jones,J"));
    }

    public Schedule(String name, List<String> classes) {
        studentName = name;
        courses = new ArrayList<>();
        color = getRandomColor();
        buildSchedule(classes);
    }

    private void buildSchedule(List<String> classes) {
        for (String curr : classes) {
            int index = 0;
            //delete SLN 
            curr = curr.substring(curr.indexOf(" ") + 1);

            Scanner front = new Scanner(curr);
            String className = "";
            //get name of class
            while (front.hasNext()) {
                String temp = front.next();
                className += (temp + " ");
                //find number part of class name and add the lecture 
                //letter after it as well
                if (isNum(temp)) {
                    className += front.next();
                    break;
                }
            }
            //+1 to account for the space after the class name
            index += className.length() + 1; 

            //delete lecture/lab/section detail
            index += front.next().length() + 1;
            //delete class credits details
            index += front.next().length() + 1;
            curr = curr.substring(index);
            front.close();

            //start working from back
            String revCurr = ScheduleBuilder.reverse(curr);

            Scanner back = new Scanner(revCurr);
            String location = "location";
            while (back.hasNext()) {
                String temp = back.next();
                //skip over instructor name if is present
                if (hasNum(temp)) {
                    location = back.next() + " " + temp;
                    break;
                }
            }
            String classTime = back.next();
            String days = back.next();
            back.close();
            
            int i = classTime.indexOf("-");
            courses.add(new Course(className, days, 
                            classTime.substring(0, i), 
                            classTime.substring(i+1), location));
            // System.out.println(courses.get(courses.size()-1));
        }
    }

    private Color getRandomColor() {
        Random rand = new Random();
        // float r = rand.nextFloat() / 2f + 0.5f;
        // float g = rand.nextFloat() / 2f + 0.5f;
        // float b = rand.nextFloat() / 2f + 0.5f;
        int r = rand.nextInt(155)+50;
        int g = rand.nextInt(155)+50;
        int b = rand.nextInt(155)+50;
        return new Color(r, g, b);
    }

    //check if string is a valid integer
    private boolean isNum(String str) {
        return str.matches("\\d+");
    }
    //check if string contains a valid integer somewhere
    private boolean hasNum(String str) {
        return str.matches(".*\\d.*");
    }

    public List<Course> getCourses() {
        return courses;
    }

    public String getStudentName() {
        return studentName;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}

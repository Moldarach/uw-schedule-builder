package myu;
import java.awt.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Random;

public class Grid {
    private List<Schedule> allSchedules = new ArrayList<>();
    //default half hour blocks is 16
    public static final int HALFHOURBLOCKS = 16;

    public static void main(String[] args) {
        new Grid(null);
    }

    public Grid(List<Schedule> allSchedules) {
        this.allSchedules = allSchedules;
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                JFrame frame = new JFrame("Visual Schedule");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {

        public TestPane() {
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(500, 500);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            int size = Math.min(getWidth() - 4, getHeight() - 4) / (HALFHOURBLOCKS+1);
            int xSize = getWidth()/(HALFHOURBLOCKS/2);
            int width = getWidth() - (size * 2);
            int height = getHeight() - (size * 2);

            //draw schedule grid
            int y = size;
            for (int horz = 0; horz < HALFHOURBLOCKS; horz++) {
                int x = xSize;
                for (int vert = 0; vert < 6; vert++) {
                    g.drawRect(x, y, xSize, size);
                    x += xSize;
                }
                y += size;
            }
             
            //write days to x-coordinate on canvas to a hashmap
            Map<String, Integer> daysToXCoord = new HashMap<>();
            String[] week = new String[]{"", "Monday", "Tuesday", 
                    "Wednesday", "Thursday", "Friday"};
            String dayKey = "";
            int x = xSize;
            for (int i = 0; i < 6; i ++) {
                g.drawString(week[i], x+(xSize/3), size-5);
                //no zero case because need to draw nothing in that box 
                switch(i) {
                    case 1:
                        dayKey = "M";
                        break;
                    case 2: 
                        dayKey = "T";
                        break;
                    case 3: 
                        dayKey = "W";
                        break;
                    case 4:
                        dayKey = "Th";
                        break;
                    case 5:
                        dayKey = "F";
                        break;
                }
                daysToXCoord.put(dayKey, x);
                x += xSize;
            } 

            //write times to canvas y-coordinates in a hashmap
            int time = 830;
            Map<Integer, Integer> timeToYCoord = new HashMap<>();
            for (int i = 1; i <= HALFHOURBLOCKS; i ++) {
                g.drawString(Integer.toString(time), xSize+5, i*size+(size/2));
                timeToYCoord.put(time, i*size);
                //roll over to 1pm instead of 1300
                if (time == 1230) {
                    time = 100;
                } else if (time % 100 == 30) { //go from half hour to on the hour mark
                    time += 70;
                } else {
                    time += 30;
                }
            }

            //draw classes
            //parse each individual schedule 
            for (Schedule sch : allSchedules) {
                //parse all the courses in current schedule
                //will possibly fail if one course is not consistent between days
                for (Course course : sch.getCourses()) {
                    String days = course.getDays();
                    int start = Integer.parseInt(course.getStart());
                    int end = Integer.parseInt(course.getEnd());
                    // Color classColor = getRandomColor();
                    
                    //handle each day that a class runs on
                    for (int i = 0; i < days.length(); i ++) {
                        g.setColor(sch.getColor());
                        String curr = days.substring(i, i+1);
                        //handle Thursday separately because it is 2 characters long
                        if (curr.equals("T") && i < days.length()-1 && days.charAt(i+1) == 'h') {
                            curr = "Th";
                            i++;
                        }
                        //because the pm times are not using military time
                        if (end < start) {
                            end += 1200;
                        }
                        //because for example, 920-830 gets 90 and not the actual 50 minutes 
                        int timeDiff = end-start-(end-start)/90*40;
                        int rectHeight = (timeDiff)/30*(size) + (int)((double)(timeDiff)%30.0/30.0*size);
                        
                        //draw in rectangle for each class
                        //+1 and -2 are to create a margin around the rectangle to 
                            //be able to display the grid lines around it
                        g.fillRect(daysToXCoord.get(curr)+1, 
                            timeToYCoord.get(start)+1, xSize-2, rectHeight-2);

                        //set color to black to write the text for each class
                        g.setColor(Color.black);
                        //write class name and location separately 
                        g.drawString(course.getName(), daysToXCoord.get(curr) + xSize/3,
                            timeToYCoord.get(start) + rectHeight/3);
                        g.drawString(course.getLocation(), daysToXCoord.get(curr) + xSize/3,
                             timeToYCoord.get(start) + rectHeight*2/3);
                    }
                }
            }
            g2d.dispose();
        }
    }


    protected boolean addSchedule(Schedule curr) {
        //no duplicate schedules allowed
        if (!allSchedules.contains(curr)) {
            allSchedules.add(curr);
            return true;
        }
        return false;
    }

    protected boolean removeSchedule(Schedule curr) {
        if (allSchedules.contains(curr)) {
            allSchedules.remove(curr);
            return true;
        }
        return false;
    }
}
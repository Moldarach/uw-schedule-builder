package myu;
import java.awt.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Grid {
    private List<Schedule> allSchedules = new ArrayList<>();
    private JFrame frame;
    private Map<String, Integer> daysToXCoord;
    private Map<Integer, Integer> timeToYCoord;
    //default half hour blocks is 16
    public static final int HALFHOURBLOCKS = 16;
    public static final int XSTART = 100;
    public static final int YSTART = 0;
    public static final int XSIZE = 100;

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

                frame = new JFrame("Visual Schedule");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JScrollPane scroll = new JScrollPane(
                    new TestPane(), 
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
                );
                frame.add(scroll);
                // frame.add(new TestPane());
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
            return new Dimension(600*allSchedules.size(), 600);
        }


        private void drawGrid(Graphics g, int size) {
            //draw schedule grid
            // int y = size;
            int y = YSTART;
            for (int horz = 0; horz <= HALFHOURBLOCKS; horz++) {
                // int x = XSIZE;
                int x = XSTART;
                for (int vert = 0; vert < 6; vert++) {
                    //increase width of rectangle based on how many total schedules there are
                    if (vert == 0) { //don't change rectangle for holding time
                        g.drawRect(x, y, XSIZE, size);
                        x += XSIZE;
                    } else {
                        g.drawRect(x, y, XSIZE*allSchedules.size(), size);
                        x += XSIZE*allSchedules.size();
                    }
                }
                y += size;
            }
        }

        private void writeDays(Graphics g, int size) {
            //write days to x-coordinate on canvas to a hashmap
            daysToXCoord = new HashMap<>();
            String[] week = new String[]{"", "Monday", "Tuesday", 
                    "Wednesday", "Thursday", "Friday"};
            String dayKey = "";
            int x = XSTART+XSIZE;
            // int x = 0;
            for (int i = 1; i < 6; i ++) {
                // g.drawString(week[i], allSchedules.size()*XSIZE+(XSIZE/3), size-5);
                g.drawString(week[i], x+(XSIZE/4), YSTART+size-5);
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
                x += XSIZE*allSchedules.size();
            }    
        }

        private void writeTimes(Graphics g, int size) {
            //write times to canvas y-coordinates in a hashmap
            int time = 830;
            timeToYCoord = new HashMap<>();
            g.setFont(new Font("Tahoma", Font.PLAIN, 20));
            for (int i = 1; i <= HALFHOURBLOCKS; i ++) {
                g.drawString(Integer.toString(time), XSTART+5, YSTART+i*size+(size/2));
                timeToYCoord.put(time, YSTART+i*size);
                //roll over to 1pm instead of 1300
                if (time == 1230) {
                    time = 100;
                } else if (time % 100 == 30) { //go from half hour to on the hour mark
                    time += 70;
                } else {
                    time += 30;
                }
            }
            g.setFont(new Font("Tahoma", Font.PLAIN, 11));
        }

        private void drawClasses(Graphics g, int size) {
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
                        
                        //for shifting classes of separate schedules apart so they don't overlap visually
                        int xOffset = XSIZE*(allSchedules.indexOf(sch));
                        //draw in rectangle for each class
                        //+1 and -2 are to create a margin around the rectangle to 
                            //be able to display the grid lines around it
                        g.fillRect(daysToXCoord.get(curr)+1 + xOffset, 
                            timeToYCoord.get(start)+1, XSIZE-2, rectHeight-2);

                        //set color to black to write the text for each class
                        g.setColor(Color.black);
                        //write class name and location separately 
                        g.drawString(course.getName(), daysToXCoord.get(curr) + XSIZE/4 + xOffset,
                            timeToYCoord.get(start) + rectHeight/3);
                        g.drawString(course.getLocation(), daysToXCoord.get(curr) + XSIZE/4 + xOffset,
                             timeToYCoord.get(start) + rectHeight*2/3);
                    }
                }
            }
        }

        private void drawLegend(Graphics g) {
            int y = 20;
            for (Schedule curr : allSchedules) {
                g.setColor(curr.getColor());
                g.fillOval(10, y, 10, 10);
                g.setColor(Color.black);
                g.drawString(curr.getStudentName(), 25, y+9);
                y += 20;
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            int size = Math.min(getWidth() - 4, getHeight() - 4) / (HALFHOURBLOCKS+1);
            drawGrid(g, size);
            writeDays(g, size);
            writeTimes(g, size);
            drawClasses(g, size);
            drawLegend(g);
            g2d.dispose();
        }
    }


    protected boolean addSchedule(Schedule curr) {
        //no duplicate schedules allowed
        if (!isDuplicate(curr.getStudentName())) {
            allSchedules.add(curr);
            refreshPanel();
            return true;
        }
        return false;
    }

    private boolean isDuplicate(String name) {
        for (Schedule curr : allSchedules) {
            if (curr.getStudentName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    protected boolean removeSchedule(Schedule curr) {
        if (allSchedules.contains(curr)) {
            allSchedules.remove(curr);
            refreshPanel();
            return true;
        }
        return false;
    }

    protected void refreshPanel() {
        frame.getContentPane().validate();
        frame.getContentPane().repaint();
    }
}
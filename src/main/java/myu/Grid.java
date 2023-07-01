package myu;
import java.awt.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Grid {
    private List<Schedule> allSchedules = new ArrayList<>();

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


    public static final int HALFHOURBLOCKS = 16;

    public class TestPane extends JPanel {

        public TestPane() {
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 400);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            int size = Math.min(getWidth() - 4, getHeight() - 4) / (HALFHOURBLOCKS+1);
            int xSize = getWidth()/(HALFHOURBLOCKS/2);
            int width = getWidth() - (size * 2);
            int height = getHeight() - (size * 2);

            //draw boxes
            // int y = (getHeight() - (size * 8)) / 2;
            int y = size;
            for (int horz = 0; horz < HALFHOURBLOCKS; horz++) {
                // int x = (getWidth() - (size * 6)) / 2;
                int x = xSize;
                for (int vert = 0; vert < (HALFHOURBLOCKS/2-2); vert++) {
                    g.drawRect(x, y, xSize, size);
                    x += xSize;
                }
                y += size;
            }
             
            //write days
            Map<String, Integer> daysToXCoord = new HashMap<>();
            String[] week = new String[]{"", "Monday", "Tuesday", 
                    "Wednesday", "Thursday", "Friday"};
            String dayKey = "";
            int x = xSize;
            for (int i = 0; i < 6; i ++) {
                g.drawString(week[i], x+(xSize/3), size-5);

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
                // System.out.println(dayKey + ", " + x);
                daysToXCoord.put(dayKey, x);
                x += xSize;
            } 

            //write times
            int time = 830;
            Map<Integer, Integer> timeToYCoord = new HashMap<>();
            for (int i = 1; i <= HALFHOURBLOCKS; i ++) {
                g.drawString(Integer.toString(time), xSize+5, i*size+(size/2));
                // System.out.println(time + ", " + i*size);
                timeToYCoord.put(time, i*size);
                if (time == 1230) {
                    time = 100;
                } else if (time % 100 == 30) {
                    time += 70;
                } else {
                    time += 30;
                }
            }

            //draw classes
            //parse each individual schedule 
            for (Schedule sch : allSchedules) {
                sch.setColor(getRandomColor());
                //parse all the courses in current schedule
                //will possibly fail if one course is not consistent between days
                for (Course course : sch.getCourses()) {
                    String days = course.getDays();
                    String description = course.getName() + "\n" + course.getLocation();
                    int start = Integer.parseInt(course.getStart());
                    int end = Integer.parseInt(course.getEnd());
                    g.setColor(getRandomColor());
                    //handle each day that a class runs on
                    for (int i = 0; i < days.length(); i ++) {
                        String curr = days.substring(i, i+1);
                        if (curr.equals("T") && i < days.length()-1 && days.charAt(i+1) == 'h') {
                            curr = "Th";
                            i++;
                        }
                        //because the pm times are not using military time
                        if (end < start) {
                            end += 1200;
                        }
                        int rectHeight = (end-start)/30*size + (end-start)%30/30;
                        // g.setColor(sch.getColor());
                        
                        g.fillRect(daysToXCoord.get(curr), 
                            timeToYCoord.get(start), xSize, rectHeight);
                    }
                }
            }

            g2d.dispose();
        }

    }

    private Color getRandomColor() {
        Random rand = new Random();
        float r = rand.nextFloat() / 2f + 0.5f;
        float g = rand.nextFloat() / 2f + 0.5f;
        float b = rand.nextFloat() / 2f + 0.5f;
        // int r = rand.nextInt(255);
        // int g = rand.nextInt(255);
        // int b = rand.nextInt(255);
        return new Color(r, g, b);
    }
}
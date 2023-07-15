package myu;

import java.awt.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/*
 * This class is a new window from the application launcher that displays
 * the visual Schedule(s). This class is the main frame/canvas while its 
 * internal class handles drawing everything inside. 
 */
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

    /*
     * constructor
     * relies on internal class Pane to handle content inside the main canvas
     * @param: List of all imported Schedules to the program
     * @return: new Grid object
     */
    public Grid(List<Schedule> allSchedules) {
        this.allSchedules = allSchedules;
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException 
                            | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                frame = new JFrame("Visual Schedule");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //call Pane class 
                JScrollPane scroll = new JScrollPane(
                    new Pane(), 
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
                );
                frame.add(scroll);
                // frame.add(new Pane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    /*
     * This class represents a panel that contains and displays all the 
     * information about each Schedule as well as the base frame (the grid, 
     * times, and days) 
     */
    public class Pane extends JPanel {

        /*
         * @param: none
         * @return: new Dimension for the overall canvas to be x-value of 
         *              Dimension increases with each added Schedule, y-value
         *              stays constant
         */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(600*allSchedules.size(), 600);
        }

        /*
         * draws the grid that all other components are drawn over 
         * @param: 
         *      -Graphics obj that allows for content to be drawn on-screen
         *      -int determining how tall each grid rectangle is
         * @return: nothing
         */
        private void drawGrid(Graphics g, int ySize) {
            //draw schedule grid
            int y = YSTART;
            for (int horz = 0; horz <= HALFHOURBLOCKS; horz++) {
                int x = XSTART;
                for (int vert = 0; vert < 6; vert++) {
                    //increase width of rectangle based on how many total schedules there are
                    if (vert == 0) { //don't change rectangle for holding times on left hand side
                        g.drawRect(x, y, XSIZE, ySize);
                        x += XSIZE;
                    } else {
                        g.drawRect(x, y, XSIZE*allSchedules.size(), ySize);
                        x += XSIZE*allSchedules.size();
                    }
                }
                y += ySize;
            }
        }

        /*
         * writes the words of each individual weekday onto its corresponding
         * space at the top of the grid
         * @param: 
         *      -Graphics obj that allows for content to be drawn on-screen
         *      -int determining how tall each grid rectangle is
         * @return: nothing
         */
        private void writeDays(Graphics g, int ySize) {
            //write days to x-coordinate on canvas to a hashmap
            daysToXCoord = new HashMap<>();
            String[] week = new String[]{"", "Monday", "Tuesday", 
                    "Wednesday", "Thursday", "Friday"};
            String dayKey = "";
            int x = XSTART+XSIZE;
            //start at i=1 to skip the first box where nothing needs to be drawn
            for (int i = 1; i < 6; i ++) {
                g.drawString(week[i], x+(XSIZE/4), YSTART+ySize-5);
                //use for-loop variable to determine which day to write out 
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

        /*
         * writes each half hour block of time (starting from 8:30) to the 
         * left side of the grid, going top to bottom
         * @param: 
         *      -Graphics obj that allows for content to be drawn on-screen
         *      -int determining how tall each grid rectangle is
         * @return: nothing
         */
        private void writeTimes(Graphics g, int ySize) {
            //write times to canvas y-coordinates in a hashmap
            int time = 830;
            timeToYCoord = new HashMap<>();
            //make font larger for writing times
            g.setFont(new Font("Tahoma", Font.PLAIN, 20));
            for (int i = 1; i <= HALFHOURBLOCKS; i ++) {
                g.drawString(Integer.toString(time), XSTART+5, YSTART+i*ySize+(ySize/2));
                timeToYCoord.put(time, YSTART+i*ySize);
                //roll over to 1pm instead of 1300
                if (time == 1230) {
                    time = 100;
                } else if (time % 100 == 30) { //go from half hour to on the hour mark
                    time += 70;
                } else {
                    time += 30;
                }
            }
            //reset font size to default size
            g.setFont(new Font("Tahoma", Font.PLAIN, 11));
        }

        /*
         * draws each individual Course in each Schedule onto the grid,  
         * represented by a rectangle with the Course name and building 
         * location drawn inside
         * @param: 
         *      -Graphics obj that allows for content to be drawn on-screen
         *      -int determining how tall each grid rectangle is
         * @return: nothing 
         */
        private void drawClasses(Graphics g, int ySize) {
            //draw classes
            //parse each individual schedule 
            for (Schedule sch : allSchedules) {
                //parse all the courses in current schedule
                //will possibly fail if one course is not consistent between days
                for (Course course : sch.getCourses()) {
                    String days = course.getDays();
                    int start = Integer.parseInt(course.getStart());
                    int end = Integer.parseInt(course.getEnd());
                    
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
                        int rectHeight = (timeDiff)/30*(ySize) + (int)((double)(timeDiff)%30.0/30.0*ySize);
                        
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

        /*
         * draws each students' name on the leftmost side of the main canvas
         * along with a dot colored the same as their Schedule 
         * @param: Graphics obj that allows for content to be drawn on-screen
         * @return: nothing 
         */
        private void drawLegend(Graphics g) {
            int y = 20;
            for (Schedule curr : allSchedules) {
                g.setColor(curr.getColor());
                g.fillOval(10, y, 10, 10);
                //for satisfactory progress per quarter 
                //  if not at or over 12 credits, will have a red name
                if (curr.getCredits() >= 12) {
                    g.setColor(Color.black); 
                } else {
                    g.setColor(Color.red);
                }
                g.drawString(curr.getStudentName(), 25, y+9);
                y += 20;
            }
        }

        /*
         * Java Swing necessary method, redraws entire Pane when it detects 
         * changes that would necessitate a redrawing
         * @param: Graphics obj that allows for content to be drawn on-screen
         * @return: nothing
         */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            int ySize = Math.min(getWidth() - 4, getHeight() - 4) / (HALFHOURBLOCKS+1);
            drawGrid(g, ySize);
            writeDays(g, ySize);
            writeTimes(g, ySize);
            drawClasses(g, ySize);
            drawLegend(g);
            g2d.dispose();
        }
    }

    /*
     * add a Schedule to the overall visual schedule 
     * no duplicate schedules allowed
     * (check isDuplicate method for what "duplicate" means)
     * @param: Schedule obj to be added
     * @return: 
     *      -true if added successfully
     *      -false if is a duplicate
     */
    protected boolean addSchedule(Schedule curr) {
        if (!isDuplicate(curr.getStudentName())) {
            allSchedules.add(curr);
            refreshPanel();
            return true;
        }
        return false;
    }

    /*
     * checks if a Schedule is a duplicate of another by comparing  
     * student names: if the student names of two Schedules are the
     * same then the later added one is considered to be "duplicate"
     * @param: String of a student's name
     * @return: 
     *      -true if the passed student name can be found in any of the 
     *          already included Schedules
     *      -false otherwise
     */
    private boolean isDuplicate(String name) {
        for (Schedule curr : allSchedules) {
            if (curr.getStudentName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /*
     * remove the given Schedule from the current list of all
     * Schedules to display
     * @param: a Schedule to remove
     * @return: 
     *      -true if the passed Schedule exists 
     *      -false otherwise
     */
    protected boolean removeSchedule(Schedule curr) {
        if (allSchedules.contains(curr)) {
            allSchedules.remove(curr);
            refreshPanel();
            return true;
        }
        return false;
    }

    /*
     * refreshes the entire canvas/frame to display updates 
     * manually to handle cases that paintComponent doesn't
     * @param: none
     * @return: nothing
     */
    protected void refreshPanel() {
        frame.getContentPane().validate();
        frame.getContentPane().repaint();
    }

    /*
     * returns the main JFrame
     * @param: none
     * @return: the main JFrame of the Grid class
     */
    protected JFrame getFrame() {
        return frame;
    }
}
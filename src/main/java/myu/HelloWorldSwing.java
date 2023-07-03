package myu;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class HelloWorldSwing extends JPanel implements ActionListener {
    private Grid visualGrid;

    public HelloWorldSwing() {
        super(new GridLayout(1, 1));
         
        JTabbedPane tabbedPane = new JTabbedPane();
        
        //Creating the panel at bottom and adding components
        JPanel panel1 = new JPanel(); // the panel is not visible in output
        JButton addSchedule = new JButton("Add a schedule");
        // JButton showSchedule = new JButton("Show all schedules");

        JButton hideSchedule = new JButton("Hide all schedules");
        
        addSchedule.addActionListener(e -> addPDF());
        // showSchedule.addActionListener(e -> scheduleDisplayChanged());

        // panel1.add(addSchedule);
        panel1.add(BorderLayout.SOUTH, addSchedule);
        // panel1.add(BorderLayout.EAST, showSchedule);
        //frame.getContentPane().add(BorderLayout.SOUTH, panel1);

        // JComponent panel2 = makeTextPanel("Panel #2");
        JPanel panel2 = new JPanel();
        panel2.add(hideSchedule);


        JPanel cards = new JPanel(new GridLayout(1, 2));
        cards.add(panel1, null);
        cards.add(panel2, null);

        // JComponent panel1 = makeTextPanel("Panel #1");
        tabbedPane.addTab("Add Schedules", null, cards,
                "Add Schedules");
         
        // JComponent panel2 = makeTextPanel("Panel #2");
        tabbedPane.addTab("Show Schedules", null, panel2,
                "Show Schedules");
         
        JComponent panel3 = makeTextPanel("Panel #3");
        tabbedPane.addTab("Tab 3", null, panel3,
                "Still does nothing");
         
         
        //Add the tabbed pane to this panel.
        add(tabbedPane);
         
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }
     
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        //Creating the Frame
        JFrame frame = new JFrame("Schedule Builder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("FILE");
        JMenu m2 = new JMenu("Help");
        mb.add(m1);
        mb.add(m2);
        JMenuItem m11 = new JMenuItem("Open");
        JMenuItem m22 = new JMenuItem("Save as");
        m1.add(m11);
        m1.add(m22);

        
        

        // Text Area at the Center
        // JTextArea ta = new JTextArea();

        //Adding Components to the frame.
        frame.add(new HelloWorldSwing(), BorderLayout.CENTER);
        // frame.getContentPane().add(BorderLayout.SOUTH, panel1);
        // frame.getContentPane().add(BorderLayout.PAGE_START, panel2);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.setVisible(true);
    }

    private void addPDF() {
        
        JFileChooser fileChooser = new JFileChooser();

        // Set the file chooser to open in file selection mode
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Set an optional file filter
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
        fileChooser.setFileFilter(filter);

        // Show the file chooser dialog
        int returnValue = fileChooser.showOpenDialog(null);

        // Check if the user selected a file
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            java.io.File selectedFile = fileChooser.getSelectedFile();

            // Process the selected file
            System.out.println("Selected File: " + selectedFile.getAbsolutePath());
            
            String name = JOptionPane.showInputDialog(fileChooser,
                        "Who does this schedule belong to?", null);
            if (name == null) { //user hit cancel button
                System.out.println("No student name given");
            } else if (name.equals("")) { //user hit enter without typing anything
                name = JOptionPane.showInputDialog(fileChooser,
                        "Please enter a name", null);
            } else {
                try {
                    Schedule testSch = new Schedule(name, ScheduleBuilder.parse(selectedFile));
                    if (visualGrid == null) {
                        List<Schedule> temp = new ArrayList<>();
                        temp.add(testSch);
                        visualGrid = new Grid(temp);
                    } else {
                        visualGrid.addSchedule(testSch);
                    }
                    

                } catch(Exception e) {
                    e.printStackTrace();
                } 
            }
        } else {
            System.out.println("No file chosen.");
        }
    }

    //just to satisfy the inherited abstract method
    public void actionPerformed(ActionEvent e) {   
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}

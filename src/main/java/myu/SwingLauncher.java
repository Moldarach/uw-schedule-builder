package myu;

// import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/*
 * This class is the main launcher for the UW Schedule Builder application
 * Its UI handles adding a schedule and nothing else right now
 */
public class SwingLauncher extends JPanel implements ActionListener {
    private Grid visualGrid;

    /*
     * constructor
     * builds the window that allows you to add a schedule
     * some features CURRENTLY DO NOT WORK such as removing 
     *      schedules or saving the entire visual schedule
     * @param: JFrame to add components to
     * @return: new SwingLauncher obj
     */
    public SwingLauncher(JFrame frame) {
        super(new GridLayout(1, 1));

        Container pane = frame.getContentPane();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("FILE");
        JMenu m2 = new JMenu("Help");
        mb.add(m1);
        mb.add(m2);
        JMenuItem m11 = new JMenuItem("Open");
        JMenuItem m22 = new JMenuItem("Save as");

        m22.addActionListener(e -> saveAs());
        m1.add(m11);
        m1.add(m22);

        //adding menu bar to pane
        frame.setJMenuBar(mb);
        
        //creating the buttons
        JButton addSchedule = new JButton("Add a schedule");
        // JButton hideSchedule = new JButton("Hide all schedules");
        
        addSchedule.addActionListener(e -> addPDF());

        JPanel panel2 = new JPanel();
        // panel2.add(hideSchedule);

        //adding addSchedule button to pane
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipady = 40;
        frame.add(addSchedule, constraints);
        
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.VERTICAL;
        frame.add(panel2, constraints);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Creating the Frame
        JFrame frame = new JFrame("Schedule Builder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        //Adding Components to the frame.
        new SwingLauncher(frame);
        // frame.add(new SwingLauncher(), BorderLayout.CENTER);
        // frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.setVisible(true);
    }

    /*
     * saves the content of the Grid class JFrame to a png file location
     *      chosen by the user
     * CURRENTLY DOES NOT WORK: outputs an empty file
     * @param: none
     * @return: nothing
     */
    private void saveAs() {
        try {
            Container contentPane = visualGrid.getFrame().getContentPane();
            BufferedImage image = new BufferedImage(contentPane.getWidth(), contentPane.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            contentPane.printAll(g2d);
            g2d.dispose();

            //choose location to save
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String path = file.getAbsolutePath();
                String name = JOptionPane.showInputDialog(fileChooser,
                        "Choose a name for the saved file", null);
                path += ("\\" + name + ".jpeg");
                try {
                    File newFile = new File(path);
                    if (newFile.createNewFile()) {
                        // ImageIO.write(image, name, newFile);
                    } else {
                        throw new IOException("file not created");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // write image
                // ImageIO.write(image, "jpeg", file); //code overwrites selected folder
                //also may be broken because using incorrect empty file stream to write
            } else {
                System.out.println("User cancelled saving");
            }
            
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    /*
     * prompts user to choose a pdf file and parses its content
     * assumes that user provides a pdf in the correct format 
     *  i.e. save as pdf of correct webpage; instructions in README
     * @param: none
     * @return: nothing
     */
    private void addPDF() {
        JFileChooser fileChooser = new JFileChooser();

        //set the file chooser to open in file selection mode
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        //set an file filter
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
        fileChooser.setFileFilter(filter);

        //show the file chooser dialog
        int returnValue = fileChooser.showOpenDialog(null);

        //check if the user selected a file
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            //get the selected file
            File selectedFile = fileChooser.getSelectedFile();

            //process the selected file
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
                        if (!visualGrid.addSchedule(testSch)) {
                            throw new IllegalStateException("Inputted student name already exists");
                        }
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
        //schedule a job for the event-dispatching thread:
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
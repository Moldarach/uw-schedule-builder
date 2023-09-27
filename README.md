# uw-schedule-builder
last updated: 7/15/23

## Overview
This program is similar to the visual schedule builder that UW provides for each student, with the added functionality of supporting multiple separate schedules on the same screen. This program was built with the intention of allowing friends to directly compare their schedules and see when people would be available/unavailable. 

This project uses Apache PDFBox to extract the data off a schedule.

## Getting the correct pdf format
The program expects its passed pdfs to be in a certain format, which can be conveniently sourced on your registration page. The steps are as follows: 
1. Navigate to your class registration page, the one where you can drop classes from.
2. On the top of the page where there are a bunch of small links, click the leftmost middle link that says *Class Schedule*. It should be directly underneath the currently highlighted *Registration* link.
3. Press "ctrl-P" to print this webpage, and choose the print destination as "Save to PDF". **Do not use the "Microsoft Print to PDF" option as the PDFS it creates is incompatabile with this program!** Save your file to somewhere you can later find, and simply navigate to and select that file when the program prompts you for your schedule.

## Notes
Currently, the ability to add pdf schedules and view the overall visual schedule works. Features such as removing a schedule without restarting the program and saving the entire visual schedule to an image do not exist, although they are planned. It is difficult to say whether these features will ever be implemented though. 

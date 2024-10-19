**Problem**:  
This year, UBC switched systems from the SSC to Workday and with this change, the degree navigator tool was removed. Now, UBC students are having trouble organizing their current and future courses that they wish to take. Specifically, students are unsure on which courses they can currently take and which prerequisites they would need for other courses. Students need a way to visualize a list of prerequisite courses to be able to plan multiple years ahead in their degree and without the degree navigator there isn't a great way to do this. 

**Solution:**  
We have envisioned a website that graphically displays a tree of the pre-requisites of a course, and all courses that require it. This allows users to easily see the prerequisite they need to take in order to take a course, and helps them map a path through their University degree. Users can view course details such as

* Course name and description  
* Pre-requisite details  
* Dependent courses   
* Historical course averages  
* Lecture/tutorial/lab hours per week

**UI Mockups:**

![Mobile UI](https://github.com/CPEN-221-2024/project-meriadoc-gradmap/blob/main/images/mobile_ui.png?raw=true)
**Figure 1\.** Mobile UI Mockup

![Desktop UI](https://github.com/CPEN-221-2024/project-meriadoc-gradmap/blob/main/images/desktop_ui.jpg?raw=true)   
**Figure 2\.** Desktop UI Mockup

The user would start by selecting a course they are interested in, and would then be presented with an interactive graph showing the course they selected at the center, and all of its prerequisites and dependents. It would also show more detailed information about the selected course, such as the prerequisite string from the UBC calendar, and how many hours and credits it is.   
The user would then be able to select a prerequisite or a dependent, which will make that course the selected course. However, the previously selected course and its prerequisites (or dependents) will still be shown. So, it will show the user a complete graph of every course they’ve looked at.  
A user will also be able to select their degree and specialization to filter out courses unrelated to them.

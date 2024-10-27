# **Data Collection**

## **Web Scraper: website data and parses it into a json or equivalent** 

* Course Name, information, lecture/discussion/lab time, prerequisites, dependencies, and required grades are separated.  
* Does not have a time requirement as is precompute   
* If the webpage cannot be properly parsed, create a github issue automatically with a link to the webpage for manual parsing.  
  * This can be done though github actions on a push   
* Creates a file with the list of webpages / courses that could not be parsed   
* JSON Configuration file that adds specific parameters to the Scraper for types of courses, specific departments, etc.

## **API Access: Access and store APIs for other information (UBCgrades, etc.)**

* UBCGrades: Pull grade data given a course  
  * Save High Grade  
  * Save Low Grade  
  * Save Average (Last year, Last 5 years if possible)   
  * Save Number of Students 

# **User Interface**

## **The UI the user will interface with on the website** 

* The user will be able to select a course using a drop down menu with a search bar  
* The user will be presented with a visual graph showing the pre requisites and dependants of the selected course  
* The user with be shown detailed information on a course in a side window including  
  * Course description  
  * Prerequisites  
  * Lecture-tutorial-lab hours  
  * Last years course average  
* The user will be able to select one of the prerequisites and dependents to change the selected course to that course and expand the graph to show that course’s dependents.  
* The user interface will be responsive and will provide a good user experience to users on computers and phones.  
* The user will be able to navigate the graph (pan, zoom, etc) using their mouse or fingers.  
* The website will load its data from the server using the server’s API.

# **Server**

## **Server Side Logic to Run**  

* Must be able to retrieve the following from a database for every UBC course   
  * Course description  
  * Prerequisites  
  * Dependencies  
  * Lecture-tutorial-lab hours  
  * Last years course average  
* Must be able to link a course with all of its prerequisites and dependencies  
* Retrieval time for a course and its prerequisites/dependencies must be under one second when a user selects a course
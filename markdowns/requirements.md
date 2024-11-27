# **Data Collection**

## **Web Scraper: website data and parses it into a json or equivalent** 

* Course Name, information, lecture/discussion/lab time, prerequisites, corequisites, cr/d/f, and required grades are separated.  
* Does not have a time requirement as is precompute.
* Parse every department webpage.  

## **API Access: Access and store APIs for other information (UBCgrades, etc.)**

* UBCGrades: Pull past year grade average data given a course.

# **User Interface**

## **The UI the user will interface with on the website** 

* The user will be able to select a course using a drop down menu with a search bar.  
* The user will be presented with a visual graph showing the pre requisites, co requisites, and dependants of the selected course.  
* The user with be shown detailed information on a course in a side window including:
  * Course description.  
  * Prerequisites.  
  * Lecture-tutorial-lab hours.  
  * Last years course average.  
* The user will be able to select one of the prerequisites and dependents to change the selected course to that course and expand the graph to show that course’s dependents.  
* The user interface will be responsive and will provide a good user experience to users on computers and phones.  
* The user will be able to navigate the graph (pan, zoom, etc) using their mouse or fingers.  
* The website will load its data from the server using the server’s API.

# **Server**

## **Server Side Logic to Run**  

* Must be able to retrieve the following from a database for every UBC course.   
  * Course description.  
  * Prerequisites.  
  * Dependencies.
  * Corequisites.
  * cr/d/f 
  * Lecture-tutorial-lab hours.  
  * Last year's course average.  
* Must be able to link a course with all of its prerequisites and dependencies.  
* Retrieval time for a course and its prerequisites/dependencies must be under one second when a user selects a course.

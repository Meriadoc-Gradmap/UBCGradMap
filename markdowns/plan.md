### **How will you coordinate your work?**

We will meet once a week on the weekend where the PM plans an agenda that can be changed or updated by anyone before the meeting. We will use GitHub issues and milestones to track our progress.

### **What tools will you use to communicate?**

We will use Discord for team communication. An alternative is Microsoft Teams, however our team is familiar with Discord and the workflow is better for us.

### **Who will own each component in your architecture?**

Ben: Graph Creation and Formatting  
Tian: Data Collection (APIs, Web Scraping), Data Formatting  
William: Tester – Will test all modules  
Finn: Developer, front end.  
Iain: Graph REST API and JSON formatter

### **What is your timeline?**

Common Timeline:

* Setup Project: Oct 27  
* Specifications Written For Public Methods: November 3  
* Individual Component End Date: November 22  
* Integration Phase and Final Testing: November 27


Front end (Finn): 

* Setup project: November 1  
* Create graph: November 13  
* Create panel: November 15  
* Create search bar: November 20  
* Flexible time (bug fixing and integration): November 22


Graph REST API (Iain): 

* Start Date: October 30  
* Expected End Date: November 8

Formatter (Iain):

* Start Date: November 16  
* Expected End Date: November 20


Graph ADT (Ben):

* Start Date: October 28  
* Expected End Date: November 8

Data Collection (Tian):

* Start Date: October 30  
* Create JSON Format: November 3  
* Create Formatter: November 3  
* Create Scraper: November 10  
* Create API Tools: November 17  
* Testing and Refining: November 22  
* Expected End Date: November 22

Testing (William)

* Start Date: Oct 27th  
* Complete Initialization of processes, set up github actions – Nov 3rd Completion date  
  * Gradle Wrapper  
  * Github Actions for Gradle on Build  
  * Github Actions for Creation of issues from error log – this will be done in separate repo in organization   
* Testing for Web Scraper: Nov 10  
* Testing for API Access: Nov 10  
* Testing for JSON Formatter: Nov 14  
* Testing Server Side Logic: Nov 14  
* Testing Client Facing UI: Nov 22  
* Finish Module Tests: Nov 22  
* Integration Tests: Nov 22 \- Submission Deadline


#### 


# ***5.a-d***

# Data Collection

## **Web Scraper** 

### **Does not have a time requirement as is precompute** 

1. We note that there is no requirement for time as this is a precompute, while this is true, we will do a test for time for each course, with the goal of it being sufficiently quick.   
   1. It is unfair to put a specific value on it at this point in the testing process however   
   2. We can make the estimate that under a quarter of a second per course is a fair time.

### **If the webpage cannot be properly parsed, create a github issue automatically with a link to the webpage for manual parsing.**

2 Components 

1. “Failed to parse” file  
   1. Test by giving a poor input ie something that will result in a fail and check to see if the failed text file is equal to it’s expected value (ie actual \== expected)   
2. Github Issue Creation  
   1. Create a github issue given a error file  
   2. This will be tested in a separate testing repo such that we know it is functional before putting it into our core file

### **Creates a file with the list of webpages / courses that could not be parsed** 

1. See above sections test cases (below is a copy)  
   1. Test by giving a poor input ie something that will result in a fail and check to see if the failed text file is equal to it’s expected value (ie actual \== expected) 

   

### **JSON Configuration file that adds specific parameters to the Scraper for types of courses, specific departments, etc.**

1. Testable by manually setting the config inputs inside of the test cases and checking if the results match the expected  
2. Exhaustive tests may not be the most necessary thing, we can instead run though swapping each setting and seeing if there are changes from that swap. Were there to be numbers or other high cardinality sets in the test we can test domain edge cases.   
   

## **API Access: Access and store APIs for other information (UBCgrades, etc.)**

### **UBCGrades: Pull grade data given a course**

1. To test: Manually get data from a course and then check if the API call works to get this data  
   1. Save High Grade  
   2. Save Low Grade  
   3. Save Average (Last year, Last 5 years if possible)   
   4. Save Number of Students   
2. Edge cases: Finding edge cases are difficult for this, maybe usage property based testing could be helpful 

## **JSON Formatter**

### **Formats API Access and Web Scraper Data into a JSON File**

1. Test makes sure via checking that firstly, the JSON file follows JSON Format  
2. And secondly checks to see if the data is all there, see the next requirements tests for more details

### **Course Name, information, lecture/discussion/lab time, prerequisites, dependencies, and required grades are separated.**

1. Test case reads from format output (JSON File) and checks to see if all the data items above are separate and accessible 

# User Interface 

## **Client Facing Interfaceable UI**

Generally Selenium Test Cases may be helpful for doing this, it seems to be used in industry[^1], so without specifics (see below) we will have a Java test file that will run though the webpage providing user inputs to see if it works[^2]. All of these automated tests can be substituted / aided with manual tests. 

### **The user will be able to select a course using a drop down menu with a search bar**

1. Test automated to do the above items with some subtests  
   1. Drop down exists  
   2. Drop down can be edited   
   3. After selecting a course the course is selected (specifically the HTML is updated with the correct information)

### **The user will be presented with a visual graph showing the pre requisites and dependants of the selected course**

1. Automated user goes though and inputs to see if the webpage is updated

### **The user with be shown detailed information on a course in a side window including**

1. Course description  
2. Prerequisites  
3. Lecture-tutorial-lab hours  
4. Last years course average

This can be done using automated user input testing and we can simply read to see if it has all the data, manual testing can be done as well including simply noting if the data is there

### **The user will be able to select one of the prerequisites and dependents to change the selected course to that course and expand the graph to show that course’s dependents.**

Simple with tests – have auto-user inputs go through and check if they can can do above, this can also be done manually 

### **The user interface will be responsive and will provide a good user experience to users on computers and phones.**

Subjective–we can do a survey to see if it works, we will also make sure it is different updates based on screen size

### **The user will be able to navigate the graph (pan, zoom, etc) using their mouse or fingers.**

Can be done with manual testing – test by having someone use the page and seeing if it works

### **The website will load its data from the server using the server’s API.**

Provide commands to load data for a course and check to see if the webpage has updated with the required information 

# Server

## **Server Side Logic** 

Server side tests can be run by calling specific Javascript functions 

### **Must be able to retrieve the following from a database for every UBC course** 

* ### **Course description**

* ### **Prerequisites**

* ### **Dependencies**

* ### **Lecture-tutorial-lab hours**

* ### **Last years course average**

Call getter function in Javascript to see if it produces the method – this is a simple expected versus actual test. We can call the internal API to get the different methods. 

### **Must be able to link a course with all of its prerequisites and dependencies**

Again, getter methods that will check expected versus actual – this can be done simply as we have specifications we have made

### **Retrieval time for a course and its prerequisites/dependencies must be under one second when a user selects a course**

We will retrieve a large quantity (maybe around 100 or so) of courses done at random and see if each call time was less than 1 second – each time we test we can do this randomly. Property based testing would be interesting but most likely ineffective for finding edge cases. 

Another way to test this would be to call the course graph a tree and find the ideal root node (ie most branches) and then test calls on that because that is our maximum. I am unsure if I will go through with this however it would be easy to find. 

# Integration Testing

Once all the elements of the program are functional assuming the UI elements all work we can run the UI tests to show that every element links together (the UI depends on everything so iff the UI works / provides the correct data then the entire system works)

# ***5.e***

# Jacoco

Test coverage should be \>95%[^3]

# Check Style 

CPEN 221 for Java, Google Style Guides[^4] will be used for other languages 

1. Java \-\> CPEN 221 Style Guide  
2. Typescript \-\> Google Style Guide[^5]  
3. JSON \-\> Google Style Guide[^6]

# Static Analysis / Linting 

Java \-\> PMD[^7] Static analysis   
Typescript \-\> ESLint[^8] will be used  
JSON \-\> JSON will be automatically put into a validator after being made 

# ***5.f***

# Gradle with Github Actions

Github actions will be used with Gradle to run automatic tests and to have confirmations the builds work

To be clear, programmers should be running tests be for committing to confirm the code they wrote works, however the test case will be run again once actions are pushed as this confirms a couple things, firstly, the system works, secondly, their push didn’t mess anything up, and thirdly the system is not computer dependent – it can be run on github so definitely can be run on any system.

# Inspections and Code Review 

Given we have multiple programmers an inspection and code review process may be necessary, within static analysis some. 

Code Review will be semi-formal, using the github code review system. 

[^1]:  [https://www.browserstack.com/guide/how-to-create-selenium-test-cases](https://www.browserstack.com/guide/how-to-create-selenium-test-cases) 

[^2]:  Something similar though with Gradle: [https://www.blazemeter.com/blog/selenium-github](https://www.blazemeter.com/blog/selenium-github) 

[^3]:  As in 95% for LOC, Branch \- may be lower in some sections due to unreachable code

[^4]:  [https://google.github.io/styleguide/](https://google.github.io/styleguide/) 

[^5]:  [https://google.github.io/styleguide/tsguide.html](https://google.github.io/styleguide/tsguide.html) 

[^6]:  [https://google.github.io/styleguide/jsoncstyleguide.xml](https://google.github.io/styleguide/jsoncstyleguide.xml) 

[^7]:  [https://pmd.github.io/](https://pmd.github.io/) 

[^8]:  [https://typescript-eslint.io/getting-started/](https://typescript-eslint.io/getting-started/) 
## **MVC 1: User Interface**

## **MVC 2: Graph Creation and Formatting**

## **MVC 3: Data Collection and Formatting**

# **Common Libraries:**

[https://github.com/google/gson](https://github.com/google/gson)  
React.js  
Typescript  
NPM

# **User Interface**

## **Server** 

* This is on the server.  
* It’s responsibility is to provide data to the user interface  
* **Model (MVC 1):** This has access to the database and exposes an API.  
* **API Specification:**  
* /api/getcourse  
  * Input: {course: string}   
    * course: the name of the string  
  * Output:  
  * Either: {error: string} if an error occurs:  
  * Or: {course:   
    {description: string,  
     prereq\_text: string,  
     credits: number,  
     hours: {  
        lecture: number,  
        lecture\_alternate: boolean,  
        tutorial: number,  
        tutorial\_alternate: boolean,  
        lab: number,  
        lab\_alternate: boolean,  
     },  
     average: number,  
     prereqs: string\[\],  
     dependants: string\[\],  
     coreqs: string\[\] }  
    * Description: From the ubc calendar  
    * Prereqs\_text: The text description from the ubc calendar  
    * Credits: How many credits the course is  
    * Hours: A cute little object that specifies how many hours per week this class has  
    * Average: The past average for that course  
    * Prereqs, dependants, coreqs: Lists of the course names of the prerequisites, dependants, and corequisites respectively.   
* /api/getallcourses  
  * Input: nothing  
  * Output:  
  * Either: {error: string} if an error occurs,  
  * Or: {courses: string\[\]}  
    * Courses: An array of the names of every single course


  ## **User Interface**

* This component allows the user to interact with the program.  
* This component is on the client side, running in the user’s web browser.  
* This component contains two subcomponents, *Panel,* *Tree* and *Search Bar*.  
* **View (MVC 1):** The ReactJS frontend. This will display data from the database to the user.  
* **Controller (MVC 1):** Javascript code in the ReactJS frontend that uses the API to retrieve data. This will run when the user searches or clicks on a graph node in the view.  
* **Model:** This component acts as the model for its sub components, *Panel* and *Tree*.

## **Panel**

* This component displays to the user detailed course information such as the course description, prerequisite string, course credits, course hours, and course grade.  
* This component is on the client.  
* Template:

```tsx  
\<Panel information: {description: string, prereqs: string, credits: number, hours: string, grade: number} /\>  
```

* **View:** This is a view that uses *User Interface* as it’s model and *Tree* and *Search Bar* as it’s controllers

## **Tree**

* This component displays an interactive graph showing the selected courses and lets the user click on those courses to change the selected course.  
* This component is on the client  
* Template

```tsx  
\<Tree selected\_course: string courses: {description: string, prereqs: string, credits: number, hours: string, grade: number, }\[\] change\_selected: void(selected: string) /\>  
```

* **View:** This displays the prereqs and dependents from the *User Interface*.  
* **Controller:** Clicking on courses changes what course is selected

## **Search Bar**

* This component lets the user search for and select a course. This is a view for the *Server* model as it needs a list of all courses to do autocompletion  
* This component is on the client

```tsx  
\<SearchBar selected\_course: string change\_selected: void(selected: string) /\>  
```

* **Controller:** This controls the tree and the panel by changing the selected course

  # 

# **Graph Creation and Formatting**

## **Graph**

* **Server**  
* This component is an ADT that contains each course as a vertex  
* This component will need to communicate with the Database component to retrieve every course with its information  
* **Model (MVC 1):** This component acts as a model for the Server where the API will call the methods below  
* **View (MVC 2):** This component views the Database component to receive each course

```java  
public class Graph {  
    public Graph(JSON Object) {  
    }

    public String\[\] GetNames() {  
        //TODO: Complete implementation  
        return names;  
    }

    public CourseVertex GetCourse(String) {  
        //TODO: Complete implementation  
        return course;  
    }

    public String\[\] GetCorequisites(String) {  
        //TODO: Complete implementation  
        return corequisites;  
    }

    public String\[\] GetPrerequisites(String) {  
        //TODO: Complete implementation  
        return prerequisites;  
    }

    public String\[\] GetDependents(String) {  
        //TODO: Complete implementation  
        return dependents;  
    }  
}  
```

# **Precompute \- Data Collection** 

## **Database**

* **Server**  
* **Model (MVC 2):** The Database acts as a model when it gets data read from the graph in MVC 2  
* **View (MVC 3):** The Database acts as a view when it collects and stores all data from the Data Formatter  
* Stores Literals from the Data Formatter as a JSON

	``` JSON  
	{  
“courses”: \[  
{   
“course\_id”: “CPEN 221”,   
“course\_name”: “Software Construction I”,   
“course\_structure”: “\[3-2-1\*\]”,  
“prerequisites”: \[“APSC 160”\],   
“dependencies”: \[“CPEN 212”, “CPEN 331” \],  
“Average\_grade”: “75%”   
}  
\]  
	}  
	```

## **Data Formatter**

* **Server**  
* **Controller (MVC 3):** The data formatter will receive *data* from the webscraper and API models and format it into a Json File  
    
  ``` java  
  public class Formatter { 	  
    
  	public Formatter(API\_Data.txt, Courses.txt) {  
  		// Store each text file as a String  
  	}  
    
  	public List Format(course) {  
  		// TODO Replace with actual algorithm    
  		return formattedList;  
  	}  
    
  	public void storeCourseInformation() {  
  		// write all course descriptions to a JSON  
  	}  
  }  
  ```  
  


  ## **Web Scraper** 

* **Server**  
* **Model (MVC 3):** Connects to data formatter   
* Parses course descriptions from ubc calendar website as plain text and stores them to a text file  
    
  ``` Java  
  public class Scraper { // Utility Class  
   	  
  	public String scrapeCourseDescription(website URL) {  
  		// TODO Replace with actual algorithm    
  		return courseDescription;  
  	}  
    
  	public void storeCourseDescriptions(file) {  
  		// write all course descriptions to a file  
  	}  
  }  
  ```

  ## 

  ## **Github Actions for Failed Text \-\> Github Issue**

* **Server**  
* **Controller (MVC 3):** Creates list of failed parse items  
* Though github actions, that will, given a list of failed to parse items in a text file create github issues that can be fixed by manually 

``` yaml   
on: \[push\] \# run the code on a push  
\# . . .   
   
run: \# read the file   
\# . . .   
gh issue create \# using the github CLI  
```  
[GitHub CLI](https://cli.github.com/manual/)  
[Actions with GitHub CLI](https://docs.github.com/en/actions/writing-workflows/choosing-what-your-workflow-does/using-github-cli-in-workflows)

## 

## **API Model**

* **Server**  
* **Model (MVC 3):** Connects to data formatter   
* Accesses Third Party APIs containing other information we may want to add (i.e. UBCgrades, etc.)  
    
  ``` Java  
  public String getAverageCourseGrade(course name) {  
   	// TODO Replace with actual algorithm    
  return averageGrade.toString();  
  }  
  ```
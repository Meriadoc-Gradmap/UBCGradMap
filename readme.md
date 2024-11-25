<p align="center"> <img src="https://github.com/CPEN-221-2024/project-meriadoc-gradmap/blob/main/images/full_logo.svg" alt="drawing" width="900"/> </p>

# **🎓Team Name: GradMap**

## **🗺️Mission Statement**

Helping students map their future!

## **❓Problem Description**

Recently, a problem faced by UBC students was caused by the transition from the Student Service Centre (SSC) to Workday. A key feature that was lost during this transition was the Degree Navigator tool. This tool provided a structured way for students to track their degree progress and understand how courses fit into their graduation requirements. Currently, Workday has an academic progress tool, however it is missing important information like the ability to view pre-requisite courses easily and the ability to plan courses multiple years in advance.

Many students like to plan their courses several years in advance and without a proper tool, long-term academic planning becomes difficult and error prone.


## **🧠Credits** 
- Ben Newington: Project Manager
  -  Developed the course graph
  -  Minor improvements to data collection
  -  Product demo
  -  Worked with all group members to integrate components together
- Tian Chen: Designer
- Iain Griesdale: Designer
  -  Develoepd API to interface from the frontend to the backend
  -  Setup Dockerfile and compose for easy deployment
  -  Minor frontend styling development to improve UI
  -  Worked with all group members to integrate components together
- Finn Bainbridge: Developer   
- William Banquier: Developer   

## **✨Features**
- Visual graph of every UBCV course.
- Search for individual course codes.
- Display course data including:
    - Name
    - Credits
    - Schedule for course hours
    - Past year course average
    - Description
- Visual representation of course averages with colour.
- Directed graph to represent prerequisites, corequisites, and dependants.
- Navigate the graph by clicking on course nodes.

## **📄Links/Sources**
- We use the API provided by https://ubcgrades.com to get previous years course average for each course.
- We gathered course information from https://vancouver.calendar.ubc.ca/.

## **🐳Running the Dockerfile**

### Install Docker
If you don't already have docker installed, you can follow one of these guides.
- https://docs.docker.com/get-started/get-docker/ Docker desktop avaiable for all operating systems
- https://docs.docker.com/engine/install/ Docker engine for linux

### Ensure Docker is installed
Type this into your powershell or command line and you should get a list of containers. For a fresh install this will be empty.
```shell
docker ps
```

### Clone this repository in your desired location
```shell
git clone https://github.com/CPEN-221-2024/project-meriadoc-gradmap
```

### Build the Docker image
This command goes into your powershell (on Windows) or terminal opened at the root directory of the cloned repository.
> If you need more details for this step, open the folder that you installed with git, you should see a bunch of folders and files including a Dockerfile file. Then you can right-click in this directory and find the open terminal button. That's where you should be able to put the command below.
>
> <p align="center"> <img src="https://github.com/CPEN-221-2024/project-meriadoc-gradmap/blob/main/images/open_in_terminal.png" alt="drawing" width="600"/> </p>

Type this command:
```shell
docker-compose up
```
<p align="center"> <img src="https://github.com/CPEN-221-2024/project-meriadoc-gradmap/blob/main/images/Terminal.png" alt="drawing" width="600"/> </p>
This step can take a while, possibly 5-10 min on slow internet or machines so get your cup of tea ☕.

### Access the local site
Once you see that the GraphApi has started, the local site is up.
<p align="center"> <img src="https://github.com/CPEN-221-2024/project-meriadoc-gradmap/blob/main/images/website_up.png" alt="drawing" width="600"/> </p>

Go to http://localhost:5173 to see GradMap!

You can also visit the site on your phone if you are connected to the same wifi at http://\<enter your ip here\>:5173

### Optional: Run the data scraper
If you want to run the data scraper to recollect course data including grades and ubc calendar information, you can first run 
```shell
docker ps
```
Locate gradmap in the list of containers, and using its ID or name run the scraper command.
```shell
docker exec project-meriadoc-gradmap-gradmap-1 scraper
# Alternatively with example ID
docker exec 74b3a910feab scraper
```
The scraper will run for a couple minutes and then the backend should automatically restart and you'll be able to see courses again.


### Debugging Docker
To rebuild the Docker container, you can run the command
```shell
docker-compose up --build
```
For example, if you were to pull new changes from the GitHub repository, you would have to rebuild the container. If you come across any issues open a GitHub issue and we will apply a fix that you can pull and rebuild from.

## **💣Running the Jacoco Test Report**
To see a report of the Jacoco testing, you can simply navigate to root of the directory and run the following:
```shell
./gradlew test
# or it also runs with build
./gradlew build
```
Then in the terminal you should see the printed test results. To see more in-depth results after running test or build, you can nagivate to `build/jacocoHtml/index.html`.


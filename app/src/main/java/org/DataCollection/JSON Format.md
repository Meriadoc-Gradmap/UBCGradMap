# Format Explanation - Tian

Below is an explanation of the format used to store course data.

### Notable items:
- Credits are stored as arrays since some courses offer varying credit levels
- Courses that appear in both prerequisites and postrequisites are corequisites courses
- cdf stands for "Credit/D/Fail" grading. Is simply a boolean for true or false
- schedule represents the [3-0-1*] thing (idk what else to call it).
- The others object simply contains all information not from the UBC Calendar website. So items like averages, medians, professors, course links, etc. will appear here
  - This list could be subject to change as we develop, hence why I thought to make it its own object

``` JSON
{
    "courses": [ // all courses stored in one array
      {
        "code": "CPEN-221",
        "name": "Software Construction I", 
        "credits": [4, 5], // Courses stored in array since some have multiple credit levels
        "description": "Software Design",
        "prerequisites": ["APSC-160"], 
        "postrequisites": ["CPEN-212", "CPEN-322", "CPEN-422"],
        "cdf": false,
        "schedule": {
          "lectures": 3,
          "alternating1": false,
          "labs": 2,
          "alternating2": false,
          "tutorials": 2,
          "alternating3": true
        },
        "others": {
          "average": 87,
          "professor": "Satish Gopalakrishnan"
          // And other stuff
        }
      },
      {
        "code": "PHYS-158",
        "name": "Introductory Physics for Engineers II",
        "credits": [3],
        "description": "Electricity",
        "prerequisites": ["PHYS-157", "MATH-101"],
        "postrequisites": ["MATH-101", "MECH-221"], 
        // Corequisites are courses that appear
        // in both prerequisites and postrequisites
        // i.e. Math 101 
        "cdf": false,
        "schedule": {
          "lectures": 3,
          "alternating1": false,
          "labs": 0,
          "alternating2": false,
          "tutorials": 1,
          "alternating3": false
        },
        "others": {
          "average": 75,
          "professor": "Marina Litinskaya"
          // And other stuff
        }
      }
    ]
  }
```
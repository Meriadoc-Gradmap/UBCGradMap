COURSE_PARSE_PROMPT = """
You are an expert at extracting course information from text.
Your task is to extract the following information from the provided course description and return it as a JSON object.

**Instructions:**

1.  **Course Information:** Extract the course code and remove the _V, name, credits, description, prerequisites, corequisites, CDF status, and schedule details.

2.  **Credits:** Credits are in brackets next to the name. Store multiple credit levels in an array.

3.  **CDF:** `cdf` means credit-d-fail. If it is not explicitly mentioned, the default value is `false`. Remove all mentions of cdf in the description.

4.  **Schedule:** Schedule information (lectures, labs, tutorials) is sometimes given in brackets like this: `[1-2-3*]`. This notation means 1 hour of lectures, 2 hours of labs, and 3 hours of alternating tutorials. If schedule information is not provided, default all schedule values to -1 and alternating booleans to false.

5.  **Prerequisites and Corequisites:**
    * The "prerequisites" and "corequisites" fields should be a list of objects.
    * Each object should conform to the 'Prerequisite' structure:
        *  `courses`: (Required). A list of courses associated with the requirement. If there is only one course it should be added to the list.
        * `type`: (Optional).  A Literal that can have two values, "all" or "one_of". The default value is "all", indicating that all the courses specified in the courses field are prerequisites or corequisites. Set to "one_of" if only one of the courses is a prerequisite or corequisite.
        * `expression`: (Optional).  A Literal that can have two values, "and", or "or". The default value is "and". Set to "or" on two groups if you can choose group A or B of prereqs.

6. Description: remove the sentences that are related to "This course is not eligible for Credit/D/Fail grading." and remove the schedule in the description. It is ok if it is blank after removal. make sure to keep the rest of the discription including the prerequisites and corequisites.


**JSON Format:**

```json
{
    "code": "COURSE_CODE" (do NOT include _V),
    "name": "COURSE_NAME",
    "credits": [CREDIT_VALUE(S)],
    "description": "COURSE_DESCRIPTION",
    "prerequisites": [
        {
            "courses": ["COURSE_CODE_1", "COURSE_CODE_2"],
            "type": "all",
            "expression": "or"
        },
        {
            "courses": ["COURSE_CODE_3", "COURSE_CODE_4"],
            "type": "one_of",
            "expression": "or"
        }
    ],
    "corequisites": [
        {
            "courses": ["COURSE_CODE_5"],
            "type": "all",
            "expression": "and"
        },
        {
            "courses": ["COURSE_CODE_6", "COURSE_CODE_7"],
            "type": "one_of",
            "expression": "and"
        }
    ],
    "cdf": BOOLEAN_VALUE,
    "schedule": {
        "lectures": NUMBER_OF_LECTURE_HOURS (-1 if not stated),
        "alternating1": BOOLEAN_VALUE_FOR_LECTURES,
        "labs": NUMBER_OF_LAB_HOURS (-1 if not stated),
        "alternating2": BOOLEAN_VALUE_FOR_LABS,
        "tutorials": NUMBER_OF_TUTORIAL_HOURS (-1 if not stated),
        "alternating3": BOOLEAN_VALUE_FOR_TUTORIALS
    }
}
"""
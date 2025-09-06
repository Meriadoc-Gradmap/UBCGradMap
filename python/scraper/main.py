import json
import time
from typing import List, Tuple, Optional, Literal

from dotenv import load_dotenv
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.messages import HumanMessage, SystemMessage
from pydantic import BaseModel, Field, ValidationError

from getdepartments import get_departments
from getcourses import get_courses_by_department
from prompt import COURSE_PARSE_PROMPT


load_dotenv()

llm = ChatGoogleGenerativeAI(model="gemini-2.0-flash-lite")


class Schedule(BaseModel):
    lectures: int = Field(default=-1, description="Number of lecture hours -1 if not stated")
    alternating1: bool = Field(default=False, description="Whether lectures alternate")
    labs: int = Field(default=-1, description="Number of lab hours -1 if not stated")
    alternating2: bool = Field(default=False, description="Whether labs alternate")
    tutorials: int = Field(default=-1, description="Number of tutorial hours -1 if not stated")
    alternating3: bool = Field(default=False, description="Whether tutorials alternate")

class Prerequisite(BaseModel):
    courses: List[str] = Field(default=[], description="List of courses. Can represent a single course.")
    type: Literal["one_of", "all"] = Field(default="all", description="Requirement type. If not specified, defaults to ALL") 
    expression: Literal["and", "or"] = Field(default="and", description="Operation between prerequisites. If not specified, defaults to and")


class Course(BaseModel):
    code: str = Field(..., description="Course code (e.g., CPEN-432)")
    name: str = Field(..., description="Course name (e.g., Real-time System Design)")
    credits: List[float] = Field(..., description="List of credit values")
    description: str = Field(..., description="Course description")
    prerequisites: List[Prerequisite] = Field(default=[], description="List of prerequisite course codes")
    corequisites: List[Prerequisite] = Field(default=[], description="List of corequisite course codes")
    cdf: bool = Field(default=False, description="Whether the course is Credit/D/Fail")
    schedule: Schedule = Field(..., description="Course schedule details")

def parse_course_info(course_string: str) -> Optional[Course]:
    """
    Parses a course information string into a Course object using the LLM.

    Args:
        course_string: The string containing course information.

    Returns:
        A Course object if parsing and validation are successful, otherwise None.
    """
    messages = [
        SystemMessage(content=COURSE_PARSE_PROMPT),
        HumanMessage(content=course_string)
    ]

    try:
        response = llm.invoke(messages)
        content = response.content
        content = content.replace("```json", "")
        content = content.replace("```", "")   
        try:
             data = json.loads(content)
        except json.JSONDecodeError:
            print(f"LLM output is not valid JSON:\n{content}")
            return None
        try:
            course = Course(**data)
            return course
        except ValidationError as e:
            print(f"Validation Error: {e}")
            return None


    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        return None

def fetch_and_save_courses(departments: List[str], course_file: str) -> List[Tuple[str, str]]:
    """Fetches course data from the web, saves it to a file, and returns the course list.

    Args:
        departments: A list of department URLs to scrape.
        course_file: The name of the file to save the course data to.

    Returns:
        A list of course tuples (title, description).  Returns an empty list on error.
    """
    courses: List[Tuple[str, str]] = []
    try:
        for department in departments:
            courses = courses + get_courses_by_department(department)
            print(department)
            time.sleep(.7)
        with open(course_file, "w") as f:
            for course in courses:
                json.dump(list(course), f)
                f.write('\n')
        print(f"Saved courses to {course_file}")
        return courses
    except RuntimeError as e:
        print(f"Error fetching courses: {e}")
        return []

def main():
    departments = get_departments()
    courses: List[Tuple[str, str]] = []
    course_file = "python/courses.json"

    try:
        with open(course_file, "r") as f:
            courses = [tuple(json.loads(line)) for line in f] 

        print(f"Loaded courses from {course_file}")
    except FileNotFoundError:
        print(f"{course_file} not found.  Fetching course data...")
        courses = fetch_and_save_courses(departments, course_file)
    except json.JSONDecodeError:
        print(f"Error decoding {course_file}. Fetching course data from web...")
        courses = fetch_and_save_courses(departments, course_file)

    print(len(courses))
    all_courses_data = [] 

    for course_title, course_description in courses:
        course_string = f"Title: {course_title}\nDescription: {course_description}"
        course = parse_course_info(course_string)

        if course:
            all_courses_data.append(course.model_dump())
            print(course.model_dump_json(indent=4))
            print(len(courses)-len(all_courses_data))
        else:
            print("Failed to parse course information.")

        time.sleep(.3)

    print(len(all_courses_data))
    with open("python/all_courses.json", "w") as f:
        json.dump(all_courses_data, f, indent=4) 

if __name__ == '__main__':
    main()
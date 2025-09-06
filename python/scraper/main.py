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

llm = ChatGoogleGenerativeAI(model="gemini-2.0-flash")


class Schedule(BaseModel):
    lectures: int = Field(default=0, description="Number of lecture hours")
    alternating1: bool = Field(default=False, description="Whether lectures alternate")
    labs: int = Field(default=0, description="Number of lab hours")
    alternating2: bool = Field(default=False, description="Whether labs alternate")
    tutorials: int = Field(default=0, description="Number of tutorial hours")
    alternating3: bool = Field(default=False, description="Whether tutorials alternate")

class Prerequisite(BaseModel):
    courses: List[str] = Field(default=[], description="List of courses. Can represent a single course.")
    type: Literal["one_of", "all"] = Field(default="all", description="Requirement type. If not specified, defaults to ALL") 
    expression: Literal["and", "or"] = Field(default="and", description="Operation between prerequisites. If not specified, defaults to and")


class Course(BaseModel):
    code: str = Field(..., description="Course code (e.g., CPEN-432)")
    name: str = Field(..., description="Course name (e.g., Real-time System Design)")
    credits: List[int] = Field(..., description="List of credit values")
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
        content = content.replace("```json", "") #Clean output from LLM
        content = content.replace("```", "")     #Clean output from LLM
        # First attempt: Load the content as JSON
        try:
             data = json.loads(content)
        except json.JSONDecodeError:
            print(f"LLM output is not valid JSON:\n{content}")
            return None
        # Validate the JSON against the Pydantic model
        try:
            course = Course(**data)
            return course
        except ValidationError as e:
            print(f"Validation Error: {e}")
            return None


    except Exception as e:
        print(f"An unexpected error occurred: {e}")  # Catch-all for unexpected errors
        return None


def main():
    departments = get_departments()
    courses: List[Tuple[str, str]] = []
    for department in departments[1:2]:
        courses = courses + get_courses_by_department("https://vancouver.calendar.ubc.ca/course-descriptions/subject/cpenv")
        time.sleep(5)

    for course_title, course_description in courses:
        course_string = f"Title: {course_title}\nDescription: {course_description}"
        course = parse_course_info(course_string)

        if course:
            print(course.model_dump_json(indent=4))  # Use model_dump_json for pretty JSON
        else:
            print("Failed to parse course information.")

        time.sleep(5)
        
if __name__ == '__main__':
    main()
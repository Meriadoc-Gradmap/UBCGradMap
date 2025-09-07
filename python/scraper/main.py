import json
import time
from typing import List, Tuple, Optional, Literal
from concurrent.futures import ThreadPoolExecutor, as_completed 

from dotenv import load_dotenv
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.messages import HumanMessage, SystemMessage
from pydantic import BaseModel, Field, ValidationError

from getdepartments import get_departments
from getcourses import get_courses_by_department
from prompt import COURSE_PARSE_PROMPT, LEGACY_STRUCTURE
from grades import get_single_grade_by_course, get_all_course_grades

load_dotenv()

llm = ChatGoogleGenerativeAI(model="gemini-2.0-flash-lite")

OUTPUT_FORMAT = "LEGACY"

class Schedule(BaseModel):
    lectures: float = Field(default=-1, description="Number of lecture hours -1 if not stated")
    alternating1: bool = Field(default=False, description="Whether lectures alternate")
    labs: float = Field(default=-1, description="Number of lab hours -1 if not stated")
    alternating2: bool = Field(default=False, description="Whether labs alternate")
    tutorials: float = Field(default=-1, description="Number of tutorial hours -1 if not stated")
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

class L_Course(BaseModel):
    code: str = Field(..., description="Course code (e.g., CPEN-432)")
    name: str = Field(..., description="Course name (e.g., Real-time System Design)")
    credits: List[float] = Field(..., description="List of credit values")
    description: str = Field(..., description="Course description")
    prerequisites: List[str] = Field(default=[], description="List of prerequisite course codes")
    corequisites: List[str] = Field(default=[], description="List of corequisite course codes")
    cdf: bool = Field(default=False, description="Whether the course is Credit/D/Fail")
    schedule: Schedule = Field(..., description="Course schedule details")

def parse_course_info(course_strings: List[str]) -> List[Optional[Course]]:
    """
    Parses a list of course information strings into Course objects using the LLM.

    Args:
        course_strings: A list of strings, each containing course information.

    Returns:
        A list of Course objects if parsing and validation are successful, otherwise None.
        Returns a list the same size as the input with None if failed.
    """
    if OUTPUT_FORMAT == "LEGACY":
        messages = [
            SystemMessage(content=LEGACY_STRUCTURE),
            HumanMessage(content="\n\n".join(course_strings)) 
        ]
    else:
        messages = [
            SystemMessage(content=COURSE_PARSE_PROMPT),
            HumanMessage(content="\n\n".join(course_strings)) 
        ]

    try:
        response = llm.invoke(messages)
        content = response.content.replace("```json", "").replace("```", "")
        
        try:
            data = json.loads(content)
            if not isinstance(data, list):
                print(f"LLM output is not a list of JSON objects (first 100 chars): {content[:100]}...")
                return [None] * len(course_strings)
            
            parsed_courses = []
            for item in data:
                try:
                    if isinstance(item, dict):
                        if OUTPUT_FORMAT == "LEGACY":
                            course = L_Course(**item)
                        else:
                            course = Course(**item)
                        parsed_courses.append(course)
                    else:
                        print(f"LLM output item is not a dictionary, skipping: {item}")
                        parsed_courses.append(None) 
                except ValidationError as e:
                    print(f"Validation Error for item: {item}. Error: {e}")
                    parsed_courses.append(None)
            

            while len(parsed_courses) < len(course_strings):
                parsed_courses.append(None)
            return parsed_courses[:len(course_strings)]

        except json.JSONDecodeError:
            print(f"LLM output is not valid JSON:\n{content}")
            return [None] * len(course_strings)

    except Exception as e:
        print(f"An unexpected error occurred during LLM invocation: {e}")
        return [None] * len(course_strings)


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
            courses.extend(get_courses_by_department(department))
            print(f"Fetched courses from {department}")
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

def process_course_grade(course_data: dict) -> dict:
    """Helper function to fetch grade for a single course and add it to the course data."""
    course_code = course_data.get("code")
    if course_code:
        try:
            grade = get_single_grade_by_course(course_code=course_code.replace(" ", "-"))
            course_data["others"] = {"grade": grade}
            print(f"Fetched grade for {course_code}: {grade}")
        except Exception as e:
            print(f"Error fetching grade for {course_code}: {e}")
            course_data["others"] = {"grade": -1}
    else:
        course_data["others"] = {"grade": -1}
    return course_data

def main():
    departments = get_departments()
    print("Retrived Departments")
    courses: List[Tuple[str, str]] = []
    course_file = "python/courses.json"
    batch_size = 10
    max_parallel_batches = 120 # For LLM parsing
    max_parallel_grade_fetches = 75 # For grade fetching

    try:
        with open(course_file, "r") as f:
            courses = [tuple(json.loads(line)) for line in f]
        print(f"Loaded {len(courses)} courses from {course_file}")
    except FileNotFoundError:
        print(f"{course_file} not found. Fetching course data...")
        courses = fetch_and_save_courses(departments, course_file)
    except json.JSONDecodeError:
        print(f"Error decoding {course_file}. Fetching course data from web...")
        courses = fetch_and_save_courses(departments, course_file)

    if not courses:
        print("No courses to process. Exiting.")
        return

    print(f"Total courses to process: {len(courses)}")
    all_courses_data = []
    failed_courses = []

    batches_to_process = []
    for i in range(0, len(courses), batch_size):
        batch_courses_tuple = courses[i : i + batch_size] 
        course_strings = [f"Title: {title}\nDescription: {description}" for title, description in batch_courses_tuple]
        batches_to_process.append((i, course_strings)) 

    total_batches = len(batches_to_process)
    print(f"Starting to process {total_batches} batches with max {max_parallel_batches} parallel batches for course parsing.")

    with ThreadPoolExecutor(max_workers=max_parallel_batches) as executor:
        futures = {executor.submit(parse_course_info, course_strings): start_index 
                   for start_index, course_strings in batches_to_process}
        
        processed_batch_count = 0
        for future in as_completed(futures):
            original_start_index = futures[future]
            processed_batch_count += 1
            batch_number = (original_start_index // batch_size) + 1 

            try:
                parsed_courses = future.result()
                
                for course_in_batch_idx, course in enumerate(parsed_courses):
                    if course:
                        all_courses_data.append(course.model_dump())
                    else:
                        original_full_course_index = original_start_index + course_in_batch_idx
                        failed_courses.append(courses[original_full_course_index])
                        failed_course_title = courses[original_full_course_index][0] if original_full_course_index < len(courses) else "Unknown"
                        print(f"Failed to parse course at original index {original_full_course_index} (Title: '{failed_course_title}') in batch {batch_number}.")
                
                print(f"Finished processing batch {batch_number} of {total_batches}. Total batches completed: {processed_batch_count}")
            except Exception as exc:
                print(f'Batch starting at index {original_start_index} generated an exception: {exc}')
    
    # Attempt to re-parse failed courses
    if failed_courses:
        print(f"\nAttempting to re-parse {len(failed_courses)} failed courses...")
        print(failed_courses)
        reparsed_courses = parse_course_info([f"Title: {title}\nDescription: {description}" for title, description in failed_courses])
        for course in reparsed_courses:
            if course:
                all_courses_data.append(course.model_dump()) 
            else:
                print("Failed again on a course during re-parsing.")
    
    print(f"\nSuccessfully parsed {len(all_courses_data)} courses out of {len(courses)} total courses.")

    print("Getting grades")
    grades = get_all_course_grades()
    print("Done")
    for course in all_courses_data:
        if grades.get(course["code"]):
            course["others"] = {"grade": grades[course["code"]]}
        else:
            course["others"] = {"grade": -1}
    

    with open(f"data/COURSE_INFO.json", "w") as f:
        json.dump(all_courses_data, f, indent=4)


if __name__ == '__main__':
    main()
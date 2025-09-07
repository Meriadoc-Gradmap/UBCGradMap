import requests
import json
import math
from typing import Dict, Optional

# Assuming ROUND_VALUE for rounding to two decimal places, similar to the Java example.
ROUND_VALUE = 100.0

def get_single_grade_by_course(course_code: str) -> float:
    """
    Retrieves the average grade for a single course from the UBCgrades API,
    given its full course code (e.g., "CPSC-101").

    The method will return -1.0 if:
    - The department code extracted from the course code is invalid.
    - The specific course is not found within its department's data.
    - The course exists but has no average grade data.
    - There is an error connecting to the UBCgrades API.

    Since this method fetches data from a 3rd party source, there are no
    guarantees about the accuracy of the data fetched.

    Args:
        course_code: The full course code (e.g., "CPSC-101").

    Returns:
        The average grade of the course as a float, or -1.0 if not found/error.

    Raises:
        RuntimeError: If there is an issue parsing the JSON data from the API.
    """
    parts = course_code.split('-')
    if len(parts) != 2:
        # Invalid course code format
        return -1.0

    department = parts[0].strip().upper()
    course_number = parts[1].strip()

    if not department or not course_number:
        return -1.0

    data: str
    try:
        response = requests.get(f"https://ubcgrades.com/api/v3/course-statistics/UBCV/{department}")
        response.raise_for_status()  # Raise an exception for HTTP errors (4xx or 5xx)
        data = response.text
    except requests.exceptions.RequestException:
        # Catch connection errors (e.g., invalid department leading to 404, network issues)
        return -1.0

    grade_array: list
    try:
        grade_array = json.loads(data)
        if not isinstance(grade_array, list):
            raise json.JSONDecodeError("Expected a JSON array", data, 0)
    except json.JSONDecodeError as e:
        # This indicates a problem with the API's response format itself
        raise RuntimeError(f"There was an issue parsing the data from the UBCgrades API for department {department}: {e}")

    for item in grade_array:
        if not isinstance(item, dict):
            continue

        subject = item.get("subject")
        course = item.get("course")

        # Check if the current item matches our target course
        if subject == department and course == course_number:
            average_value = item.get("average")
            grade: float

            if average_value == "" or average_value is None:
                grade = -1.0
            else:
                try:
                    average_float = float(average_value)
                    grade = round(average_float * ROUND_VALUE) / ROUND_VALUE
                except (ValueError, TypeError):
                    # If 'average' is not a valid number, treat as no grade data
                    grade = -1.0
            return grade

    # If the loop finishes, the course was not found in the department's data
    return -1.0

def get_all_course_grades() -> Dict[str, float]:
    """
    Retrieves the average grade for all courses in all departments from the UBCgrades API
    and returns them in a dictionary.

    The function makes API calls to first get all subject codes, and then for each
    subject, retrieves all course statistics. This is more efficient than fetching
    individual course grades one by one.

    Returns:
        A dictionary where keys are course codes (e.g., "CPSC-101") and values are
        their average grades as floats. Courses with no valid grade data or
        encountering errors during fetching will have a value of -1.0.

    Raises:
        RuntimeError: If there is an issue parsing the JSON data from the API
                      when fetching subjects or course statistics.
    """
    all_grades: Dict[str, float] = {}
    campus = "UBCV" # Assuming UBC Vancouver campus based on the provided example.

    # 1. Get all distinct subjects (departments)
    subjects_data: str
    try:
        subjects_response = requests.get(f"https://ubcgrades.com/api/v3/subjects/{campus}")
        subjects_response.raise_for_status()
        subjects_data = subjects_response.text
    except requests.exceptions.RequestException as e:
        print(f"Error fetching subjects from API: {e}")
        return {}

    subjects_list: list
    try:
        subjects_list = json.loads(subjects_data)
        if not isinstance(subjects_list, list):
            raise json.JSONDecodeError("Expected a JSON array for subjects", subjects_data, 0)
    except json.JSONDecodeError as e:
        raise RuntimeError(f"There was an issue parsing the subjects data from the UBCgrades API: {e}")

    # Iterate through each subject to get course statistics
    for subject_item in subjects_list:
        if not isinstance(subject_item, dict) or "subject" not in subject_item:
            continue
        department = subject_item["subject"]

        course_statistics_data: str
        try:
            # 2. Fetch all course statistics for the current department
            dept_courses_response = requests.get(f"https://ubcgrades.com/api/v3/course-statistics/{campus}/{department}")
            dept_courses_response.raise_for_status()
            course_statistics_data = dept_courses_response.text
        except requests.exceptions.RequestException as e:
            # Log the error but continue to the next department
            print(f"Warning: Could not fetch course statistics for department {department}: {e}")
            continue

        dept_grade_array: list
        try:
            dept_grade_array = json.loads(course_statistics_data)
            if not isinstance(dept_grade_array, list):
                raise json.JSONDecodeError(f"Expected a JSON array for department {department} courses", course_statistics_data, 0)
        except json.JSONDecodeError as e:
            raise RuntimeError(f"Issue parsing course statistics for department {department}: {e}")

        # 3. Populate the dictionary with course codes and grades
        for course_item in dept_grade_array:
            if not isinstance(course_item, dict):
                continue

            subject_code = course_item.get("subject")
            course_number = course_item.get("course")
            average_value = course_item.get("average")

            if subject_code and course_number:
                course_code = f"{subject_code}-{course_number}"
                grade: float

                if average_value == "" or average_value is None:
                    grade = -1.0
                else:
                    try:
                        average_float = float(average_value)
                        grade = round(average_float * ROUND_VALUE) / ROUND_VALUE
                    except (ValueError, TypeError):
                        grade = -1.0
                all_grades[course_code] = grade
    
    return all_grades


if __name__ == "__main__":
    print("--- Retrieving single course grades ---")

    # Valid course example
    cpsc_110_grade = get_single_grade_by_course("CPSC-110")
    print(f"Average grade for CPSC-110: {cpsc_110_grade}")

    # Another valid course example
    math_100_grade = get_single_grade_by_course("MATH-100")
    print(f"Average grade for MATH-100: {math_100_grade}")

    # Course that likely doesn't exist
    non_existent_course_grade = get_single_grade_by_course("CPSC-999")
    print(f"Average grade for CPSC-999: {non_existent_course_grade}")

    # Invalid department code (will result in connection error or empty array from API)
    invalid_dept_grade = get_single_grade_by_course("XXXX-101")
    print(f"Average grade for XXXX-101: {invalid_dept_grade}")

    # Course with potentially no grade data (or a course that genuinely doesn't exist)
    soci_grades = get_single_grade_by_course("SOCI-101")
    print(f"Average grade for SOCI-101: {soci_grades}")

    # Malformed input
    malformed_grade = get_single_grade_by_course("CPSC101")
    print(f"Average grade for 'CPSC101' (malformed): {malformed_grade}")

    malformed_grade_empty = get_single_grade_by_course("-101")
    print(f"Average grade for '-101' (malformed): {malformed_grade_empty}")

    try:
        print("\n--- Testing RuntimeError (requires a malformed API response) ---")
        pass # This part is hard to test without mocking, but the logic is there.
    except RuntimeError as e:
        print(f"Caught expected RuntimeError: {e}")

    print("\n--- Retrieving all course grades ---")
    all_course_grades_dict = get_all_course_grades()
    if all_course_grades_dict:
        print(f"Successfully retrieved grades for {len(all_course_grades_dict)} courses.")
        # Print a few examples
        print("Example grades:")
        count = 0
        for course_code, grade in all_course_grades_dict.items():
            if count < 5:
                print(f"  {course_code}: {grade}")
                count += 1
            else:
                break
        print(f"Average grade for CPSC-110 (from all grades dict): {all_course_grades_dict.get('CPSC-110', 'Not Found')}")
        print(f"Average grade for MATH-100 (from all grades dict): {all_course_grades_dict.get('MATH-100', 'Not Found')}")
    else:
        print("Failed to retrieve any course grades.")
import requests
import json
import math
from typing import Optional

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
        # print(f"Warning: Invalid course code format '{course_code}'. Expected 'DEPT-NUM'.")
        return -1.0

    department = parts[0].strip().upper()
    course_number = parts[1].strip()

    if not department or not course_number:
        # print(f"Warning: Department or course number missing in '{course_code}'.")
        return -1.0

    data: str
    try:
        response = requests.get(f"https://ubcgrades.com/api/v3/course-statistics/UBCV/{department}")
        response.raise_for_status()  # Raise an exception for HTTP errors (4xx or 5xx)
        data = response.text
    except requests.exceptions.RequestException:
        # Catch connection errors (e.g., invalid department leading to 404, network issues)
        # For a single grade, returning -1.0 indicates "not found" or "unavailable"
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

# Example usage:
if __name__ == "__main__":
    print("--- Retrieving single course grades ---")

    # Valid course example
    cpsc_110_grade = get_single_grade_by_course("CPSC-110")
    print(f"Average grade for CPSC-110: {cpsc_110_grade}") # Expected: a float like 76.0

    # Another valid course example
    math_100_grade = get_single_grade_by_course("MATH-100")
    print(f"Average grade for MATH-100: {math_100_grade}") # Expected: a float like 68.0

    # Course that likely doesn't exist
    non_existent_course_grade = get_single_grade_by_course("CPSC-999")
    print(f"Average grade for CPSC-999: {non_existent_course_grade}") # Expected: -1.0

    # Invalid department code (will result in connection error or empty array from API)
    invalid_dept_grade = get_single_grade_by_course("XXXX-101")
    print(f"Average grade for XXXX-101: {invalid_dept_grade}") # Expected: -1.0

    # Course with potentially no grade data (or a course that genuinely doesn't exist)
    # This might vary, but if 'average' is null/empty, it should be -1.0
    soci_grades = get_single_grade_by_course("SOCI-101")
    print(f"Average grade for SOCI-101: {soci_grades}")

    # Malformed input
    malformed_grade = get_single_grade_by_course("CPSC101")
    print(f"Average grade for 'CPSC101' (malformed): {malformed_grade}") # Expected: -1.0

    malformed_grade_empty = get_single_grade_by_course("-101")
    print(f"Average grade for '-101' (malformed): {malformed_grade_empty}") # Expected: -1.0

    try:
        # Example of a scenario that would raise RuntimeError (e.g., malformed JSON response)
        # This is hard to test without mocking the requests library to return bad JSON.
        # For demonstration, let's just show how it would be caught if it happened.
        print("\n--- Testing RuntimeError (requires a malformed API response) ---")
        # In a real scenario, if the UBCgrades API returned `{"error": "something"}`
        # instead of a list of course stats, this would trigger the RuntimeError.
        # For now, we simulate by passing bad data directly if we could.
        # As it stands, the current `requests.get` won't easily yield a malformed list.
        # If you were to mock `requests.get` to return `response.text = '{"not_a_list": true}'`,
        # then `json.loads` would succeed, but `isinstance(grade_array, list)` would fail,
        # triggering the RuntimeError.
        pass
    except RuntimeError as e:
        print(f"Caught expected RuntimeError: {e}")
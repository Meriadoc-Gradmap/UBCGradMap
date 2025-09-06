import requests
from bs4 import BeautifulSoup
from typing import List, Tuple
import re
import time

def get_courses_by_department(url: str) -> List[Tuple[str, str]]:
    """
    Fetches a list of courses from a department page on the UBC course descriptions website.
    This URL must be from the UBC calendar website (i.e., it contains
    https://vancouver.calendar.ubc.ca/course-descriptions/subject/<DEPARTMENT-CODE-HERE>).
    Otherwise, the method will return an empty List. If the department code in the URL is
    incorrect, the method makes no guarantees about its output. Users should check their
    URLs are valid before usage of this method.

    Args:
        url: The URL of the department page containing course descriptions.

    Returns:
        A list of courses, each containing the course title and description, as a tuple.  (title, description)
    Raises:
        RuntimeError: If there is an error connecting to the URL.
    """

    if not "https://vancouver.calendar.ubc.ca/course-descriptions/subject/" in url:
        return []

    try:
        response = requests.get(url)
        response.raise_for_status()  # Raise HTTPError for bad responses
        doc = BeautifulSoup(response.content, 'html.parser')
    except requests.exceptions.RequestException as e:
        print(f"Error connecting to {url}: {e}. Sleeping for 10 seconds...")
        time.sleep(10)
        try:
            response = requests.get(url)
            response.raise_for_status()  # Raise HTTPError for bad responses
            doc = BeautifulSoup(response.content, 'html.parser')
        except requests.exceptions.RequestException as e:
            raise RuntimeError(f"Failed to connect after retry: {e}")

    department_elements = doc.find_all("li")
    course_list: List[Tuple[str, str]] = []

    for element in department_elements:
        h3_tag = element.find("h3")
        p_tag = element.find("p")

        if h3_tag and p_tag:
            title = h3_tag.text.strip()
            desc = ""

            a_tag_in_p = p_tag.find("a")
            if a_tag_in_p:
                # Handle the case with a link in the description
                p_html = str(p_tag)  # Get the HTML of the <p> tag as a string

                # Split the HTML string around the <a> tag
                parts = re.split(r'<a[^>]*>|</a>', p_html)

                if len(parts) >= 3: #ensure there are pre link and post parts
                    pre = BeautifulSoup(parts[0], 'html.parser').get_text().strip()
                    link = a_tag_in_p.get("href").strip()
                    post = BeautifulSoup(parts[2], 'html.parser').get_text().strip()

                    desc = pre + link + post
                else:
                    desc = p_tag.get_text().strip()


            else:
                desc = p_tag.text.strip()

            course_list.append((title, desc))

    return course_list

if __name__ == '__main__':
    example_url = "https://vancouver.calendar.ubc.ca/course-descriptions/subject/cpenv"

    try:
        courses = get_courses_by_department(example_url)
        print(courses)


    except RuntimeError as e:
        print(f"Error: {e}")
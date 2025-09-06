import requests
from bs4 import BeautifulSoup
from typing import List

def get_departments() -> List[str]:
    """
    Fetches a list of department URLs from the UBC course descriptions page.
    This method should be called before attempting to retrieve course data to
    ensure the most up-to-date department info is fetched.

    Returns:
        A list of URLs for each department's course descriptions.
    Raises:
        RuntimeError: If there is an error connecting to the URL.
    """
    try:
        response = requests.get("https://vancouver.calendar.ubc.ca/course-descriptions/courses-subject")
        response.raise_for_status()  # Raise HTTPError for bad responses (4xx or 5xx)
        doc = BeautifulSoup(response.content, 'html.parser')
    except requests.exceptions.RequestException as e:
        raise RuntimeError(f"There was an issue connecting to the UBC Calendar website: {e}")

    department_elements = doc.find_all("li")  # Corrected to find_all

    url_list: List[str] = []
    for element in department_elements:
        a_tag = element.find("a")
        if a_tag:
            href = a_tag.get("href")
            if href and "https://vancouver.calendar.ubc.ca/course-descriptions/subject/" in href:
                url_list.append(href)

    return url_list

if __name__ == '__main__':
    try:
        department_urls = get_departments()
        for url in department_urls:
            print(url)  # Or do something else with the URLs
        print(f"Found {len(department_urls)} department URLs.")

    except RuntimeError as e:
        print(f"Error: {e}")
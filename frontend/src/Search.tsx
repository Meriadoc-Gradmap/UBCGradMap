import { useEffect, useRef, useState } from "react";
import { API_ENDPOINT } from "./Course";
import Fuse from "fuse.js";

/**
 * A floating search bar that allows the user to search for courses
 * with functional autocompletion.
 *
 * @param props.entered A funtion that gets called when a course is selected
 */
export default function Search(props: { entered: (a: string) => void }) {

  const [text, setText] = useState("");

  const [courses, setCourses] = useState<string[]>([]);
  const [focus, setFocus] = useState(false);

  const [selected, setSelected] = useState("");

  const fuseRef = useRef<Fuse<string> | null>(null);
  const [searchResults, setSearchResults] = useState<{ name: string, code: string }[]>([]);

  // Load all of the courses for the autocompletion
  useEffect(() => {
    fetch(API_ENDPOINT + "api/getallcourses").then(async (response) => {
      if (!response.ok) {
        console.error("Cannot access API!");
        return;
      }
      const json = await response.json();

      // Convert the courses to human-readable format
      let output = json.map((a: string) => { return a.replace("-", " ") });
      setCourses(output);

    });
  }, []);

  useEffect(() => {
    // When we get our course list build a Fuse object for searching
    fuseRef.current = new Fuse<string>(courses, { includeScore: false });
  }, [courses]);


  // Always bock when text changes
  useEffect(() => {
    if (fuseRef.current !== null) {
      // Search again
      let results = fuseRef.current.search(text);
      setSearchResults(results.map((res) => {
        return { name: res.item, code: res.item.replace(" ", "-") }
      }).filter((_v, idx) => idx < 5));
    }
  }, [text]);

  let keyPressed = (key: string, shiftKey: boolean, e: any) => {
    if (key == "Enter") {
      // Enter pressed
      if (searchResults.length > 0 && selected == "") {
        // If nothing's selected take the first search result
        let sel = searchResults[0];
        props.entered(sel.code);
        setText(sel.name);
        setFocus(false);
        setSelected("");
      }
      else if (searchResults.length > 0) {
        // Take the selected item
        let idx = searchResults.map((i) => i.code).indexOf(selected);
        let sel = searchResults[idx];
        props.entered(sel.code);
        setText(sel.name);
        setFocus(false);
        setSelected("");
      }
    }
    else if (key == "ArrowDown" || (key == "Tab" && !shiftKey)) {
      // Down arrow pressed
      let idx = searchResults.map((i) => i.code).indexOf(selected);
      if ((selected == "" || idx == -1) && searchResults.length > 0) {
        setSelected(searchResults[0].code);
      }
      else {
        idx = (idx + 1) % searchResults.length;
        setSelected(searchResults[idx].code);
      }

      // This makes the tab key work
      e.preventDefault();
    }
    else if (key == "ArrowUp" || (key == "Tab" && shiftKey)) {
      // Up arrow pressed
      let idx = searchResults.map((i) => i.code).indexOf(selected);
      if ((selected == "" || idx == -1) && searchResults.length > 0) {
        setSelected(searchResults[searchResults.length - 1].code);
      }
      else {
        idx = (idx - 1 + searchResults.length) % searchResults.length;
        setSelected(searchResults[idx].code);
      }

      // This makes the tab key work
      e.preventDefault();
    }
  }

  return (
    <div className="fixed top-16 lg:top-0 left-0 right-0 m-5 bg-white shadow-lg rounded-xl p-3 lg:w-1/4 rounded-box-gradient"
      onFocus={() => { setFocus(true) }} onBlur={() => { 
        // Javascript is stupid so we have to wait a bit
        // So it doesn't remove the popup before the use can click on it
        setTimeout(() => { setFocus(false) }, 100)
      }}>
      <div className="w-full flex flex-col" >
        <div className="w-full flex flex-row">
          <input className="appearance-none bg-transparent border-gray-60 border-b-2 flex flex-grow text-gray-700 mr-3 py-1 px-2 leading-tight focus:outline-none"
            onChange={(e) => {
              setText(e.target.value);
              setFocus(true);
            }} value={text}
            placeholder="Enter a course..."
            onKeyDown={(e) => { keyPressed(e.key, e.shiftKey, e) }}
          />
          <button className="lg:hidden flex flex-shrink py-2">
            <svg className="fill-current w-4 h-4 mr-2" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path d="M416 208c0 45.9-14.9 88.3-40 122.7L502.6 457.4c12.5 12.5 12.5 32.8 0 45.3s-32.8 12.5-45.3 0L330.7 376c-34.4 25.2-76.8 40-122.7 40C93.1 416 0 322.9 0 208S93.1 0 208 0S416 93.1 416 208zM208 352a144 144 0 1 0 0-288 144 144 0 1 0 0 288z" /></svg>
          </button>
        </div>
        {focus ? <div className="flex w-full flex-col">
          {
            searchResults.map((res) =>
              <button key={res.code} className={`p-3 border-b-2 border-b-gray-50 ${selected == res.code ? "bg-gray-100" : ""}`}
                onPointerDown={() => {
                  setText(res.name);
                  setFocus(false);
                  props.entered(res.code);
                }}
                onMouseEnter={() => {
                  setSelected(res.code);
                }}
                onMouseLeave={() => {
                  setSelected("");
                }}
              >
                {res.name}
              </button>
            )
          }
        </div> : <span></span>}
      </div>
    </div>
  )
}

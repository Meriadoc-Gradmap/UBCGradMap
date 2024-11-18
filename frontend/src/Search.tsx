import { useEffect, useRef, useState } from "react";
import { API_ENDPOINT } from "./Course";
import Fuse from "fuse.js";

export default function Search(props: {entered: (a: string)=>void}) {

  const [text, setText] = useState("");

  const [courses, setCourses] = useState<string[]>([]);
  const [focus, setFocus] = useState(false);

  const [selected, setSelected] = useState("");

  const fuseRef = useRef<Fuse<string> | null>(null);
  const [searchResults, setSearchResults] = useState<{name: string, code: string}[]>([]);

  useEffect(() => {
    fetch(API_ENDPOINT + "/api/getallcourses").then(async (response) => {
      if (!response.ok) {
        console.error("Cannot access API!");
        return;
      }
      const json = await response.json();

      // TODO: Perhaps we should do some validation here...
      let output = json.map((a: string) => {return a.replace("-", " ")});
      setCourses(output);

    });
  }, []);

  useEffect(() => {
      fuseRef.current = new Fuse<string>(courses, {includeScore: false});
  }, [courses]);


  useEffect(() => {
    if (fuseRef.current !== null) {
      let results = fuseRef.current.search(text);
      setSearchResults(results.map((res) => {
        return {name: res.item, code: res.item.replace(" ", "-")}
      }).filter((_v, idx) => idx < 5));
    }
  }, [text]);

  let keyPressed = (key: string, shiftKey: boolean, e: KeyboardEvent) => {
    if (key == "Enter") {
      // Enter pressed
      if (searchResults.length > 0 && selected == "") {
        let sel = searchResults[0];
        props.entered(sel.code);
        setText(sel.name);
        setFocus(false);
      }
      else if (searchResults.length > 0) {
        let idx = searchResults.map((i) => i.code).indexOf(selected);
        let sel = searchResults[idx];
        props.entered(sel.code);
        setText(sel.name);
        setFocus(false);
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

      e.preventDefault();
    }
  }

  // {/*onBlur={()=>{setFocus(false)}} onFocus={()=>{setFocus(true)}}*/}
  return (
    <div className="fixed top-0 left-0 right-0 m-5 bg-white shadow-md rounded p-3 lg:w-1/4"
        onFocus={() => {setFocus(true)}} onBlur={() => {setTimeout(() => {setFocus(false)}, 100)}}>
      <div className="w-full flex flex-col" >
        <div className="w-full flex flex-row">
          <input className="appearance-none bg-transparent border-gray-60 border-b-2 flex flex-grow text-gray-700 mr-3 py-1 px-2 leading-tight focus:outline-none"
            onChange={(e) => {setText(e.target.value)}} value={text} 
            placeholder="CPEN 211" 
            onKeyDown={(e) => {keyPressed(e.key, e.shiftKey, e)}}
          />
          <button className="lg:hidden flex flex-shrink">
            <svg className="fill-current w-4 h-4 mr-2" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path d="M416 208c0 45.9-14.9 88.3-40 122.7L502.6 457.4c12.5 12.5 12.5 32.8 0 45.3s-32.8 12.5-45.3 0L330.7 376c-34.4 25.2-76.8 40-122.7 40C93.1 416 0 322.9 0 208S93.1 0 208 0S416 93.1 416 208zM208 352a144 144 0 1 0 0-288 144 144 0 1 0 0 288z"/></svg>
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
        </div>: <span></span>}
      </div>
    </div>
  )
}

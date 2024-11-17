import { useEffect, useRef, useState } from "react";
import { API_ENDPOINT } from "./Course";
import Fuse from "fuse.js";

export default function Search(props: {entered: (a: string)=>void}) {

  const [text, setText] = useState("");

  const [courses, setCourses] = useState<string[]>([]);
  const [focus, setFocus] = useState(false);

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
      }));
    }
  }, [text]);

  return (
    <div className="w-full flex flex-col" onBlur={()=>{setFocus(false)}} onFocus={()=>{setFocus(true)}}>
      <div className="w-full flex flex-row">
        <input className="appearance-none bg-transparent border-gray-60 border-b-2 flex flex-grow text-gray-700 mr-3 py-1 px-2 leading-tight focus:outline-none"
          onChange={(e) => {setText(e.target.value)}} value={text} 
          placeholder="CPEN 211"  />

        <button className="lg:hidden flex flex-shrink">
          <svg className="fill-current w-4 h-4 mr-2" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path d="M416 208c0 45.9-14.9 88.3-40 122.7L502.6 457.4c12.5 12.5 12.5 32.8 0 45.3s-32.8 12.5-45.3 0L330.7 376c-34.4 25.2-76.8 40-122.7 40C93.1 416 0 322.9 0 208S93.1 0 208 0S416 93.1 416 208zM208 352a144 144 0 1 0 0-288 144 144 0 1 0 0 288z"/></svg>
        </button>
      </div>
      {focus ? <div className="flex w-full flex-col">
        {
          searchResults.map((res) => 
            <button key={res.code} className="p-3 border-b-2 border-b-gray-50 hover:bg-gray-50"
                onClick={() => {
                  setText(res.name);
                  setFocus(false);
                  props.entered(res.code);
                }}
            >
              {res.name}
            </button>
          )
        }
      </div>: <span></span>}
    </div>
  )
}

import { Course } from "./Course";

export default function Panel(props: { currentCourse: Course | undefined }) {

  if (props.currentCourse == undefined) {
    return <></>;
  }

  return (
    <div className="fixed bottom-0 left-0 right-0 lg:top-20 lg:left-0 lg:right-auto h-1/4 lg:h-auto lg:w-1/4 m-5 bg-white shadow-xl rounded-2xl p-3 overflow-y-auto overflow-x-wrap rounded-box">
      <h1 className="text-xl font-semibold ubc-blue">{props.currentCourse.code} - {props.currentCourse.name}</h1>
      <p className="text-gray-500 my-1">
        {props.currentCourse.credits.length > 0 ? "Credits: " + props.currentCourse.credits.join("-") : ""} &nbsp;
        {props.currentCourse.schedule.lectures != -1 ?
          <><br />{" Lecture: " + props.currentCourse.schedule.lectures + "h"}</> : ""} &nbsp;
        {props.currentCourse.schedule.labs != -1 ?
          " Labs: " + props.currentCourse.schedule.labs + "h" : ""} &nbsp;
        {props.currentCourse.schedule.tutorials != -1 ?
          " Tutorials: " + props.currentCourse.schedule.tutorials + "h" : ""}
        {props.currentCourse.others.grade != -1 ? <><br />Average: {props.currentCourse.others.grade}%</> : ""}
        {props.currentCourse.cdf ? "" : (<><br /> <span className="italic">This course is ineligible for Credit/D/Fail</span></>)}
      </p>
      <p className="">{props.currentCourse.description}</p>
    </div>
  )
}

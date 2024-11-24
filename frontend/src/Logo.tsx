import imgUrl from "./assets/full_logo.svg"

/**
 * A cool floating logo that links to our github repo
 */
export default function Logo() {
  return <div className="fixed top-0 sm:left-auto lg:right-0 lg:h-auto m-5 main-logo">
    <a href="https://github.com/CPEN-221-2024/project-meriadoc-gradmap" target="_blank">
      <img src={imgUrl} alt="gradmap logo" />
    </a>
  </div>
}

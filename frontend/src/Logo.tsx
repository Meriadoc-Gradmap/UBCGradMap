import imgUrl from "./assets/full_logo.svg"

export default function Logo() {
  return <div className="fixed top-0 sm:left-0 lg:right-0 w-40 lg:h-auto m-5">
    <img src={imgUrl} alt="gradmap logo" />
  </div>
}

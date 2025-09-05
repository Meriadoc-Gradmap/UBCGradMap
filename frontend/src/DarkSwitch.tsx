import Switch from "@mui/material/Switch";

const label = { inputProps: { 'aria-label': 'Switch demo' } };

function DarkSwitch() {
  return (
    <div className="fixed top-10 sm:left-auto lg:right-0 lg:h-auto m-5">
      <Switch {...label} />
    </div>
  )
}

export default DarkSwitch

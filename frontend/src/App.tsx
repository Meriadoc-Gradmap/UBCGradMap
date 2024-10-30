import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import CourseTree from './Tree'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <CourseTree></CourseTree>
    </>
  )
}

export default App

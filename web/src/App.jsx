import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <div>
        <a href="https://vite.dev" target="_blank">
          <img src={viteLogo} className="logo" alt="Vite logo" />
        </a>
        <a href="https://react.dev" target="_blank">
          <img src={reactLogo} className="logo react" alt="React logo" />
        </a>
      </div>
      <h1>Vite + React</h1>
      <div className="card">
        <button onClick={() => setCount((count) => count + 1)}>count is {count}</button>
        <p>
          Edit <code>src/App.jsx</code> and save to test HMR
        </p>
      </div>
      <p className="bg-primary">bg-primary 장안의 화재 메인 컬러</p>
      <p className="bg-system-red">system-red</p>
      <p className="bg-system-yellow">system-yellow</p>
      <p className="bg-system-green">system-green</p>
      <p className="bg-system-blue">system-blue</p>
      <p className="bg-gray-600">gray600</p>
      <p className="bg-gray-500">gray500</p>
      <p className="bg-gray-400">gray400</p>
      <p className="bg-gray-300">gray300</p>
      <p className="bg-gray-200">gray200</p>
      <p className="bg-gray-100">gray100</p>
    </>
  )
}

export default App

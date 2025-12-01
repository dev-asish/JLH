import { useState } from 'react'
import './App.css'
import { API_BASE_URL } from './config'
import { getAuthToken, handleAuthError } from './authUtils'

function JavaCompiler() {
  const [code, setCode] = useState('')
  const [output, setOutput] = useState('')
  const [errors, setErrors] = useState('')
  const [loading, setLoading] = useState(false)

  const handleRun = async () => {
    const token = getAuthToken()
    if (!token) {
      setErrors('You must be logged in to use the compiler.')
      return
    }

    setLoading(true)
    setOutput('')
    setErrors('')

    try {
      const response = await fetch(`${API_BASE_URL}/compiler/run`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ code })
      })

      if (handleAuthError(response, setErrors)) {
        setLoading(false)
        return
      }

      const data = await response.json()

      if (data.output) {
        setOutput(data.output)
      }
      if (data.errors) {
        setErrors(data.errors)
      }
    } catch (err) {
      setErrors("Backend unreachable or unauthorized. Please login again.")
      console.error('Compiler error:', err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="compiler-container">
      <h2>Java Compiler</h2>
      <p className="compiler-description">
        Write and run Java code. Make sure your code includes a public class with a main method.
      </p>

      <div className="compiler-editor">
        <label htmlFor="code-editor">Java Code:</label>
        <textarea
          id="code-editor"
          value={code}
          onChange={(e) => setCode(e.target.value)}
          placeholder="Enter your Java code here..."
          rows={15}
          disabled={loading}
          className="code-textarea"
        />
      </div>

      <div className="compiler-actions">
        <button 
          onClick={handleRun} 
          disabled={loading || !code.trim()}
          className="run-button"
        >
          {loading ? 'Running...' : 'Run Code'}
        </button>
      </div>

      {output && (
        <div className="compiler-output">
          <h3>Output:</h3>
          <pre className="output-text">{output}</pre>
        </div>
      )}

      {errors && (
        <div className="compiler-errors">
          <h3>Errors:</h3>
          <pre className="error-text">{errors}</pre>
        </div>
      )}
    </div>
  )
}

export default JavaCompiler




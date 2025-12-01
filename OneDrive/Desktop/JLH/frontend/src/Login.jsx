import { useState } from 'react'
import './Login.css'
import { API_BASE_URL } from './config'
import { clearAuth } from './authUtils'

function Login({ onLoginSuccess, onRegisterClick }) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    if (!username.trim() || !password.trim()) {
      setError('Please enter both username and password')
      return
    }

    setLoading(true)
    setError('')

    try {
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Login failed')
      }

      // Save token, username, and role to localStorage
      localStorage.setItem('authToken', data.token)
      localStorage.setItem('token', data.token) // Keep for backward compatibility
      localStorage.setItem('username', data.username)
      localStorage.setItem('role', data.role || 'USER')

      // Call the success callback to redirect to main app
      if (onLoginSuccess) {
        onLoginSuccess()
      }
    } catch (err) {
      if (err.message && err.message.includes('fetch')) {
        setError("Backend unreachable or unauthorized. Please login again.")
      } else {
        setError(err.message || 'Login failed. Please check your credentials.')
      }
      console.error('Login error:', err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-container">
      <div className="login-box">
        <h1>☕ Java Learning Hub</h1>
        <h2>Welcome Back</h2>
        
        {error && (
          <div className="login-error">
            <p>{error}</p>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="login-form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter your username"
              disabled={loading}
              autoComplete="username"
            />
          </div>

          <div className="login-form-group">
            <label htmlFor="password">Password</label>
            <div className="password-wrapper">
              <input
                type={showPassword ? "text" : "password"}
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your password"
                disabled={loading}
                autoComplete="current-password"
                inputMode="text"
                pattern=".*"
              />
              <button
                type="button"
                className="toggle-password-btn"
                onClick={() => setShowPassword(!showPassword)}
                aria-pressed={showPassword}
                aria-label={showPassword ? "Hide password" : "Show password"}
                tabIndex={0}
              >
                {showPassword ? "👁️" : "👁️‍🗨️"}
              </button>
            </div>
          </div>

          <button type="submit" className="login-button" disabled={loading}>
            {loading ? 'Logging in...' : 'Sign In'}
          </button>
        </form>

        <div>
          <p>
            Don't have an account?{' '}
            <button
              type="button"
              onClick={onRegisterClick}
            >
              Register here
            </button>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Login


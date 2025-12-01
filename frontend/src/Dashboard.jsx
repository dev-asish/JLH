import { useState, useEffect } from 'react'
import './App.css'
import { API_BASE_URL } from './config'
import { getAuthToken, handleAuthError } from './authUtils'

function Dashboard() {
  const [dashboardData, setDashboardData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    fetchDashboard()
  }, [])

  const fetchDashboard = async () => {
    const token = getAuthToken()
    if (!token) {
      // Clear any old tokens and redirect
      window.location.href = '/'
      return
    }

    setLoading(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE_URL}/dashboard/me`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      if (handleAuthError(response, setError)) {
        setLoading(false)
        return
      }

      if (!response.ok) {
        throw new Error('Failed to fetch dashboard')
      }
      const data = await response.json()
      setDashboardData(data)
    } catch (err) {
      setError("Backend unreachable or unauthorized. Please login again.")
      console.error('Error fetching dashboard:', err)
    } finally {
      setLoading(false)
    }
  }

  const isNewUser = () => {
    if (!dashboardData) return true
    return dashboardData.quizzesTaken === 0 &&
           dashboardData.topicsViewed === 0 &&
           dashboardData.coursesVisited === 0 &&
           dashboardData.practiceAttempts === 0 &&
           !dashboardData.lastCompiledOutput &&
           !dashboardData.lastActive
  }

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A'
    try {
      const date = new Date(dateString)
      return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    } catch (e) {
      return dateString
    }
  }

  if (loading) {
    return <div className="loading">Loading dashboard...</div>
  }

  if (error) {
    return (
      <div className="error-message">
        <p>{error}</p>
        <button onClick={fetchDashboard} style={{ marginTop: '1rem' }}>
          Retry
        </button>
      </div>
    )
  }

  if (isNewUser()) {
    return (
      <div className="dashboard-container">
        <h2>📊 Dashboard</h2>
        <div className="dashboard-welcome">
          <div className="welcome-card">
            <h3>Welcome new learner 👋</h3>
            <p>Start your Java learning journey! Explore topics, take quizzes, practice coding, and use the compiler.</p>
          </div>
        </div>
        <div className="dashboard-stats-grid">
          <div className="stat-card">
            <div className="stat-icon">📚</div>
            <div className="stat-value">0</div>
            <div className="stat-label">Topics Viewed</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">🎓</div>
            <div className="stat-value">0</div>
            <div className="stat-label">Courses Visited</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">📝</div>
            <div className="stat-value">0</div>
            <div className="stat-label">Quizzes Taken</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">🎯</div>
            <div className="stat-value">0</div>
            <div className="stat-label">Practice Attempts</div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="dashboard-container">
      <h2>📊 Dashboard</h2>
      
      <div className="dashboard-stats-grid">
        <div className="stat-card">
          <div className="stat-icon">📝</div>
          <div className="stat-value">{dashboardData.quizzesTaken}</div>
          <div className="stat-label">Quizzes Taken</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">✅</div>
          <div className="stat-value">{dashboardData.totalCorrectAnswers}</div>
          <div className="stat-label">Total Correct Answers</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">🎯</div>
          <div className="stat-value">
            {dashboardData.lastQuizScore !== null 
              ? `${dashboardData.lastQuizScore.toFixed(1)}%` 
              : 'N/A'}
          </div>
          <div className="stat-label">Last Quiz Score</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">📚</div>
          <div className="stat-value">{dashboardData.topicsViewed}</div>
          <div className="stat-label">Topics Viewed</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">🎓</div>
          <div className="stat-value">{dashboardData.coursesVisited}</div>
          <div className="stat-label">Courses Visited</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">💻</div>
          <div className="stat-value">{dashboardData.practiceAttempts}</div>
          <div className="stat-label">Practice Attempts</div>
        </div>
      </div>

      <div className="dashboard-details">
        <div className="dashboard-section">
          <h3>Recent Activity</h3>
          <div className="activity-info">
            <p><strong>Last Active:</strong> {formatDate(dashboardData.lastActive)}</p>
            <p><strong>Account Created:</strong> {formatDate(dashboardData.createdAt)}</p>
          </div>
        </div>

        {dashboardData.lastCompiledOutput && (
          <div className="dashboard-section">
            <h3>Last Compiled Output</h3>
            <div className="compiled-output-preview">
              <pre>{dashboardData.lastCompiledOutput}</pre>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default Dashboard



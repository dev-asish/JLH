import { useState, useEffect } from 'react'
import './App.css'
import { API_BASE_URL } from './config'
import { getAuthToken, handleAuthError } from './authUtils'

function PracticePage({ questionId, onBack }) {
  const [question, setQuestion] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (questionId) {
      fetchQuestion(questionId)
    }
  }, [questionId])

  const fetchQuestion = async (id) => {
    const token = getAuthToken()
    if (!token) {
      setError('You must be logged in to view practice questions')
      return
    }

    setLoading(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE_URL}/practice/questions/${id}`, {
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
        throw new Error('Failed to fetch practice question')
      }
      const data = await response.json()
      setQuestion(data)
    } catch (err) {
      setError("Backend unreachable or unauthorized. Please login again.")
      console.error('Error fetching practice question:', err)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <div className="loading">Loading question...</div>
  }

  if (error) {
    return (
      <div>
        <div className="error-message">
          <p>{error}</p>
        </div>
        {onBack && (
          <button onClick={onBack} style={{ marginTop: '1rem' }}>
            Back to Practice Questions
          </button>
        )}
      </div>
    )
  }

  if (!question) {
    return (
      <div>
        <div className="empty-state">Question not found.</div>
        {onBack && (
          <button onClick={onBack} style={{ marginTop: '1rem' }}>
            Back to Practice Questions
          </button>
        )}
      </div>
    )
  }

  return (
    <div className="practice-page">
      <div className="practice-page-header">
        {onBack && (
          <button onClick={onBack} className="back-button">
            ← Back to Practice Questions
          </button>
        )}
        <div className="practice-meta">
          <span className={`practice-difficulty difficulty-${question.difficulty?.toLowerCase()}`}>
            {question.difficulty}
          </span>
        </div>
      </div>
      
      <h1 className="practice-page-title">{question.title}</h1>
      
      <div className="practice-content">
        <div className="practice-description-section">
          <h3>Description</h3>
          <p>{question.description}</p>
        </div>

        {question.sampleInput && (
          <div className="practice-example">
            <h3>Sample Input</h3>
            <pre className="example-code">{question.sampleInput}</pre>
          </div>
        )}

        {question.sampleOutput && (
          <div className="practice-example">
            <h3>Sample Output</h3>
            <pre className="example-code">{question.sampleOutput}</pre>
          </div>
        )}
      </div>
    </div>
  )
}

export default PracticePage




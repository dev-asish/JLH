import { useState, useEffect } from 'react'
import './App.css'
import { API_BASE_URL } from './config'
import { getAuthToken, handleAuthError } from './authUtils'

function PracticeList({ onQuestionSelect }) {
  const [questions, setQuestions] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    fetchQuestions()
  }, [])

  const fetchQuestions = async () => {
    const token = getAuthToken()
    if (!token) {
      setError('You must be logged in to view practice questions')
      return
    }

    setLoading(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE_URL}/practice/questions`, {
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
        throw new Error('Failed to fetch practice questions')
      }
      const data = await response.json()
      setQuestions(data)
    } catch (err) {
      setError("Backend unreachable or unauthorized. Please login again.")
      console.error('Error fetching practice questions:', err)
    } finally {
      setLoading(false)
    }
  }

  if (loading && questions.length === 0) {
    return <div className="loading">Loading practice questions...</div>
  }

  return (
    <div className="practice-container">
      <h2>Practice Questions</h2>
      <p className="practice-description">
        Test your Java programming skills with these coding challenges. Click on any question to view details.
      </p>
      
      {error && (
        <div className="error-message">
          <p>{error}</p>
        </div>
      )}

      {questions.length === 0 && !loading ? (
        <div className="empty-state">No practice questions available.</div>
      ) : (
        <div className="practice-list">
          {questions.map(question => (
            <div 
              key={question.id} 
              className="practice-card"
              onClick={() => onQuestionSelect && onQuestionSelect(question.id)}
              style={{ cursor: 'pointer' }}
            >
              <div className="practice-header">
                <h3 className="practice-title">{question.title}</h3>
                <span className={`practice-difficulty difficulty-${question.difficulty?.toLowerCase()}`}>
                  {question.difficulty}
                </span>
              </div>
              <p className="practice-description-preview">
                {question.description?.substring(0, 120)}...
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default PracticeList




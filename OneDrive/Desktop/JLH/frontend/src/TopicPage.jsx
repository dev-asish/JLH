import { useState, useEffect } from 'react'
import './App.css'
import { API_BASE_URL } from './config'
import { getAuthToken, handleAuthError } from './authUtils'

function TopicPage({ topicId, onBack }) {
  const [topic, setTopic] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (topicId) {
      fetchTopic(topicId)
    }
  }, [topicId])

  const fetchTopic = async (id) => {
    const token = getAuthToken()
    if (!token) {
      setError('You must be logged in to view topics')
      return
    }

    setLoading(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE_URL}/topics/${id}`, {
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
        throw new Error('Failed to fetch topic')
      }
      const data = await response.json()
      setTopic(data)
    } catch (err) {
      setError("Backend unreachable or unauthorized. Please login again.")
      console.error('Error fetching topic:', err)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <div className="loading">Loading topic...</div>
  }

  if (error) {
    return (
      <div>
        <div className="error-message">
          <p>{error}</p>
        </div>
        {onBack && (
          <button onClick={onBack} style={{ marginTop: '1rem' }}>
            Back to Topics
          </button>
        )}
      </div>
    )
  }

  if (!topic) {
    return (
      <div>
        <div className="empty-state">Topic not found.</div>
        {onBack && (
          <button onClick={onBack} style={{ marginTop: '1rem' }}>
            Back to Topics
          </button>
        )}
      </div>
    )
  }

  return (
    <div className="topic-page">
      <div className="topic-page-header">
        {onBack && (
          <button onClick={onBack} className="back-button">
            ← Back to Topics
          </button>
        )}
        <div className="topic-meta">
          <span className={`topic-difficulty difficulty-${topic.difficulty?.toLowerCase()}`}>
            {topic.difficulty}
          </span>
        </div>
      </div>
      
      <h1 className="topic-page-title">{topic.title}</h1>
      
      <div className="topic-content">
        <p>{topic.content}</p>
      </div>
    </div>
  )
}

export default TopicPage




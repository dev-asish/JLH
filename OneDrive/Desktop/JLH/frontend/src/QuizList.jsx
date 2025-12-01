import { useState, useEffect } from 'react'
import './App.css'
import { API_BASE_URL } from './config'
import { getAuthToken, handleAuthError } from './authUtils'

function QuizList({ onTopicSelect }) {
  const [topics, setTopics] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    fetchTopics()
  }, [])

  const fetchTopics = async () => {
    const token = getAuthToken()
    if (!token) {
      setError('You must be logged in to view quizzes')
      return
    }

    setLoading(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE_URL}/quiz/questions`, {
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
        throw new Error('Failed to fetch quiz questions')
      }
      const data = await response.json()
      
      // Group questions by topic and count
      const topicMap = new Map()
      data.forEach(question => {
        const topic = question.topic || 'General'
        const count = topicMap.get(topic) || 0
        topicMap.set(topic, count + 1)
      })

      const topicList = Array.from(topicMap.entries()).map(([topic, count]) => ({
        topic,
        count
      }))

      setTopics(topicList)
    } catch (err) {
      setError("Backend unreachable or unauthorized. Please login again.")
      console.error('Error fetching quiz topics:', err)
    } finally {
      setLoading(false)
    }
  }

  if (loading && topics.length === 0) {
    return <div className="loading">Loading quiz topics...</div>
  }

  return (
    <div className="quiz-list-container">
      <h2>MCQ Quizzes</h2>
      <p className="quiz-description">
        Test your knowledge with multiple choice questions. Select a topic to start a quiz.
      </p>
      
      {error && (
        <div className="error-message">
          <p>{error}</p>
        </div>
      )}

      {topics.length === 0 && !loading ? (
        <div className="empty-state">No quiz topics available.</div>
      ) : (
        <div className="quiz-topics-list">
          {topics.map((item, index) => (
            <div 
              key={index}
              className="quiz-topic-card"
              onClick={() => onTopicSelect && onTopicSelect(item.topic)}
              style={{ cursor: 'pointer' }}
            >
              <div className="quiz-topic-header">
                <h3 className="quiz-topic-title">{item.topic}</h3>
                <span className="quiz-count-badge">{item.count} {item.count === 1 ? 'question' : 'questions'}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default QuizList




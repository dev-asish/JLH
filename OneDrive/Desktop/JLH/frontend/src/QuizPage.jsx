import { useState, useEffect } from 'react'
import './App.css'
import { API_BASE_URL } from './config'
import { getAuthToken, handleAuthError } from './authUtils'
import QuizResult from './QuizResult'

function QuizPage({ topic, onBack, onSubmit }) {
  const [questions, setQuestions] = useState([])
  const [answers, setAnswers] = useState({})
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [submitted, setSubmitted] = useState(false)
  const [result, setResult] = useState(null)

  useEffect(() => {
    if (topic) {
      fetchQuestions(topic)
    }
  }, [topic])

  const fetchQuestions = async (topicName) => {
    const token = getAuthToken()
    if (!token) {
      setError('You must be logged in to take quizzes')
      return
    }

    setLoading(true)
    setError('')
    try {
      const url = topicName === 'All' 
        ? `${API_BASE_URL}/quiz/questions`
        : `${API_BASE_URL}/quiz/questions/topic/${encodeURIComponent(topicName)}`
      
      const response = await fetch(url, {
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
      setQuestions(data)
    } catch (err) {
      setError("Backend unreachable or unauthorized. Please login again.")
      console.error('Error fetching quiz questions:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleOptionSelect = (questionId, option) => {
    setAnswers(prev => ({
      ...prev,
      [questionId]: option
    }))
  }

  const handleSubmit = async () => {
    const token = getAuthToken()
    if (!token) {
      setError('You must be logged in to submit quizzes')
      return
    }

    // Prepare answers array
    const answersArray = Object.entries(answers).map(([id, selectedOption]) => ({
      id: parseInt(id),
      selectedOption: selectedOption
    }))

    if (answersArray.length === 0) {
      setError('Please answer at least one question before submitting.')
      return
    }

    setLoading(true)
    setError('')

    try {
      const response = await fetch(`${API_BASE_URL}/quiz/submit`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ answers: answersArray })
      })

      if (handleAuthError(response, setError)) {
        setLoading(false)
        return
      }

      if (!response.ok) {
        throw new Error('Failed to submit quiz')
      }

      const data = await response.json()
      setResult(data)
      setSubmitted(true)
    } catch (err) {
      setError("Backend unreachable or unauthorized. Please login again.")
      console.error('Error submitting quiz:', err)
    } finally {
      setLoading(false)
    }
  }

  if (loading && questions.length === 0) {
    return <div className="loading">Loading quiz questions...</div>
  }

  if (submitted && result) {
    return <QuizResult result={result} questions={questions} onBack={onBack} />
  }

  if (error && questions.length === 0) {
    return (
      <div>
        <div className="error-message">
          <p>{error}</p>
        </div>
        {onBack && (
          <button onClick={onBack} style={{ marginTop: '1rem' }}>
            Back to Quiz Topics
          </button>
        )}
      </div>
    )
  }

  return (
    <div className="quiz-page">
      <div className="quiz-page-header">
        {onBack && (
          <button onClick={onBack} className="back-button">
            ← Back to Quiz Topics
          </button>
        )}
        <h2>Quiz: {topic}</h2>
        <div className="quiz-progress">
          {Object.keys(answers).length} / {questions.length} answered
        </div>
      </div>

      {error && (
        <div className="error-message">
          <p>{error}</p>
        </div>
      )}

      {questions.length === 0 ? (
        <div className="empty-state">No questions available for this topic.</div>
      ) : (
        <>
          <div className="quiz-questions">
            {questions.map((question, index) => (
              <div key={question.id} className="quiz-question-card">
                <div className="quiz-question-number">Question {index + 1}</div>
                <h3 className="quiz-question-text">{question.question}</h3>
                <div className="quiz-options">
                  <label className={`quiz-option ${answers[question.id] === 'A' ? 'selected' : ''}`}>
                    <input
                      type="radio"
                      name={`question-${question.id}`}
                      value="A"
                      checked={answers[question.id] === 'A'}
                      onChange={() => handleOptionSelect(question.id, 'A')}
                    />
                    <span>A. {question.optionA}</span>
                  </label>
                  <label className={`quiz-option ${answers[question.id] === 'B' ? 'selected' : ''}`}>
                    <input
                      type="radio"
                      name={`question-${question.id}`}
                      value="B"
                      checked={answers[question.id] === 'B'}
                      onChange={() => handleOptionSelect(question.id, 'B')}
                    />
                    <span>B. {question.optionB}</span>
                  </label>
                  <label className={`quiz-option ${answers[question.id] === 'C' ? 'selected' : ''}`}>
                    <input
                      type="radio"
                      name={`question-${question.id}`}
                      value="C"
                      checked={answers[question.id] === 'C'}
                      onChange={() => handleOptionSelect(question.id, 'C')}
                    />
                    <span>C. {question.optionC}</span>
                  </label>
                  <label className={`quiz-option ${answers[question.id] === 'D' ? 'selected' : ''}`}>
                    <input
                      type="radio"
                      name={`question-${question.id}`}
                      value="D"
                      checked={answers[question.id] === 'D'}
                      onChange={() => handleOptionSelect(question.id, 'D')}
                    />
                    <span>D. {question.optionD}</span>
                  </label>
                </div>
              </div>
            ))}
          </div>

          <div className="quiz-submit-section">
            <button 
              onClick={handleSubmit} 
              disabled={loading || Object.keys(answers).length === 0}
              className="submit-quiz-button"
            >
              {loading ? 'Submitting...' : 'Submit Quiz'}
            </button>
          </div>
        </>
      )}
    </div>
  )
}

export default QuizPage




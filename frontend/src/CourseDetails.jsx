import { useState, useEffect, useRef } from 'react'
import './App.css'
import { API_BASE_URL } from './config'
import { getAuthToken, handleAuthError } from './authUtils'

function CourseDetails({ courseId, onBack }) {
  const [course, setCourse] = useState(null)
  const [lessons, setLessons] = useState([])
  const [currentLessonIndex, setCurrentLessonIndex] = useState(0)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const lessonContentRef = useRef(null)

  useEffect(() => {
    if (courseId) {
      fetchCourseDetails()
      fetchCourseContent()
    }
  }, [courseId])

  useEffect(() => {
    // Smooth scroll to lesson content when lesson changes
    if (lessonContentRef.current && lessons.length > 0) {
      setTimeout(() => {
        lessonContentRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }, 100)
    }
  }, [currentLessonIndex, lessons])

  const fetchCourseDetails = async () => {
    const token = getAuthToken()
    if (!token) {
      setError('You must be logged in to view course details')
      return
    }

    setLoading(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE_URL}/courses/${courseId}`, {
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
        throw new Error('Failed to fetch course details')
      }
      const data = await response.json()
      setCourse(data)
    } catch (err) {
      setError("Backend unreachable or unauthorized. Please login again.")
      console.error('Error fetching course:', err)
    } finally {
      setLoading(false)
    }
  }

  const fetchCourseContent = async () => {
    const token = getAuthToken()
    if (!token) {
      return
    }

    try {
      const response = await fetch(`${API_BASE_URL}/courses/${courseId}/content`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      if (handleAuthError(response, setError)) {
        return
      }

      if (!response.ok) {
        throw new Error('Failed to fetch course content')
      }
      const data = await response.json()
      setLessons(data)
      if (data.length > 0) {
        setCurrentLessonIndex(0)
      }
    } catch (err) {
      console.error("Backend unreachable or unauthorized. Please login again.", err)
    }
  }

  const handleStartCourse = () => {
    if (lessons.length > 0) {
      setCurrentLessonIndex(0)
      if (lessonContentRef.current) {
        lessonContentRef.current.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }
    }
  }

  const handleNextLesson = () => {
    if (currentLessonIndex < lessons.length - 1) {
      setCurrentLessonIndex(currentLessonIndex + 1)
    }
  }

  const handlePreviousLesson = () => {
    if (currentLessonIndex > 0) {
      setCurrentLessonIndex(currentLessonIndex - 1)
    }
  }

  const handleLessonSelect = (index) => {
    setCurrentLessonIndex(index)
  }

  const handleKeyDown = (e) => {
    if (e.key === 'ArrowRight' && currentLessonIndex < lessons.length - 1) {
      handleNextLesson()
    } else if (e.key === 'ArrowLeft' && currentLessonIndex > 0) {
      handlePreviousLesson()
    }
  }

  if (loading && !course) {
    return <div className="loading">Loading course details...</div>
  }

  if (error && !course) {
    return (
      <div>
        <div className="error-message">
          <p>{error}</p>
        </div>
        {onBack && (
          <button onClick={onBack} style={{ marginTop: '1rem' }}>
            Back to Courses
          </button>
        )}
      </div>
    )
  }

  if (!course) {
    return (
      <div>
        <div className="empty-state">Course not found.</div>
        {onBack && (
          <button onClick={onBack} style={{ marginTop: '1rem' }}>
            Back to Courses
          </button>
        )}
      </div>
    )
  }

  const currentLesson = lessons[currentLessonIndex] || null

  return (
    <div className="course-details-container" onKeyDown={handleKeyDown} tabIndex={0}>
      <div className="course-details-header">
        {onBack && (
          <button onClick={onBack} className="back-button">
            ← Back to Courses
          </button>
        )}
      </div>

      <div className="course-info">
        <h1 className="course-details-title">{course.title}</h1>
        <p className="course-details-description">{course.description}</p>
        {course.category && (
          <div className="course-meta">
            <span className="course-category">Category: {course.category}</span>
          </div>
        )}
        {course.difficulty && (
          <div className="course-meta">
            <span className={`course-difficulty difficulty-${course.difficulty?.toLowerCase()}`}>
              Difficulty: {course.difficulty}
            </span>
          </div>
        )}
      </div>

      {lessons.length > 0 && (
        <>
          <div className="lessons-sidebar">
            <h3>Lessons ({lessons.length})</h3>
            <div className="lessons-list">
              {lessons.map((lesson, index) => (
                <button
                  key={lesson.id}
                  className={`lesson-item ${index === currentLessonIndex ? 'active' : ''}`}
                  onClick={() => handleLessonSelect(index)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter' || e.key === ' ') {
                      e.preventDefault()
                      handleLessonSelect(index)
                    }
                  }}
                >
                  <span className="lesson-number">{index + 1}</span>
                  <span className="lesson-title">{lesson.title}</span>
                </button>
              ))}
            </div>
            <button 
              className="start-course-button"
              onClick={handleStartCourse}
              disabled={currentLessonIndex === 0}
            >
              Start Course
            </button>
          </div>

          {currentLesson && (
            <div className="lesson-viewer" ref={lessonContentRef}>
              <div className="lesson-header">
                <h2 className="lesson-viewer-title">
                  Lesson {currentLessonIndex + 1}: {currentLesson.title}
                </h2>
                <div className="lesson-navigation">
                  <button
                    className="nav-lesson-btn"
                    onClick={handlePreviousLesson}
                    disabled={currentLessonIndex === 0}
                    aria-label="Previous lesson"
                  >
                    ← Previous
                  </button>
                  <span className="lesson-counter">
                    {currentLessonIndex + 1} / {lessons.length}
                  </span>
                  <button
                    className="nav-lesson-btn"
                    onClick={handleNextLesson}
                    disabled={currentLessonIndex >= lessons.length - 1}
                    aria-label="Next lesson"
                  >
                    Next →
                  </button>
                </div>
              </div>
              <div className="lesson-content">
                <pre className="lesson-text">{currentLesson.content}</pre>
              </div>
            </div>
          )}
        </>
      )}

      {lessons.length === 0 && !loading && (
        <div className="empty-state">No lessons available for this course.</div>
      )}
    </div>
  )
}

export default CourseDetails



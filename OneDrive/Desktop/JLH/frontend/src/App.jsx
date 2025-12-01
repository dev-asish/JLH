import { useState, useEffect } from 'react'
import './App.css'
import { API_BASE_URL } from './config'
import { getAuthToken, clearAuth, handleAuthError } from './authUtils'
import Login from './Login'
import Register from './Register'
import TopicList from './TopicList'
import TopicPage from './TopicPage'
import JavaCompiler from './JavaCompiler'
import PracticeList from './PracticeList'
import PracticePage from './PracticePage'
import QuizList from './QuizList'
import QuizPage from './QuizPage'
import Dashboard from './Dashboard'
import CourseDetails from './CourseDetails'

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [username, setUsername] = useState('')
  const [showLogin, setShowLogin] = useState(false)
  const [showRegister, setShowRegister] = useState(false)
  const [currentView, setCurrentView] = useState('dashboard') // 'dashboard', 'courses', 'topics', 'compiler', 'practice', or 'quiz'
  const [selectedTopicId, setSelectedTopicId] = useState(null)
  const [selectedQuestionId, setSelectedQuestionId] = useState(null)
  const [selectedQuizTopic, setSelectedQuizTopic] = useState(null)
  const [selectedCourseId, setSelectedCourseId] = useState(null)
  const [courses, setCourses] = useState([])
  const [formData, setFormData] = useState({ title: '', description: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  // Check if user is logged in on component mount
  useEffect(() => {
    const token = getAuthToken()
    const storedUsername = localStorage.getItem('username')
    
    if (token && storedUsername) {
      setIsLoggedIn(true)
      setUsername(storedUsername)
    } else {
      setIsLoggedIn(false)
      setShowLogin(true)
    }
  }, [])

  // Fetch all courses from backend
  const fetchCourses = async () => {
    const token = getAuthToken()
    if (!token) {
      setError('You must be logged in to view courses')
      return
    }

    setLoading(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE_URL}/courses`, {
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
        throw new Error('Failed to fetch courses')
      }
      const data = await response.json()
      setCourses(data)
    } catch (err) {
      setError("Backend unreachable or unauthorized. Please login again.")
      console.error('Error fetching courses:', err)
    } finally {
      setLoading(false)
    }
  }

  // Load courses automatically when logged in
  useEffect(() => {
    if (isLoggedIn) {
      fetchCourses()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoggedIn])

  // Add a new course
  const addCourse = async (e) => {
    e.preventDefault()
    const token = getAuthToken()
    if (!token) {
      setError('You must be logged in to add courses')
      return
    }

    if (!formData.title.trim() || !formData.description.trim()) {
      setError('Please fill in both title and description')
      return
    }

    setLoading(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE_URL}/courses`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(formData),
      })

      if (handleAuthError(response, setError)) {
        setLoading(false)
        return
      }

      if (!response.ok) {
        throw new Error('Failed to add course')
      }
      const newCourse = await response.json()
      setCourses([...courses, newCourse])
      setFormData({ title: '', description: '' })
    } catch (err) {
      setError("Backend unreachable or unauthorized. Please login again.")
      console.error('Error adding course:', err)
    } finally {
      setLoading(false)
    }
  }

  // Delete a course
  const deleteCourse = async (id) => {
    const token = getAuthToken()
    if (!token) {
      setError('You must be logged in to delete courses')
      return
    }

    setLoading(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE_URL}/courses/${id}`, {
        method: 'DELETE',
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
        throw new Error('Failed to delete course')
      }
      setCourses(courses.filter(course => course.id !== id))
    } catch (err) {
      setError("Backend unreachable or unauthorized. Please login again.")
      console.error('Error deleting course:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleLogout = () => {
    clearAuth()
    setIsLoggedIn(false)
    setUsername('')
    setShowLogin(true)
    window.location.reload()
  }

  const handleLoginSuccess = () => {
    const storedUsername = localStorage.getItem('username')
    setIsLoggedIn(true)
    setUsername(storedUsername)
    setShowLogin(false)
    setShowRegister(false)
  }

  const handleRegisterSuccess = () => {
    setShowRegister(false)
    setShowLogin(true)
  }

  const handleRegisterClick = () => {
    setShowRegister(true)
    setShowLogin(false)
  }

  const handleTopicSelect = (topicId) => {
    setSelectedTopicId(topicId)
    setCurrentView('topic')
  }

  const handleBackToTopics = () => {
    setSelectedTopicId(null)
    setCurrentView('topics')
  }

  const handleQuestionSelect = (questionId) => {
    setSelectedQuestionId(questionId)
    setCurrentView('practice-question')
  }

  const handleBackToPractice = () => {
    setSelectedQuestionId(null)
    setCurrentView('practice')
  }

  const handleQuizTopicSelect = (topic) => {
    setSelectedQuizTopic(topic)
    setCurrentView('quiz-page')
  }

  const handleBackToQuizList = () => {
    setSelectedQuizTopic(null)
    setCurrentView('quiz')
  }

  const handleCourseSelect = (courseId) => {
    setSelectedCourseId(courseId)
    setCurrentView('course-details')
  }

  const handleBackToCourses = () => {
    setSelectedCourseId(null)
    setCurrentView('courses')
  }

  // Show register page if no token and showRegister is true
  const token = getAuthToken()
  if (!token && showRegister) {
    return <Register onRegisterSuccess={handleRegisterSuccess} />
  }

  // Show login page
  if (showLogin || (!token && !showRegister)) {
    return <Login onLoginSuccess={handleLoginSuccess} onRegisterClick={handleRegisterClick} />
  }

  // Show "must login" message if not logged in
  if (!isLoggedIn) {
    return (
      <div className="app">
        <h1>Java Learning Hub</h1>
        <div className="login-prompt">
          <p>You must log in first.</p>
          <button onClick={() => setShowLogin(true)}>Go to Login Page</button>
        </div>
      </div>
    )
  }

  // Main app UI (user is logged in)
  return (
    <div className="app">
      {/* Top Bar with Welcome and Logout */}
      <div className="top-bar">
        <div className="top-bar-content">
          <span className="welcome-text">Welcome, {username}</span>
          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </div>

      <h1>Java Learning Hub</h1>

      {/* Navigation Tabs */}
      <div className="nav-tabs">
        <button 
          className={currentView === 'dashboard' ? 'nav-tab active' : 'nav-tab'}
          onClick={() => {
            setCurrentView('dashboard')
            setSelectedTopicId(null)
            setSelectedQuestionId(null)
            setSelectedQuizTopic(null)
          }}
        >
          📊 Dashboard
        </button>
        <button 
          className={currentView === 'courses' ? 'nav-tab active' : 'nav-tab'}
          onClick={() => {
            setCurrentView('courses')
            setSelectedTopicId(null)
            setSelectedQuestionId(null)
            setSelectedQuizTopic(null)
            setSelectedCourseId(null)
          }}
        >
          📚 Courses
        </button>
        <button 
          className={currentView === 'topics' ? 'nav-tab active' : 'nav-tab'}
          onClick={() => {
            setCurrentView('topics')
            setSelectedTopicId(null)
            setSelectedQuestionId(null)
            setSelectedQuizTopic(null)
          }}
        >
          📖 Topics
        </button>
        <button 
          className={currentView === 'compiler' ? 'nav-tab active' : 'nav-tab'}
          onClick={() => {
            setCurrentView('compiler')
            setSelectedTopicId(null)
            setSelectedQuestionId(null)
            setSelectedQuizTopic(null)
          }}
        >
          💻 Compiler
        </button>
        <button 
          className={currentView === 'practice' || currentView === 'practice-question' ? 'nav-tab active' : 'nav-tab'}
          onClick={() => {
            setCurrentView('practice')
            setSelectedTopicId(null)
            setSelectedQuestionId(null)
            setSelectedQuizTopic(null)
          }}
        >
          🎯 Practice
        </button>
        <button 
          className={currentView === 'quiz' || currentView === 'quiz-page' ? 'nav-tab active' : 'nav-tab'}
          onClick={() => {
            setCurrentView('quiz')
            setSelectedTopicId(null)
            setSelectedQuestionId(null)
            setSelectedQuizTopic(null)
          }}
        >
          📝 Quiz
        </button>
      </div>

      {/* Error message */}
      {error && (
        <div className="error-message">
          <p>{error}</p>
        </div>
      )}

      {/* Show Topic Page if topic is selected */}
      {currentView === 'topic' && selectedTopicId && (
        <TopicPage 
          topicId={selectedTopicId} 
          onBack={handleBackToTopics}
        />
      )}

      {/* Show Topic List if topics view is active and no topic selected */}
      {currentView === 'topics' && !selectedTopicId && (
        <TopicList onTopicSelect={handleTopicSelect} />
      )}

      {/* Show Practice Question Page if question is selected */}
      {currentView === 'practice-question' && selectedQuestionId && (
        <PracticePage 
          questionId={selectedQuestionId} 
          onBack={handleBackToPractice}
        />
      )}

      {/* Show Practice List if practice view is active and no question selected */}
      {currentView === 'practice' && !selectedQuestionId && (
        <PracticeList onQuestionSelect={handleQuestionSelect} />
      )}

      {/* Show Quiz Page if quiz topic is selected */}
      {currentView === 'quiz-page' && selectedQuizTopic && (
        <QuizPage 
          topic={selectedQuizTopic} 
          onBack={handleBackToQuizList}
        />
      )}

      {/* Show Quiz List if quiz view is active and no topic selected */}
      {currentView === 'quiz' && !selectedQuizTopic && (
        <QuizList onTopicSelect={handleQuizTopicSelect} />
      )}

      {/* Show Compiler view if compiler view is active */}
      {currentView === 'compiler' && (
        <JavaCompiler />
      )}

      {/* Show Dashboard view if dashboard view is active */}
      {currentView === 'dashboard' && (
        <Dashboard />
      )}

      {/* Show Course Details view if course is selected */}
      {currentView === 'course-details' && selectedCourseId && (
        <CourseDetails 
          courseId={selectedCourseId} 
          onBack={handleBackToCourses}
        />
      )}

      {/* Show Courses view if courses view is active */}
      {currentView === 'courses' && !selectedCourseId && (
        <>
          {/* Load Courses Button */}
          <div className="actions">
            <button onClick={fetchCourses} disabled={loading}>
              {loading ? 'Loading...' : 'Load Courses'}
            </button>
          </div>

      {/* Add Course Form */}
      <div className="form-container">
        <h2>Add New Course</h2>
        <form onSubmit={addCourse}>
          <div className="form-group">
            <label htmlFor="title">Title:</label>
            <input
              type="text"
              id="title"
              name="title"
              value={formData.title}
              onChange={handleInputChange}
              placeholder="Enter course title"
              disabled={loading}
            />
          </div>
          <div className="form-group">
            <label htmlFor="description">Description:</label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              placeholder="Enter course description"
              rows="3"
              disabled={loading}
            />
          </div>
          <button type="submit" disabled={loading}>
            {loading ? 'Adding...' : 'Add Course'}
          </button>
        </form>
      </div>

      {/* Courses List */}
      <div className="courses-container">
        <h2>Courses ({courses.length})</h2>
        {loading && courses.length === 0 ? (
          <div className="loading">Loading courses...</div>
        ) : courses.length === 0 ? (
          <div className="empty-state">No courses available. Click "Load Courses" to fetch them.</div>
        ) : (
          <div className="courses-list">
            {courses.map(course => (
              <div 
                key={course.id} 
                className="course-card"
                onClick={() => handleCourseSelect(course.id)}
                style={{ cursor: 'pointer' }}
              >
                <div className="course-content">
                  <h3 className="course-title">{course.title}</h3>
                  <p className="course-description">{course.description}</p>
                </div>
                <div className="course-actions">
                  <button
                    className="view-course-btn"
                    onClick={(e) => {
                      e.stopPropagation()
                      handleCourseSelect(course.id)
                    }}
                    disabled={loading}
                  >
                    View Course
                  </button>
                  <button
                    className="delete-btn"
                    onClick={(e) => {
                      e.stopPropagation()
                      deleteCourse(course.id)
                    }}
                    disabled={loading}
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
        </>
      )}
    </div>
  )
}

export default App

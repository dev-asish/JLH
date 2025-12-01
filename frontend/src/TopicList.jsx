import { useState, useEffect } from 'react'
import './App.css'
import { API_BASE_URL } from './config'
import { getAuthToken, handleAuthError } from './authUtils'

function TopicList({ onTopicSelect }) {
  const [topics, setTopics] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [expandedCategories, setExpandedCategories] = useState(new Set())

  useEffect(() => {
    fetchTopics()
    // Initialize expanded state: expanded on desktop, collapsed on mobile
    const isMobile = window.innerWidth <= 768
    if (!isMobile) {
      // Expand all categories on desktop
      setExpandedCategories(new Set())
    }
  }, [])

  const fetchTopics = async () => {
    const token = getAuthToken()
    if (!token) {
      setError('You must be logged in to view topics')
      return
    }

    setLoading(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE_URL}/topics`, {
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
        throw new Error('Failed to fetch topics')
      }
      const data = await response.json()
      setTopics(data)
      
      // Initialize expanded categories on desktop
      const isMobile = window.innerWidth <= 768
      if (!isMobile && data.length > 0) {
        const categories = new Set(data.map(t => t.category).filter(Boolean))
        setExpandedCategories(categories)
      }
    } catch (err) {
      setError("Backend unreachable or unauthorized. Please login again.")
      console.error('Error fetching topics:', err)
    } finally {
      setLoading(false)
    }
  }

  const toggleCategory = (category) => {
    setExpandedCategories(prev => {
      const newSet = new Set(prev)
      if (newSet.has(category)) {
        newSet.delete(category)
      } else {
        newSet.add(category)
      }
      return newSet
    })
  }

  const handleCategoryKeyDown = (e, category) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault()
      toggleCategory(category)
    }
  }

  // Group topics by category
  const groupedTopics = topics.reduce((acc, topic) => {
    const category = topic.category || 'Uncategorized'
    if (!acc[category]) {
      acc[category] = []
    }
    acc[category].push(topic)
    return acc
  }, {})

  const categories = Object.keys(groupedTopics).sort()

  if (loading && topics.length === 0) {
    return <div className="loading">Loading topics...</div>
  }

  return (
    <div className="topics-container">
      <h2>Java Learning Topics</h2>
      
      {error && (
        <div className="error-message">
          <p>{error}</p>
        </div>
      )}

      {topics.length === 0 && !loading ? (
        <div className="empty-state">No topics available.</div>
      ) : (
        <div className="topics-categories">
          {categories.map(category => {
            const categoryTopics = groupedTopics[category]
            const isExpanded = expandedCategories.has(category)
            
            return (
              <div key={category} className="topic-category-section">
                <div 
                  className="category-header"
                  onClick={() => toggleCategory(category)}
                  onKeyDown={(e) => handleCategoryKeyDown(e, category)}
                  role="button"
                  tabIndex={0}
                  aria-expanded={isExpanded}
                  aria-label={`${category} category, ${categoryTopics.length} topics`}
                >
                  <div className="category-title-wrapper">
                    <h3 className="category-title">{category}</h3>
                    <span className="category-count">{categoryTopics.length} topics</span>
                  </div>
                  <span className="category-toggle-icon">
                    {isExpanded ? '▼' : '▶'}
                  </span>
                </div>
                
                <div 
                  className={`category-content ${isExpanded ? 'expanded' : 'collapsed'}`}
                  aria-hidden={!isExpanded}
                >
                  <div className="topics-list">
                    {categoryTopics.map(topic => (
                      <div 
                        key={topic.id} 
                        className="topic-card"
                        onClick={() => onTopicSelect && onTopicSelect(topic.id)}
                        onKeyDown={(e) => {
                          if (e.key === 'Enter' || e.key === ' ') {
                            e.preventDefault()
                            onTopicSelect && onTopicSelect(topic.id)
                          }
                        }}
                        role="button"
                        tabIndex={0}
                        style={{ cursor: 'pointer' }}
                      >
                        <div className="topic-header">
                          <h3 className="topic-title">{topic.title}</h3>
                          <span className={`topic-difficulty difficulty-${topic.difficulty?.toLowerCase()}`}>
                            {topic.difficulty}
                          </span>
                        </div>
                        <p className="topic-preview">
                          {topic.content?.substring(0, 150)}...
                        </p>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}

export default TopicList




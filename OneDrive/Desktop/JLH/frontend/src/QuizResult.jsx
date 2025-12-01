import './App.css'

function QuizResult({ result, questions, onBack }) {
  if (!result) {
    return (
      <div>
        <div className="empty-state">No results available.</div>
        {onBack && (
          <button onClick={onBack} style={{ marginTop: '1rem' }}>
            Back to Quiz Topics
          </button>
        )}
      </div>
    )
  }

  const score = result.score || 0
  const totalQuestions = result.totalQuestions || 0
  const correctAnswers = result.correctAnswers || 0
  const results = result.results || []

  // Create a map of questions for easy lookup
  const questionMap = new Map()
  questions.forEach(q => questionMap.set(q.id, q))

  const getScoreColor = () => {
    if (score >= 80) return '#28a745'
    if (score >= 60) return '#ffc107'
    return '#dc3545'
  }

  return (
    <div className="quiz-result">
      <div className="quiz-result-header">
        {onBack && (
          <button onClick={onBack} className="back-button">
            ← Back to Quiz Topics
          </button>
        )}
      </div>

      <div className="quiz-score-summary">
        <h2>Quiz Results</h2>
        <div className="score-circle" style={{ borderColor: getScoreColor() }}>
          <div className="score-value" style={{ color: getScoreColor() }}>
            {score.toFixed(1)}%
          </div>
        </div>
        <p className="score-details">
          You got <strong>{correctAnswers}</strong> out of <strong>{totalQuestions}</strong> questions correct.
        </p>
      </div>

      <div className="quiz-results-details">
        <h3>Question Review</h3>
        {results.map((item, index) => {
          const question = questionMap.get(item.questionId)
          return (
            <div 
              key={item.questionId} 
              className={`result-item ${item.isCorrect ? 'correct' : 'incorrect'}`}
            >
              <div className="result-question-header">
                <span className="result-question-number">Question {index + 1}</span>
                <span className={`result-status ${item.isCorrect ? 'correct-badge' : 'incorrect-badge'}`}>
                  {item.isCorrect ? '✓ Correct' : '✗ Incorrect'}
                </span>
              </div>
              <p className="result-question-text">{item.question}</p>
              <div className="result-answers">
                <div className={`result-answer ${item.selectedOption === item.correctOption ? 'correct-answer' : 'wrong-answer'}`}>
                  <strong>Your Answer:</strong> {item.selectedOption}. {getOptionText(question, item.selectedOption)}
                </div>
                {!item.isCorrect && (
                  <div className="result-answer correct-answer">
                    <strong>Correct Answer:</strong> {item.correctOption}. {getOptionText(question, item.correctOption)}
                  </div>
                )}
              </div>
              {item.explanation && (
                <div className="result-explanation">
                  <strong>Explanation:</strong> {item.explanation}
                </div>
              )}
            </div>
          )
        })}
      </div>
    </div>
  )

  function getOptionText(question, option) {
    if (!question) return ''
    switch (option) {
      case 'A': return question.optionA
      case 'B': return question.optionB
      case 'C': return question.optionC
      case 'D': return question.optionD
      default: return ''
    }
  }
}

export default QuizResult




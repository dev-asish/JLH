// Authentication utility functions
export const getAuthToken = () => {
  return localStorage.getItem("authToken") || localStorage.getItem('token')
}

export const clearAuth = () => {
  localStorage.removeItem('authToken')
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
}

export const redirectToLogin = () => {
  clearAuth()
  window.location.href = '/'
}

export const handleAuthError = (response, setError) => {
  if (response.status === 401 || response.status === 403) {
    clearAuth()
    setError("Backend unreachable or unauthorized. Please login again.")
    setTimeout(() => {
      redirectToLogin()
    }, 2000)
    return true
  }
  return false
}





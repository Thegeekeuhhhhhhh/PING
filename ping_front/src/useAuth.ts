import { useNavigate } from 'react-router-dom'

export const useAuth = () => {
  const navigate = useNavigate()

  const isAuthenticated = () => {
    const token = localStorage.getItem('token')
    return token !== null && token !== undefined
  }

  const logout = () => {
    localStorage.removeItem('token')
    navigate('/login')
  }

  const loginWithToken = (token: string) => {
    localStorage.setItem('token', token)
    navigate('/dashboard')
  }

  return {
    isAuthenticated,
    logout,
    loginWithToken
  }
}

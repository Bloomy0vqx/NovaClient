import { useEffect } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from './stores/useAuthStore'
import { useSettingsStore } from './stores/useSettingsStore'
import { useModStore } from './stores/useModStore'
import Layout from './components/layout/Layout'
import LoginPage from './pages/LoginPage'
import ConsolePage from './pages/ConsolePage'

export default function App() {
  const isLoggedIn = useAuthStore((s) => s.isLoggedIn)
  const loadAccounts = useAuthStore((s) => s.loadAccounts)
  const loadSettings = useSettingsStore((s) => s.loadSettings)
  const loadMods = useModStore((s) => s.loadMods)

  useEffect(() => {
    loadAccounts()
    loadSettings()
    loadMods()
  }, [])

  return (
    <Routes>
      <Route path="/console-window" element={<ConsolePage standalone />} />
      <Route
        path="/login"
        element={isLoggedIn ? <Navigate to="/" replace /> : <LoginPage />}
      />
      <Route
        path="/*"
        element={isLoggedIn ? <Layout /> : <Navigate to="/login" replace />}
      />
    </Routes>
  )
}

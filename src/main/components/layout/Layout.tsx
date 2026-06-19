import { useState, useEffect } from 'react'
import TitleBar from './TitleBar'
import PlayBar from './PlayBar'
import Footer from './Footer'
import DashboardPage from '../../pages/DashboardPage'
import CosmeticsPage from '../../pages/CosmeticsPage'
import ModsPage from '../../pages/ModsPage'
import SettingsPage from '../../pages/SettingsPage'
import AboutPage from '../../pages/AboutPage'
import PatcherPage from '../../pages/PatcherPage'
import PartnershipsPage from '../../pages/PartnershipsPage'
import ConsolePage from '../../pages/ConsolePage'
import ScreenshotGalleryPage from '../../pages/ScreenshotGalleryPage'
import { KeystrokeHud, CrosshairHud, DebugCountersHud, BedwarsStatsHud, PlayerModelHud } from '../hud'
import { useHudStore } from '../../stores/useHudStore'

export type TabKey = 'home' | 'cosmetics' | 'mods' | 'partnerships' | 'patcher' | 'settings' | 'console' | 'about' | 'gallery'

const navigationItems = [
  { id: 'home' as TabKey, label: 'Home', icon: 'fa-solid fa-house' },
  { id: 'cosmetics' as TabKey, label: 'Cosmetics', icon: 'fa-solid fa-shirt' },
  { id: 'mods' as TabKey, label: 'Mods', icon: 'fa-solid fa-puzzle-piece' },
  { id: 'partnerships' as TabKey, label: 'Partnerships', icon: 'fa-solid fa-handshake' },
  { id: 'patcher' as TabKey, label: 'Patcher', icon: 'fa-solid fa-wrench' },
  { id: 'settings' as TabKey, label: 'Settings', icon: 'fa-solid fa-gear' },
  { id: 'console' as TabKey, label: 'Console', icon: 'fa-solid fa-terminal' },
  { id: 'about' as TabKey, label: 'About', icon: 'fa-solid fa-info-circle' },
  { id: 'gallery' as TabKey, label: 'Gallery', icon: 'fa-solid fa-images' },
]

export default function Layout() {
  const [activeTab, setActiveTab] = useState<TabKey>('home')
  const { loadHud } = useHudStore()

  useEffect(() => {
    loadHud()
  }, [])

  const playHeight = activeTab === 'home' ? 300 : 135

  const renderContent = () => {
    switch (activeTab) {
      case 'home': return <DashboardPage />
      case 'cosmetics': return <CosmeticsPage />
      case 'mods': return <ModsPage />
      case 'partnerships': return <PartnershipsPage />
      case 'patcher': return <PatcherPage />
      case 'settings': return <SettingsPage />
      case 'console': return <ConsolePage />
      case 'about': return <AboutPage />
      case 'gallery': return <ScreenshotGalleryPage />
      default: return <DashboardPage />
    }
  }

  return (
    <div style={{ width: '100%', height: '100%', display: 'flex', flexDirection: 'column', background: '#12121F' }}>
      <TitleBar activeTab={activeTab} onTabChange={(tab) => setActiveTab(tab as TabKey)} />
      <div style={{ flex: 1, display: 'flex', overflow: 'hidden' }}>
        {/* Sidebar Navigation */}
        <div style={{
          width: '80px',
          background: '#181830',
          borderRight: '1px solid #252540',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '0',
          gap: '6px',
          overflowY: 'auto',
        }}>
          {navigationItems.map((item) => (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              style={{
                width: '50px',
                height: '50px',
                borderRadius: '12px',
                border: 'none',
                background: activeTab === item.id ? '#8B5CF6' : 'transparent',
                color: activeTab === item.id ? '#ffffff' : '#8b8ba0',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                transition: 'all 0.2s ease',
                fontSize: '18px',
              }}
              onMouseEnter={(e) => {
                if (activeTab !== item.id) {
                  e.currentTarget.style.background = '#252540'
                  e.currentTarget.style.color = '#ffffff'
                }
              }}
              onMouseLeave={(e) => {
                if (activeTab !== item.id) {
                  e.currentTarget.style.background = 'transparent'
                  e.currentTarget.style.color = '#8b8ba0'
                }
              }}
              title={item.label}
            >
              <i className={item.icon} />
            </button>
          ))}
        </div>

        {/* Main Content Area */}
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
          <div style={{ flex: 1, overflowY: 'auto', paddingBottom: '50px' }}>
            <PlayBar height={playHeight} />
            <div
              style={{
                display: 'flex',
                justifyContent: 'center',
                padding: '30px 20px 75px',
              }}
            >
              {renderContent()}
            </div>
          </div>
          <Footer />
        </div>
      </div>
      {/* HUD Overlays */}
      <KeystrokeHud />
      <CrosshairHud />
      <DebugCountersHud />
      <BedwarsStatsHud />
      <PlayerModelHud />
    </div>
  )
}

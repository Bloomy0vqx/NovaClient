import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '../stores/useAuthStore'
import { useSettingsStore } from '../stores/useSettingsStore'
import { useLanguage } from '../i18n'
import { Card, CardItem } from '../components/ui/Card'
import img26 from '../assets/versions/26.1.png'
import img121 from '../assets/versions/1.21.png'

export default function DashboardPage() {
  const navigate = useNavigate()
  const account = useAuthStore((s) => s.account)
  const {
    selectedVersion, versions, isProxyRunning, isLaunching,
    setVersions, setProxyRunning, setLaunching, setLaunchProgress, launchProgress,
    settings,
  } = useSettingsStore()
  const { t } = useLanguage()

  const [posts] = useState([
    {
      titleKey: 'dashboard.welcomeTitle' as const,
      descKey: 'dashboard.welcomeDesc' as const,
      authorKey: 'dashboard.novaTeam' as const,
      url: 'https://github.com/0vqx/Nova-Client',
      external: true,
      image: img26,
    },
    {
      titleKey: 'dashboard.cosmeticsTitle' as const,
      descKey: 'dashboard.cosmeticsDesc' as const,
      authorKey: 'dashboard.novaTeam' as const,
      url: '/cosmetics',
      external: false,
      image: img121,
    },
  ])

  useEffect(() => {
    window.nova.versions.get().then(setVersions)
  }, [])

  const filteredVersions = versions.filter((v) => v.type === 'release')
  const latestRelease = filteredVersions[0]?.id || '1.21.4'
  const displayVersion = selectedVersion === 'latest_release' ? latestRelease : selectedVersion

  const handleLaunch = async (playTarget: 'launch' | 'join', serverIp?: string) => {
    setLaunching(true)
    setLaunchProgress('Preparing...')
    try {
      const version = selectedVersion === 'latest_release' ? latestRelease : selectedVersion
      setLaunchProgress(`Launching ${version}...`)
      const unsub = window.nova.minecraft.onProgress((d) => {
        if (typeof d === 'string') setLaunchProgress(d)
      })
      await window.nova.minecraft.launch({
        username: account?.username || 'Player',
        uuid: account?.uuid || '',
        accessToken: account?.accessToken || '0',
        version,
        ram: settings.ram,
        javaPath: settings.javaPath,
        serverIp: playTarget === 'join' ? serverIp : undefined,
        customDir: settings.customDir || undefined,
      })
      unsub()
      setLaunchProgress(null)
    } catch (e: any) {
      setLaunchProgress(`Error: ${e?.message || e}`)
    } finally {
      setLaunching(false)
    }
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%', padding: '20px' }}>
      <h4
        style={{
          textAlign: 'center',
          fontSize: '1.5em',
          letterSpacing: '2px',
          fontWeight: 600,
          marginBottom: '25px',
          color: '#f7f7f7',
          fontFamily: 'Inter, sans-serif',
        }}
      >
        {t('dashboard.recentNews')}
      </h4>

      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(380px, 1fr))',
          gap: '20px',
          width: '100%',
          maxWidth: '1200px',
          marginBottom: '40px',
        }}
      >
        {posts.map((post, i) => (
          <div
            key={i}
            style={{
              backgroundColor: '#181830',
              borderRadius: '16px',
              overflow: 'hidden',
              display: 'flex',
              flexDirection: 'column',
              border: '1px solid #252540',
              transition: 'all 0.3s ease',
              cursor: 'pointer',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-4px)'
              e.currentTarget.style.borderColor = '#8B5CF6'
              e.currentTarget.style.boxShadow = '0 8px 24px rgba(139, 92, 246, 0.15)'
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = 'translateY(0)'
              e.currentTarget.style.borderColor = '#252540'
              e.currentTarget.style.boxShadow = 'none'
            }}
            onClick={() => {
              if (post.external) {
                window.nova.shell.openExternal(post.url)
              } else {
                navigate(post.url)
              }
            }}
          >
            <div
              style={{
                width: '100%',
                height: '180px',
                backgroundImage: `linear-gradient(to bottom, rgba(10, 10, 26, 0.3), #181830), url(${post.image})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                display: 'flex',
                alignItems: 'flex-end',
                justifyContent: 'flex-start',
                position: 'relative',
                padding: '16px',
              }}
            >
              <span style={{ color: '#fff', fontFamily: 'Inter, sans-serif', fontWeight: 700, fontSize: '20px', textShadow: '0 2px 12px rgba(0,0,0,0.8)', position: 'relative', zIndex: 1 }}>
                {t(post.titleKey)}
              </span>
            </div>
            <div style={{ padding: '16px', flex: 1 }}>
              <p
                style={{
                  fontSize: '14px',
                  fontWeight: 400,
                  lineHeight: 1.6,
                  color: '#a0a0b0',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  display: '-webkit-box',
                  WebkitLineClamp: 2,
                  WebkitBoxOrient: 'vertical' as any,
                  marginBottom: '12px',
                }}
              >
                {t(post.descKey)}
              </p>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <p style={{ fontSize: '12px', color: '#8b8ba0' }}>
                  Posted by <strong style={{ color: '#8B5CF6' }}>{t(post.authorKey)}</strong>
                </p>
                <button
                  style={{
                    padding: '6px 14px',
                    background: 'rgba(139, 92, 246, 0.1)',
                    border: '1px solid rgba(139, 92, 246, 0.3)',
                    borderRadius: '8px',
                    color: '#8B5CF6',
                    fontSize: '12px',
                    cursor: 'pointer',
                    transition: 'all 0.2s ease',
                    fontFamily: 'Inter, sans-serif',
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.background = 'rgba(139, 92, 246, 0.2)'
                    e.currentTarget.style.borderColor = '#8B5CF6'
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.background = 'rgba(139, 92, 246, 0.1)'
                    e.currentTarget.style.borderColor = 'rgba(139, 92, 246, 0.3)'
                  }}
                  onClick={(e) => {
                    e.stopPropagation()
                    if (post.external) {
                      window.nova.shell.openExternal(post.url)
                    } else {
                      navigate(post.url)
                    }
                  }}
                >
                  <i className="fa-solid fa-arrow-right" style={{ marginRight: 6 }} />
                  {t('dashboard.readMore')}
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div
        style={{
          width: '100%',
          maxWidth: '800px',
          backgroundColor: '#181830',
          borderRadius: '16px',
          border: '1px solid #252540',
          padding: '24px',
          transition: 'all 0.3s ease',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '20px' }}>
          <div style={{
            width: '48px',
            height: '48px',
            borderRadius: '12px',
            background: 'linear-gradient(135deg, #8B5CF6, #1a5bb8)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}>
            <i className="fa-solid fa-rocket" style={{ color: '#fff', fontSize: '20px' }} />
          </div>
          <div>
            <h3 style={{ color: '#f7f7f7', fontSize: '18px', fontWeight: 600, fontFamily: 'Inter, sans-serif', margin: 0 }}>
              {t('dashboard.novaClient')}
            </h3>
            <p style={{ color: '#8b8ba0', fontSize: '13px', margin: 0, fontFamily: 'Inter, sans-serif' }}>
              {t('dashboard.readyToPlay')}
            </p>
          </div>
        </div>

        <div style={{ marginBottom: '20px', padding: '16px', background: '#12121F', borderRadius: '12px', border: '1px solid #252540' }}>
          <p style={{ color: '#f7f7f7', fontSize: '14px', fontFamily: 'Inter, sans-serif', margin: '0 0 8px 0' }}>
            {t('dashboard.welcomeBack', { username: account?.username || 'Player' })}
          </p>
          <p style={{ color: '#8b8ba0', fontSize: '12px', fontFamily: 'Inter, sans-serif', margin: 0 }}>
            {t('dashboard.accountType', { type: account?.type || '' })}
          </p>
        </div>

        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
          <button
            onClick={() => handleLaunch('launch')}
            disabled={isLaunching}
            style={{
              padding: '12px 24px',
              background: isLaunching ? '#1a5bb8' : 'linear-gradient(135deg, #3cc065, #32b058)',
              border: 'none',
              borderRadius: '10px',
              color: '#f7f7f7',
              fontSize: '14px',
              fontWeight: 500,
              cursor: isLaunching ? 'not-allowed' : 'pointer',
              fontFamily: 'Inter, sans-serif',
              opacity: isLaunching ? 0.7 : 1,
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              transition: 'all 0.2s ease',
              boxShadow: isLaunching ? 'none' : '0 4px 12px rgba(139, 92, 246, 0.3)',
            }}
            onMouseEnter={(e) => {
              if (!isLaunching) {
                e.currentTarget.style.transform = 'translateY(-2px)'
                e.currentTarget.style.boxShadow = '0 6px 16px rgba(139, 92, 246, 0.4)'
              }
            }}
            onMouseLeave={(e) => {
              if (!isLaunching) {
                e.currentTarget.style.transform = 'translateY(0)'
                e.currentTarget.style.boxShadow = '0 4px 12px rgba(139, 92, 246, 0.3)'
              }
            }}
          >
            <i className="fa-solid fa-play" style={{ fontSize: '12px' }} />
            {isLaunching && launchProgress ? launchProgress : t('dashboard.launch', { version: displayVersion })}
          </button>
          <button
            onClick={() => handleLaunch('join', 'hypixel.net')}
            disabled={isLaunching}
            style={{
              padding: '12px 20px',
              background: 'rgba(139, 92, 246, 0.1)',
              border: '1px solid rgba(139, 92, 246, 0.3)',
              borderRadius: '10px',
              color: '#8B5CF6',
              fontSize: '14px',
              fontWeight: 500,
              cursor: isLaunching ? 'not-allowed' : 'pointer',
              fontFamily: 'Inter, sans-serif',
              opacity: isLaunching ? 0.6 : 1,
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              transition: 'all 0.2s ease',
            }}
            onMouseEnter={(e) => {
              if (!isLaunching) {
                e.currentTarget.style.background = 'rgba(139, 92, 246, 0.2)'
                e.currentTarget.style.borderColor = '#8B5CF6'
              }
            }}
            onMouseLeave={(e) => {
              if (!isLaunching) {
                e.currentTarget.style.background = 'rgba(139, 92, 246, 0.1)'
                e.currentTarget.style.borderColor = 'rgba(139, 92, 246, 0.3)'
              }
            }}
          >
            <i className="fa-solid fa-right-to-bracket" style={{ fontSize: '12px' }} />
            {t('dashboard.quickJoin')}
          </button>
        </div>
      </div>
    </div>
  )
}

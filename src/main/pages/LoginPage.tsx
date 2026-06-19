import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '../stores/useAuthStore'
import { useLanguage } from '../i18n'
import type { MinecraftAccount } from '../types'

import bgImage from '../assets/background.jpg'
import novaLogo from '../assets/nova-logo.png'

export default function LoginPage() {
  const navigate = useNavigate()
  const setAccount = useAuthStore((s) => s.setAccount)
  const { t } = useLanguage()
  const [username, setUsername] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState<'ms' | 'offline' | null>(null)
  const [showOffline, setShowOffline] = useState(false)

  const validateUsername = (name: string) => {
    if (name.length < 3 || name.length > 16) return t('login.usernameError.length')
    if (!/^[a-zA-Z0-9_]+$/.test(name)) return t('login.usernameError.chars')
    return ''
  }

  const handleOfflineLogin = async () => {
    const name = username.trim()
    const err = validateUsername(name)
    if (err) { setError(err); return }
    setError('')
    setLoading('offline')
    try {
      const uuid = await window.nova.offline.createUUID(name)
      const account: MinecraftAccount = {
        id: uuid,
        username: name,
        uuid,
        accessToken: 'offline-' + crypto.randomUUID(),
        type: 'offline',
        lastLogin: new Date().toISOString(),
      }
      setAccount(account)
      navigate('/')
    } catch (e: any) {
      setError(t('login.failedOffline'))
    } finally {
      setLoading(null)
    }
  }

  const handleMicrosoftLogin = async () => {
    setLoading('ms')
    setError('')
    try {
      const account = await window.nova.microsoft.login()
      account.lastLogin = new Date().toISOString()
      setAccount(account)
      navigate('/')
    } catch (e: any) {
      setError(e.message || t('login.failedMicrosoft'))
    } finally {
      setLoading(null)
    }
  }

  return (
    <>
      <style>{`
        :root {
          --bg:      #12121F;
          --bg-card: #181830;
          --text:    #ffffff;
          --muted:   #9ca3af;
          --accent:  #00a4ef;
          --green:   #8B5CF6;
          --gray-bar:#3c4e62;
          --border:  rgba(255,255,255,0.05);
        }

        .login-wrapper {
          width: 100vw;
          height: 100vh;
          overflow: hidden;
          background: var(--bg);
          font-family: 'Inter', sans-serif;
          color: var(--text);
          user-select: none;
        }

        .layout {
          display: grid;
          grid-template-columns: 1.4fr 1fr;
          width: 100%;
          height: 100%;
        }

        .left {
          position: relative;
          overflow: hidden;
          background: #5c92dc;
        }

        .left-img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          display: block;
        }

        .right {
          background: var(--bg);
          display: flex;
          flex-direction: column;
          padding: 40px 50px;
          overflow-y: auto;
          position: relative;
        }

        .window-controls {
          position: absolute;
          top: 15px;
          right: 15px;
          display: flex;
          gap: 15px;
          color: #fff;
          font-size: 14px;
          cursor: pointer;
        }

        .app-logo {
          margin-bottom: 30px;
        }
        
        .feat-row {
          display: grid;
          grid-template-columns: 1.2fr 1fr;
          gap: 20px;
          margin-bottom: 28px;
          align-items: center;
        }

        .feat-info-title {
          font-size: 1rem;
          font-weight: 700;
          color: #fff;
          margin-bottom: 4px;
        }

        .feat-info-desc {
          color: var(--muted);
          font-size: 0.8rem;
          line-height: 1.4;
        }

        .fps-display {
          display: flex;
          flex-direction: column;
          gap: 8px;
        }

        .fps-bar-wrapper {
          display: flex;
          align-items: center;
          justify-content: flex-end;
          gap: 8px;
        }

        .fps-icon {
          width: 16px;
          height: 16px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .fps-bar {
          height: 22px;
          border-radius: 4px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 0.65rem;
          font-weight: 700;
          color: #fff;
        }

        .fps-bar.nova {
          background: #8B5CF6;
          width: 140px;
        }

        .fps-bar.vanilla {
          background: #3c4e62;
          width: 90px;
          color: #9ca3af;
        }

        .fps-bar.optifine {
          background: #263240;
          width: 70px;
          color: #7f8c8d;
        }

        .cosmetics-grid {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 8px;
          align-items: center;
        }

        .cosm-item {
          height: 45px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .mod-grid {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 6px;
        }

        .mod-btn {
          background: #161628;
          border: none;
          border-radius: 4px;
          color: #fff;
          font-size: 0.65rem;
          padding: 6px 4px;
          text-align: center;
          font-weight: 500;
          white-space: nowrap;
        }

        .mod-btn.green-link {
          color: #8B5CF6;
          background: transparent;
          font-weight: 600;
        }

        .version-strip {
          display: flex;
          gap: 6px;
          justify-content: space-between;
        }

        .version-block {
          width: 32px;
          height: 45px;
          background: #161628;
          border-radius: 4px;
          background-size: cover;
          background-position: center;
          position: relative;
          display: flex;
          align-items: flex-end;
          justify-content: center;
          padding-bottom: 4px;
          font-size: 0.65rem;
          font-weight: 700;
        }

        .action-area {
          margin-top: auto;
          display: flex;
          flex-direction: column;
          gap: 12px;
        }

        .btn {
          width: 100%;
          height: 48px;
          border: none;
          border-radius: 6px;
          font-family: 'Inter', sans-serif;
          font-weight: 600;
          font-size: 0.95rem;
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 10px;
          transition: opacity 0.15s ease;
        }

        .btn:hover {
          opacity: 0.9;
        }
        
        .btn:disabled {
          opacity: 0.5;
          cursor: not-allowed;
        }

        .btn-microsoft {
          background: #00a4ef;
          color: #fff;
        }

        .btn-microsoft svg {
          width: 16px;
          height: 16px;
        }

        .btn-guest {
          background: #1c1c30;
          color: #9ca3af;
        }

        .footer-cr {
          text-align: center;
          font-size: 0.65rem;
          color: #5b6576;
          margin-top: 25px;
        }
        
        .guest-input {
          padding: 14px 16px;
          border-radius: 6px;
          border: 1px solid var(--border);
          background: #161628;
          color: #fff;
          font-size: 0.95rem;
          outline: none;
          width: 100%;
          font-family: 'Inter', sans-serif;
        }
        
        .guest-input:focus {
          border-color: var(--green);
        }
      `}</style>
      <div className="login-wrapper">
        <div className="layout">
          <div className="left">
            <img className="left-img" src={bgImage} alt="Minecraft Background" />
          </div>

          <div className="right">
            <div className="window-controls" onClick={() => window.nova.window.close()}>
              <span>&mdash;</span>
              <span>&#9634;</span>
              <span>&times;</span>
            </div>

            <div className="app-logo">
              <img src={novaLogo} alt="Nova Logo" style={{ width: '55px', height: '55px', objectFit: 'contain' }} />
            </div>

            <div className="feat-row">
              <div className="feat-info">
                <div className="feat-info-title">Boosted Frames</div>
                <div className="feat-info-desc">Experience best-in-class performance on any hardware</div>
              </div>
              <div className="fps-display">
                <div className="fps-bar-wrapper">
                  <div className="fps-icon"></div>
                  <div className="fps-bar nova">400+ FPS</div>
                </div>
                <div className="fps-bar-wrapper">
                  <div className="fps-icon"></div>
                  <div className="fps-bar vanilla">150 FPS</div>
                </div>
                <div className="fps-bar-wrapper">
                  <div className="fps-icon"></div>
                  <div className="fps-bar optifine">100 FPS</div>
                </div>
              </div>
            </div>

            <div className="feat-row">
              <div className="feat-info">
                <div className="feat-info-title">Cosmetics &amp; Emotes</div>
                <div className="feat-info-desc">Express yourself with various outfits and tons of dances</div>
              </div>
              <div className="cosmetics-grid">
                <div className="cosm-item"></div>
                <div className="cosm-item"></div>
                <div className="cosm-item"></div>
              </div>
            </div>

            <div className="feat-row">
              <div className="feat-info">
                <div className="feat-info-title">Countless Mods</div>
                <div className="feat-info-desc">All of your favourite mods available in one easy-to-use interface</div>
              </div>
              <div className="mod-grid">
                <button className="mod-btn">Skyblock Addons</button>
                <button className="mod-btn">CPS Mod</button>
                <button className="mod-btn">Freelook</button>
                <button className="mod-btn">Zoom Mod</button>
                <button className="mod-btn">Bedwars Mod</button>
                <button className="mod-btn">Armor Status</button>
                <button className="mod-btn">Pack Organizer</button>
                <button className="mod-btn">Replay Mod</button>
                <div className="mod-btn green-link">+ 100's more</div>
              </div>
            </div>

            <div className="feat-row">
              <div className="feat-info">
                <div className="feat-info-title">Multi Version</div>
                <div className="feat-info-desc">Supporting all the latest versions of Minecraft: Java Edition</div>
              </div>
              <div className="version-strip">
                <div className="version-block" style={{ backgroundImage: "linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url('https://placehold.co/32x45/222/fff?text=1.16')" }}>1.16</div>
                <div className="version-block" style={{ backgroundImage: "linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url('https://placehold.co/32x45/222/fff?text=1.12')" }}>1.12</div>
                <div className="version-block" style={{ backgroundImage: "linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url('https://placehold.co/32x45/222/fff?text=MC')" }}>1.21</div>
                <div className="version-block" style={{ backgroundImage: "linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url('https://placehold.co/32x45/222/fff?text=1.17')" }}>1.17</div>
                <div className="version-block" style={{ backgroundImage: "linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url('https://placehold.co/32x45/222/fff?text=1.8')" }}>1.8</div>
              </div>
            </div>

            <div className="action-area">
              {!showOffline ? (
                <>
                  <button className="btn btn-microsoft" onClick={handleMicrosoftLogin} disabled={loading === 'ms'}>
                    <svg viewBox="0 0 23 23">
                      <path fill="#f25022" d="M0 0h11v11H0z" />
                      <path fill="#7fba00" d="M12 0h11v11H12z" />
                      <path fill="#00a4ef" d="M0 12h11v11H0z" />
                      <path fill="#ffb900" d="M12 12h11v11H12z" />
                    </svg>
                    {loading === 'ms' ? t('login.signingIn') : t('login.signInMicrosoft')}
                  </button>
                  <button className="btn btn-guest" onClick={() => setShowOffline(true)}>{t('login.continueGuest')}</button>
                </>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', width: '100%' }}>
                  <input
                    className="guest-input"
                    placeholder={t('login.guestPlaceholder')}
                    value={username}
                    maxLength={16}
                    onChange={(e) => { setUsername(e.target.value); setError('') }}
                    onKeyDown={(e) => e.key === 'Enter' && handleOfflineLogin()}
                  />
                  {error && <div style={{ color: '#e74c3c', fontSize: '0.8rem', textAlign: 'center' }}>{error}</div>}

                  <div style={{ display: 'flex', gap: '8px' }}>
                    <button className="btn" style={{ background: 'var(--green)', color: '#fff', flex: 1 }} onClick={handleOfflineLogin} disabled={loading === 'offline'}>
                      {loading === 'offline' ? t('login.loading') : t('login.playOffline')}
                    </button>
                    <button className="btn btn-guest" style={{ width: 'auto', padding: '0 20px' }} onClick={() => setShowOffline(false)}>
                      {t('login.back')}
                    </button>
                  </div>
                </div>
              )}
            </div>

            <div className="footer-cr">
              &copy; 2026 Nova Client
            </div>
          </div>
        </div>
      </div>
    </>
  )
}

import novaLogo from '../../assets/nova-logo.png'

interface Props {
  activeTab: string
  onTabChange: (tab: string) => void
}

export default function TitleBar({ activeTab, onTabChange }: Props) {
  const windowBtnStyle = {
    width: '35px',
    height: '35px',
    background: '#161628',
    border: 'none',
    borderRadius: '8px',
    color: 'rgba(255,255,255,0.6)',
    fontSize: '14px',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
    WebkitAppRegion: 'no-drag',
  }

  return (
    <>
      <div
        style={{
          position: 'fixed',
          top: 0,
          left: 0,
          width: '100%',
          height: '60px',
          zIndex: 10,
          backgroundColor: '#0e0e1a',
          display: 'flex',
          alignItems: 'center',
          borderBottom: '1px solid rgba(255,255,255,0.04)',
          WebkitAppRegion: 'drag',
        } as any}
      >
        <div style={{ display: 'flex', alignItems: 'center', marginLeft: '25px' }}>
          <img
            src={novaLogo}
            alt="Nova Launcher"
            style={{
              width: '40px',
              height: '40px',
              borderRadius: '50%',
              WebkitUserDrag: 'none',
            } as any}
          />
          <h2
            style={{
              color: '#ffffff',
              fontSize: '18px',
              fontWeight: 700,
              fontFamily: 'Inter, sans-serif',
              marginLeft: '10px',
              cursor: 'default',
              WebkitUserDrag: 'none',
              letterSpacing: '0.5px',
            } as any}
          >
            Nova
          </h2>
        </div>

        <div
          style={{
            display: 'flex',
            gap: '6px',
            marginRight: '25px',
            alignItems: 'center',
            marginLeft: 'auto',
          }}
        >
          <button
            onClick={() => window.nova.window.minimize()}
            style={windowBtnStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.background = '#252540'
              e.currentTarget.style.color = '#ffffff'
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.background = '#161628'
              e.currentTarget.style.color = 'rgba(255,255,255,0.6)'
            }}
          >
            <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
              <path d="M2 6h8" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
            </svg>
          </button>
          <button
            onClick={() => window.nova.window.close()}
            style={windowBtnStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.background = '#e04060'
              e.currentTarget.style.color = '#ffffff'
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.background = '#161628'
              e.currentTarget.style.color = 'rgba(255,255,255,0.6)'
            }}
          >
            <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
              <path d="M2 2l8 8M10 2l-8 8" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
            </svg>
          </button>
        </div>
      </div>
    </>
  )
}

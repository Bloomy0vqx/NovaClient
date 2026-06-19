import { useEffect, useState } from 'react'
import { useHudStore } from '../stores/useHudStore'
import { useLanguage } from '../i18n'

export default function ScreenshotGalleryPage() {
  const { screenshots, removeScreenshot, screenshot, updateScreenshot, saveHud } = useHudStore()
  const { t } = useLanguage()
  const [selected, setSelected] = useState<string | null>(null)

  useEffect(() => {
    saveHud()
  }, [screenshots])

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%', padding: '0 20px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%', maxWidth: '900px', marginBottom: '20px' }}>
        <div>
          <h2 style={{ color: '#f7f7f7', fontSize: '22px', fontWeight: 300, margin: 0 }}>
            Screenshot Gallery
          </h2>
          <p style={{ color: '#f7f7f766', fontSize: '12px', margin: '4px 0 0 0' }}>
            {screenshots.length} screenshot{screenshots.length !== 1 ? 's' : ''}
          </p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button
            onClick={() => updateScreenshot({ autoCapture: !screenshot.autoCapture })}
            style={{
              padding: '8px 16px',
              borderRadius: '8px',
              border: 'none',
              background: screenshot.autoCapture ? '#38b060' : '#68688060',
              color: '#f7f7f7',
              fontSize: '12px',
              cursor: 'pointer',
              fontFamily: 'Roboto, sans-serif',
              transition: 'background 0.3s',
            }}
          >
            Auto-Capture: {screenshot.autoCapture ? 'ON' : 'OFF'}
          </button>
        </div>
      </div>

      {screenshots.length === 0 ? (
        <div
          style={{
            width: '100%',
            maxWidth: '900px',
            height: '400px',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            background: '#181830',
            borderRadius: '16px',
            border: '1px solid #252540',
          }}
        >
          <i className="fa-solid fa-camera" style={{ fontSize: '48px', color: 'rgba(255,255,255,0.15)', marginBottom: '16px' }}></i>
          <p style={{ color: 'rgba(255,255,255,0.35)', fontSize: '14px', margin: 0 }}>No screenshots yet</p>
          <p style={{ color: 'rgba(255,255,255,0.2)', fontSize: '12px', margin: '4px 0 0 0' }}>
            Screenshots taken in-game will appear here
          </p>
        </div>
      ) : (
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
            gap: '16px',
            width: '100%',
            maxWidth: '900px',
            marginBottom: '40px',
          }}
        >
          {screenshots.map((s) => (
            <div
              key={s.id}
              onClick={() => setSelected(selected === s.id ? null : s.id)}
              style={{
                background: '#181830',
                borderRadius: '12px',
                border: selected === s.id ? '2px solid #8B5CF6' : '1px solid #252540',
                overflow: 'hidden',
                cursor: 'pointer',
                transition: 'all 0.2s ease',
              }}
            >
              <div
                style={{
                  width: '100%',
                  height: '140px',
                  background: '#252540',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  overflow: 'hidden',
                }}
              >
                <img
                  src={s.thumbnail || s.path}
                  alt="Screenshot"
                  style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                  onError={(e) => {
                    ;(e.target as HTMLImageElement).style.display = 'none'
                  }}
                />
              </div>
              <div style={{ padding: '10px' }}>
                <div style={{ fontSize: '11px', color: '#f7f7f799', marginBottom: '2px' }}>
                  {new Date(s.timestamp).toLocaleDateString()} {new Date(s.timestamp).toLocaleTimeString()}
                </div>
                {s.server && (
                  <div style={{ fontSize: '10px', color: '#8B5CF6', marginBottom: '2px' }}>
                    {s.server}
                  </div>
                )}
                {selected === s.id && (
                  <div style={{ display: 'flex', gap: '6px', marginTop: '8px' }}>
                    <button
                      onClick={(e) => {
                        e.stopPropagation()
                        if (s.path) window.nova?.shell?.openExternal(s.path)
                      }}
                      style={{
                        flex: 1,
                        padding: '4px',
                        borderRadius: '4px',
                        border: 'none',
                        background: '#8B5CF6',
                        color: '#fff',
                        fontSize: '10px',
                        cursor: 'pointer',
                      }}
                    >
                      Open
                    </button>
                    <button
                      onClick={(e) => {
                        e.stopPropagation()
                        removeScreenshot(s.id)
                      }}
                      style={{
                        padding: '4px 8px',
                        borderRadius: '4px',
                        border: 'none',
                        background: '#c43058',
                        color: '#fff',
                        fontSize: '10px',
                        cursor: 'pointer',
                      }}
                    >
                      Delete
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

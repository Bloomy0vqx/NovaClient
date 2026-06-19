import { useEffect } from 'react'
import { usePatcherStore } from '../stores/usePatcherStore'
import { useLanguage } from '../i18n'

export default function PatcherPage() {
  const { patches, customizing, setCustomizing, togglePatch, updatePatchValue, loadPatches } = usePatcherStore()
  const { t } = useLanguage()

  useEffect(() => {
    loadPatches()
  }, [])

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%', padding: '0 20px' }}>
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))',
          gap: '25px 20px',
          marginBottom: '75px',
          width: '100%',
          maxWidth: '1200px',
        }}
      >
        {patches.map((patch) => (
          <div
            key={patch.id}
            style={{
              width: '100%',
              height: '240px',
              backgroundColor: '#181830',
              borderRadius: '16px',
              border: '1px solid #252540',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              position: 'relative',
              transition: 'all 0.25s ease',
            }}
            onMouseEnter={(e) => { e.currentTarget.style.backgroundColor = '#1e1e38'; e.currentTarget.style.borderColor = 'rgba(139,92,246,0.3)' }}
            onMouseLeave={(e) => { e.currentTarget.style.backgroundColor = '#181830'; e.currentTarget.style.borderColor = '#252540' }}
          >
            <div
              style={{
                width: '60px',
                height: '60px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '28px',
                color: '#9090a8',
                marginBottom: '10px',
                WebkitUserDrag: 'none',
              } as any}
            >
              <i className={patch.icon}></i>
            </div>

            <h3
              style={{
                fontWeight: 500,
                fontSize: '15px',
                color: '#f7f7f7',
                textAlign: 'center',
                marginBottom: '4px',
                padding: '0 10px',
              }}
            >
              {patch.name}
            </h3>

            <p
              style={{
                fontWeight: 400,
                fontSize: '11px',
                color: '#8b8ba0',
                textAlign: 'center',
                marginBottom: 'auto',
                padding: '0 15px',
                lineHeight: '1.4',
              }}
            >
              {t(`patcher.desc.${patch.id}` as any) || ''}
            </p>

            {patch.hasValues && (
              <button
                onClick={() => setCustomizing(patch)}
                style={{
                  width: '100%',
                  height: '36px',
                  border: 'none',
                  background: 'rgba(255,255,255,0.06)',
                  color: '#9090a8',
                  fontSize: '11px',
                  letterSpacing: '1px',
                  cursor: 'pointer',
                  fontFamily: 'Roboto, sans-serif',
                  transition: 'all 0.2s ease',
                }}
                onMouseEnter={(e) => { e.currentTarget.style.background = 'rgba(255,255,255,0.1)'; e.currentTarget.style.color = '#f7f7f7' }}
                onMouseLeave={(e) => { e.currentTarget.style.background = 'rgba(255,255,255,0.06)'; e.currentTarget.style.color = '#9090a8' }}
              >
                {t('patcher.options')}
              </button>
            )}

            <button
              onClick={() => togglePatch(patch.id)}
              style={{
                width: '100%',
                height: '44px',
                border: 'none',
                cursor: 'pointer',
                fontFamily: 'Roboto, sans-serif',
                fontSize: '12px',
                fontWeight: 500,
                letterSpacing: '2px',
                background: patch.enabled ? '#38b060' : '#c43058',
                color: '#f7f7f7',
                borderRadius: '0 0 16px 16px',
                marginTop: 'auto',
                transition: 'all 0.2s ease',
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.background = patch.enabled ? '#29d67a' : '#de2152'
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.background = patch.enabled ? '#38b060' : '#c43058'
              }}
            >
              {patch.enabled ? t('patcher.enabled') : t('patcher.disabled')}
            </button>
          </div>
        ))}
      </div>

      {customizing && (
        <div
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            zIndex: 100,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <div
            style={{
              position: 'absolute',
              inset: 0,
              backgroundColor: 'rgba(0,0,0,0.44)',
            }}
            onClick={() => setCustomizing(null)}
          />
          <div
            style={{
              position: 'relative',
              backgroundColor: '#181830',
              border: '1px solid #252540',
              borderRadius: '16px',
              width: '60%',
              maxWidth: '600px',
              padding: '30px',
              zIndex: 101,
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '15px', marginBottom: '20px' }}>
              <i className={customizing.icon} style={{ fontSize: '28px', color: '#9090a8' }}></i>
              <h2 style={{ fontWeight: 500, fontSize: '20px', color: '#f7f7f7' }}>{customizing.name}</h2>
            </div>
            <h3 style={{ color: '#9090a8', fontSize: '14px', marginBottom: '15px', fontWeight: 400 }}>
              {t('patcher.enterValues')}
            </h3>
            {customizing.values &&
              Object.entries(customizing.values).map(([key, val]) => (
                <div key={key} style={{ marginBottom: '12px' }}>
                  <h4 style={{ color: '#f7f7f7', fontSize: '13px', fontWeight: 400, marginBottom: '4px', textTransform: 'capitalize' }}>
                    {key.replace(/([A-Z])/g, ' $1').trim()}:
                  </h4>
                  <input
                    className="input-field"
                    style={{ width: '100%', padding: '8px 12px', fontSize: '13px' }}
                    value={val}
                    onChange={(e) => updatePatchValue(customizing.id, key, e.target.value)}
                  />
                </div>
              ))}
          </div>
        </div>
      )}
    </div>
  )
}

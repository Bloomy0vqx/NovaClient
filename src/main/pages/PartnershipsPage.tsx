import { useLanguage } from '../i18n'
import { partnerships } from './partnershipsData'

export default function PartnershipsPage() {
  const { t } = useLanguage()

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}>
      <div style={{ display: 'flex', flexDirection: 'column', width: '90%', maxWidth: '1000px' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '20px' }}>
          <h1 style={{ fontWeight: 200, fontSize: '25px', color: 'var(--text-primary)' }}>
            {t('tab.partnerships')}
          </h1>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '20px' }}>
          {partnerships.map((partnership) => (
            <div
              key={partnership.id}
              style={{
                background: 'var(--bg-secondary)',
                borderRadius: '12px',
                padding: '24px',
                border: partnership.featured ? '2px solid var(--accent)' : '1px solid var(--border)',
                cursor: 'pointer',
                transition: 'transform 0.2s, box-shadow 0.2s',
              }}
              onClick={() => window.open(partnership.discord, '_blank')}
            >
              {partnership.featured && (
                <div style={{
                  background: 'var(--accent)',
                  color: 'white',
                  fontSize: '12px',
                  padding: '4px 8px',
                  borderRadius: '4px',
                  display: 'inline-block',
                  marginBottom: '12px',
                }}>
                  Featured
                </div>
              )}
              <div style={{
                width: '64px',
                height: '64px',
                background: 'var(--bg-tertiary)',
                borderRadius: '12px',
                marginBottom: '16px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '32px',
              }}>
                🏢
              </div>
              <h3 style={{ fontWeight: 600, fontSize: '18px', color: 'var(--text-primary)', marginBottom: '8px' }}>
                {partnership.name}
              </h3>
              <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginBottom: '12px', lineHeight: '1.5' }}>
                {partnership.description}
              </p>
              {partnership.discount && (
                <div style={{
                  background: 'rgba(76, 175, 80, 0.1)',
                  color: '#4caf50',
                  fontSize: '14px',
                  padding: '6px 12px',
                  borderRadius: '6px',
                  display: 'inline-block',
                  fontWeight: 600,
                }}>
                  {partnership.discount}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

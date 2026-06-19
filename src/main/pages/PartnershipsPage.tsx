import { useLanguage } from '../i18n'

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
        <div style={{
          textAlign: 'center',
          padding: '80px 0',
          color: '#8b8ba0',
        }}>
          <i className="fa-solid fa-handshake" style={{ fontSize: '48px', marginBottom: '20px', display: 'block' }}></i>
          <p style={{ fontSize: '16px' }}>Coming soon</p>
        </div>
      </div>
    </div>
  )
}

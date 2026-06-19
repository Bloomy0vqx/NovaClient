import orbitLogo from '../../assets/nova-logo.png'
import { useLanguage } from '../../i18n'

export default function Footer() {
  const { t } = useLanguage()
  const links = [
    { icon: 'fa-brands fa-github', url: 'https://github.com/0vqx/Nova-Client' },
    { icon: 'fa-brands fa-youtube', url: 'https://youtube.com/@officialnovaclient' },
    { icon: 'fa-brands fa-discord', url: 'https://discord.gg/yEwyZjzRKH' }
  ]

  return (
    <div
      style={{
        position: 'fixed',
        bottom: 0,
        left: 0,
        width: '100%',
        height: '50px',
        backgroundColor: '#0e0e1a',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        zIndex: 10,
        padding: '0 15px',
        borderTop: '1px solid rgba(255,255,255,0.03)',
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
        <img
          src={orbitLogo}
          alt="Nova Launcher"
          style={{
            width: '28px',
            height: '28px',
            borderRadius: '50%',
            objectFit: 'cover',
            WebkitUserDrag: 'none',
            transition: 'transform 0.2s ease',
          } as any}
          onMouseEnter={(e) => { e.currentTarget.style.transform = 'scale(1.1)' }}
          onMouseLeave={(e) => { e.currentTarget.style.transform = 'scale(1)' }}
        />
        <h4 style={{ color: 'rgba(255,255,255,0.3)', fontSize: '13px', fontWeight: 400, fontFamily: 'Roboto, sans-serif' }}>
          {t('footer.version')}
        </h4>
      </div>

      <ul style={{ display: 'flex', listStyle: 'none', margin: 0, padding: 0, gap: '20px' }}>
        {links.map((link, i) => (
          <li
            key={i}
            style={{ cursor: 'pointer', transition: 'transform 0.2s ease' }}
            onClick={() => window.nova.shell.openExternal(link.url)}
            onMouseEnter={(e) => { e.currentTarget.style.transform = 'scale(1.15)' }}
            onMouseLeave={(e) => { e.currentTarget.style.transform = 'scale(1)' }}
          >
            <i className={link.icon} style={{ color: 'rgba(255,255,255,0.4)', fontSize: '18px', transition: 'color 0.2s ease' }}></i>
          </li>
        ))}
      </ul>

      <h4 style={{ color: 'rgba(255,255,255,0.3)', fontSize: '12px', fontWeight: 300, fontFamily: 'Roboto, sans-serif' }}>
        {t('footer.notAffiliated')}
      </h4>
    </div>
  )
}

import { Card, CardItem } from '../components/ui/Card'
import { useLanguage } from '../i18n'

export default function AboutPage() {
  const { t } = useLanguage()

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}>
      <Card
        icon="fa-solid fa-circle-info"
        title={t('about.title')}
        subtitle={t('about.subtitle')}
        className="about-card"
        contentClass="vertical-card-container"
      >
        <div style={{ display: 'flex', flexDirection: 'column', width: '100%' }}>
          <CardItem title={t('about.aboutUs')} icon="fa-solid fa-user">
            <p style={{ textAlign: 'center', fontSize: 'smaller', fontWeight: 300, letterSpacing: '0.55px', lineHeight: 1.5 }}>
              {t('about.aboutDesc')}
              <br />
              {t('about.notAffiliated')}
            </p>
          </CardItem>
          <div style={{ display: 'flex', flexDirection: 'row', justifyContent: 'center', gap: '15px' }}>
            <CardItem title="Nova Launcher" icon="fa-brands fa-discord" subtitle={t('about.joinCommunity')} className="little-card">
              <button className="btn-blue" style={{ background: '#5865F2' }} onClick={() => window.nova.shell.openExternal('https://discord.gg/invite/yEwyZjzRKH')}>
                <i className="fa-brands fa-discord" style={{ marginRight: '5px' }}></i>
                {t('about.discord')}
              </button>
            </CardItem>
            <CardItem title="About Launcher" icon="fa-solid fa-code-branch" subtitle={`Electron: v${navigator.userAgent.match(/Electron\/([\d.]+)/)?.[1] || '?'} • Chrome: v${navigator.userAgent.match(/Chrome\/([\d.]+)/)?.[1] || '?'}`} className="little-card">
              <h4 style={{ marginTop: '3px', fontWeight: 400, textAlign: 'center' }}>{t('about.version')}</h4>
              <button className="btn-blue" style={{ marginTop: '8px' }} onClick={() => window.nova.shell.openNovaFolder()}>
                <i className="fa-solid fa-folder-open" style={{ marginRight: '5px' }}></i>
                {t('about.openNovaFolder')}
              </button>
            </CardItem>
            <CardItem title="Minecraft" icon="fa-solid fa-moon" subtitle={t('about.officialWebsite')} className="little-card">
              <button className="btn-blue" onClick={() => window.nova.shell.openExternal('https://minecraft.net')}>
                <i className="fa-solid fa-up-right-from-square" style={{ marginRight: '5px' }}></i>
                Minecraft
              </button>
            </CardItem>
          </div>
        </div>
      </Card>
    </div>
  )
}

import { useHudStore } from '../../stores/useHudStore'

export default function BedwarsStatsHud() {
  const { bedwarsStats } = useHudStore()

  if (!bedwarsStats.enabled) return null

  const stats = [
    { label: 'Kills', value: 0, show: bedwarsStats.showKills },
    { label: 'Deaths', value: 0, show: bedwarsStats.showDeaths },
    { label: 'Wins', value: 0, show: bedwarsStats.showWins },
    { label: 'Losses', value: 0, show: bedwarsStats.showLosses },
    { label: 'K/D', value: '0.0', show: bedwarsStats.showKDR },
  ].filter((s) => s.show)

  return (
    <div
      style={{
        position: 'fixed',
        left: bedwarsStats.position.x,
        top: bedwarsStats.position.y,
        transform: `scale(${bedwarsStats.scale})`,
        transformOrigin: 'top left',
        zIndex: 9995,
        pointerEvents: 'none',
        background: '#00000080',
        borderRadius: '6px',
        padding: '8px 12px',
        border: '1px solid #ffffff22',
      }}
    >
      <div
        style={{
          fontSize: '12px',
          fontWeight: 700,
          color: '#FFAA00',
          marginBottom: '4px',
          fontFamily: 'Roboto, sans-serif',
        }}
      >
        Bedwars Stats
      </div>
      {stats.map((stat) => (
        <div
          key={stat.label}
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            gap: '12px',
            fontSize: '11px',
            fontFamily: 'Roboto, sans-serif',
          }}
        >
          <span style={{ color: '#ffffff99' }}>{stat.label}</span>
          <span style={{ color: '#FFFFFF', fontWeight: 600 }}>{stat.value}</span>
        </div>
      ))}
    </div>
  )
}

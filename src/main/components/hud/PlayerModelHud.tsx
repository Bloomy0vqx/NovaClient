import { useState, useEffect } from 'react'
import { useHudStore } from '../../stores/useHudStore'
import { useAuthStore } from '../../stores/useAuthStore'

export default function PlayerModelHud() {
  const { playerModel } = useHudStore()
  const { accounts, activeAccount } = useAuthStore()
  const [rotation, setRotation] = useState(0)
  const [skinUrl, setSkinUrl] = useState('')

  useEffect(() => {
    const account = accounts.find((a) => a.id === activeAccount)
    if (account?.uuid) {
      setSkinUrl(`https://mc-heads.net/body/${account.uuid}/100`)
    }
  }, [accounts, activeAccount])

  useEffect(() => {
    if (!playerModel.enabled || !playerModel.dynamicRotation) return
    let raf: number
    const animate = () => {
      setRotation((prev) => (prev + 0.5) % 360)
      raf = requestAnimationFrame(animate)
    }
    raf = requestAnimationFrame(animate)
    return () => cancelAnimationFrame(raf)
  }, [playerModel.enabled, playerModel.dynamicRotation])

  if (!playerModel.enabled) return null

  return (
    <div
      style={{
        position: 'fixed',
        left: playerModel.position.x,
        top: playerModel.position.y,
        transform: `scale(${playerModel.scale})`,
        transformOrigin: 'top left',
        zIndex: 9994,
        pointerEvents: 'none',
      }}
    >
      <div
        style={{
          width: '64px',
          height: '120px',
          overflow: 'hidden',
          borderRadius: '8px',
          background: '#00000040',
          border: '1px solid #ffffff11',
        }}
      >
        {skinUrl ? (
          <img
            src={skinUrl}
            alt="Player Model"
            style={{
              width: '100%',
              height: '100%',
              objectFit: 'contain',
              transform: `rotateY(${rotation}deg)`,
              imageRendering: 'pixelated',
            }}
          />
        ) : (
          <div
            style={{
              width: '100%',
              height: '100%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <i className="fa-solid fa-person" style={{ fontSize: '24px', color: '#ffffff33' }}></i>
          </div>
        )}
      </div>
    </div>
  )
}

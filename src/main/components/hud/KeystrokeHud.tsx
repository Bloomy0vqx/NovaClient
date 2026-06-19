import { useState, useEffect, useCallback } from 'react'
import { useHudStore } from '../../stores/useHudStore'

interface KeyState {
  w: boolean
  a: boolean
  s: boolean
  d: boolean
  lmb: boolean
  rmb: boolean
}

export default function KeystrokeHud() {
  const { keystroke } = useHudStore()
  const [keys, setKeys] = useState<KeyState>({ w: false, a: false, s: false, d: false, lmb: false, rmb: false })
  const [cps, setCps] = useState(0)
  const [clicks, setClicks] = useState<number[]>([])

  const handleKeyDown = useCallback((e: KeyboardEvent) => {
    const key = e.key.toLowerCase()
    if (['w', 'a', 's', 'd'].includes(key)) {
      setKeys((prev) => ({ ...prev, [key]: true }))
    }
  }, [])

  const handleKeyUp = useCallback((e: KeyboardEvent) => {
    const key = e.key.toLowerCase()
    if (['w', 'a', 's', 'd'].includes(key)) {
      setKeys((prev) => ({ ...prev, [key]: false }))
    }
  }, [])

  const handleMouseDown = useCallback((e: MouseEvent) => {
    if (e.button === 0) setKeys((prev) => ({ ...prev, lmb: true }))
    if (e.button === 2) setKeys((prev) => ({ ...prev, rmb: true }))
    const now = Date.now()
    setClicks((prev) => [...prev.filter((t) => now - t < 1000), now])
  }, [])

  const handleMouseUp = useCallback((e: MouseEvent) => {
    if (e.button === 0) setKeys((prev) => ({ ...prev, lmb: false }))
    if (e.button === 2) setKeys((prev) => ({ ...prev, rmb: false }))
  }, [])

  useEffect(() => {
    window.addEventListener('keydown', handleKeyDown)
    window.addEventListener('keyup', handleKeyUp)
    window.addEventListener('mousedown', handleMouseDown)
    window.addEventListener('mouseup', handleMouseUp)
    return () => {
      window.removeEventListener('keydown', handleKeyDown)
      window.removeEventListener('keyup', handleKeyUp)
      window.removeEventListener('mousedown', handleMouseDown)
      window.removeEventListener('mouseup', handleMouseUp)
    }
  }, [handleKeyDown, handleKeyUp, handleMouseDown, handleMouseUp])

  useEffect(() => {
    const interval = setInterval(() => {
      const now = Date.now()
      setClicks((prev) => prev.filter((t) => now - t < 1000))
    }, 100)
    return () => clearInterval(interval)
  }, [])

  useEffect(() => {
    setCps(clicks.length)
  }, [clicks])

  if (!keystroke.enabled) return null

  const boxStyle: React.CSSProperties = {
    width: '44px',
    height: '44px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: keystroke.style === 'boxed' ? '6px' : keystroke.style === 'minimal' ? '0' : '4px',
    background: keystroke.backgroundColor,
    border: keystroke.style === 'boxed' ? `1px solid ${keystroke.accentColor}33` : 'none',
    fontSize: '14px',
    fontWeight: 600,
    color: keystroke.textColor,
    fontFamily: 'Roboto Mono, monospace',
    transition: 'all 0.05s ease',
    userSelect: 'none',
  }

  const activeKeyStyle: React.CSSProperties = {
    ...boxStyle,
    background: keystroke.accentColor + '40',
    color: keystroke.accentColor,
    border: keystroke.style === 'boxed' ? `1px solid ${keystroke.accentColor}80` : 'none',
    transform: 'scale(0.95)',
  }

  const getKeyStyle = (active: boolean) => (active ? activeKeyStyle : boxStyle)

  return (
    <div
      style={{
        position: 'fixed',
        left: keystroke.position.x,
        top: keystroke.position.y,
        transform: `scale(${keystroke.scale})`,
        transformOrigin: 'top left',
        zIndex: 9998,
        pointerEvents: 'none',
        display: 'flex',
        flexDirection: 'column',
        gap: '4px',
      }}
    >
      {keystroke.showWasd && (
        <>
          <div style={{ display: 'flex', gap: '4px' }}>
            <div style={getKeyStyle(keys.w)}>W</div>
          </div>
          <div style={{ display: 'flex', gap: '4px' }}>
            <div style={getKeyStyle(keys.a)}>A</div>
            <div style={getKeyStyle(keys.s)}>S</div>
            <div style={getKeyStyle(keys.d)}>D</div>
          </div>
        </>
      )}
      {keystroke.showCps && (
        <div
          style={{
            ...boxStyle,
            width: 'auto',
            padding: '0 10px',
            marginTop: keystroke.showWasd ? '4px' : '0',
            fontSize: '11px',
          }}
        >
          {cps} CPS
        </div>
      )}
    </div>
  )
}

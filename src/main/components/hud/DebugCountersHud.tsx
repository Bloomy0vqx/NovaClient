import { useState, useEffect } from 'react'
import { useHudStore } from '../../stores/useHudStore'

export default function DebugCountersHud() {
  const { debugCounters } = useHudStore()
  const [fps, setFps] = useState(0)
  const [ram, setRam] = useState({ used: 0, total: 0 })
  const [coords, setCoords] = useState({ x: 0, y: 0, z: 0 })

  useEffect(() => {
    let frames = 0
    let lastTime = performance.now()
    const measureFps = () => {
      frames++
      const now = performance.now()
      if (now - lastTime >= 1000) {
        setFps(frames)
        frames = 0
        lastTime = now
      }
      requestAnimationFrame(measureFps)
    }
    const raf = requestAnimationFrame(measureFps)
    return () => cancelAnimationFrame(raf)
  }, [])

  useEffect(() => {
    const updateRam = () => {
      if ('memory' in performance) {
        const mem = (performance as any).memory
        setRam({ used: Math.round(mem.usedJSHeapSize / 1048576), total: Math.round(mem.jsHeapSizeLimit / 1048576) })
      } else {
        setRam({ used: 0, total: 0 })
      }
    }
    const interval = setInterval(updateRam, 1000)
    return () => clearInterval(interval)
  }, [])

  useEffect(() => {
    const handleKey = (e: KeyboardEvent) => {
      if (e.key === 'c' && e.ctrlKey) {
        setCoords({ x: Math.random() * 1000, y: 64, z: Math.random() * 1000 })
      }
    }
    window.addEventListener('keydown', handleKey)
    return () => window.removeEventListener('keydown', handleKey)
  }, [])

  if (!debugCounters.enabled) return null

  const lines: string[] = []
  if (debugCounters.showFps) lines.push(`FPS: ${fps}`)
  if (debugCounters.showRam) lines.push(`RAM: ${ram.used}MB / ${ram.total}MB`)
  if (debugCounters.showCoords) lines.push(`XYZ: ${coords.x.toFixed(1)} ${coords.y} ${coords.z.toFixed(1)}`)

  return (
    <div
      style={{
        position: 'fixed',
        left: debugCounters.position.x,
        top: debugCounters.position.y,
        transform: `scale(${debugCounters.scale})`,
        transformOrigin: 'top left',
        zIndex: 9996,
        pointerEvents: 'none',
        display: 'flex',
        flexDirection: 'column',
        gap: '2px',
      }}
    >
      {lines.map((line, i) => (
        <div
          key={i}
          style={{
            background: '#00000080',
            padding: '2px 8px',
            borderRadius: '3px',
            color: '#00FF00',
            fontSize: '11px',
            fontFamily: 'Roboto Mono, monospace',
            fontWeight: 500,
          }}
        >
          {line}
        </div>
      ))}
    </div>
  )
}

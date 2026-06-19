import { useHudStore } from '../../stores/useHudStore'

export default function CrosshairHud() {
  const { crosshair } = useHudStore()

  if (!crosshair.enabled) return null

  const size = crosshair.size
  const half = size / 2
  const gap = 4
  const thickness = 2
  const dotSize = 3

  const renderCross = () => {
    if (crosshair.mode === 'dot') {
      return (
        <div
          style={{
            width: dotSize,
            height: dotSize,
            borderRadius: '50%',
            background: crosshair.color,
            position: 'absolute',
            left: half - dotSize / 2,
            top: half - dotSize / 2,
          }}
        />
      )
    }

    if (crosshair.mode === 'circle') {
      return (
        <div
          style={{
            width: size * 0.6,
            height: size * 0.6,
            border: `2px solid ${crosshair.color}`,
            borderRadius: '50%',
            position: 'absolute',
            left: half - (size * 0.6) / 2,
            top: half - (size * 0.6) / 2,
          }}
        />
      )
    }

    return (
      <>
        {/* Top */}
        <div
          style={{
            position: 'absolute',
            left: half - thickness / 2,
            top: half - gap / 2 - (size * 0.3),
            width: thickness,
            height: size * 0.3,
            background: crosshair.color,
          }}
        />
        {/* Bottom */}
        <div
          style={{
            position: 'absolute',
            left: half - thickness / 2,
            top: half + gap / 2,
            width: thickness,
            height: size * 0.3,
            background: crosshair.color,
          }}
        />
        {/* Left */}
        <div
          style={{
            position: 'absolute',
            left: half - gap / 2 - (size * 0.3),
            top: half - thickness / 2,
            width: size * 0.3,
            height: thickness,
            background: crosshair.color,
          }}
        />
        {/* Right */}
        <div
          style={{
            position: 'absolute',
            left: half + gap / 2,
            top: half - thickness / 2,
            width: size * 0.3,
            height: thickness,
            background: crosshair.color,
          }}
        />
        {crosshair.dot && (
          <div
            style={{
              width: dotSize,
              height: dotSize,
              borderRadius: '50%',
              background: crosshair.color,
              position: 'absolute',
              left: half - dotSize / 2,
              top: half - dotSize / 2,
            }}
          />
        )}
      </>
    )
  }

  return (
    <div
      style={{
        position: 'fixed',
        left: '50%',
        top: '50%',
        transform: `translate(-50%, -50%) scale(${crosshair.scale})`,
        width: size,
        height: size,
        zIndex: 9997,
        pointerEvents: 'none',
      }}
    >
      {renderCross()}
    </div>
  )
}

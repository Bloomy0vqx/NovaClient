import { useEffect, useState } from 'react'
import type { CosmeticItem } from '../../types'
import { useAuthStore } from '../../stores/useAuthStore'

interface Props {
  cosmetics: CosmeticItem[]
  previewId: string | null
  equipped: CosmeticItem[]
  onClose: () => void
  onToggleEquip: (id: string) => void
}

export default function CosmeticPreviewModal({ cosmetics, previewId, equipped, onClose, onToggleEquip }: Props) {
  const [currentIndex, setCurrentIndex] = useState(0)
  const account = useAuthStore((s) => s.account)
  const uuid = account?.uuid || '8667ba71b85a4004af54457a9734eed7' // fallback to default Steve UUID

  const previewed = cosmetics.find((c) => c.id === previewId)

  useEffect(() => {
    if (!previewed) return
    const idx = cosmetics.findIndex((c) => c.id === previewId)
    setCurrentIndex(idx)
  }, [previewId])

  useEffect(() => {
    const handleKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose()
      if (e.key === 'ArrowLeft') setCurrentIndex((i) => (i - 1 + cosmetics.length) % cosmetics.length)
      if (e.key === 'ArrowRight') setCurrentIndex((i) => (i + 1) % cosmetics.length)
    }
    window.addEventListener('keydown', handleKey)
    return () => window.removeEventListener('keydown', handleKey)
  }, [cosmetics.length, onClose])

  const current = cosmetics[currentIndex] || previewed
  if (!current) return null

  const isEquipped = equipped.some((c) => c.id === current.id)

  const rarityColors: Record<string, string> = {
    common: '#a0a0c0',
    rare: '#00d4ff',
    epic: '#7b5bff',
    legendary: '#ff5b9e',
  }

  const typeIconMap: Record<string, string> = {
    cape: 'fa-solid fa-shirt',
    wings: 'fa-solid fa-feather',
    particle: 'fa-solid fa-sparkles',
    hat: 'fa-solid fa-hat-wizard',
    bandana: 'fa-solid fa-bandage',
    suit: 'fa-solid fa-user-tie',
    shoes: 'fa-solid fa-shoe-prints',
    accessory: 'fa-solid fa-glasses',
    cloak: 'fa-solid fa-mask',
    pet: 'fa-solid fa-dog',
    emote: 'fa-solid fa-face-smile',
    trail: 'fa-solid fa-shoe-prints',
    deathfx: 'fa-solid fa-skull',
  }

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
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
          backgroundColor: 'rgba(0,0,0,0.8)',
        }}
        onClick={onClose}
      />

      <div
        style={{
          position: 'relative',
          width: '480px',
          maxWidth: '95vw',
          maxHeight: '90vh',
          overflow: 'hidden',
          backgroundColor: '#2e2e3e',
          borderRadius: '15px',
        }}
      >
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '12px 20px',
            borderBottom: '1px solid rgba(255,255,255,0.04)',
          }}
        >
          <h2 style={{ fontSize: '14px', color: 'rgba(255,255,255,0.8)', fontWeight: 400 }}>
            Cosmetic Preview
          </h2>
          <button
            onClick={onClose}
            style={{
              background: 'none',
              border: 'none',
              color: 'rgba(255,255,255,0.4)',
              cursor: 'pointer',
              fontSize: '18px',
              padding: '6px',
            }}
          >
            <i className="fa-solid fa-xmark"></i>
          </button>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '30px 24px' }}>
          <div
            style={{
              width: '120px',
              height: '168px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              marginBottom: '20px',
              background: 'linear-gradient(to bottom, rgba(255,255,255,0.02), rgba(0,0,0,0.2))',
              borderRadius: '16px',
              border: '1px solid rgba(255,255,255,0.05)',
              boxShadow: `0 0 40px ${current.color}40`,
              position: 'relative',
              overflow: 'hidden',
            }}
          >
            <img 
               src={`https://crafatar.com/renders/body/${uuid}?overlay=true`}
               alt="Minecraft Skin"
               style={{ height: '140px', position: 'absolute', opacity: 0.8, filter: 'brightness(0.6)' }}
            />
            <i
              className={typeIconMap[current.type] || 'fa-solid fa-box'}
              style={{
                fontSize: '70px',
                color: current.color || '#fff',
                filter: `drop-shadow(0 0 20px ${current.color})`,
                position: 'relative',
                zIndex: 2,
              }}
            ></i>
          </div>

          <div style={{ textAlign: 'center', marginBottom: '16px' }}>
            <h3 style={{ fontSize: '18px', fontWeight: 400, color: current.color || 'var(--text-primary)' }}>
              {current.name}
            </h3>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', marginTop: '4px' }}>
              <span style={{ fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '1px', color: rarityColors[current.rarity] }}>
                {current.rarity}
              </span>
              <span style={{ fontSize: '11px', color: '#3b3b3b' }}>{current.category}</span>
            </div>
          </div>

          <button
            onClick={() => onToggleEquip(current.id)}
            style={{
              width: '100%',
              padding: '10px 0',
              borderRadius: '10px',
              border: 'none',
              cursor: 'pointer',
              fontFamily: 'Roboto, sans-serif',
              fontSize: '13px',
              fontWeight: 500,
              background: isEquipped ? 'rgba(255,255,255,0.06)' : 'linear-gradient(135deg, #00d4ff, #0099cc)',
              color: 'var(--text-primary)',
              borderColor: isEquipped ? 'rgba(0,212,255,0.2)' : 'none',
              transition: 'all 0.3s',
            }}
          >
            {isEquipped ? 'Equipped' : 'Equip'}
          </button>
        </div>

        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '16px',
            padding: '12px 20px',
            borderTop: '1px solid rgba(255,255,255,0.04)',
            fontSize: '12px',
            color: '#3b3b3b',
          }}
        >
          <button
            onClick={() => setCurrentIndex((i) => (i - 1 + cosmetics.length) % cosmetics.length)}
            style={{
              background: 'none',
              border: 'none',
              color: 'rgba(255,255,255,0.4)',
              cursor: 'pointer',
              fontFamily: 'Roboto, sans-serif',
              fontSize: '12px',
            }}
          >
            ← Prev
          </button>
          <span>
            {currentIndex + 1} / {cosmetics.length}
          </span>
          <button
            onClick={() => setCurrentIndex((i) => (i + 1) % cosmetics.length)}
            style={{
              background: 'none',
              border: 'none',
              color: 'rgba(255,255,255,0.4)',
              cursor: 'pointer',
              fontFamily: 'Roboto, sans-serif',
              fontSize: '12px',
            }}
          >
            Next →
          </button>
        </div>
      </div>
    </div>
  )
}

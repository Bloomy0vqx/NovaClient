import { useEffect, useCallback, useState } from 'react'
import { useModStore } from '../stores/useModStore'
import { useSettingsStore } from '../stores/useSettingsStore'
import { useLanguage } from '../i18n'
import type { ModCategory } from '../types'

const categoryKeys: Record<ModCategory, string> = {
  utility: 'mods.utility',
  performance: 'mods.performance',
  visual: 'mods.visual',
  pvp: 'mods.pvp',
  minigame: 'mods.minigame',
  cosmetic: 'mods.cosmetic',
}

const allCategories: (ModCategory | 'all')[] = ['all', 'performance', 'utility', 'visual', 'minigame', 'cosmetic']

export default function ModsPage() {
  const { mods, search, categoryFilter, isLoading, viewMode, setSearch, setCategoryFilter, setViewMode, toggleMod, installMod, uninstallMod, updateMods, fetchMods } = useModStore()
  const { t } = useLanguage()
  const { selectedVersion, versions, settings } = useSettingsStore()
  const [isUpdating, setIsUpdating] = useState(false)

  const getDisplayVersion = useCallback(() => {
    const latestRelease = versions.filter(v => v.type === 'release')[0]?.id || '1.21.4'
    return selectedVersion === 'latest_release' ? latestRelease : selectedVersion
  }, [selectedVersion, versions])

  useEffect(() => {
    fetchMods(getDisplayVersion(), settings.selectedLoader)
  }, [selectedVersion, settings.selectedLoader])

  const filtered = mods.filter((m) => {
    if (viewMode === 'installed' && !m.installed) return false
    if (categoryFilter !== 'all' && m.category !== categoryFilter) return false
    if (search && !m.name.toLowerCase().includes(search.toLowerCase()) && !m.description.toLowerCase().includes(search.toLowerCase())) return false
    return true
  })

  const handleUpdateMods = async () => {
    setIsUpdating(true)
    try {
      await updateMods(getDisplayVersion(), settings.selectedLoader)
    } finally {
      setIsUpdating(false)
    }
  }

  const installed = useModStore((s) => s.installedMods)
  const enabled = installed.filter((m) => m.enabled)

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}>
      <div style={{ display: 'flex', flexDirection: 'column', width: '90%', maxWidth: '1000px' }}>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            marginBottom: '20px',
          }}
        >
          <h1 style={{ fontWeight: 200, fontSize: '25px', color: 'var(--text-primary)' }}>
            {t('mods.title')}
          </h1>
          <div style={{ display: 'flex', gap: '10px', color: '#9090a8', fontSize: '12px' }}>
            <span style={{ color: '#8B5CF6' }}>
              <i className="fa-solid fa-cube" style={{ marginRight: '4px' }}></i>
              MC {getDisplayVersion()}
            </span>
            <span style={{ color: settings.selectedLoader !== 'none' ? '#3cc065' : '#c43058' }}>
              <i className={`fa-solid ${settings.selectedLoader === 'fabric' ? 'fa-scroll' : settings.selectedLoader === 'forge' ? 'fa-hammer' : 'fa-triangle-exclamation'}`} style={{ marginRight: '4px' }}></i>
              {settings.selectedLoader === 'none' ? 'No loader' : settings.selectedLoader.charAt(0).toUpperCase() + settings.selectedLoader.slice(1)}
            </span>
            <span>{t('mods.installed', { count: installed.length })}</span>
            <span>{t('mods.enabled', { count: enabled.length })}</span>
          </div>
        </div>

        <div style={{ display: 'flex', gap: '8px', marginBottom: '20px', flexWrap: 'wrap' }}>
          <div style={{ display: 'flex', gap: '4px', marginRight: '8px' }}>
            {(['browse', 'installed'] as const).map((mode) => (
              <button
                key={mode}
                onClick={() => setViewMode(mode)}
                style={{
                  padding: '8px 16px',
                  borderRadius: '6px',
                  border: 'none',
                  background: viewMode === mode ? '#8B5CF6' : '#252540',
                  color: 'var(--text-primary)',
                  fontSize: '12px',
                  cursor: 'pointer',
                  fontFamily: 'Roboto, sans-serif',
                  fontWeight: viewMode === mode ? 600 : 400,
                  transition: 'all 0.2s ease',
                }}
              >
                {mode === 'browse' ? t('mods.browse') : `${t('mods.installedBadge')} (${installed.length})`}
              </button>
            ))}
          </div>
          {viewMode === 'installed' && installed.length > 0 && (
            <button
              onClick={handleUpdateMods}
              disabled={isUpdating}
              style={{
                padding: '8px 16px',
                borderRadius: '6px',
                border: 'none',
                background: isUpdating ? '#252540' : '#8B5CF6',
                color: 'var(--text-primary)',
                fontSize: '12px',
                cursor: isUpdating ? 'wait' : 'pointer',
                fontFamily: 'Roboto, sans-serif',
                transition: 'all 0.2s ease',
              }}
            >
              {isUpdating ? (
                <><i className="fa-solid fa-spinner fa-spin" style={{ marginRight: '6px' }}></i>Updating...</>
              ) : (
                <><i className="fa-solid fa-arrows-rotate" style={{ marginRight: '6px' }}></i>{t('mods.updateForVersion', { version: getDisplayVersion() })}</>
              )}
            </button>
          )}
          {viewMode === 'browse' && (
            <>
              {allCategories.map((cat) => (
                <button
                  key={cat}
                  onClick={() => setCategoryFilter(cat)}
                  style={{
                    padding: '8px 16px',
                    borderRadius: '6px',
                    border: 'none',
                    background: categoryFilter === cat ? '#8B5CF6' : '#252540',
                    color: 'var(--text-primary)',
                    fontSize: '12px',
                    cursor: 'pointer',
                    fontFamily: 'Roboto, sans-serif',
                    transition: 'all 0.2s ease',
                  }}
                >
                  {cat === 'all' ? t('mods.all') : t(categoryKeys[cat] as any)}
                </button>
              ))}
            </>
          )}
        </div>

        {viewMode === 'browse' && (
          <div style={{ marginBottom: '20px' }}>
            <input
              className="input-field"
              style={{ width: '100%', maxWidth: '400px', padding: '8px 12px', borderRadius: '8px' }}
              placeholder={t('mods.searchPlaceholder')}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        )}

        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          {isLoading ? (
            <div style={{ textAlign: 'center', padding: '60px 0', color: '#9090a8' }}>
              <i className="fa-solid fa-spinner fa-spin" style={{ fontSize: '24px', marginBottom: '10px' }}></i>
              <p>{t('mods.loading')}</p>
            </div>
          ) : (
            filtered.map((mod) => (
              <div
                key={mod.id}
                style={{
                  backgroundColor: '#181830',
                  borderRadius: '12px',
                  border: '1px solid #252540',
                  padding: '15px',
                  display: 'flex',
                  alignItems: 'flex-start',
                  gap: '15px',
                  transition: 'all 0.25s ease',
                }}
                onMouseEnter={(e) => { e.currentTarget.style.borderColor = 'rgba(139,92,246,0.3)' }}
                onMouseLeave={(e) => { e.currentTarget.style.borderColor = '#252540' }}
              >
                <div
                  style={{
                    width: '50px',
                    height: '50px',
                    borderRadius: '10px',
                    background: '#161628',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '24px',
                    color: '#9090a8',
                    flexShrink: 0,
                    overflow: 'hidden',
                  }}
                >
                  {mod.iconUrl ? (
                    <img
                      src={mod.iconUrl}
                      alt={mod.name}
                      style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '10px' }}
                      onError={(e) => {
                        e.currentTarget.style.display = 'none'
                        e.currentTarget.parentElement!.innerHTML = '<i class="fa-solid fa-puzzle-piece"></i>'
                      }}
                    />
                  ) : (
                    <i className="fa-solid fa-puzzle-piece"></i>
                  )}
                </div>

                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '10px' }}>
                    <div>
                      <h3 style={{ fontWeight: 400, fontSize: '16px', color: 'var(--text-primary)' }}>{mod.name}</h3>
                      <p style={{ fontSize: '12px', color: '#9090a8', marginTop: '2px' }}>{mod.description}</p>
                    </div>
                    {mod.installed && (
                      <span style={{ color: '#3cc065', fontSize: '11px', whiteSpace: 'nowrap' }}>
                        <i className="fa-solid fa-check" style={{ marginRight: '4px' }}></i>
                        {t('mods.installedBadge')}
                      </span>
                    )}
                  </div>

                  <div style={{ display: 'flex', gap: '15px', marginTop: '8px', fontSize: '11px', color: '#9090a8' }}>
                    <span>{mod.author}</span>
                    <span>v{mod.version}</span>
                    <span className="capitalize">{t(categoryKeys[mod.category] as any)}</span>
                    <span>{t('mods.downloads', { count: (mod.downloads / 1000000).toFixed(1) })}</span>
                  </div>

                  <div style={{ display: 'flex', gap: '8px', marginTop: '10px' }}>
                    {mod.isInstalling ? (
                      <button
                        disabled
                        style={{
                          padding: '6px 16px',
                          borderRadius: '6px',
                          border: 'none',
                          background: 'rgba(139, 92, 246, 0.1)',
                          color: '#8B5CF6',
                          fontSize: '11px',
                          cursor: 'wait',
                          fontFamily: 'Roboto, sans-serif',
                          opacity: 0.7,
                        }}
                      >
                        <i className="fa-solid fa-spinner fa-spin" style={{ marginRight: '4px' }}></i>
                        {t('mods.installing')}
                      </button>
                    ) : mod.installed ? (
                      <>
                        <button
                          onClick={() => toggleMod(mod.id)}
                          style={{
                            padding: '6px 16px',
                            borderRadius: '6px',
                            border: 'none',
                            background: mod.enabled ? '#38b060' : 'rgba(255,255,255,0.06)',
                            color: 'var(--text-primary)',
                            fontSize: '11px',
                            cursor: 'pointer',
                            fontFamily: 'Roboto, sans-serif',
                            transition: 'all 0.2s ease',
                          }}
                          onMouseEnter={(e) => {
                            e.currentTarget.style.background = mod.enabled ? '#48d575' : 'rgba(255,255,255,0.1)'
                          }}
                          onMouseLeave={(e) => {
                            e.currentTarget.style.background = mod.enabled ? '#38b060' : 'rgba(255,255,255,0.06)'
                          }}
                        >
                          {mod.enabled ? t('mods.enabledButton') : t('mods.disabledButton')}
                        </button>
                        <button
                          onClick={() => uninstallMod(mod.id)}
                          style={{
                            padding: '6px 16px',
                            borderRadius: '6px',
                            border: 'none',
                            background: 'rgba(196, 48, 88, 0.15)',
                            color: '#c43058',
                            fontSize: '11px',
                            cursor: 'pointer',
                            fontFamily: 'Roboto, sans-serif',
                            transition: 'all 0.2s ease',
                          }}
                          onMouseEnter={(e) => { e.currentTarget.style.background = 'rgba(196, 48, 88, 0.25)' }}
                          onMouseLeave={(e) => { e.currentTarget.style.background = 'rgba(196, 48, 88, 0.15)' }}
                        >
                          <i className="fa-solid fa-trash" style={{ marginRight: '4px' }}></i>
                          {t('mods.uninstall')}
                        </button>
                      </>
                    ) : (
                      <button
                        onClick={() => installMod(mod.id)}
                        style={{
                          padding: '6px 16px',
                          borderRadius: '6px',
                          border: 'none',
                          background: '#8B5CF6',
                          color: 'var(--text-primary)',
                          fontSize: '11px',
                          cursor: 'pointer',
                          fontFamily: 'Roboto, sans-serif',
                          transition: 'all 0.2s ease',
                        }}
                        onMouseEnter={(e) => { e.currentTarget.style.background = '#7C3AED' }}
                        onMouseLeave={(e) => { e.currentTarget.style.background = '#8B5CF6' }}
                      >
                        <i className="fa-solid fa-download" style={{ marginRight: '4px' }}></i>
                        {t('mods.install')}
                      </button>
                    )}
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        {!isLoading && filtered.length === 0 && (
          <div style={{ textAlign: 'center', padding: '60px 0', color: '#9090a8' }}>
            <p>{t('mods.noResults')}</p>
          </div>
        )}
      </div>
    </div>
  )
}

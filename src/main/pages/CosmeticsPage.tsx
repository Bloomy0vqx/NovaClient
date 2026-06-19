import { useEffect, useState, useCallback } from 'react'
import { useSettingsStore } from '../stores/useSettingsStore'
import { useLanguage } from '../i18n'
import type { ModCategory } from '../types'

interface CosmeticMod {
  id: string
  name: string
  description: string
  version: string
  author: string
  category: ModCategory
  downloads: number
  iconUrl?: string
  installed: boolean
  enabled: boolean
  fileName?: string
  isInstalling?: boolean
}

interface CustomCape {
  id: string
  name: string
  filePath: string
}

const categoryKeys: Record<ModCategory, string> = {
  utility: 'mods.utility',
  performance: 'mods.performance',
  visual: 'mods.visual',
  pvp: 'mods.pvp',
  minigame: 'mods.minigame',
  cosmetic: 'mods.cosmetic',
}

const cosmeticCategories: (ModCategory | 'all')[] = ['all', 'cosmetic']

export default function CosmeticsPage() {
  const { t } = useLanguage()
  const { selectedVersion, versions, settings } = useSettingsStore()
  const [cosmetics, setCosmetics] = useState<CosmeticMod[]>([])
  const [customCapes, setCustomCapes] = useState<CustomCape[]>([])
  const [capeName, setCapeName] = useState('')
  const [search, setSearch] = useState('')
  const [categoryFilter, setCategoryFilter] = useState<'cosmetic' | 'all'>('cosmetic')
  const [isLoading, setIsLoading] = useState(false)

  const getDisplayVersion = useCallback(() => {
    const latestRelease = versions.filter(v => v.type === 'release')[0]?.id || '1.21.4'
    return selectedVersion === 'latest_release' ? latestRelease : selectedVersion
  }, [selectedVersion, versions])

  const fetchCosmetics = useCallback(async () => {
    setIsLoading(true)
    try {
      const version = getDisplayVersion()
      let url = 'https://api.modrinth.com/v2/search?limit=30&facets=[["categories:cosmetic"]]'
      
      if (version && version.startsWith('1.')) {
        url += `&facets=[["versions:${version}"],["categories:cosmetic"]]`
      }

      const res = await fetch(url)
      const data = await res.json()

      const fetchedCosmetics: CosmeticMod[] = (data.hits || []).map((hit: any) => ({
        id: hit.project_id,
        name: hit.title,
        description: hit.description,
        version: hit.latest_version,
        author: hit.author,
        category: 'cosmetic' as ModCategory,
        downloads: hit.downloads,
        iconUrl: hit.icon_url || undefined,
        installed: false,
        enabled: false,
      }))

      setCosmetics(fetchedCosmetics)
    } catch (err) {
      console.error('Failed to fetch cosmetics from Modrinth', err)
    } finally {
      setIsLoading(false)
    }
  }, [getDisplayVersion])

  useEffect(() => {
    fetchCosmetics()
  }, [selectedVersion, fetchCosmetics])

  const loadCustomCapes = useCallback(async () => {
    try {
      const capes = await window.nova.customCapes.list()
      setCustomCapes(capes)
    } catch {}
  }, [])

  useEffect(() => {
    loadCustomCapes()
  }, [loadCustomCapes])

  const handleUploadCape = useCallback(async () => {
    try {
      const picked = await window.nova.customCapes.pickFile()
      if (picked.canceled || !picked.filePath) return
      const name = capeName.trim() || 'My Cape'
      const result = await window.nova.customCapes.upload({ name, filePath: picked.filePath })
      if (result.success) {
        setCapeName('')
        loadCustomCapes()
      }
    } catch (err) {
      console.error('Failed to upload cape', err)
    }
  }, [capeName, loadCustomCapes])

  const handleRemoveCape = useCallback(async (capeId: string) => {
    try {
      await window.nova.customCapes.remove({ capeId })
      loadCustomCapes()
    } catch {}
  }, [loadCustomCapes])

  const filtered = cosmetics.filter((m) => {
    if (categoryFilter !== 'all' && m.category !== categoryFilter) return false
    if (search && !m.name.toLowerCase().includes(search.toLowerCase()) && !m.description.toLowerCase().includes(search.toLowerCase())) return false
    return true
  })

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%', padding: '20px' }}>
      <div style={{ display: 'flex', flexDirection: 'column', width: '90%', maxWidth: '1000px' }}>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            marginBottom: '24px',
          }}
        >
          <h3 style={{ color: '#f7f7f7', fontSize: '24px', fontWeight: 600, fontFamily: 'Inter, sans-serif', margin: 0 }}>
            {t('cosmetics.title')}
          </h3>
          <div style={{ display: 'flex', gap: '12px', color: '#8b8ba0', fontSize: '13px', fontFamily: 'Inter, sans-serif' }}>
            <span style={{ color: '#8B5CF6' }}>
              <i className="fa-solid fa-cube" style={{ marginRight: '4px' }}></i>
              MC {getDisplayVersion()}
            </span>
            <span>{cosmetics.length} cosmetics</span>
          </div>
        </div>

        {/* Custom Capes Section */}
        <div style={{ marginBottom: '24px' }}>
          <h4 style={{ color: '#f7f7f7', fontSize: '16px', fontWeight: 600, fontFamily: 'Inter, sans-serif', margin: '0 0 12px 0' }}>
            {t('cosmetics.customCapes') || 'Custom Capes'}
          </h4>
          <div style={{ display: 'flex', gap: '10px', marginBottom: '12px', alignItems: 'center' }}>
            <input
              style={{
                flex: 1,
                maxWidth: '260px',
                padding: '10px 14px',
                background: '#181830',
                border: '1px solid #252540',
                borderRadius: '8px',
                color: '#f7f7f7',
                fontSize: '13px',
                fontFamily: 'Inter, sans-serif',
              }}
              placeholder="Cape name..."
              value={capeName}
              onChange={(e) => setCapeName(e.target.value)}
            />
            <button
              onClick={handleUploadCape}
              style={{
                padding: '10px 18px',
                borderRadius: '8px',
                border: 'none',
                background: '#8B5CF6',
                color: '#fff',
                fontSize: '13px',
                fontWeight: 500,
                cursor: 'pointer',
                fontFamily: 'Inter, sans-serif',
                transition: 'all 0.2s ease',
              }}
              onMouseEnter={(e) => { e.currentTarget.style.background = '#7C3AED' }}
              onMouseLeave={(e) => { e.currentTarget.style.background = '#8B5CF6' }}
            >
              <i className="fa-solid fa-plus" style={{ marginRight: '6px' }}></i>
              {t('cosmetics.uploadCape') || 'Upload Cape'}
            </button>
          </div>

          {customCapes.length > 0 && (
            <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
              {customCapes.map((cape) => (
                <div
                  key={cape.id}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '10px',
                    padding: '10px 14px',
                    background: '#181830',
                    border: '1px solid #252540',
                    borderRadius: '10px',
                    minWidth: '180px',
                  }}
                >
                  <i className="fa-solid fa-shirt" style={{ color: '#8B5CF6', fontSize: '16px' }}></i>
                  <span style={{ flex: 1, color: '#f7f7f7', fontSize: '13px', fontFamily: 'Inter, sans-serif' }}>
                    {cape.name}
                  </span>
                  <button
                    onClick={() => handleRemoveCape(cape.id)}
                    style={{
                      background: 'rgba(196, 48, 88, 0.15)',
                      color: '#c43058',
                      border: 'none',
                      borderRadius: '6px',
                      padding: '4px 8px',
                      cursor: 'pointer',
                      fontSize: '12px',
                      fontFamily: 'Inter, sans-serif',
                    }}
                  >
                    <i className="fa-solid fa-trash"></i>
                  </button>
                </div>
              ))}
            </div>
          )}
          {customCapes.length === 0 && (
            <p style={{ color: '#555', fontSize: '13px', fontFamily: 'Inter, sans-serif', margin: 0 }}>
              No custom capes yet. Upload a .png image to get started.
            </p>
          )}
        </div>

        <div style={{ display: 'flex', gap: '12px', marginBottom: '24px', flexWrap: 'wrap' }}>
          {cosmeticCategories.map((cat) => (
            <button
              key={cat}
              onClick={() => setCategoryFilter(cat as 'cosmetic' | 'all')}
              style={{
                padding: '10px 20px',
                borderRadius: '10px',
                border: categoryFilter === cat ? '1px solid #8B5CF6' : '1px solid #252540',
                background: categoryFilter === cat ? 'rgba(139, 92, 246, 0.15)' : '#181830',
                color: categoryFilter === cat ? '#8B5CF6' : '#8b8ba0',
                fontSize: '14px',
                fontWeight: 500,
                cursor: 'pointer',
                fontFamily: 'Inter, sans-serif',
                transition: 'all 0.3s ease',
              }}
              onMouseEnter={(e) => {
                if (categoryFilter !== cat) {
                  e.currentTarget.style.background = '#252540'
                  e.currentTarget.style.borderColor = '#8B5CF6'
                  e.currentTarget.style.color = '#f7f7f7'
                }
              }}
              onMouseLeave={(e) => {
                if (categoryFilter !== cat) {
                  e.currentTarget.style.background = '#181830'
                  e.currentTarget.style.borderColor = '#252540'
                  e.currentTarget.style.color = '#8b8ba0'
                }
              }}
            >
              {cat === 'all' ? t('mods.all') : t(categoryKeys[cat] as any)}
            </button>
          ))}
        </div>

        <div style={{ marginBottom: '24px' }}>
          <input
            style={{
              width: '100%',
              maxWidth: '400px',
              padding: '12px 16px',
              background: '#181830',
              border: '1px solid #252540',
              borderRadius: '10px',
              color: '#f7f7f7',
              fontSize: '14px',
              fontFamily: 'Inter, sans-serif',
            }}
            placeholder={t('mods.searchPlaceholder')}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {isLoading ? (
            <div style={{ textAlign: 'center', padding: '60px 0', color: '#8b8ba0', fontFamily: 'Inter, sans-serif' }}>
              <i className="fa-solid fa-spinner fa-spin" style={{ fontSize: '24px', marginBottom: '10px' }}></i>
              <p>{t('mods.loading')}</p>
            </div>
          ) : (
            filtered.map((cosmetic) => (
              <div
                key={cosmetic.id}
                style={{
                  backgroundColor: '#181830',
                  borderRadius: '16px',
                  border: '1px solid #252540',
                  padding: '20px',
                  display: 'flex',
                  alignItems: 'flex-start',
                  gap: '16px',
                  transition: 'all 0.3s ease',
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.borderColor = '#8B5CF6'
                  e.currentTarget.style.transform = 'translateY(-2px)'
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.borderColor = '#252540'
                  e.currentTarget.style.transform = 'translateY(0)'
                }}
              >
                <div
                  style={{
                    width: '60px',
                    height: '60px',
                    borderRadius: '12px',
                    background: '#12121F',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '24px',
                    color: '#8b8ba0',
                    flexShrink: 0,
                    overflow: 'hidden',
                  }}
                >
                  {cosmetic.iconUrl ? (
                    <img
                      src={cosmetic.iconUrl}
                      alt={cosmetic.name}
                      style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '12px' }}
                      onError={(e) => {
                        e.currentTarget.style.display = 'none'
                        e.currentTarget.parentElement!.innerHTML = '<i class="fa-solid fa-shirt"></i>'
                      }}
                    />
                  ) : (
                    <i className="fa-solid fa-shirt"></i>
                  )}
                </div>

                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '10px' }}>
                    <div>
                      <h4 style={{ fontWeight: 600, fontSize: '16px', color: '#f7f7f7', fontFamily: 'Inter, sans-serif', margin: '0 0 4px 0' }}>
                        {cosmetic.name}
                      </h4>
                      <p style={{ fontSize: '13px', color: '#8b8ba0', fontFamily: 'Inter, sans-serif', margin: 0 }}>
                        {cosmetic.description}
                      </p>
                    </div>
                    {cosmetic.installed && (
                      <span style={{ color: '#3cc065', fontSize: '12px', whiteSpace: 'nowrap', fontFamily: 'Inter, sans-serif' }}>
                        <i className="fa-solid fa-check" style={{ marginRight: '4px' }}></i>
                        {t('mods.installedBadge')}
                      </span>
                    )}
                  </div>

                  <div style={{ display: 'flex', gap: '16px', marginTop: '10px', fontSize: '12px', color: '#8b8ba0', fontFamily: 'Inter, sans-serif' }}>
                    <span>{cosmetic.author}</span>
                    <span>v{cosmetic.version}</span>
                    <span>{t('mods.downloads', { count: (cosmetic.downloads / 1000000).toFixed(1) })}</span>
                  </div>

                  <div style={{ display: 'flex', gap: '8px', marginTop: '12px' }}>
                    {cosmetic.isInstalling ? (
                      <button
                        disabled
                        style={{
                          padding: '8px 16px',
                          borderRadius: '8px',
                          border: 'none',
                          background: 'rgba(139, 92, 246, 0.1)',
                          color: '#8B5CF6',
                          fontSize: '13px',
                          cursor: 'wait',
                          fontFamily: 'Inter, sans-serif',
                          opacity: 0.7,
                        }}
                      >
                        <i className="fa-solid fa-spinner fa-spin" style={{ marginRight: '6px' }}></i>
                        {t('mods.installing')}
                      </button>
                    ) : cosmetic.installed ? (
                      <button
                        style={{
                          padding: '8px 16px',
                          borderRadius: '8px',
                          border: 'none',
                          background: 'rgba(196, 48, 88, 0.15)',
                          color: '#c43058',
                          fontSize: '13px',
                          cursor: 'pointer',
                          fontFamily: 'Inter, sans-serif',
                          transition: 'all 0.2s ease',
                        }}
                        onMouseEnter={(e) => {
                          e.currentTarget.style.background = 'rgba(196, 48, 88, 0.25)'
                          e.currentTarget.style.borderColor = '#c43058'
                        }}
                        onMouseLeave={(e) => {
                          e.currentTarget.style.background = 'rgba(196, 48, 88, 0.15)'
                          e.currentTarget.style.borderColor = 'rgba(196, 48, 88, 0.3)'
                        }}
                      >
                        <i className="fa-solid fa-trash" style={{ marginRight: '6px' }}></i>
                        {t('mods.uninstall')}
                      </button>
                    ) : (
                      <button
                        style={{
                          padding: '8px 16px',
                          borderRadius: '8px',
                          border: 'none',
                          background: 'rgba(139, 92, 246, 0.15)',
                          color: '#8B5CF6',
                          fontSize: '13px',
                          cursor: 'pointer',
                          fontFamily: 'Inter, sans-serif',
                          transition: 'all 0.2s ease',
                        }}
                        onMouseEnter={(e) => {
                          e.currentTarget.style.background = 'rgba(139, 92, 246, 0.25)'
                          e.currentTarget.style.borderColor = '#8B5CF6'
                        }}
                        onMouseLeave={(e) => {
                          e.currentTarget.style.background = 'rgba(139, 92, 246, 0.15)'
                          e.currentTarget.style.borderColor = 'rgba(139, 92, 246, 0.3)'
                        }}
                      >
                        <i className="fa-solid fa-download" style={{ marginRight: '6px' }}></i>
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
          <div style={{ textAlign: 'center', padding: '60px 0', color: '#8b8ba0', fontFamily: 'Inter, sans-serif' }}>
            <p>{t('mods.noResults')}</p>
          </div>
        )}
      </div>
    </div>
  )
}

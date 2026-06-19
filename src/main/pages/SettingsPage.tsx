import { useState, useEffect } from 'react'
import { useSettingsStore } from '../stores/useSettingsStore'
import { useAuthStore } from '../stores/useAuthStore'
import { useLanguage } from '../i18n'
import { Card, CardItem } from '../components/ui/Card'
import type { Language } from '../i18n'

type Tab = 'game' | 'java' | 'account'

export default function SettingsPage() {
  const { settings, updateSettings, versions, setVersions, selectedVersion, setSelectedVersion } = useSettingsStore()
  const { account, accounts, switchAccount, removeAccount, logout } = useAuthStore()
  const { t, language, setLanguage } = useLanguage()
  const [tab, setTab] = useState<Tab>('game')
  const [expandedGroup, setExpandedGroup] = useState<string | null>(null)

  const tabs: { id: Tab; label: string }[] = [
    { id: 'game', label: t('settings.gameLauncher') },
    { id: 'java', label: t('settings.javaSettings') },
    { id: 'account', label: t('settings.account') },
  ]

  useEffect(() => {
    window.nova.versions.get().then(setVersions)
  }, [])

  // Filter versions
  const filteredVersions = versions.filter(v => {
    const id = v.id;
    const exactMatches = ['1.7.10', '1.8.9', '1.12.2', '1.16.5', '1.18.2', '26.1', '26.1.2'];
    if (exactMatches.includes(id)) return true;
    if (id.startsWith('1.20.')) return true;
    if (id.startsWith('1.21.')) return true;
    return false;
  });

  // Group versions by major version
  const versionGroups = filteredVersions.reduce((acc, version) => {
    let major = '';
    if (version.id.startsWith('26.')) {
      major = '26';
    } else {
      const parts = version.id.split('.');
      if (parts.length >= 2) {
        major = parts[0] + '.' + parts[1];
      } else {
        major = version.id;
      }
    }
    
    if (!acc[major]) {
      acc[major] = []
    }
    acc[major].push(version)
    return acc
  }, {} as Record<string, typeof versions>)

  const groupOrder = ['26', '1.21', '1.20', '1.18', '1.16', '1.12', '1.8', '1.7'];
  const sortedGroups = Object.keys(versionGroups).sort((a, b) => {
    let idxA = groupOrder.indexOf(a);
    let idxB = groupOrder.indexOf(b);
    if (idxA === -1) idxA = 999;
    if (idxB === -1) idxB = 999;
    return idxA - idxB;
  });

  const languages: { id: Language; label: string; flag: string }[] = [
    { id: 'en', label: 'English', flag: '🇺🇸' },
    { id: 'es', label: 'Español', flag: '🇪🇸' },
    { id: 'fr', label: 'Français', flag: '🇫🇷' },
  ]

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%', padding: '20px' }}>
      <div style={{ display: 'flex', gap: '12px', marginBottom: '30px' }}>
        {tabs.map((t) => (
          <button
            key={t.id}
            onClick={() => setTab(t.id)}
            style={{
              padding: '10px 24px',
              borderRadius: '10px',
              border: tab === t.id ? '1px solid #8B5CF6' : '1px solid #252540',
              background: tab === t.id ? 'rgba(139, 92, 246, 0.15)' : '#181830',
              color: tab === t.id ? '#8B5CF6' : '#8b8ba0',
              fontSize: '14px',
              fontWeight: 500,
              cursor: 'pointer',
              fontFamily: 'Inter, sans-serif',
              transition: 'all 0.3s ease',
            }}
            onMouseEnter={(e) => {
              if (tab !== t.id) {
                e.currentTarget.style.background = '#252540'
                e.currentTarget.style.borderColor = '#8B5CF6'
                e.currentTarget.style.color = '#f7f7f7'
              }
            }}
            onMouseLeave={(e) => {
              if (tab !== t.id) {
                e.currentTarget.style.background = '#181830'
                e.currentTarget.style.borderColor = '#252540'
                e.currentTarget.style.color = '#8b8ba0'
              }
            }}
          >
            {t.label}
          </button>
        ))}
      </div>

      {tab === 'game' && (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '20px', width: '100%' }}>
          <div
            style={{
              width: '100%',
              maxWidth: '800px',
              backgroundColor: '#181830',
              borderRadius: '16px',
              border: '1px solid #252540',
              padding: '24px',
              transition: 'all 0.3s ease',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '24px' }}>
              <div style={{
                width: '48px',
                height: '48px',
                borderRadius: '12px',
                background: 'linear-gradient(135deg, #8B5CF6, #1a5bb8)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}>
                <i className="fa-solid fa-gears" style={{ color: '#fff', fontSize: '20px' }} />
              </div>
              <div>
                <h3 style={{ color: '#f7f7f7', fontSize: '18px', fontWeight: 600, fontFamily: 'Inter, sans-serif', margin: 0 }}>
                  {t('settings.gameLauncherTitle')}
                </h3>
                <p style={{ color: '#8b8ba0', fontSize: '13px', margin: 0, fontFamily: 'Inter, sans-serif' }}>
                  {t('settings.gameLauncherSubtitle')}
                </p>
              </div>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              <div style={{ padding: '16px', background: '#12121F', borderRadius: '12px', border: '1px solid #252540' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '16px' }}>
                  <i className="fa-solid fa-language" style={{ color: '#8B5CF6', fontSize: '18px' }} />
                  <div>
                    <h4 style={{ color: '#f7f7f7', fontSize: '15px', fontWeight: 600, fontFamily: 'Inter, sans-serif', margin: 0 }}>
                      {t('settings.language')}
                    </h4>
                    <p style={{ color: '#8b8ba0', fontSize: '12px', margin: 0, fontFamily: 'Inter, sans-serif' }}>
                      {t('settings.selectLanguage')}
                    </p>
                  </div>
                </div>
                <div style={{ display: 'flex', gap: '10px' }}>
                  {languages.map((lang) => (
                    <button
                      key={lang.id}
                      onClick={() => setLanguage(lang.id)}
                      style={{
                        padding: '12px 20px',
                        borderRadius: '10px',
                        border: language === lang.id ? '2px solid #8B5CF6' : '1px solid #252540',
                        background: language === lang.id ? 'rgba(139, 92, 246, 0.15)' : '#181830',
                        color: language === lang.id ? '#8B5CF6' : '#f7f7f7',
                        fontSize: '14px',
                        fontWeight: language === lang.id ? 600 : 400,
                        cursor: 'pointer',
                        fontFamily: 'Inter, sans-serif',
                        transition: 'all 0.2s ease',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '8px',
                      }}
                      onMouseEnter={(e) => {
                        if (language !== lang.id) {
                          e.currentTarget.style.background = '#252540'
                          e.currentTarget.style.borderColor = '#8B5CF6'
                        }
                      }}
                      onMouseLeave={(e) => {
                        if (language !== lang.id) {
                          e.currentTarget.style.background = '#181830'
                          e.currentTarget.style.borderColor = '#252540'
                        }
                      }}
                    >
                      <span style={{ fontSize: '18px' }}>{lang.flag}</span>
                      <span>{lang.label}</span>
                      {language === lang.id && (
                        <i className="fa-solid fa-check" style={{ fontSize: '12px', marginLeft: '4px' }}></i>
                      )}
                    </button>
                  ))}
                </div>
              </div>

              <div style={{ padding: '16px', background: '#12121F', borderRadius: '12px', border: '1px solid #252540' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '16px' }}>
                  <i className="fa-solid fa-cube" style={{ color: '#8B5CF6', fontSize: '18px' }} />
                  <div>
                    <h4 style={{ color: '#f7f7f7', fontSize: '15px', fontWeight: 600, fontFamily: 'Inter, sans-serif', margin: 0 }}>
                      {t('settings.minecraftVersion')}
                    </h4>
                    <p style={{ color: '#8b8ba0', fontSize: '12px', margin: 0, fontFamily: 'Inter, sans-serif' }}>
                      {t('settings.selectVersion')}
                    </p>
                  </div>
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  {sortedGroups.map((group) => (
                    <div key={group} style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                      <button
                        onClick={() => setExpandedGroup(expandedGroup === group ? null : group)}
                        style={{
                          width: '100%',
                          padding: '14px 18px',
                          background: expandedGroup === group ? 'rgba(139, 92, 246, 0.15)' : '#181830',
                          border: expandedGroup === group ? '1px solid #8B5CF6' : '1px solid #252540',
                          borderRadius: '10px',
                          color: expandedGroup === group ? '#8B5CF6' : '#f7f7f7',
                          fontSize: '14px',
                          fontWeight: 600,
                          cursor: 'pointer',
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'center',
                          transition: 'all 0.3s ease',
                          fontFamily: 'Inter, sans-serif',
                        }}
                        onMouseEnter={(e) => {
                          if (expandedGroup !== group) {
                            e.currentTarget.style.background = '#252540'
                            e.currentTarget.style.borderColor = '#8B5CF6'
                          }
                        }}
                        onMouseLeave={(e) => {
                          if (expandedGroup !== group) {
                            e.currentTarget.style.background = '#181830'
                            e.currentTarget.style.borderColor = '#252540'
                          }
                        }}
                      >
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                          <i className="fa-solid fa-code-branch" style={{ fontSize: '14px' }}></i>
                          <span>{t('settings.versionGroup', { group })}</span>
                        </div>
                        <i 
                          className="fa-solid fa-chevron-down" 
                          style={{ 
                            fontSize: '12px', 
                            transition: 'transform 0.3s',
                            transform: expandedGroup === group ? 'rotate(180deg)' : 'rotate(0deg)'
                          }}
                        ></i>
                      </button>
                      {expandedGroup === group && (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '6px', padding: '4px 0 4px 16px', borderLeft: '2px solid #252540', marginLeft: '16px' }}>
                          {versionGroups[group]
                            .sort((a, b) => b.id.localeCompare(a.id, undefined, { numeric: true }))
                            .map((version) => (
                              <button
                                key={version.id}
                                onClick={() => setSelectedVersion(version.id)}
                                style={{
                                  width: '100%',
                                  padding: '12px 16px',
                                  background: selectedVersion === version.id ? 'rgba(40, 175, 85, 0.15)' : '#12121F',
                                  border: selectedVersion === version.id ? '1px solid #3cc065' : '1px solid #252540',
                                  borderRadius: '8px',
                                  color: selectedVersion === version.id ? '#3cc065' : '#f7f7f7',
                                  fontSize: '13px',
                                  fontWeight: selectedVersion === version.id ? 600 : 400,
                                  cursor: 'pointer',
                                  display: 'flex',
                                  justifyContent: 'space-between',
                                  alignItems: 'center',
                                  transition: 'all 0.2s ease',
                                  fontFamily: 'Inter, sans-serif',
                                }}
                                onMouseEnter={(e) => {
                                  if (selectedVersion !== version.id) {
                                    e.currentTarget.style.background = '#252540'
                                    e.currentTarget.style.borderColor = '#8B5CF6'
                                  }
                                }}
                                onMouseLeave={(e) => {
                                  if (selectedVersion !== version.id) {
                                    e.currentTarget.style.background = '#12121F'
                                    e.currentTarget.style.borderColor = '#252540'
                                  }
                                }}
                              >
                                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                  <i className="fa-solid fa-cube" style={{ fontSize: '12px', opacity: 0.7 }}></i>
                                  <span>{version.id}</span>
                                </div>
                                {selectedVersion === version.id && (
                                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                                    <span style={{ fontSize: '11px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>{t('settings.selected')}</span>
                                    <i className="fa-solid fa-check" style={{ fontSize: '14px' }}></i>
                                  </div>
                                )}
                              </button>
                            ))}
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              </div>

              <div style={{ padding: '16px', background: '#12121F', borderRadius: '12px', border: '1px solid #252540' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '16px' }}>
                  <i className="fa-solid fa-sliders" style={{ color: '#8B5CF6', fontSize: '18px' }} />
                  <div>
                    <h4 style={{ color: '#f7f7f7', fontSize: '15px', fontWeight: 600, fontFamily: 'Inter, sans-serif', margin: 0 }}>
                      {t('settings.allocatedMemory')}
                    </h4>
                    <p style={{ color: '#8b8ba0', fontSize: '12px', margin: 0, fontFamily: 'Inter, sans-serif' }}>
                      {t('settings.memorySubtitle')}
                    </p>
                  </div>
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div>
                    <label style={{ fontSize: '12px', color: '#8b8ba0', marginBottom: '8px', display: 'block', fontFamily: 'Inter, sans-serif' }}>
                      {t('settings.minRam', { value: settings.ram.min })}
                    </label>
                    <input
                      type="range"
                      min={1}
                      max={8}
                      step={1}
                      value={settings.ram.min}
                      onChange={(e) => {
                        const v = parseInt(e.target.value)
                        const max = Math.max(v, settings.ram.max)
                        updateSettings({ ram: { min: v, max } })
                      }}
                      style={{ width: '100%', accentColor: '#8B5CF6' }}
                    />
                  </div>
                  <div>
                    <label style={{ fontSize: '12px', color: '#8b8ba0', marginBottom: '8px', display: 'block', fontFamily: 'Inter, sans-serif' }}>
                      {t('settings.maxRam', { value: settings.ram.max })}
                    </label>
                    <input
                      type="range"
                      min={2}
                      max={16}
                      step={1}
                      value={settings.ram.max}
                      onChange={(e) => {
                        const v = parseInt(e.target.value)
                        const min = Math.min(v, settings.ram.min)
                        updateSettings({ ram: { min, max: v } })
                      }}
                      style={{ width: '100%', accentColor: '#8B5CF6' }}
                    />
                  </div>
                  <p style={{ fontSize: '12px', color: '#8b8ba0', textAlign: 'center', fontFamily: 'Inter, sans-serif' }}>
                    {t('settings.ramLeft', { value: Math.round(16 - settings.ram.max) })}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {tab === 'java' && (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '20px', width: '100%' }}>
          <div
            style={{
              width: '100%',
              maxWidth: '800px',
              backgroundColor: '#181830',
              borderRadius: '16px',
              border: '1px solid #252540',
              padding: '24px',
              transition: 'all 0.3s ease',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '24px' }}>
              <div style={{
                width: '48px',
                height: '48px',
                borderRadius: '12px',
                background: 'linear-gradient(135deg, #8B5CF6, #1a5bb8)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}>
                <i className="fa-solid fa-code" style={{ color: '#fff', fontSize: '20px' }} />
              </div>
              <div>
                <h3 style={{ color: '#f7f7f7', fontSize: '18px', fontWeight: 600, fontFamily: 'Inter, sans-serif', margin: 0 }}>
                  {t('settings.javaTitle')}
                </h3>
                <p style={{ color: '#8b8ba0', fontSize: '13px', margin: 0, fontFamily: 'Inter, sans-serif' }}>
                  {t('settings.javaSubtitle')}
                </p>
              </div>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              <div style={{ padding: '16px', background: '#12121F', borderRadius: '12px', border: '1px solid #252540' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '16px' }}>
                  <i className="fa-solid fa-memory" style={{ color: '#8B5CF6', fontSize: '18px' }} />
                  <div>
                    <h4 style={{ color: '#f7f7f7', fontSize: '15px', fontWeight: 600, fontFamily: 'Inter, sans-serif', margin: 0 }}>
                      {t('settings.jvmArguments')}
                    </h4>
                  </div>
                </div>
                <textarea
                  style={{
                    width: '100%',
                    minHeight: '80px',
                    fontFamily: 'monospace',
                    fontSize: '12px',
                    resize: 'vertical',
                    background: '#181830',
                    border: '1px solid #252540',
                    borderRadius: '8px',
                    padding: '12px',
                    color: '#f7f7f7',
                  }}
                  placeholder="-Xms2G -Xmx4G"
                />
                <div style={{ display: 'flex', gap: '8px', marginTop: '12px', flexWrap: 'wrap' }}>
                  {['Default', 'Zulu optimized', 'GraalVM Community', 'GraalVM Enterprise'].map((p) => (
                    <button
                      key={p}
                      style={{
                        padding: '8px 16px',
                        background: 'rgba(139, 92, 246, 0.1)',
                        border: '1px solid rgba(139, 92, 246, 0.3)',
                        borderRadius: '8px',
                        color: '#8B5CF6',
                        fontSize: '12px',
                        cursor: 'pointer',
                        transition: 'all 0.2s ease',
                        fontFamily: 'Inter, sans-serif',
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.background = 'rgba(139, 92, 246, 0.2)'
                        e.currentTarget.style.borderColor = '#8B5CF6'
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.background = 'rgba(139, 92, 246, 0.1)'
                        e.currentTarget.style.borderColor = 'rgba(139, 92, 246, 0.3)'
                      }}
                    >
                      {p}
                    </button>
                  ))}
                </div>
              </div>

              <div style={{ padding: '16px', background: '#12121F', borderRadius: '12px', border: '1px solid #252540' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '16px' }}>
                  <i className="fa-solid fa-diagram-project" style={{ color: '#8B5CF6', fontSize: '18px' }} />
                  <div>
                    <h4 style={{ color: '#f7f7f7', fontSize: '15px', fontWeight: 600, fontFamily: 'Inter, sans-serif', margin: 0 }}>
                      {t('settings.javaExecutable')}
                    </h4>
                  </div>
                </div>
                <div style={{ display: 'flex', gap: '8px' }}>
                  <input
                    style={{
                      flex: 1,
                      padding: '12px',
                      background: '#181830',
                      border: '1px solid #252540',
                      borderRadius: '8px',
                      color: '#f7f7f7',
                      fontSize: '13px',
                      fontFamily: 'Inter, sans-serif',
                    }}
                    placeholder={t('settings.customJre')}
                    value={settings.javaPath}
                    onChange={(e) => updateSettings({ javaPath: e.target.value })}
                  />
                  <button
                    style={{
                      padding: '12px 20px',
                      background: 'rgba(139, 92, 246, 0.15)',
                      border: '1px solid rgba(139, 92, 246, 0.3)',
                      borderRadius: '8px',
                      color: '#8B5CF6',
                      fontSize: '13px',
                      cursor: 'pointer',
                      transition: 'all 0.2s ease',
                      fontFamily: 'Inter, sans-serif',
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
                    <i className="fa-solid fa-folder-open" style={{ marginRight: '6px' }}></i>
                    {t('settings.browse')}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {tab === 'account' && (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '20px', width: '100%' }}>
          <div
            style={{
              width: '100%',
              maxWidth: '800px',
              backgroundColor: '#181830',
              borderRadius: '16px',
              border: '1px solid #252540',
              padding: '24px',
              transition: 'all 0.3s ease',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '24px' }}>
              <div style={{
                width: '48px',
                height: '48px',
                borderRadius: '12px',
                background: 'linear-gradient(135deg, #8B5CF6, #1a5bb8)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}>
                <i className="fa-solid fa-user" style={{ color: '#fff', fontSize: '20px' }} />
              </div>
              <div>
                <h3 style={{ color: '#f7f7f7', fontSize: '18px', fontWeight: 600, fontFamily: 'Inter, sans-serif', margin: 0 }}>
                  {t('settings.accountTitle')}
                </h3>
                <p style={{ color: '#8b8ba0', fontSize: '13px', margin: 0, fontFamily: 'Inter, sans-serif' }}>
                  {t('settings.accountSubtitle')}
                </p>
              </div>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              {account && (
                <div style={{ padding: '16px', background: '#12121F', borderRadius: '12px', border: '1px solid #252540' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '16px' }}>
                    <i className="fa-solid fa-user" style={{ color: '#8B5CF6', fontSize: '18px' }} />
                    <div>
                      <h4 style={{ color: '#f7f7f7', fontSize: '15px', fontWeight: 600, fontFamily: 'Inter, sans-serif', margin: 0 }}>
                        {account.username}
                      </h4>
                      <p style={{ color: '#8b8ba0', fontSize: '12px', margin: 0, fontFamily: 'Inter, sans-serif' }}>
                        {t('dashboard.accountType', { type: account.type })}
                      </p>
                    </div>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'center' }}>
                    <button
                      onClick={logout}
                      style={{
                        padding: '12px 24px',
                        background: 'rgba(196, 48, 88, 0.15)',
                        border: '1px solid rgba(196, 48, 88, 0.3)',
                        borderRadius: '10px',
                        color: '#c43058',
                        fontSize: '14px',
                        fontWeight: 500,
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
                      <i className="fa-solid fa-right-from-bracket" style={{ marginRight: '8px' }}></i>
                      {t('settings.logout')}
                    </button>
                  </div>
                </div>
              )}

              {accounts.length > 0 && (
                <div style={{ padding: '16px', background: '#12121F', borderRadius: '12px', border: '1px solid #252540' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '16px' }}>
                    <i className="fa-solid fa-users" style={{ color: '#8B5CF6', fontSize: '18px' }} />
                    <div>
                      <h4 style={{ color: '#f7f7f7', fontSize: '15px', fontWeight: 600, fontFamily: 'Inter, sans-serif', margin: 0 }}>
                        {t('settings.savedAccounts')}
                      </h4>
                    </div>
                  </div>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    {accounts.map((a) => (
                      <div
                        key={a.id}
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'space-between',
                          padding: '12px 16px',
                          borderRadius: '10px',
                          background: a.id === account?.id ? 'rgba(60,192,101,0.1)' : '#181830',
                          border: a.id === account?.id ? '1px solid #3cc065' : '1px solid #252540',
                          transition: 'all 0.2s ease',
                        }}
                      >
                        <div>
                          <span style={{ fontSize: '14px', color: '#f7f7f7', fontFamily: 'Inter, sans-serif' }}>{a.username}</span>
                          <span style={{ fontSize: '12px', color: '#8b8ba0', marginLeft: '8px', fontFamily: 'Inter, sans-serif' }}>{a.type}</span>
                        </div>
                        <div style={{ display: 'flex', gap: '6px' }}>
                          {a.id !== account?.id && (
                            <>
                              <button
                                onClick={() => switchAccount(a.id)}
                                style={{
                                  padding: '8px 16px',
                                  background: 'rgba(139, 92, 246, 0.15)',
                                  border: '1px solid rgba(139, 92, 246, 0.3)',
                                  borderRadius: '8px',
                                  color: '#8B5CF6',
                                  fontSize: '12px',
                                  cursor: 'pointer',
                                  transition: 'all 0.2s ease',
                                  fontFamily: 'Inter, sans-serif',
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
                                {t('settings.switch')}
                              </button>
                              <button
                                onClick={() => removeAccount(a.id)}
                                style={{
                                  padding: '8px 12px',
                                  background: 'rgba(196, 48, 88, 0.15)',
                                  border: '1px solid rgba(196, 48, 88, 0.3)',
                                  borderRadius: '8px',
                                  color: '#c43058',
                                  fontSize: '12px',
                                  cursor: 'pointer',
                                  transition: 'all 0.2s ease',
                                  fontFamily: 'Inter, sans-serif',
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
                                <i className="fa-solid fa-trash"></i>
                              </button>
                            </>
                          )}
                          {a.id === account?.id && (
                            <span style={{ color: '#3cc065', fontSize: '12px', fontWeight: 500, fontFamily: 'Inter, sans-serif' }}>{t('settings.active')}</span>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

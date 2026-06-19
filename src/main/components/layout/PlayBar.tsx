import { useState, useEffect } from 'react'
import { useAuthStore } from '../../stores/useAuthStore'
import { useSettingsStore } from '../../stores/useSettingsStore'
import { useLanguage } from '../../i18n'
import type { ModLoader } from '../../types'
import playbarBg from '../../assets/playbarbg.jpg'
import img26 from '../../assets/versions/26.1.png'
import img121 from '../../assets/versions/1.21.png'
import img120 from '../../assets/versions/1.20.png'
import img118 from '../../assets/versions/1.18.png'
import img116 from '../../assets/versions/1.16.png'
import img112 from '../../assets/versions/1.12.png'
import img18 from '../../assets/versions/1.8.png'
import img17 from '../../assets/versions/1.7.png'

interface Props {
  height: number
}

const LC_VERSIONS_BG: Record<string, string> = {
  '26': img26,
  '1.21': img121,
  '1.20': img120,
  '1.18': img118,
  '1.16': img116,
  '1.12': img112,
  '1.8': img18,
  '1.7': img17,
}

export default function PlayBar({ height }: Props) {
  const account = useAuthStore((s) => s.account)
  const {
    selectedVersion, versions, isLaunching, launchProgress,
    setSelectedVersion, setVersions, setLaunching, setLaunchProgress,
    settings, updateSettings,
  } = useSettingsStore()
  const { t } = useLanguage()

  const [isSelectingVersion, setIsSelectingVersion] = useState(false)
  const [playTarget, setPlayTarget] = useState<'launch' | 'join'>('launch')
  const [serverIp, setServerIp] = useState('hypixel.net')
  const [expandedGroup, setExpandedGroup] = useState<string | null>(null)
  const [isSelectingLoader, setIsSelectingLoader] = useState(false)
  const [loaderInstalling, setLoaderInstalling] = useState(false)

  const selectedLoader = settings.selectedLoader

  const switchLoader = (loader: typeof selectedLoader) => {
    updateSettings({ selectedLoader: loader })
    setIsSelectingLoader(false)
  }

  useEffect(() => {
    window.nova.versions.get().then(setVersions)
  }, [])

  useEffect(() => {
    if (!isSelectingLoader) return
    const handler = (e: MouseEvent) => {
      const target = e.target as HTMLElement
      if (!target.closest('[data-loader-dropdown]')) {
        setIsSelectingLoader(false)
      }
    }
    setTimeout(() => document.addEventListener('mousedown', handler), 0)
    return () => document.removeEventListener('mousedown', handler)
  }, [isSelectingLoader])

  // Backend already filters to supported versions, just pass through
  const allowedFilteredVersions = versions

  // Group versions by major version
  const versionGroups = allowedFilteredVersions.reduce((acc, version) => {
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

  const latestRelease = allowedFilteredVersions[0]?.id || '1.21.4'
  const displayVersion = selectedVersion === 'latest_release' ? latestRelease : selectedVersion
  const optifineVersions = ['1.7.10', '1.8.9', '1.12.2', '1.16.5']
  const fabricVersions = ['1.16.5', '1.18.2', '1.20', '1.21', '26.1', '26.1.2']
  const availableLoaders = (['none', 'fabric', 'forge', 'optifine'] as const).filter(
    (loader) => {
      if (loader === 'optifine') return optifineVersions.includes(displayVersion)
      if (loader === 'fabric') return fabricVersions.some(v => displayVersion?.startsWith(v))
      return true
    },
  )

   const handleLaunch = async () => {
     setLaunching(true)
     setLaunchProgress('Preparing...')
     try {
       let version = selectedVersion === 'latest_release' ? latestRelease : selectedVersion
       
       // Install selected loader if needed
       if (selectedLoader !== 'none') {
         setLaunchProgress('Installing mod loader...')
         setLoaderInstalling(true)
         
         try {
           let result: any
           if (selectedLoader === 'fabric') {
             result = await window.nova.loaders.installFabric({ version })
           } else if (selectedLoader === 'forge') {
             result = await window.nova.loaders.installForge({ version })
           } else if (selectedLoader === 'optifine') {
             result = await window.nova.loaders.installOptiFine({ version })
           }
             if (result?.version) {
               version = result.version
             }
         } finally {
           setLoaderInstalling(false)
         }
         
         setLaunchProgress('Launching...')
       }
       
       setLaunchProgress(t('play.launchVersion', { version }))
       const unsub = window.nova.minecraft.onProgress((d) => {
         if (typeof d === 'string') setLaunchProgress(d)
       })
        await window.nova.minecraft.launch({
          username: account?.username || 'Player',
          uuid: account?.uuid || '',
          accessToken: account?.accessToken || '0',
          version,
          ram: settings.ram,
          javaPath: settings.javaPath,
          serverIp: playTarget === 'join' ? serverIp : undefined,
          customDir: settings.customDir || undefined,
          enablePatcher: settings.patcher.enabled,
          patcherConfig: settings.patcher.config,
        })
       unsub()
       setLaunchProgress(null)
     } catch (e: any) {
       setLoaderInstalling(false)
       setLaunchProgress(`Error: ${e?.message || e}`)
     } finally {
       setLaunching(false)
     }
   }

  const selectVersion = (ver: string) => {
    setSelectedVersion(ver)
    setIsSelectingVersion(false)
  }

  return (
    <>
      <div
        style={{
          width: '100%',
          height: `${height}px`,
          marginTop: '110px',
          background: '#12121F',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          position: 'relative',
          overflow: 'hidden',
          transition: 'height 0.6s cubic-bezier(0.4, 0, 0.2, 1)',
        }}
      >
        <div
          style={{
            position: 'absolute',
            inset: '-8%',
            background: `url(${playbarBg}) center/cover no-repeat`,
            zIndex: 0,
            animation: 'orbitBgPan 30s ease-in-out infinite alternate',
          }}
        />
        <div
          style={{
            position: 'absolute',
            inset: 0,
            background: 'linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.3))',
            zIndex: 1,
          }}
        />
        <div style={{ position: 'relative', zIndex: 2, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '10px' }}>
          <div
            style={{
              display: 'flex',
              gap: '12px',
              transition: 'all 0.3s',
            }}
          >
            <button
              onClick={() => setPlayTarget('launch')}
              style={{
                padding: '4px 16px',
                borderRadius: '3px',
                border: 'none',
                background: playTarget === 'launch' ? '#8B5CF6' : 'transparent',
                color: '#f7f7f7',
                fontSize: '12px',
                cursor: 'pointer',
                fontFamily: 'Roboto, sans-serif',
                transition: 'background 0.2s',
              }}
            >
              {t('play.launch')}
            </button>
            <button
              onClick={() => setPlayTarget('join')}
              style={{
                padding: '4px 16px',
                borderRadius: '3px',
                border: 'none',
                background: playTarget === 'join' ? '#8B5CF6' : 'transparent',
                color: '#f7f7f7',
                fontSize: '12px',
                cursor: 'pointer',
                fontFamily: 'Roboto, sans-serif',
                transition: 'background 0.2s',
              }}
            >
              {t('play.joinServer')}
            </button>
          </div>

        {playTarget === 'join' && height > 200 && (
          <div style={{ marginBottom: '10px', display: 'flex', gap: '8px' }}>
            <input
              className="input-field"
              style={{ width: '250px', padding: '6px 10px', fontSize: '12px' }}
              placeholder={t('play.serverPlaceholder')}
              value={serverIp}
              onChange={(e) => setServerIp(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleLaunch()}
            />
          </div>
        )}

         <div
           className="no-drag"
           data-loader-dropdown
           style={{
             display: 'flex',
             alignItems: 'center',
             gap: '4px',
             position: 'relative',
           }}
         >
           {!isLaunching && (
             <>
               <button
                 onClick={() => setIsSelectingLoader(!isSelectingLoader)}
                 style={{
                   width: '100px',
                   height: '30px',
                    background: 'rgba(124, 58, 237, 0.6)',
                   border: '1px solid rgba(255,255,255,0.2)',
                   borderRadius: '4px',
                   color: '#f7f7f7',
                   cursor: 'pointer',
                   fontSize: '12px',
                   fontFamily: 'Roboto, sans-serif',
                   display: 'flex',
                   alignItems: 'center',
                   justifyContent: 'center',
                   gap: '4px',
                 }}
               >
                 <span>{selectedLoader === 'none' ? 'Vanilla' : selectedLoader === 'optifine' ? 'OptiFine' : selectedLoader.charAt(0).toUpperCase() + selectedLoader.slice(1)}</span>
                 <i className="fa-solid fa-chevron-down" style={{ fontSize: '10px' }}></i>
               </button>
               {isSelectingLoader && (
                 <div style={{
                   position: 'absolute',
                   bottom: '36px',
                   left: 0,
                    background: 'rgba(14, 14, 26, 0.98)',
                   border: '1px solid rgba(255,255,255,0.15)',
                   borderRadius: '6px',
                   padding: '6px',
                   zIndex: 9999,
                   minWidth: '110px',
                   boxShadow: '0 -4px 20px rgba(0,0,0,0.6)',
                 }}>
                    {availableLoaders.map((loader) => (
                     <div
                       key={loader}
                       onClick={() => {
                         switchLoader(loader)
                         setIsSelectingLoader(false)
                       }}
                       style={{
                         padding: '8px 12px',
                         cursor: 'pointer',
                         borderRadius: '4px',
                          background: selectedLoader === loader ? 'rgba(139,92,246,0.3)' : 'transparent',
                          color: selectedLoader === loader ? '#3cc065' : '#ccc',
                         fontSize: '12px',
                         fontFamily: 'Roboto, sans-serif',
                         transition: 'background 0.15s',
                       }}
                       onMouseEnter={(e) => { if (selectedLoader !== loader) e.currentTarget.style.background = 'rgba(255,255,255,0.08)' }}
                       onMouseLeave={(e) => { if (selectedLoader !== loader) e.currentTarget.style.background = 'transparent' }}
                     >
                       {loader === 'none' ? 'Vanilla' : loader === 'optifine' ? 'OptiFine' : loader.charAt(0).toUpperCase() + loader.slice(1)}
                     </div>
                   ))}
                 </div>
               )}
             </>
           )}

            <button
             onClick={handleLaunch}
             disabled={isLaunching || loaderInstalling}
             style={{
               width: isLaunching || loaderInstalling ? '320px' : '280px',
               height: '56px',
                background: (isLaunching || loaderInstalling)
                  ? 'rgba(168, 56, 200, 0.85)'
                  : 'rgba(124, 58, 237, 0.85)',
               border: 'none',
               borderTopLeftRadius: '10px',
               borderBottomLeftRadius: '10px',
               borderTopRightRadius: (isLaunching || loaderInstalling) ? '10px' : '0',
               borderBottomRightRadius: (isLaunching || loaderInstalling) ? '10px' : '0',
               color: '#f7f7f7',
               cursor: (isLaunching || loaderInstalling) ? 'not-allowed' : 'pointer',
               fontFamily: 'Roboto, sans-serif',
               display: 'flex',
               flexDirection: 'column',
               alignItems: 'center',
               justifyContent: 'center',
               transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
               animation: (!isLaunching && !loaderInstalling) ? 'glowPulse 4s ease-in-out infinite' : 'none',
                boxShadow: (!isLaunching && !loaderInstalling) ? '0 0 12px rgba(139, 92, 246, 0.2)' : 'none',
             }}
             onMouseEnter={(e) => {
               if (!(isLaunching || loaderInstalling)) {
                  e.currentTarget.style.background = 'rgba(139, 92, 246, 0.9)'
                  e.currentTarget.style.transform = 'translateY(-1px)'
                  e.currentTarget.style.boxShadow = '0 0 20px rgba(139, 92, 246, 0.35), 0 4px 12px rgba(139, 92, 246, 0.2)'
               }
             }}
             onMouseLeave={(e) => {
               if (!(isLaunching || loaderInstalling)) {
                  e.currentTarget.style.background = 'rgba(124, 58, 237, 0.85)'
                  e.currentTarget.style.transform = 'translateY(0)'
                  e.currentTarget.style.boxShadow = '0 0 12px rgba(139, 92, 246, 0.2)'
               }
             }}
           >
             <span
               style={{
                 fontSize: '18px',
                 fontWeight: 600,
                 letterSpacing: '1.5px',
                 textShadow: '0 0 5px rgba(0,0,0,0.5)',
               }}
             >
               {isLaunching ? t('play.launching') : loaderInstalling ? 'Installing Loader...' : t('play.launchVersion', { version: displayVersion })}
             </span>
             <span
               style={{
                 fontSize: '12px',
                 opacity: 0.75,
                 textShadow: '0 0 5px rgba(0,0,0,0.5)',
               }}
             >
               {isLaunching && launchProgress ? launchProgress : 
                 loaderInstalling ? 'Preparing mod loader...' : 
                 t('play.readyToLaunch')}
             </span>
           </button>

            {!isLaunching && !loaderInstalling && (
              <button
                onClick={() => setIsSelectingVersion(!isSelectingVersion)}
                style={{
                  width: '40px',
                  height: '56px',
                  background: 'rgba(124, 58, 237, 0.85)',
                 border: 'none',
                 borderTopRightRadius: '10px',
                 borderBottomRightRadius: '10px',
                 color: '#f7f7f7',
                 cursor: 'pointer',
                 fontSize: '20px',
                 display: 'flex',
                 alignItems: 'center',
                 justifyContent: 'center',
               }}
                onMouseEnter={(e) => { e.currentTarget.style.background = 'rgba(139, 92, 246, 0.85)' }}
                onMouseLeave={(e) => { e.currentTarget.style.background = 'rgba(124, 58, 237, 0.85)' }}
             >
               <i className="fa-solid fa-angle-down"></i>
             </button>
           )}
         </div>
        </div>
      </div>

      {isSelectingVersion && (
        <div
          className="no-drag"
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            backgroundColor: 'rgba(0,0,0,0.8)',
            zIndex: 100,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            animation: 'fadeIn 0.3s ease',
          }}
          onClick={() => setIsSelectingVersion(false)}
        >
          <h2
            style={{
              color: '#f7f7f7',
              fontFamily: 'Roboto, sans-serif',
              fontWeight: 300,
              marginBottom: '10px',
              fontSize: '22px',
              letterSpacing: '3px',
            }}
          >
            {t('play.launchOptions')}
          </h2>
          <h5
            style={{
              color: '#9090a8',
              fontFamily: 'Roboto, sans-serif',
              fontWeight: 300,
              marginBottom: '30px',
              letterSpacing: '2px',
              fontSize: '13px',
            }}
          >
            {t('play.selectVersion')}
          </h5>
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(2, 400px)',
              gap: '20px',
              padding: '10px',
              maxHeight: '65vh',
              overflowY: 'auto',
            }}
            onClick={(e) => e.stopPropagation()}
          >
            {sortedGroups.map((group) => {
              const bgUrl = LC_VERSIONS_BG[group] || LC_VERSIONS_BG['1.21'];
              const isGroupExpanded = expandedGroup === group;
              const defaultMinor = versionGroups[group].sort((a, b) => a.id.localeCompare(b.id, undefined, { numeric: true }))[0].id;
              const isSelected = selectedVersion.startsWith(group);

              return (
                <div key={group} style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  <div
                    onClick={() => selectVersion(defaultMinor)}
                    style={{
                      width: '400px',
                      height: '200px',
                      borderRadius: '16px',
                      overflow: 'hidden',
                      cursor: 'pointer',
                      background: `url(${bgUrl}) center/cover no-repeat`,
                      border: isSelected ? '3px solid #3cc065' : '3px solid transparent',
                      boxShadow: isSelected ? '0 0 15px rgba(139,92,246,0.3), 0 4px 12px rgba(0,0,0,0.3)' : '0 4px 12px rgba(0,0,0,0.4)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      position: 'relative',
                      transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                    }}
                    onMouseEnter={(e) => {
                      const el = e.currentTarget as HTMLDivElement
                      el.style.transform = 'scale(1.02) translateY(-2px)'
                      el.style.boxShadow = isSelected
                        ? '0 0 20px rgba(139,92,246,0.4), 0 6px 20px rgba(0,0,0,0.35)'
                        : '0 0 15px rgba(255,255,255,0.06), 0 6px 20px rgba(0,0,0,0.35)'
                      if (!isSelected) el.style.border = '3px solid rgba(255,255,255,0.15)'
                    }}
                    onMouseLeave={(e) => {
                      const el = e.currentTarget as HTMLDivElement
                      el.style.transform = 'scale(1) translateY(0)'
                      el.style.boxShadow = isSelected ? '0 0 15px rgba(139,92,246,0.3), 0 4px 12px rgba(0,0,0,0.3)' : '0 4px 12px rgba(0,0,0,0.4)'
                      if (!isSelected) el.style.border = '3px solid transparent'
                    }}
                  >
                    <div
                      style={{
                        position: 'absolute',
                        inset: 0,
                        background: 'linear-gradient(to bottom, rgba(0,0,0,0.2) 0%, rgba(0,0,0,0.7) 100%)',
                        borderRadius: '13px',
                      }}
                    />
                    <h1
                      style={{
                        color: '#ffffff',
                        fontFamily: 'Montserrat, sans-serif',
                        fontWeight: 900,
                        fontSize: '48px',
                        letterSpacing: '-1px',
                        position: 'relative',
                        zIndex: 1,
                        textShadow: '0 4px 12px rgba(0,0,0,0.8)',
                        margin: 0,
                        textTransform: 'uppercase'
                      }}
                    >
                      NOVA {group}
                    </h1>
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        setExpandedGroup(isGroupExpanded ? null : group);
                      }}
                      style={{
                        position: 'absolute',
                        bottom: '15px',
                        right: '15px',
                        background: 'rgba(0,0,0,0.5)',
                        border: '1px solid rgba(255,255,255,0.2)',
                        borderRadius: '8px',
                        padding: '6px 12px',
                        color: '#fff',
                        cursor: 'pointer',
                        zIndex: 2,
                        display: 'flex',
                        alignItems: 'center',
                        gap: '6px',
                        transition: 'all 0.2s'
                      }}
                      onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(0,0,0,0.8)'}
                      onMouseLeave={(e) => e.currentTarget.style.background = 'rgba(0,0,0,0.5)'}
                    >
                      <span style={{ fontSize: '12px', fontWeight: 600 }}>{t('play.versions')}</span>
                      <i className="fa-solid fa-chevron-down" style={{ fontSize: '10px', transform: isGroupExpanded ? 'rotate(180deg)' : 'none', transition: 'transform 0.3s' }}></i>
                    </button>
                    {isSelected && (
                      <div
                        style={{
                          position: 'absolute',
                          top: '15px',
                          left: '15px',
                          background: '#3cc065',
                          color: 'white',
                          padding: '4px 12px',
                          borderRadius: '12px',
                          fontSize: '12px',
                          fontWeight: 700,
                          zIndex: 1,
                          boxShadow: '0 2px 8px rgba(0,0,0,0.5)'
                        }}
                      >
                        {t('play.selected')}
                      </div>
                    )}
                  </div>
                  
                  {isGroupExpanded && (
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '8px', marginTop: '4px' }}>
                      {versionGroups[group]
                        .sort((a, b) => a.id.localeCompare(b.id, undefined, { numeric: true }))
                        .map((version) => (
                          <button
                            key={version.id}
                            onClick={() => selectVersion(version.id)}
                            style={{
                              padding: '12px',
                              background: selectedVersion === version.id ? 'rgba(139, 92, 246, 0.15)' : 'rgba(40, 40, 55, 0.8)',
                              border: selectedVersion === version.id ? '1px solid #3cc065' : '1px solid rgba(255,255,255,0.1)',
                              borderRadius: '8px',
                              color: selectedVersion === version.id ? '#3cc065' : '#f7f7f7',
                              fontSize: '14px',
                              fontWeight: selectedVersion === version.id ? 600 : 400,
                              cursor: 'pointer',
                              display: 'flex',
                              justifyContent: 'center',
                              alignItems: 'center',
                              transition: 'all 0.2s',
                              backdropFilter: 'blur(10px)',
                            }}
                            onMouseEnter={(e) => {
                              if (selectedVersion !== version.id) {
                                e.currentTarget.style.background = 'rgba(60, 60, 80, 0.9)'
                              }
                            }}
                            onMouseLeave={(e) => {
                              if (selectedVersion !== version.id) {
                                e.currentTarget.style.background = 'rgba(40, 40, 55, 0.8)'
                              }
                            }}
                          >
                            {version.id}
                          </button>
                        ))}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      )}
    </>
  )
}

function renderVersionCard(label: string, version: string, selected: boolean, onClick: () => void, bgUrl?: string) {
  return (
    <div
      key={version}
      onClick={onClick}
      style={{
        width: '400px',
        height: '150px',
        borderRadius: '15px',
        overflow: 'hidden',
        cursor: 'pointer',
        background: bgUrl
          ? `url(${bgUrl}) center/cover no-repeat`
          : 'linear-gradient(135deg, #232324, #1a1a1a)',
        border: selected ? '2px solid #28af55' : '2px solid #30323456',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        position: 'relative',
        transition: 'all 0.4s ease-out',
      }}
      onMouseEnter={(e) => {
        const el = e.currentTarget as HTMLDivElement
        el.style.transform = 'scale(1.05)'
        el.style.borderColor = '#28af55'
        el.style.boxShadow = '0 0 20px rgba(40,175,85,0.3)'
      }}
      onMouseLeave={(e) => {
        const el = e.currentTarget as HTMLDivElement
        el.style.transform = 'scale(1)'
        if (!selected) el.style.borderColor = '#30323456'
        el.style.boxShadow = 'none'
      }}
    >
      <div
        style={{
          position: 'absolute',
          inset: 0,
          background: 'rgba(0,0,0,0.5)',
          borderRadius: '15px',
        }}
      />
      <h3
        style={{
          color: '#f7f7f7',
          fontFamily: 'Roboto, sans-serif',
          fontWeight: 300,
          fontSize: '24px',
          letterSpacing: '1px',
          position: 'relative',
          zIndex: 1,
          textShadow: '0 2px 8px rgba(0,0,0,0.8)',
        }}
      >
        {label === 'Latest' ? `Latest (${version})` : `Version ${version}`}
      </h3>
      {selected && (
        <div
          style={{
            position: 'absolute',
            top: '10px',
            right: '10px',
            background: '#28af55',
            color: 'white',
            padding: '2px 10px',
            borderRadius: '10px',
            fontSize: '11px',
            fontWeight: 500,
            zIndex: 1,
          }}
        >
          SELECTED
        </div>
      )}
    </div>
  )
}

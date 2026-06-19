export interface MinecraftAccount {
  id: string
  username: string
  uuid: string
  accessToken: string
  refreshToken?: string
  avatarUrl?: string
  type: 'microsoft' | 'offline' | 'guest'
  lastLogin: string
}

export interface MinecraftVersion {
  id: string
  type: 'release' | 'snapshot' | 'old_alpha' | 'old_beta'
  url: string
  time: string
  releaseTime: string
}

export interface ModEntry {
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

export type ModCategory = 'utility' | 'performance' | 'visual' | 'pvp' | 'minigame' | 'cosmetic'

export interface CosmeticItem {
  id: string
  name: string
  type: CosmaticType
  rarity: CosmaticRarity
  previewUrl?: string
  equipped: boolean
  category: string
  color?: string
}

export type CosmaticType = 'cape' | 'wings' | 'particle' | 'hat' | 'bandana' | 'suit' | 'shoes' | 'accessory' | 'cloak' | 'pet' | 'emote' | 'trail' | 'deathfx'

export type CosmaticRarity = 'common' | 'rare' | 'epic' | 'legendary'

export interface ProxyConfig {
  enabled: boolean
  host: string
  port: number
  targetHost: string
  targetPort: number
  modules: {
    stats: boolean
    emotes: boolean
    bedwars: boolean
    waypoints: boolean
    autoBoop: boolean
    bedwarsStats: boolean
  }
}

export interface HudConfig {
  enabled: boolean
  position: { x: number; y: number }
  scale: number
  visible: boolean
}

export interface KeystrokeHudConfig extends HudConfig {
  showCps: boolean
  showWasd: boolean
  style: 'boxed' | 'minimal' | 'label'
  backgroundColor: string
  textColor: string
  accentColor: string
}

export interface PlayerModelHudConfig extends HudConfig {
  dynamicRotation: boolean
  showArmor: boolean
}

export interface CrosshairHudConfig extends HudConfig {
  mode: 'static' | 'dynamic' | 'dot' | 'cross' | 'circle'
  color: string
  size: number
  dynamic: boolean
  dot: boolean
}

export interface PackDisplayHudConfig extends HudConfig {
  iconsOnly: boolean
  showCount: boolean
}

export interface BossBarHudConfig extends HudConfig {
  barPosition: 'top' | 'bottom' | 'custom'
  showText: boolean
  showBar: boolean
  barScale: number
}

export interface ScoreboardHudConfig extends HudConfig {
  backgroundColor: string
  titleColor: string
  textColor: string
  width: number
}

export interface DebugCountersConfig extends HudConfig {
  showFps: boolean
  showTps: boolean
  showRam: boolean
  showCoords: boolean
}

export interface BedwarsStatsConfig extends HudConfig {
  showKills: boolean
  showDeaths: boolean
  showWins: boolean
  showLosses: boolean
  showKDR: boolean
}

export interface ScreenshotConfig {
  autoCapture: boolean
  saveDir: string
  showToast: boolean
}

export interface ScreenshotEntry {
  id: string
  path: string
  thumbnail: string
  timestamp: number
  server?: string
  gameMode?: string
}

export interface PatcherConfig {
  enabled: boolean
  config: string
}

export type ModLoader = 'none' | 'fabric' | 'forge' | 'optifine'

export interface LauncherSettings {
  ram: { min: number; max: number }
  javaPath: string
  customDir: string
  proxy: ProxyConfig
  patcher: PatcherConfig
  selectedLoader: ModLoader
}

export interface WindowApi {
  window: {
    minimize: () => Promise<void>
    maximize: () => Promise<void>
    close: () => Promise<void>
    isMaximized: () => Promise<boolean>
    setSize: (w: number, h: number) => Promise<void>
  }
  shell: {
    openExternal: (url: string) => Promise<void>
    openNovaFolder: () => Promise<void>
  }
  microsoft: {
    login: () => Promise<MinecraftAccount>
    refresh: (token: string) => Promise<{ accessToken: string; refreshToken: string }>
  }
  offline: {
    createUUID: (username: string) => Promise<string>
  }
  minecraft: {
    launch: (opts: any) => Promise<number>
    onDebug: (cb: (data: any) => void) => () => void
    onData: (cb: (data: any) => void) => () => void
    onProgress: (cb: (data: any) => void) => () => void
  }
  proxy: {
    start: (config?: any) => Promise<boolean>
    stop: () => Promise<boolean>
    getStatus: () => Promise<boolean>
  }
  versions: {
    get: () => Promise<MinecraftVersion[]>
  }
  mods: {
    getModsDir: () => Promise<string>
    download: (opts: { projectId: string; versionId: string; fileName: string }) => Promise<{ success: boolean; path: string }>
    uninstall: (opts: { fileName: string }) => Promise<{ success: boolean }>
    list: () => Promise<string[]>
    enable: (opts: { fileName: string }) => Promise<{ success: boolean }>
    disable: (opts: { fileName: string }) => Promise<{ success: boolean }>
  }
  loaders: {
    installFabric: (opts: { version: string }) => Promise<{ success: boolean; version: string }>
    installForge: (opts: { version: string }) => Promise<{ success: boolean; version: string }>
    installOptiFine: (opts: { version: string }) => Promise<{ success: boolean }>
  }
  nametagLogo: {
    pickFile: () => Promise<{ canceled: boolean; filePath?: string }>
    save: (opts: { filePath: string }) => Promise<{ success: boolean; savedPath?: string }>
    get: () => Promise<{ exists: boolean; path?: string }>
    clear: () => Promise<{ success: boolean }>
  }

  customCapes: {
    pickFile: () => Promise<{ canceled: boolean; filePath?: string }>
    upload: (opts: { name: string; filePath: string }) => Promise<{ success: boolean; capeId?: string }>
    list: () => Promise<Array<{ id: string; name: string; filePath: string }>>
    remove: (opts: { capeId: string }) => Promise<{ success: boolean }>
  }
}

declare global {
  interface Window {
    nova: WindowApi
  }
}

import { create } from 'zustand'
import type { LauncherSettings, MinecraftVersion } from '../types'

interface SettingsState {
  settings: LauncherSettings
  selectedVersion: string
  versions: MinecraftVersion[]
  isProxyRunning: boolean
  isLaunching: boolean
  launchProgress: string | null
  updateSettings: (partial: Partial<LauncherSettings>) => void
  setSelectedVersion: (v: string) => void
  setVersions: (v: MinecraftVersion[]) => void
  setProxyRunning: (v: boolean) => void
  setLaunching: (v: boolean) => void
  setLaunchProgress: (v: string | null) => void
  loadSettings: () => void
}

const defaults: LauncherSettings = {
  ram: { min: 2, max: 4 },
  javaPath: '',
  customDir: '',
  proxy: {
    enabled: false,
    host: '127.0.0.1',
    port: 25566,
    targetHost: 'hypixel.net',
    targetPort: 25565,
    modules: {
      stats: true,
      emotes: true,
      bedwars: true,
      waypoints: true,
    },
  },
  patcher: {
    enabled: true,
    config: 'fullbright=true,cps=true,reach=true,velocity=true,autotool=true,sprint=true,noslow=true,antikb=true,timer=true,autoclicker=true',
  },
  selectedLoader: 'none',
}

export const useSettingsStore = create<SettingsState>((set) => ({
  settings: defaults,
  selectedVersion: 'latest_release',
  versions: [],
  isProxyRunning: false,
  isLaunching: false,
  launchProgress: null,

  updateSettings: (partial) => {
    set((s) => {
      const updated = { ...s.settings, ...partial }
      localStorage.setItem('orbit_settings', JSON.stringify(updated))
      return { settings: updated }
    })
  },

  setSelectedVersion: (v) => set({ selectedVersion: v }),
  setVersions: (v) => set({ versions: v }),
  setProxyRunning: (v) => set({ isProxyRunning: v }),
  setLaunching: (v) => set({ isLaunching: v }),
  setLaunchProgress: (v) => set({ launchProgress: v }),

  loadSettings: () => {
    const stored = localStorage.getItem('orbit_settings')
    if (stored) {
      const parsed = JSON.parse(stored)
      set({ settings: { ...defaults, ...parsed } })
    }
  },
}))

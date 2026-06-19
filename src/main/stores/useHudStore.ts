import { create } from 'zustand'
import type {
  KeystrokeHudConfig,
  PlayerModelHudConfig,
  CrosshairHudConfig,
  PackDisplayHudConfig,
  BossBarHudConfig,
  ScoreboardHudConfig,
  DebugCountersConfig,
  BedwarsStatsConfig,
  ScreenshotConfig,
  ScreenshotEntry,
} from '../types'

interface HudState {
  keystroke: KeystrokeHudConfig
  playerModel: PlayerModelHudConfig
  crosshair: CrosshairHudConfig
  packDisplay: PackDisplayHudConfig
  bossBar: BossBarHudConfig
  scoreboard: ScoreboardHudConfig
  debugCounters: DebugCountersConfig
  bedwarsStats: BedwarsStatsConfig
  screenshot: ScreenshotConfig
  screenshots: ScreenshotEntry[]
  editingHud: string | null

  setEditingHud: (id: string | null) => void
  updateKeystroke: (partial: Partial<KeystrokeHudConfig>) => void
  updatePlayerModel: (partial: Partial<PlayerModelHudConfig>) => void
  updateCrosshair: (partial: Partial<CrosshairHudConfig>) => void
  updatePackDisplay: (partial: Partial<PackDisplayHudConfig>) => void
  updateBossBar: (partial: Partial<BossBarHudConfig>) => void
  updateScoreboard: (partial: Partial<ScoreboardHudConfig>) => void
  updateDebugCounters: (partial: Partial<DebugCountersConfig>) => void
  updateBedwarsStats: (partial: Partial<BedwarsStatsConfig>) => void
  updateScreenshot: (partial: Partial<ScreenshotConfig>) => void
  addScreenshot: (entry: ScreenshotEntry) => void
  removeScreenshot: (id: string) => void
  loadHud: () => void
  saveHud: () => void
}

const defaultKeystroke: KeystrokeHudConfig = {
  enabled: false, position: { x: 10, y: 200 }, scale: 1, visible: true,
  showCps: true, showWasd: true, style: 'boxed',
  backgroundColor: '#00000080', textColor: '#FFFFFF', accentColor: '#FFFFFF',
}

const defaultPlayerModel: PlayerModelHudConfig = {
  enabled: false, position: { x: 50, y: 150 }, scale: 1, visible: true,
  dynamicRotation: true, showArmor: true,
}

const defaultCrosshair: CrosshairHudConfig = {
  enabled: false, position: { x: 0.5, y: 0.5 }, scale: 1, visible: true,
  mode: 'dynamic', color: '#FFFFFF', size: 16, dynamic: true, dot: false,
}

const defaultPackDisplay: PackDisplayHudConfig = {
  enabled: false, position: { x: 10, y: 10 }, scale: 1, visible: true,
  iconsOnly: false, showCount: true,
}

const defaultBossBar: BossBarHudConfig = {
  enabled: false, position: { x: 0.5, y: 10 }, scale: 1, visible: true,
  barPosition: 'top', showText: true, showBar: true, barScale: 1.0,
}

const defaultScoreboard: ScoreboardHudConfig = {
  enabled: false, position: { x: 0.95, y: 0.1 }, scale: 1, visible: true,
  backgroundColor: '#00000080', titleColor: '#FFAA00', textColor: '#FFFFFF', width: 150,
}

const defaultDebugCounters: DebugCountersConfig = {
  enabled: false, position: { x: 10, y: 10 }, scale: 1, visible: true,
  showFps: true, showTps: true, showRam: true, showCoords: false,
}

const defaultBedwarsStats: BedwarsStatsConfig = {
  enabled: false, position: { x: 10, y: 300 }, scale: 1, visible: true,
  showKills: true, showDeaths: true, showWins: true, showLosses: true, showKDR: true,
}

const defaultScreenshot: ScreenshotConfig = {
  autoCapture: false, saveDir: 'screenshots', showToast: true,
}

export const useHudStore = create<HudState>((set, get) => ({
  keystroke: defaultKeystroke,
  playerModel: defaultPlayerModel,
  crosshair: defaultCrosshair,
  packDisplay: defaultPackDisplay,
  bossBar: defaultBossBar,
  scoreboard: defaultScoreboard,
  debugCounters: defaultDebugCounters,
  bedwarsStats: defaultBedwarsStats,
  screenshot: defaultScreenshot,
  screenshots: [],
  editingHud: null,

  setEditingHud: (id) => set({ editingHud: id }),
  updateKeystroke: (p) => set((s) => ({ keystroke: { ...s.keystroke, ...p } })),
  updatePlayerModel: (p) => set((s) => ({ playerModel: { ...s.playerModel, ...p } })),
  updateCrosshair: (p) => set((s) => ({ crosshair: { ...s.crosshair, ...p } })),
  updatePackDisplay: (p) => set((s) => ({ packDisplay: { ...s.packDisplay, ...p } })),
  updateBossBar: (p) => set((s) => ({ bossBar: { ...s.bossBar, ...p } })),
  updateScoreboard: (p) => set((s) => ({ scoreboard: { ...s.scoreboard, ...p } })),
  updateDebugCounters: (p) => set((s) => ({ debugCounters: { ...s.debugCounters, ...p } })),
  updateBedwarsStats: (p) => set((s) => ({ bedwarsStats: { ...s.bedwarsStats, ...p } })),
  updateScreenshot: (p) => set((s) => ({ screenshot: { ...s.screenshot, ...p } })),
  addScreenshot: (entry) => set((s) => ({ screenshots: [entry, ...s.screenshots] })),
  removeScreenshot: (id) => set((s) => ({ screenshots: s.screenshots.filter((e) => e.id !== id) })),

  loadHud: () => {
    const stored = localStorage.getItem('orbit_hud')
    if (stored) {
      const data = JSON.parse(stored)
      set({
        keystroke: { ...defaultKeystroke, ...data.keystroke },
        playerModel: { ...defaultPlayerModel, ...data.playerModel },
        crosshair: { ...defaultCrosshair, ...data.crosshair },
        packDisplay: { ...defaultPackDisplay, ...data.packDisplay },
        bossBar: { ...defaultBossBar, ...data.bossBar },
        scoreboard: { ...defaultScoreboard, ...data.scoreboard },
        debugCounters: { ...defaultDebugCounters, ...data.debugCounters },
        bedwarsStats: { ...defaultBedwarsStats, ...data.bedwarsStats },
        screenshot: { ...defaultScreenshot, ...data.screenshot },
        screenshots: data.screenshots || [],
      })
    }
  },

  saveHud: () => {
    const s = get()
    localStorage.setItem(
      'orbit_hud',
      JSON.stringify({
        keystroke: s.keystroke,
        playerModel: s.playerModel,
        crosshair: s.crosshair,
        packDisplay: s.packDisplay,
        bossBar: s.bossBar,
        scoreboard: s.scoreboard,
        debugCounters: s.debugCounters,
        bedwarsStats: s.bedwarsStats,
        screenshot: s.screenshot,
        screenshots: s.screenshots,
      })
    )
  },
}))

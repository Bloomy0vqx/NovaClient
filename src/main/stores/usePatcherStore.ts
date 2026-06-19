import { create } from 'zustand'

export interface PatchOption {
  id: string
  name: string
  icon: string
  enabled: boolean
  values?: Record<string, string>
  hasValues?: boolean
}

const defaultPatches: PatchOption[] = [
  { id: 'freelook', name: 'Freelook & AutoTextHotKey', icon: 'fa-solid fa-eye', enabled: false },
  { id: 'removePinned', name: 'Remove Pinned Servers', icon: 'fa-solid fa-thumbtack', enabled: false },
  { id: 'removeBlog', name: 'Remove Blog Posts', icon: 'fa-solid fa-newspaper', enabled: false },
  { id: 'cloaks', name: 'Cloaks+', icon: 'fa-solid fa-mask', enabled: true },
  { id: 'removeMods', name: 'Remove Mods Packet', icon: 'fa-solid fa-cube', enabled: false },
  { id: 'nickHider', name: 'Nick Hider', icon: 'fa-solid fa-user-secret', enabled: false, hasValues: true, values: { to: 'You' } },
  { id: 'overlays', name: 'Nova Overlays', icon: 'fa-solid fa-layer-group', enabled: true },
  { id: 'reachText', name: 'Reach Text', icon: 'fa-solid fa-ruler', enabled: false, hasValues: true, values: { to: 'blocks' } },
  { id: 'uncapReach', name: 'Uncap Reach Display', icon: 'fa-solid fa-ruler-combined', enabled: false },
  { id: 'discordRPC', name: 'Discord RPC', icon: 'fa-brands fa-discord', enabled: true, hasValues: true, values: { clientID: '', icon: '', afkText: 'AFK' } },
  { id: 'removeLevelHead', name: 'Remove Fake Level Head', icon: 'fa-solid fa-tag', enabled: false },
  { id: 'fixPing', name: 'Fix Ping Sound', icon: 'fa-solid fa-bell', enabled: false },
  { id: 'removeHitDelay', name: 'Remove Hit Delay', icon: 'fa-solid fa-bolt', enabled: false },
  { id: 'windowTitle', name: 'Window Title', icon: 'fa-solid fa-window-maximize', enabled: true, hasValues: true, values: { to: 'Orbit Client (Modded)' } },
  { id: 'levelHead', name: 'Level Head', icon: 'fa-solid fa-ranking-star', enabled: false, hasValues: true, values: { to: 'Level' } },
  { id: 'toggleSprint', name: 'Toggle Sprint Texts', icon: 'fa-solid fa-person-running', enabled: false, hasValues: true, values: { sprinting: 'Sprinting', sneaking: 'Sneaking' } },
  { id: 'fpsSpoof', name: 'FPS Spoof', icon: 'fa-solid fa-chart-line', enabled: false, hasValues: true, values: { multiplier: '2.0' } },
  { id: 'fps', name: 'FPS', icon: 'fa-solid fa-chart-simple', enabled: false, hasValues: true, values: { to: 'FPS' } },
  { id: 'cps', name: 'CPS', icon: 'fa-solid fa-hand-pointer', enabled: false, hasValues: true, values: { to: 'CPS' } },
  { id: 'autoGG', name: 'Auto GG', icon: 'fa-solid fa-message', enabled: false, hasValues: true, values: { to: '/achat gg' } },
  { id: 'removeHash', name: 'Remove hashing', icon: 'fa-solid fa-hashtag', enabled: false },
  { id: 'optionsSP', name: 'Options in Singleplayer', icon: 'fa-solid fa-gear', enabled: false },
  { id: 'toggleSneak', name: 'Toggle Sneak in Container', icon: 'fa-solid fa-box', enabled: false },
  { id: 'pingText', name: 'Change ping text', icon: 'fa-solid fa-signal', enabled: false, hasValues: true, values: { to: 'ms' } },
  { id: 'pingValue', name: 'Change ping value', icon: 'fa-solid fa-signal', enabled: false, hasValues: true, values: { pingValue: '100' } },
  { id: 'clothCloaks', name: 'Always render cloth cloaks', icon: 'fa-solid fa-vest', enabled: false },
  { id: 'hurtCam', name: 'Adjust hurt cam', icon: 'fa-solid fa-camera', enabled: false, hasValues: true, values: { multiplier: '0.3' } },
  { id: 'chatLimit', name: 'Remove chat limit', icon: 'fa-solid fa-comment', enabled: false, hasValues: true, values: { limit: '255' } },
  { id: 'chatCooldown', name: 'Remove Nova chat cooldown', icon: 'fa-solid fa-comment-slash', enabled: true },
  { id: 'mumbleFix', name: 'Mumble fix', icon: 'fa-solid fa-microphone', enabled: false },
  { id: 'websocket', name: 'Websocket', icon: 'fa-solid fa-plug', enabled: false, hasValues: true, values: { to: 'wss://' } },
  { id: 'showWrapped', name: 'Show wrapped', icon: 'fa-solid fa-gift', enabled: false },

  // === AxolotlClient modules ===
  { id: 'motionBlur', name: 'Motion Blur', icon: 'fa-solid fa-droplet', enabled: false, hasValues: true, values: { strength: '50', inGuis: 'false' } },
  { id: 'tablistCustom', name: 'Tablist Custom', icon: 'fa-solid fa-table-columns', enabled: false, hasValues: true, values: { numericalPing: 'true', showPlayerHeads: 'true', showHeader: 'true', showFooter: 'true', pingColor: '#00FF00', backgroundColor: '#00000080' } },
  { id: 'scrollableTooltips', name: 'Scrollable Tooltips', icon: 'fa-solid fa-arrows-up-down', enabled: false, hasValues: true, values: { scrollAmount: '5', inverse: 'false', shiftHorizontal: 'true' } },
  { id: 'crosshairCustom', name: 'Custom Crosshair', icon: 'fa-solid fa-crosshairs', enabled: false, hasValues: true, values: { mode: 'static', color: '#FFFFFF', size: '16', dynamic: 'true', dot: 'false' } },
  { id: 'keystrokeHud', name: 'Keystroke HUD', icon: 'fa-solid fa-keyboard', enabled: false, hasValues: true, values: { showCps: 'true', showWasd: 'true', style: 'boxed', backgroundColor: '#00000080', textColor: '#FFFFFF', accentColor: '#FFFFFF' } },
  { id: 'playerModelHud', name: 'Player Model HUD', icon: 'fa-solid fa-person', enabled: false, hasValues: true, values: { scale: '1.0', dynamicRotation: 'true', showArmor: 'true' } },
  { id: 'packDisplayHud', name: 'Pack Display HUD', icon: 'fa-solid fa-palette', enabled: false, hasValues: true, values: { iconsOnly: 'false', showCount: 'true' } },
  { id: 'bossBarCustom', name: 'Custom Boss Bar', icon: 'fa-solid fa-dragon', enabled: false, hasValues: true, values: { position: 'top', showText: 'true', showBar: 'true', barScale: '1.0' } },
  { id: 'scoreboardCustom', name: 'Custom Scoreboard', icon: 'fa-solid fa-list-ol', enabled: false, hasValues: true, values: { backgroundColor: '#00000080', titleColor: '#FFAA00', textColor: '#FFFFFF', width: '150' } },
  { id: 'actionBarCustom', name: 'Custom Action Bar', icon: 'fa-solid fa-comment-dots', enabled: false, hasValues: true, values: { textColor: '#FFFF55', shadow: 'true' } },
  { id: 'debugCounters', name: 'Debug Counters', icon: 'fa-solid fa-microchip', enabled: false, hasValues: true, values: { showFps: 'true', showTps: 'true', showRam: 'true', showCoords: 'false' } },
  { id: 'autoBoop', name: 'Auto Boop (Hypixel)', icon: 'fa-solid fa-hand-point-right', enabled: false, hasValues: true, values: { filters: '', cooldown: '3' } },
  { id: 'bedwarsStats', name: 'Bedwars Stats HUD', icon: 'fa-solid fa-bed', enabled: false, hasValues: true, values: { showKills: 'true', showDeaths: 'true', showWins: 'true', showLosses: 'true', showKDR: 'true' } },
  { id: 'screenshotGallery', name: 'Screenshot Gallery', icon: 'fa-solid fa-images', enabled: false, hasValues: true, values: { autoCapture: 'false', saveDir: 'screenshots', showToast: 'true' } },
]

interface PatcherState {
  patches: PatchOption[]
  customizing: PatchOption | null
  setCustomizing: (p: PatchOption | null) => void
  togglePatch: (id: string) => void
  updatePatchValue: (id: string, key: string, value: string) => void
  loadPatches: () => void
  savePatches: () => void
}

export const usePatcherStore = create<PatcherState>((set, get) => ({
  patches: defaultPatches,
  customizing: null,

  setCustomizing: (p) => set({ customizing: p }),

  togglePatch: (id) => {
    set((s) => ({
      patches: s.patches.map((p) =>
        p.id === id ? { ...p, enabled: !p.enabled } : p
      ),
    }))
    get().savePatches()
  },

  updatePatchValue: (id, key, value) => {
    set((s) => ({
      patches: s.patches.map((p) =>
        p.id === id && p.values
          ? { ...p, values: { ...p.values, [key]: value } }
          : p
      ),
    }))
    get().savePatches()
  },

  loadPatches: () => {
    const stored = localStorage.getItem('orbit_patcher')
    if (stored) {
      const saved: { id: string; enabled: boolean; values?: Record<string, string> }[] = JSON.parse(stored)
      set((s) => ({
        patches: s.patches.map((p) => {
          const match = saved.find((sp) => sp.id === p.id)
          if (match) {
            return {
              ...p,
              enabled: match.enabled,
              values: match.values || p.values,
            }
          }
          return p
        }),
      }))
    }
  },

  savePatches: () => {
    const { patches } = get()
    localStorage.setItem(
      'orbit_patcher',
      JSON.stringify(
        patches.map((p) => ({
          id: p.id,
          enabled: p.enabled,
          values: p.values,
        }))
      )
    )
  },
}))

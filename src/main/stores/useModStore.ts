import { create } from 'zustand'
import type { ModEntry, ModCategory, ModLoader } from '../types'

interface ModState {
  mods: ModEntry[]
  search: string
  categoryFilter: ModCategory | 'all'
  viewMode: 'browse' | 'installed'
  isLoading: boolean
  installedMods: ModEntry[]
  setSearch: (s: string) => void
  setCategoryFilter: (c: ModCategory | 'all') => void
  setViewMode: (m: 'browse' | 'installed') => void
  toggleMod: (id: string) => Promise<void>
  installMod: (id: string) => Promise<void>
  uninstallMod: (id: string) => Promise<void>
  updateMods: (gameVersion: string, loader: ModLoader) => Promise<void>
  loadMods: () => void
  saveMods: () => void
  fetchMods: (gameVersion?: string, loader?: ModLoader) => Promise<void>
}

function loadSavedInstalled(): ModEntry[] {
  try {
    const stored = localStorage.getItem('orbit_mods')
    if (!stored) return []
    const saved: any[] = JSON.parse(stored)
    return saved.filter((m) => m.installed).map((m) => ({
      id: m.id,
      name: m.id,
      description: '',
      version: '',
      author: '',
      category: 'utility' as ModCategory,
      downloads: 0,
      installed: true,
      enabled: m.enabled ?? true,
      fileName: m.fileName,
    }))
  } catch {
    return []
  }
}

export const useModStore = create<ModState>((set, get) => ({
  mods: [],
  search: '',
  categoryFilter: 'all',
  viewMode: 'browse',
  isLoading: false,
  installedMods: loadSavedInstalled(),

  setSearch: (s) => {
    set({ search: s })
    get().fetchMods()
  },
  setCategoryFilter: (c) => {
    set({ categoryFilter: c })
    get().fetchMods()
  },
  setViewMode: (m) => set({ viewMode: m }),

  toggleMod: async (id) => {
    const allMods = [...get().mods, ...get().installedMods]
    const mod = allMods.find((m) => m.id === id)
    if (!mod || !mod.installed || !mod.fileName) return

    const newEnabled = !mod.enabled

    const updateMod = (m: ModEntry) => m.id === id ? { ...m, enabled: newEnabled } : m
    set((s) => ({
      mods: s.mods.map(updateMod),
      installedMods: s.installedMods.map(updateMod),
    }))

    try {
      if (newEnabled) {
        await window.nova.mods.enable({ fileName: mod.fileName })
      } else {
        await window.nova.mods.disable({ fileName: mod.fileName })
      }
    } catch (e) {
      console.error('Failed to toggle mod:', e)
    }
    get().saveMods()
  },

  installMod: async (id) => {
    const mod = get().mods.find((m) => m.id === id)
    if (!mod || mod.installed) return

    try {
      set((s) => ({
        mods: s.mods.map((m) => m.id === id ? { ...m, isInstalling: true } : m),
      }))

      const fileName = `${mod.name.replace(/[^a-zA-Z0-9]/g, '_')}.jar`
      await window.nova.mods.download({
        projectId: id,
        versionId: mod.version || '',
        fileName,
      })

      const updatedMod = { ...mod, installed: true, enabled: true, fileName, isInstalling: false }

      set((s) => ({
        mods: s.mods.map((m) => m.id === id ? updatedMod : m),
        installedMods: [...s.installedMods.filter((m) => m.id !== id), updatedMod],
      }))
      get().saveMods()
    } catch (err) {
      console.error('Failed to install mod:', err)
      set((s) => ({
        mods: s.mods.map((m) => m.id === id ? { ...m, isInstalling: false } : m),
      }))
    }
  },

  uninstallMod: async (id) => {
    const allMods = [...get().mods, ...get().installedMods]
    const mod = allMods.find((m) => m.id === id)
    if (!mod || !mod.installed || !mod.fileName) return

    try {
      await window.nova.mods.uninstall({ fileName: mod.fileName })

      set((s) => ({
        mods: s.mods.map((m) => m.id === id ? { ...m, installed: false, enabled: false, fileName: undefined } : m),
        installedMods: s.installedMods.filter((m) => m.id !== id),
      }))
      get().saveMods()
    } catch (err) {
      console.error('Failed to uninstall mod:', err)
    }
  },

  updateMods: async (gameVersion, loader) => {
    const installedMods = get().installedMods.filter((m) => m.fileName)

    for (const mod of installedMods) {
      try {
        await window.nova.mods.uninstall({ fileName: mod.fileName! })

        const facets: string[][] = []
        if (gameVersion) facets.push([`versions:${gameVersion}`])
        if (loader && loader !== 'none') facets.push([`categories:${loader}`])

        const searchUrl = `https://api.modrinth.com/v2/search?query=${encodeURIComponent(mod.name)}&limit=5&facets=${JSON.stringify(facets)}`
        const searchRes = await fetch(searchUrl)
        if (!searchRes.ok) continue
        const searchData = await searchRes.json()
        const hit = searchData.hits?.find((h: any) => h.project_id === mod.id)
        if (!hit) {
          set((s) => ({
            installedMods: s.installedMods.filter((m) => m.id !== mod.id),
            mods: s.mods.map((m) => m.id === mod.id ? { ...m, installed: false, enabled: false, fileName: undefined } : m),
          }))
          continue
        }

        const newFileName = `${mod.name.replace(/[^a-zA-Z0-9]/g, '_')}.jar`
        await window.nova.mods.download({
          projectId: mod.id,
          versionId: hit.latest_version,
          fileName: newFileName,
        })

        const updated = { ...mod, installed: true, enabled: true, fileName: newFileName, version: hit.version_number || mod.version }
        set((s) => ({
          installedMods: s.installedMods.map((m) => m.id === mod.id ? updated : m),
          mods: s.mods.map((m) => m.id === mod.id ? updated : m),
        }))
      } catch (err) {
        console.error(`Failed to update mod ${mod.name}:`, err)
      }
    }
    get().saveMods()
  },

  loadMods: () => {
    set({ installedMods: loadSavedInstalled() })
  },

  saveMods: () => {
    const { mods, installedMods } = get()
    const allMap = new Map<string, any>()
    for (const m of [...mods, ...installedMods]) {
      allMap.set(m.id, { id: m.id, installed: m.installed, enabled: m.enabled, fileName: m.fileName })
    }
    localStorage.setItem('orbit_mods', JSON.stringify(Array.from(allMap.values())))
  },

  fetchMods: async (gameVersion?: string, loader?: ModLoader) => {
    const { search, categoryFilter } = get()
    set({ isLoading: true })
    try {
      let url = 'https://api.modrinth.com/v2/search?limit=30'
      const facets: string[][] = []

      if (gameVersion && gameVersion.startsWith('1.')) {
        facets.push([`versions:${gameVersion}`])
      }

      if (loader && loader !== 'none') {
        facets.push([`categories:${loader}`])
      }

      if (categoryFilter !== 'all') {
        let modrinthCat: string = categoryFilter
        if (categoryFilter === 'performance') modrinthCat = 'optimization'
        else if (categoryFilter === 'visual') modrinthCat = 'graphics'
        facets.push([`categories:${modrinthCat}`])
      }

      if (search) {
        url += `&query=${encodeURIComponent(search)}`
      }

      if (facets.length > 0) {
        url += `&facets=${JSON.stringify(facets)}`
      }

      const res = await fetch(url)
      const data = await res.json()

      const savedInstalled = get().installedMods
      const installedMap = new Map(savedInstalled.map((m) => [m.id, m]))

      const fetchedMods: ModEntry[] = (data.hits || []).map((hit: any) => {
        const saved = installedMap.get(hit.project_id)
        return {
          id: hit.project_id,
          name: hit.title,
          description: hit.description,
          version: hit.latest_version,
          author: hit.author,
          category: categoryFilter !== 'all' ? categoryFilter : 'utility',
          downloads: hit.downloads,
          iconUrl: hit.icon_url || undefined,
          installed: saved?.installed || false,
          enabled: saved?.enabled ?? false,
          fileName: saved?.fileName,
        }
      })

      // Add installed mods not in search results
      const fetchedIds = new Set(fetchedMods.map((m) => m.id))
      for (const saved of savedInstalled) {
        if (!fetchedIds.has(saved.id)) {
          fetchedMods.push(saved)
        }
      }

      set({ mods: fetchedMods, isLoading: false })
    } catch (err) {
      console.error('Failed to fetch mods from Modrinth', err)
      set({ isLoading: false })
    }
  },
}))

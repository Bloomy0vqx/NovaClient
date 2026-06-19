import { contextBridge, ipcRenderer } from 'electron'

function wrap(name: string, fn: (...args: any[]) => Promise<any>) {
  return async (...args: any[]) => {
    try {
      return await fn(...args)
    } catch (e: any) {
      console.error(`[nova:${name}]`, (e as any)?.message || e)
      throw e
    }
  }
}

const api = {
  window: {
    minimize: wrap('minimize', () => ipcRenderer.invoke('window:minimize')),
    maximize: wrap('maximize', () => ipcRenderer.invoke('window:maximize')),
    close: wrap('close', () => ipcRenderer.invoke('window:close')),
    isMaximized: wrap('isMaximized', () => ipcRenderer.invoke('window:isMaximized')),
    setSize: wrap('setSize', (w: number, h: number) => ipcRenderer.invoke('window:setSize', w, h)),
  },

  shell: {
    openExternal: wrap('openExternal', (url: string) =>
      ipcRenderer.invoke('shell:openExternal', url),
    ),
    openNovaFolder: wrap('openNovaFolder', () => ipcRenderer.invoke('shell:openNovaFolder')),
  },

  microsoft: {
    login: wrap('microsoft:login', () => ipcRenderer.invoke('microsoft:login')),
    refresh: wrap('microsoft:refresh', (token: string) =>
      ipcRenderer.invoke('microsoft:refresh', token),
    ),
  },

  offline: {
    createUUID: wrap('offline:uuid', (username: string) =>
      ipcRenderer.invoke('offline:uuid', username),
    ),
  },

  minecraft: {
    launch: (opts: any) => ipcRenderer.invoke('minecraft:launch', opts),

    onDebug: (cb: (data: string) => void) => {
      const handler = (_: any, data: string) => cb(data)
      ipcRenderer.on('minecraft:debug', handler)
      return () => ipcRenderer.removeListener('minecraft:debug', handler)
    },

    onData: (cb: (data: string) => void) => {
      const handler = (_: any, data: string) => cb(data)
      ipcRenderer.on('minecraft:data', handler)
      return () => ipcRenderer.removeListener('minecraft:data', handler)
    },

    onProgress: (cb: (data: string) => void) => {
      const handler = (_: any, data: string) => cb(data)
      ipcRenderer.on('minecraft:progress', handler)
      return () => ipcRenderer.removeListener('minecraft:progress', handler)
    },
  },

  proxy: {
    start: wrap('proxy:start', (config: any) =>
      ipcRenderer.invoke('proxy:start', config || {}),
    ),
    stop: wrap('proxy:stop', () => ipcRenderer.invoke('proxy:stop')),
    getStatus: wrap('proxy:getStatus', () => ipcRenderer.invoke('proxy:getStatus')),
  },

  versions: {
    get: wrap('get:versions', () => ipcRenderer.invoke('get:versions')),
  },

  mods: {
    getModsDir: wrap('mods:getModsDir', () => ipcRenderer.invoke('mods:getModsDir')),
    download: wrap('mods:download', (opts: any) => ipcRenderer.invoke('mods:download', opts)),
    uninstall: wrap('mods:uninstall', (opts: any) =>
      ipcRenderer.invoke('mods:uninstall', opts),
    ),
    list: wrap('mods:list', () => ipcRenderer.invoke('mods:list')),
    enable: wrap('mods:enable', (opts: any) => ipcRenderer.invoke('mods:enable', opts)),
    disable: wrap('mods:disable', (opts: any) => ipcRenderer.invoke('mods:disable', opts)),
  },

  loaders: {
    installFabric: wrap('loader:install-fabric', (opts: any) =>
      ipcRenderer.invoke('loader:install-fabric', opts),
    ),
    installForge: wrap('loader:install-forge', (opts: any) =>
      ipcRenderer.invoke('loader:install-forge', opts),
    ),
    installOptiFine: wrap('loader:install-optifine', (opts: any) =>
      ipcRenderer.invoke('loader:install-optifine', opts),
    ),
  },

  nametagLogo: {
    pickFile: wrap('nametagLogo:pickFile', () => ipcRenderer.invoke('nametagLogo:pickFile')),
    save: wrap('nametagLogo:save', (opts: { filePath: string }) =>
      ipcRenderer.invoke('nametagLogo:save', opts),
    ),
    get: wrap('nametagLogo:get', () => ipcRenderer.invoke('nametagLogo:get')),
    clear: wrap('nametagLogo:clear', () => ipcRenderer.invoke('nametagLogo:clear')),
  },

  customCapes: {
    pickFile: wrap('customCapes:pickFile', () => ipcRenderer.invoke('customCapes:pickFile')),
    upload: wrap('customCapes:upload', (opts: { name: string; filePath: string }) =>
      ipcRenderer.invoke('customCapes:upload', opts),
    ),
    list: wrap('customCapes:list', () => ipcRenderer.invoke('customCapes:list')),
    remove: wrap('customCapes:remove', (opts: { capeId: string }) =>
      ipcRenderer.invoke('customCapes:remove', opts),
    ),
  },

  cosmetics: {
    list: wrap('cosmetics:list', () => ipcRenderer.invoke('cosmetics:list')),
  },
}

contextBridge.exposeInMainWorld('nova', api)

import { app, BrowserWindow, ipcMain, shell, dialog } from 'electron'
import path from 'path'
import { fileURLToPath } from 'url'
import crypto from 'crypto'
import fs from 'fs'
import { execFile } from 'child_process'
import { Client } from 'minecraft-launcher-core'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

function execAsync(cmd: string, args: string[], opts: { cwd?: string; timeout?: number }): Promise<string> {
  return new Promise((resolve, reject) => {
    execFile(cmd, args, { encoding: 'utf8', timeout: opts.timeout || 180_000, cwd: opts.cwd, windowsHide: true }, (err, stdout, stderr) => {
      if (err) {
        (err as any).stderr = stderr
        reject(err)
      } else {
        resolve(stdout)
      }
    })
  })
}

function getIconPath(): string {
  const icon = 'icon.png'
  return process.env.VITE_DEV_SERVER_URL
    ? path.join(__dirname, '../resources', icon)
    : path.join((process as any).resourcesPath, icon)
}

/**
 * Ensure a minimal `launcher_profiles.json` exists in a given game directory.
 */
function ensureLauncherProfiles(gameDir: string): { created: boolean } {
  const profilesPath = path.join(gameDir, 'launcher_profiles.json')
  const alreadyExists = fs.existsSync(profilesPath)
  if (!alreadyExists) {
    const dummy = {
      profiles: {
        Nova: {
          name: 'Nova',
          type: 'custom',
          created: new Date().toISOString(),
          lastUsed: new Date().toISOString(),
          lastVersionId: '',
          gameDir,
        },
      },
      selectedProfile: 'Nova',
      clientToken: crypto.randomUUID(),
    }
    fs.writeFileSync(profilesPath, JSON.stringify(dummy, null, 2))
  }
  return { created: !alreadyExists }
}

let mainWindow: BrowserWindow | null = null
let consoleWindow: BrowserWindow | null = null
let proxyInstance: any = null
let proxyRunning = false

function createConsoleWindow(): void {
  if (consoleWindow) {
    consoleWindow.focus()
    return
  }

  consoleWindow = new BrowserWindow({
    width: 900,
    height: 600,
    title: 'Nova Client — Console',
    frame: false,
    transparent: true,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false,
      sandbox: false,
    },
    autoHideMenuBar: true,
    backgroundColor: '#00000000',
    resizable: true,
    minimizable: true,
  })

  if (process.env.VITE_DEV_SERVER_URL) {
    consoleWindow.loadURL(process.env.VITE_DEV_SERVER_URL + '#/console-window')
  } else {
    consoleWindow.loadFile(path.join(__dirname, '../dist/index.html'), { hash: '/console-window' })
  }

  consoleWindow.on('closed', () => {
    consoleWindow = null
  })
}

function createWindow(): void {
  mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    minWidth: 1000,
    minHeight: 700,
    frame: false,
    transparent: true,
    backgroundColor: '#0a0a1a',
    icon: getIconPath(),
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false,
      sandbox: false,
    },
  })

  if (!process.env.VITE_DEV_SERVER_URL) {
    // Open DevTools in production to debug black screen
    mainWindow.webContents.openDevTools()
  }

  if (process.env.VITE_DEV_SERVER_URL) {
    mainWindow.loadURL(process.env.VITE_DEV_SERVER_URL)
  } else {
    mainWindow.loadFile(path.join(__dirname, '../dist/index.html'))
  }
}

app.whenReady().then(createWindow)

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') app.quit()
})

app.on('activate', () => {
  if (BrowserWindow.getAllWindows().length === 0) createWindow()
})

// ── Window IPC ──────────────────────────────────────────────
// Use the focused window as target so both main and console windows work.

ipcMain.handle('window:minimize', (event) => {
  const win = BrowserWindow.fromWebContents(event.sender)
  win?.minimize()
})

ipcMain.handle('window:maximize', (event) => {
  const win = BrowserWindow.fromWebContents(event.sender)
  if (!win) return
  if (win.isMaximized()) {
    win.unmaximize()
  } else {
    win.maximize()
  }
})

ipcMain.handle('window:close', (event) => {
  const win = BrowserWindow.fromWebContents(event.sender)
  win?.close()
})

ipcMain.handle('window:isMaximized', (event) => {
  const win = BrowserWindow.fromWebContents(event.sender)
  return win?.isMaximized() ?? false
})

ipcMain.handle('window:setSize', (event, w: number, h: number) => {
  const win = BrowserWindow.fromWebContents(event.sender)
  win?.setSize(w, h)
  win?.center()
})

// ── Shell IPC ───────────────────────────────────────────────

ipcMain.handle('shell:openExternal', (_: any, url: string) => shell.openExternal(url))

ipcMain.handle('shell:openNovaFolder', () => {
  const novaDir = path.join(app.getPath('appData'), '.nova')
  if (!fs.existsSync(novaDir)) fs.mkdirSync(novaDir, { recursive: true })
  return shell.openPath(novaDir)
})

// ── Microsoft OAuth ─────────────────────────────────────────

ipcMain.handle('microsoft:login', async () => {
  const REDIRECT_URI = 'https://login.live.com/oauth20_desktop.srf'
  const CLIENT_ID = '00000000402b5328'

  const codeVerifier = crypto.randomUUID() + crypto.randomUUID()
  const codeChallenge = crypto
    .createHash('sha256')
    .update(codeVerifier)
    .digest('base64')
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '')

  const authUrl =
    `https://login.live.com/oauth20_authorize.srf?client_id=${CLIENT_ID}` +
    `&response_type=code&redirect_uri=${encodeURIComponent(REDIRECT_URI)}` +
    `&scope=XboxLive.signin%20offline_access&code_challenge=${codeChallenge}` +
    `&code_challenge_method=S256&prompt=select_account`

  const code = await new Promise<string>((resolve, reject) => {
    const authWindow = new BrowserWindow({
      width: 600,
      height: 700,
      title: 'Sign in to Microsoft',
      webPreferences: { nodeIntegration: false, contextIsolation: true },
    })
    authWindow.loadURL(authUrl)
    authWindow.on('closed', () => reject(new Error('Auth window closed')))
    authWindow.webContents.on('will-redirect', (_: any, url: string) => {
      if (url.startsWith(REDIRECT_URI)) {
        const c = new URL(url).searchParams.get('code')
        if (c) {
          resolve(c)
          authWindow.close()
        }
      }
    })
  })

  const tokenRes = await (
    await fetch('https://login.live.com/oauth20_token.srf', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        client_id: CLIENT_ID,
        code_verifier: codeVerifier,
        code,
        redirect_uri: REDIRECT_URI,
        grant_type: 'authorization_code',
      }),
    })
  ).json()

  if (!tokenRes.access_token) throw new Error('Failed to get access token')

  const xboxRes = await (
    await fetch('https://user.auth.xboxlive.com/user/authenticate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
      body: JSON.stringify({
        Properties: {
          AuthMethod: 'RPS',
          SiteName: 'user.auth.xboxlive.com',
          RpsTicket: `d=${tokenRes.access_token}`,
        },
        RelyingParty: 'http://auth.xboxlive.com',
        TokenType: 'JWT',
      }),
    })
  ).json()

  const xstsRes = await (
    await fetch('https://xsts.auth.xboxlive.com/xsts/authorize', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
      body: JSON.stringify({
        Properties: { SandboxId: 'RETAIL', UserTokens: [xboxRes.Token] },
        RelyingParty: 'rp://api.minecraftservices.com/',
        TokenType: 'JWT',
      }),
    })
  ).json()

  const mcAuthRes = await (
    await fetch('https://api.minecraftservices.com/authentication/login_with_xbox', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        identityToken: `XBL3.0 x=${xboxRes.DisplayClaims.xui[0].uhs};${xstsRes.Token}`,
      }),
    })
  ).json()

  const profileRes = await (
    await fetch('https://api.minecraftservices.com/minecraft/profile', {
      headers: { Authorization: `Bearer ${mcAuthRes.access_token}` },
    })
  ).json()

  return {
    id: profileRes.id,
    username: profileRes.name,
    uuid: profileRes.id,
    accessToken: mcAuthRes.access_token,
    refreshToken: tokenRes.refresh_token,
    avatarUrl: `https://crafthead.net/helm/${profileRes.id}/128`,
  }
})

// ── Microsoft Refresh ───────────────────────────────────────

ipcMain.handle('microsoft:refresh', async (_: any, refreshToken: string) => {
  const CLIENT_ID = '00000000402b5328'
  const res = await (
    await fetch('https://login.live.com/oauth20_token.srf', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        client_id: CLIENT_ID,
        refresh_token: refreshToken,
        grant_type: 'refresh_token',
        redirect_uri: 'https://login.live.com/oauth20_desktop.srf',
      }),
    })
  ).json()
  return { accessToken: res.access_token, refreshToken: res.refresh_token }
})

// ── Offline UUID ────────────────────────────────────────────

ipcMain.handle('offline:uuid', (_: any, username: string) => {
  const hash = crypto.createHash('md5').update(`OfflinePlayer:${username}`).digest('hex')
  return `${hash.slice(0, 8)}-${hash.slice(8, 12)}-${hash.slice(12, 16)}-${hash.slice(16, 20)}-${hash.slice(20, 32)}`
})

// ── Cosmetics ───────────────────────────────────────────────

ipcMain.handle('cosmetics:list', async () => {
  const lunarBase = path.join(app.getPath('home'), '.lunarclient', 'jit', 'assets', 'lunar-jit', 'cosmetics')
  const scanDir = (dir: string, outPath: string): { name: string; file: string }[] => {
    if (!fs.existsSync(dir)) return []
    return fs.readdirSync(dir)
      .filter(f => f.endsWith('.webp') && !f.toLowerCase().includes('lunar') && !f.endsWith('.mcmeta'))
      .map(f => ({ name: f.replace('.webp', ''), file: `file://${path.join(dir, f).replace(/\\/g, '/')}` }))
  }
  return {
    capes: scanDir(path.join(lunarBase, 'cloaks'), 'cloaks'),
    wings: scanDir(path.join(lunarBase, 'wings'), 'wings'),
    bandannas: scanDir(path.join(lunarBase, 'models', 'hats', 'bandanna', 'textures'), 'bandannas'),
  }
})

// ── Custom Capes IPC ───────────────────────────────────────

const customCapesPath = path.join(app.getPath('appData'), '.nova', 'custom_capes.json')

function loadCustomCapes(): Array<{ id: string; name: string; filePath: string }> {
  try {
    if (fs.existsSync(customCapesPath)) {
      return JSON.parse(fs.readFileSync(customCapesPath, 'utf-8')).capes || []
    }
  } catch {}
  return []
}

function saveCustomCapes(capes: Array<{ id: string; name: string; filePath: string }>) {
  const dir = path.dirname(customCapesPath)
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true })
  fs.writeFileSync(customCapesPath, JSON.stringify({ capes }, null, 2))
}

ipcMain.handle('customCapes:pickFile', async () => {
  const result = await dialog.showOpenDialog(mainWindow!, {
    title: 'Select Cape Image',
    filters: [
      { name: 'Images', extensions: ['png', 'jpg', 'jpeg', 'webp'] },
    ],
    properties: ['openFile'],
  })
  return { canceled: result.canceled, filePath: result.filePaths[0] }
})

ipcMain.handle('customCapes:upload', async (_: any, opts: { name: string; filePath: string }) => {
  const capesDir = path.join(app.getPath('appData'), '.nova', 'custom_capes')
  if (!fs.existsSync(capesDir)) fs.mkdirSync(capesDir, { recursive: true })

  const id = 'custom_' + Date.now()
  const ext = path.extname(opts.filePath)
  const destPath = path.join(capesDir, id + ext)

  try {
    fs.copyFileSync(opts.filePath, destPath)
    const capes = loadCustomCapes()
    capes.push({ id, name: opts.name, filePath: destPath })
    saveCustomCapes(capes)
    return { success: true, capeId: id }
  } catch (err) {
    console.error('Failed to upload cape:', err)
    return { success: false }
  }
})

ipcMain.handle('customCapes:list', () => {
  return loadCustomCapes()
})

ipcMain.handle('customCapes:remove', async (_: any, opts: { capeId: string }) => {
  const capes = loadCustomCapes()
  const cape = capes.find((c) => c.id === opts.capeId)
  if (cape && fs.existsSync(cape.filePath)) {
    try { fs.unlinkSync(cape.filePath) } catch {}
  }
  const filtered = capes.filter((c) => c.id !== opts.capeId)
  saveCustomCapes(filtered)
  return { success: true }
})

// ── Nametag Logo IPC ─────────────────────────────────────

ipcMain.handle('nametagLogo:pickFile', async () => {
  const result = await dialog.showOpenDialog(mainWindow!, {
    title: 'Select Nametag Logo Image',
    filters: [
      { name: 'Images', extensions: ['png', 'jpg', 'jpeg', 'webp'] },
    ],
    properties: ['openFile'],
  })
  return { canceled: result.canceled, filePath: result.filePaths[0] }
})

ipcMain.handle('nametagLogo:save', async (_: any, opts: { filePath: string }) => {
  const logoDir = path.join(app.getPath('appData'), '.nova', 'nametag_logo')
  if (!fs.existsSync(logoDir)) fs.mkdirSync(logoDir, { recursive: true })

  const destPath = path.join(logoDir, 'logo.png')
  try {
    fs.copyFileSync(opts.filePath, destPath)
    return { success: true, savedPath: destPath }
  } catch (err) {
    console.error('Failed to save nametag logo:', err)
    return { success: false }
  }
})

ipcMain.handle('nametagLogo:get', () => {
  const logoPath = path.join(app.getPath('appData'), '.nova', 'nametag_logo', 'logo.png')
  if (fs.existsSync(logoPath)) {
    return { exists: true, path: logoPath }
  }
  return { exists: false }
})

ipcMain.handle('nametagLogo:clear', () => {
  const logoPath = path.join(app.getPath('appData'), '.nova', 'nametag_logo', 'logo.png')
  try {
    if (fs.existsSync(logoPath)) fs.unlinkSync(logoPath)
    return { success: true }
  } catch {
    return { success: false }
  }
})

// ── Minecraft Launch ────────────────────────────────────────

ipcMain.handle('minecraft:launch', async (_: any, opts: any) => {
  createConsoleWindow()

  const gameDir = opts.customDir || path.join(app.getPath('appData'), '.nova')
  const versionDir = path.join(gameDir, 'versions', opts.version)
  const versionJson = path.join(versionDir, `${opts.version}.json`)
  const versionJar = path.join(versionDir, `${opts.version}.jar`)

  // ── Custom launch path (local JSON + JAR) ──────────────

  if (fs.existsSync(versionJson) && fs.existsSync(versionJar)) {
    const { spawn } = await import('child_process')
    let versionData: any = JSON.parse(fs.readFileSync(versionJson, 'utf-8'))
    const librariesDir = path.join(gameDir, 'libraries')
    const nativesDir = path.join(gameDir, 'natives', opts.version)
    const assetsDir = path.join(gameDir, 'assets')

    // inheritsFrom resolution (Fabric / Forge / OptiFine)
    if (versionData.inheritsFrom) {
      const parentVersion = versionData.inheritsFrom
      const parentDir = path.join(gameDir, 'versions', parentVersion)
      let parentJson = path.join(parentDir, `${parentVersion}.json`)
      let parentJar = path.join(parentDir, `${parentVersion}.jar`)

      // Download base version from Mojang if missing
      if (!fs.existsSync(parentJson) || !fs.existsSync(parentJar) || fs.statSync(parentJar).size === 0) {
        try {
          mainWindow?.webContents.send('minecraft:progress', `Downloading base version ${parentVersion}...`)
          if (!fs.existsSync(parentDir)) fs.mkdirSync(parentDir, { recursive: true })
          const manifest: any = await (await fetch('https://launchermeta.mojang.com/mc/game/version_manifest_v2.json')).json()
          const entry = (manifest.versions || []).find((v: any) => v.id === parentVersion)
          if (entry) {
            const vData: any = await (await fetch(entry.url)).json()
            fs.writeFileSync(parentJson, JSON.stringify(vData, null, 2))
            if (vData.downloads?.client?.url) {
              const jarResp = await fetch(vData.downloads.client.url)
              if (jarResp.ok) fs.writeFileSync(parentJar, Buffer.from(await jarResp.arrayBuffer()))
            }
          }
        } catch (e: any) {
          console.error(`Failed to download base version ${parentVersion}:`, e.message)
        }
      }

      if (fs.existsSync(parentJson)) {
        const parentData = JSON.parse(fs.readFileSync(parentJson, 'utf-8'))
        const parentLibs = parentData.libraries || []
        const childLibs = versionData.libraries || []
        versionData = { ...parentData, ...versionData, libraries: [...parentLibs, ...childLibs] }
        if (fs.existsSync(parentJar) && fs.statSync(versionJar).size === 0) {
          fs.copyFileSync(parentJar, versionJar)
        }
      }
    }

    // Ensure natives directory
    if (!fs.existsSync(nativesDir)) fs.mkdirSync(nativesDir, { recursive: true })

    // Native library extraction
    if (versionData.libraries) {
      for (const lib of versionData.libraries) {
        if (lib.rules) {
          let allowed = false
          for (const rule of lib.rules) {
            if (!rule.os || rule.os.name === 'win32' || rule.os.name === 'windows') {
              allowed = rule.action === 'allow'
            }
          }
          if (!allowed) continue
        }

        if (lib.natives?.windows && lib.downloads?.classifiers) {
          const classifier = lib.downloads.classifiers['natives-windows']
          if (classifier) {
            const libPath = path.join(librariesDir, classifier.path)
            if (!fs.existsSync(libPath)) {
              try {
                const dir = path.dirname(libPath)
                if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true })
                const resp = await fetch(classifier.url)
                if (resp.ok) {
                  const buf = Buffer.from(await resp.arrayBuffer())
                  fs.writeFileSync(libPath, buf)
                }
              } catch {
                console.warn(`Failed to download native library: ${lib.name}`)
              }
            }

            if (fs.existsSync(libPath)) {
              try {
                const AdmZip = require('adm-zip')
                const zip = new AdmZip(libPath)
                const entries = zip.getEntries()
                for (const entry of entries) {
                  if (!entry.isDirectory && !entry.entryName.startsWith('META-INF')) {
                    const outPath = path.join(nativesDir, entry.entryName)
                    const outDir = path.dirname(outPath)
                    if (!fs.existsSync(outDir)) fs.mkdirSync(outDir, { recursive: true })
                    fs.writeFileSync(outPath, entry.getData())
                  }
                }
              } catch {
                console.warn(`Failed to extract native library: ${lib.name} (adm-zip not available)`)
              }
            }
          }
        }
      }
    }

    // Java path resolution / Mojang runtime download
    let javaPath: string = opts.javaPath || 'java'
    const javaVersion = versionData.javaVersion
    if (javaVersion?.component) {
      const javaDir = path.join(gameDir, 'java', javaVersion.component)
      const javaExe = path.join(javaDir, 'bin', 'java.exe')
      if (fs.existsSync(javaExe)) {
        javaPath = javaExe
      } else {
        mainWindow?.webContents.send(
          'minecraft:progress',
          `Downloading Java ${javaVersion.majorVersion} (this may take a few minutes)...`,
        )
        try {
          await downloadJavaRuntime(javaVersion.component, javaDir, (msg: string) => {
            mainWindow?.webContents.send('minecraft:progress', msg)
          })
          javaPath = javaExe
        } catch (err: any) {
          throw new Error(
            `Java ${javaVersion.majorVersion} is required but could not be downloaded: ${err.message}. ` +
              `Please install Java ${javaVersion.majorVersion} manually.`,
          )
        }
      }
    }

    // Library download + classpath assembly
    const classpath: string[] = []
    if (versionData.libraries) {
      for (const lib of versionData.libraries) {
        if (lib.rules) {
          let allowed = false
          for (const rule of lib.rules) {
            if (!rule.os || rule.os.name === 'win32' || rule.os.name === 'windows') {
              allowed = rule.action === 'allow'
            }
          }
          if (!allowed) continue
        }

        if (lib.downloads?.artifact) {
          const libPath = path.join(librariesDir, lib.downloads.artifact.path)
          if (!fs.existsSync(libPath)) {
            try {
              const dir = path.dirname(libPath)
              if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true })
              const resp = await fetch(lib.downloads.artifact.url)
              if (resp.ok) {
                const buf = Buffer.from(await resp.arrayBuffer())
                fs.writeFileSync(libPath, buf)
              }
            } catch {
              console.warn(`Failed to download library: ${lib.name}`)
            }
          }
          if (fs.existsSync(libPath)) classpath.push(libPath)
        } else if (lib.name) {
          const parts = lib.name.split(':')
          if (parts.length >= 3) {
            const [group, artifact, version] = parts
            const groupPath = group.replace(/\./g, '/')
            const jarName = `${artifact}-${version}.jar`
            const localPath = path.join(librariesDir, groupPath, artifact, version, jarName)
            if (!fs.existsSync(localPath)) {
              const repo = lib.url || 'https://repo1.maven.org/maven2/'
              const downloadUrl = repo.endsWith('/')
                ? repo + `${groupPath}/${artifact}/${version}/${jarName}`
                : `${repo}/${groupPath}/${artifact}/${version}/${jarName}`
              try {
                const dir = path.dirname(localPath)
                if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true })
                const resp = await fetch(downloadUrl)
                if (resp.ok) {
                  const buf = Buffer.from(await resp.arrayBuffer())
                  fs.writeFileSync(localPath, buf)
                }
              } catch {
                console.warn(`Failed to download library: ${lib.name}`)
              }
            }
            if (fs.existsSync(localPath)) classpath.push(localPath)
          }
        }
      }
    }

    classpath.push(versionJar)
    const classpathStr = classpath.join(';')

    // Asset index download
    const assetIndexId = versionData.assetIndex?.id || versionData.assets || 'objects'
    const indexesDir = path.join(assetsDir, 'indexes')
    if (!fs.existsSync(indexesDir)) fs.mkdirSync(indexesDir, { recursive: true })
    const indexPath = path.join(indexesDir, `${assetIndexId}.json`)
    if (!fs.existsSync(indexPath) && versionData.assetIndex?.url) {
      try {
        mainWindow?.webContents.send('minecraft:progress', 'Downloading asset index...')
        const resp = await fetch(versionData.assetIndex.url)
        if (resp.ok) {
          const buf = Buffer.from(await resp.arrayBuffer())
          fs.writeFileSync(indexPath, buf)
        } else {
          console.error(`Asset index download failed: HTTP ${resp.status}`)
        }
      } catch (e: any) {
        console.error('Asset index download error:', e.message || e)
      }
    }

    // NovaClientMod installation — copy to mods/ folder (Fabric scans mods/ automatically)
    if (opts.enablePatcher) {
      installNovaClientMod(gameDir, opts.version)
    }

    // JVM arguments
    const jvmArgs = [
      `-Xms${opts.ram.min}G`,
      `-Xmx${opts.ram.max}G`,
      `-Djava.library.path=${nativesDir}`,
      '-Dminecraft.launcher.brand=nova-client',
      '-Dminecraft.launcher.version=1.0.0',
    ]

    jvmArgs.push(
      '-cp',
      classpathStr,
      versionData.mainClass || 'net.minecraft.client.main.Main',
      '--username',
      opts.username,
      '--version',
      opts.version,
      '--gameDir',
      gameDir,
      '--assetsDir',
      assetsDir,
      '--assetIndex',
      assetIndexId,
      '--uuid',
      opts.uuid,
      '--accessToken',
      opts.accessToken || '0',
      '--userType',
      'msa',
    )

    if (opts.serverIp) {
      const [host, port] = opts.serverIp.split(':')
      jvmArgs.push('--server', host, '--port', port || '25565')
    }

    mainWindow?.webContents.send('minecraft:progress', `Launching ${opts.version}...`)

    return new Promise<number>((resolve, reject) => {
      let stderr = ''
      const child = spawn(javaPath, jvmArgs, {
        cwd: gameDir,
        detached: true,
        stdio: 'pipe',
      })
      child.stdout?.on('data', (data: Buffer) => {
        const str = data.toString()
        mainWindow?.webContents.send('minecraft:data', str)
        consoleWindow?.webContents.send('minecraft:data', str)
      })
      child.stderr?.on('data', (data: Buffer) => {
        const str = data.toString()
        stderr += str
        mainWindow?.webContents.send('minecraft:debug', str)
        consoleWindow?.webContents.send('minecraft:debug', str)
      })
      child.on('close', (code: number | null) => {
        if (code !== 0 && stderr) {
          reject(new Error(`Game exited with code ${code}:\n${stderr.slice(-2000)}`))
        } else {
          resolve(code as number)
        }
      })
      child.on('error', (err: Error) => reject(err.message as any))
      child.unref()
    })
  }

  // ── Standard launch path (minecraft-launcher-core) ─────

  const client = new Client()
  const launchOptions: any = {
    clientPackage: null,
    authorization: {
      access_token: opts.accessToken,
      client_token: crypto.randomUUID(),
      uuid: opts.uuid,
      name: opts.username,
      user_properties: '{}',
    },
    root: gameDir,
    version: { number: opts.version, type: 'release' },
    memory: { min: `${opts.ram.min}G`, max: `${opts.ram.max}G` },
    ...(opts.javaPath ? { javaPath: opts.javaPath } : {}),
    overrides: {
      detached: false,
      hide: false,
      gameDirectory: gameDir,
    },
    extraJVMArgs: [],
  }

  // NovaClientMod for standard path — copy to mods/, no extra JVM flags needed for Fabric
  if (opts.enablePatcher) {
    installNovaClientMod(gameDir, opts.version)
  }

  if (opts.serverIp) {
    launchOptions.server = {
      host: opts.serverIp.split(':')[0],
      port: parseInt(opts.serverIp.split(':')[1] || '25565'),
    }
  }

  return new Promise<number>((resolve, reject) => {
    client.on('debug', (msg: string) => {
      mainWindow?.webContents.send('minecraft:debug', msg)
      consoleWindow?.webContents.send('minecraft:debug', msg)
    })
    client.on('data', (msg: string) => {
      mainWindow?.webContents.send('minecraft:data', msg)
      consoleWindow?.webContents.send('minecraft:data', msg)
    })
    client.on('progress', (msg: string) => mainWindow?.webContents.send('minecraft:progress', msg))
    client.launch(launchOptions)
    client.on('close', (code: number) => resolve(code))
    client.on('error', (msg: string) => reject(msg))
  })
})

// ── NovaClientMod helper ────────────────────────────────────

function installNovaClientMod(gameDir: string, version: string): { classpath: string[]; adapterClass: string; isFabric: boolean; isForge: boolean } | null {
  const novaClientModDir = process.env.VITE_DEV_SERVER_URL
    ? path.join(__dirname, '../resources/novaclientmod')
    : path.join((process as any).resourcesPath, 'novaclientmod')

  // Extract actual MC version from version strings
  // Handles: "26.1.2", "1.20.1-forge-47.1.79", "fabric-loader-0.19.3-26.1.2"
  let mcVersion = version.split('-')[0]
  if (version.includes('fabric-loader') || version.includes('forge')) {
    // For "fabric-loader-0.19.3-26.1.2", extract the last segment after the loader prefix
    const parts = version.split('-')
    mcVersion = parts[parts.length - 1]
  }
  const baseVersion = mcVersion

  const adapterMap: Record<string, { jar: string; className: string; isFabric: boolean; isForge: boolean }> = {}

  // Forge adapters (placed in mods/ folder)
  if (baseVersion === '1.7.10') adapterMap[version] = { jar: 'adapter-1_7_10.jar', className: 'com.novaclient.adapter.Adapter7_10', isFabric: false, isForge: true }
  if (baseVersion === '1.12.2') adapterMap[version] = { jar: 'adapter-1_12_2.jar', className: 'com.novaclient.adapter.Adapter12_2', isFabric: false, isForge: true }
  if (baseVersion === '1.16.5') adapterMap[version] = { jar: 'adapter-1_16_5.jar', className: 'com.novaclient.adapter.Adapter16_5', isFabric: false, isForge: true }

  // Fabric adapters (placed in mods/ folder)
  if (baseVersion.startsWith('1.20'))
    adapterMap[version] = { jar: 'adapter-1_20_x.jar', className: 'com.novaclient.adapter.Adapter120x', isFabric: true, isForge: false }
  if (baseVersion.startsWith('1.21'))
    adapterMap[version] = { jar: 'adapter-1_21_x.jar', className: 'com.novaclient.adapter.Adapter121x', isFabric: true, isForge: false }
  // 26.x launches with Fabric loader
  if (baseVersion === '26.1' && !baseVersion.includes('26.1.2')) adapterMap[version] = { jar: 'adapter-26_1.jar', className: 'com.novaclient.adapter.Adapter26_1Fabric', isFabric: true, isForge: false }
  if (baseVersion === '26.1.2') adapterMap[version] = { jar: 'adapter-26_1_2.jar', className: 'com.novaclient.adapter.Adapter26_1_2Fabric', isFabric: true, isForge: false }

  const entry = adapterMap[version]
  if (!entry) return null

  const coreJar = path.join(novaClientModDir, 'core.jar')
  const adapterJar = path.join(novaClientModDir, entry.jar)
  if (!fs.existsSync(adapterJar)) return null

  const cleanDir = (dir: string) => {
    if (!fs.existsSync(dir)) return
    fs.readdirSync(dir).forEach(file => {
      if (file.includes('adapter-') || file.includes('core.jar')) {
        try { fs.unlinkSync(path.join(dir, file)) } catch (e) {}
      }
    })
  }

  // For both Fabric and Forge: always place into the standard mods/ folder.
  // Fabric Loader always scans mods/ — no special JVM flags needed.
  // Forge also expects JARs in mods/.
  cleanDir(path.join(gameDir, '.client')) // clean legacy dir
  cleanDir(path.join(gameDir, 'mods'))    // clean stale adapters

  const destDir = path.join(gameDir, 'mods')
  if (!fs.existsSync(destDir)) fs.mkdirSync(destDir, { recursive: true })

  const adapterDest = path.join(destDir, entry.jar)
  fs.copyFileSync(adapterJar, adapterDest)

  // For Forge/legacy adapters, core.jar must also be on the classpath.
  // Fabric adapters are fat-jars (core already embedded).
  const coreDest = path.join(destDir, 'core.jar')
  if (!entry.isFabric && fs.existsSync(coreJar)) {
    fs.copyFileSync(coreJar, coreDest)
  }

  return { classpath: entry.isFabric ? [adapterDest] : [adapterDest, coreDest], adapterClass: entry.className, isFabric: entry.isFabric, isForge: entry.isForge }
}

// ── Proxy IPC ───────────────────────────────────────────────

ipcMain.handle('proxy:start', async (_: any, config: any) => {
  if (proxyRunning) return true
  try {
    const proxyDir = process.env.VITE_DEV_SERVER_URL
      ? path.join(__dirname, '../proxy')
      : path.join((process as any).resourcesPath, 'proxy')
    const { SolarProxy } = await import(path.join(proxyDir, 'dist/proxy.js'))
    const proxy = new SolarProxy({
      host: config.host || '127.0.0.1',
      port: config.port || 25566,
      targetHost: config.targetHost || 'hypixel.net',
      targetPort: config.targetPort || 25565,
    })
    await proxy.start()
    proxyInstance = proxy
    proxyRunning = true
    return true
  } catch (err) {
    console.error('Proxy failed:', err)
    return false
  }
})

ipcMain.handle('proxy:stop', async () => {
  if (proxyInstance) {
    proxyInstance.stop()
    proxyInstance = null
  }
  proxyRunning = false
  return true
})

ipcMain.handle('proxy:getStatus', () => proxyRunning)

// ── Version List ────────────────────────────────────────────

ipcMain.handle('get:versions', async () => {
  try {
    const manifest = await (
      await fetch('https://launchermeta.mojang.com/mc/game/version_manifest_v2.json')
    ).json()
    const allVersions = manifest.versions || []

    const supported = [
      '1.7.10',
      '1.8.9',
      '1.12.2',
      '1.16.5',
      '1.18.2',
      '1.20',
      '1.20.1',
      '1.20.2',
      '1.20.3',
      '1.20.4',
      '1.20.5',
      '1.20.6',
      '1.21',
      '1.21.1',
      '1.21.2',
      '1.21.3',
      '1.21.4',
      '1.21.5',
      '1.21.6',
      '1.21.7',
      '1.21.8',
      '1.21.9',
      '1.21.10',
      '1.21.11',
      '26.1',
      '26.1.2',
    ]

    const filtered = allVersions
      .filter((v: any) => supported.includes(v.id))
      .map((v: any) => ({ id: v.id, type: v.type, url: v.url, releaseTime: v.releaseTime }))

    if (!filtered.some((v: any) => v.id === '26.1')) {
      filtered.unshift({
        id: '26.1',
        type: 'release',
        url: '',
        releaseTime: new Date().toISOString(),
      })
    }
    if (!filtered.some((v: any) => v.id === '26.1.2')) {
      filtered.unshift({
        id: '26.1.2',
        type: 'release',
        url: '',
        releaseTime: new Date().toISOString(),
      })
    }

    return filtered
  } catch {
    return []
  }
})

// ── Mods IPC ────────────────────────────────────────────────

ipcMain.handle('mods:getModsDir', () => {
  const novaDir = path.join(app.getPath('appData'), '.nova')
  return path.join(novaDir, 'mods')
})

ipcMain.handle('mods:download', async (_: any, opts: any) => {
  const novaDir = path.join(app.getPath('appData'), '.nova')
  const modsDir = path.join(novaDir, 'mods')
  if (!fs.existsSync(modsDir)) fs.mkdirSync(modsDir, { recursive: true })

  let versionId = opts.versionId

  if (opts.gameVersion || opts.loader) {
    const facets: string[][] = []
    if (opts.gameVersion) facets.push([`versions:${opts.gameVersion}`])
    if (opts.loader && opts.loader !== 'none') facets.push([`categories:${opts.loader}`])

    const url =
      `https://api.modrinth.com/v2/project/${opts.projectId}/version?` +
      facets.map((f) => `facets=${encodeURIComponent(JSON.stringify([f]))}`).join('&')

    try {
      const resp = await fetch(url)
      if (resp.ok) {
        const versions = await resp.json()
        if (versions.length > 0) versionId = versions[0].version_id
      }
    } catch {
      // ignore — fall through with original versionId
    }
  }

  const resp = await fetch(
    `https://api.modrinth.com/v2/project/${opts.projectId}/version/${versionId}`,
  )
  if (!resp.ok) throw new Error(`Failed to fetch mod version info: ${resp.status}`)
  const versionData = await resp.json()
  if (!versionData.files || versionData.files.length === 0)
    throw new Error('No files found for this mod version')

  const downloadUrl = versionData.files[0].url
  const fileName = opts.fileName || versionData.files[0].filename
  const filePath = path.join(modsDir, fileName)

  const fileResp = await fetch(downloadUrl)
  if (!fileResp.ok) throw new Error(`Failed to download mod: ${fileResp.status}`)
  const buf = Buffer.from(await fileResp.arrayBuffer())
  fs.writeFileSync(filePath, buf)

  return { success: true, path: filePath }
})

ipcMain.handle('mods:uninstall', async (_: any, opts: any) => {
  const novaDir = path.join(app.getPath('appData'), '.nova')
  const modsDir = path.join(novaDir, 'mods')
  const filePath = path.join(modsDir, opts.fileName)
  const disabledPath = filePath + '.disabled'
  if (fs.existsSync(filePath)) fs.unlinkSync(filePath)
  if (fs.existsSync(disabledPath)) fs.unlinkSync(disabledPath)
  return { success: true }
})

ipcMain.handle('mods:enable', async (_: any, opts: any) => {
  const novaDir = path.join(app.getPath('appData'), '.nova')
  const modsDir = path.join(novaDir, 'mods')
  const filePath = path.join(modsDir, opts.fileName)
  const disabledPath = filePath + '.disabled'
  if (fs.existsSync(disabledPath)) fs.renameSync(disabledPath, filePath)
  return { success: true }
})

ipcMain.handle('mods:disable', async (_: any, opts: any) => {
  const novaDir = path.join(app.getPath('appData'), '.nova')
  const modsDir = path.join(novaDir, 'mods')
  const filePath = path.join(modsDir, opts.fileName)
  const disabledPath = filePath + '.disabled'
  if (fs.existsSync(filePath) && !fs.existsSync(disabledPath)) fs.renameSync(filePath, disabledPath)
  return { success: true }
})

ipcMain.handle('mods:list', () => {
  const novaDir = path.join(app.getPath('appData'), '.nova')
  const modsDir = path.join(novaDir, 'mods')
  return fs.existsSync(modsDir) ? fs.readdirSync(modsDir).filter((f) => f.endsWith('.jar')) : []
})

// ── Loader Installers ───────────────────────────────────────

ipcMain.handle('loader:install-fabric', async (_: any, opts: any) => {
  const { version } = opts
  const novaDir = path.join(app.getPath('appData'), '.nova')
  const loadersDir = path.join(novaDir, 'loaders')
  if (!fs.existsSync(loadersDir)) fs.mkdirSync(loadersDir, { recursive: true })

  const installerUrl =
    'https://maven.fabricmc.net/net/fabricmc/fabric-installer/0.11.2/fabric-installer-0.11.2.jar'
  const installerPath = path.join(loadersDir, 'fabric-installer.jar')

  const resp = await fetch(installerUrl)
  if (!resp.ok) throw new Error(`Failed to download Fabric installer: ${resp.status}`)
  fs.writeFileSync(installerPath, Buffer.from(await resp.arrayBuffer()))

  const javaPath = findJava()
  if (!javaPath) throw new Error('Java not found. Please install Java to use Fabric.')

  try {
    await execAsync(javaPath, ['-jar', installerPath, 'client', '-dir', novaDir, '-mcversion', version], { timeout: 120_000 })
  } catch (err: any) {
    // Fabric installer may throw "launcher profile not found" even though it created the version.
    // Check if the version was actually created before throwing.
    const versionsDir = path.join(novaDir, 'versions')
    const found = fs.readdirSync(versionsDir).filter((v) => v.startsWith('fabric-loader') && v.includes(version))
    if (found.length === 0) {
      throw new Error(`Fabric installation failed: ${err.stderr || err.message}`)
    }
    // Version was created despite the error - continue
  } finally {
    try {
      fs.unlinkSync(installerPath)
    } catch {
      // ignore
    }
  }
  const versionsDir = path.join(novaDir, 'versions')
  return {
    success: true,
    version:
      fs
        .readdirSync(versionsDir)
        .filter((v) => {
          if (!v.startsWith('fabric-loader')) return false
          // Match exact MC version: "fabric-loader-X-Y.Z.W" must end with the version
          const match = v.match(/fabric-loader-[\d.]+-(.+)$/)
          return match && match[1] === version
        })
        .sort()
        .reverse()[0] || `fabric-loader-${version}`,
  }
})

function findForgeVersion(versionsDir: string, mcVersion: string): string | null {
  if (!fs.existsSync(versionsDir)) return null
  const entries = fs.readdirSync(versionsDir)
  const candidates = entries.filter((v) => {
    const lower = v.toLowerCase()
    const hasForge = lower.includes('forge')
    const hasMcVer = lower.includes(mcVersion)
    return hasForge && hasMcVer
  })
  if (candidates.length === 0) return null
  candidates.sort().reverse()
  return candidates[0]
}

function copyForgeToNova(novaDir: string, mcDir: string, versionDirName: string): boolean {
  const novaVersions = path.join(novaDir, 'versions')
  const mcVersions = path.join(mcDir, 'versions')
  const mcLibs = path.join(mcDir, 'libraries')
  const novaLibs = path.join(novaDir, 'libraries')

  const srcVersionDir = path.join(mcVersions, versionDirName)
  const dstVersionDir = path.join(novaVersions, versionDirName)

  if (!fs.existsSync(srcVersionDir)) return false

  if (!fs.existsSync(dstVersionDir)) fs.mkdirSync(dstVersionDir, { recursive: true })
  for (const f of fs.readdirSync(srcVersionDir)) {
    const src = path.join(srcVersionDir, f)
    const dst = path.join(dstVersionDir, f)
    if (!fs.existsSync(dst)) fs.copyFileSync(src, dst)
  }

  if (fs.existsSync(mcLibs)) {
    copyDirRecursive(mcLibs, novaLibs)
  }

  return true
}

function copyDirRecursive(src: string, dst: string): void {
  if (!fs.existsSync(src)) return
  if (!fs.existsSync(dst)) fs.mkdirSync(dst, { recursive: true })
  for (const entry of fs.readdirSync(src, { withFileTypes: true })) {
    const srcPath = path.join(src, entry.name)
    const dstPath = path.join(dst, entry.name)
    if (entry.isDirectory()) {
      copyDirRecursive(srcPath, dstPath)
    } else {
      if (!fs.existsSync(dstPath)) fs.copyFileSync(srcPath, dstPath)
    }
  }
}

function detectMinecraftDir(): string {
  const appData = app.getPath('appData')
  const candidates = [
    path.join(appData, '.minecraft'),
    path.join(appData, 'minecraft'),
    path.join(appData, '.minecraft-launcher'),
  ]
  for (const dir of candidates) {
    if (fs.existsSync(path.join(dir, 'versions'))) return dir
  }
  return path.join(appData, '.minecraft')
}

ipcMain.handle('loader:install-forge', async (_: any, opts: any) => {
  const { version } = opts
  const novaDir = path.join(app.getPath('appData'), '.nova')
  const loadersDir = path.join(novaDir, 'loaders')
  const versionsDir = path.join(novaDir, 'versions')
  if (!fs.existsSync(loadersDir)) fs.mkdirSync(loadersDir, { recursive: true })
  if (!fs.existsSync(versionsDir)) fs.mkdirSync(versionsDir, { recursive: true })

  // Check if already installed in .nova
  const existing = findForgeVersion(versionsDir, version)
  if (existing) {
    return { success: true, version: existing }
  }

  // Also check .minecraft directly
  const mcDir = detectMinecraftDir()
  const mcExisting = findForgeVersion(path.join(mcDir, 'versions'), version)
  if (mcExisting) {
    const copied = copyForgeToNova(novaDir, mcDir, mcExisting)
    if (copied) {
      return { success: true, version: mcExisting }
    }
  }

  // Resolve Forge version from promotions API
  let forgeVer = ''
  try {
    const promoResp = await fetch('https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json')
    if (promoResp.ok) {
      const promoData: any = await promoResp.json()
      const promos = promoData.promos || {}
      forgeVer = promos[`${version}-recommended`] || promos[`${version}-latest`] || ''
    }
  } catch {}

  if (!forgeVer) {
    throw new Error(
      `Forge installer not found for Minecraft ${version}. Forge may not support this version.`,
    )
  }

  // Download installer
  const installerFileName = `forge-${forgeVer}-installer.jar`
  const installerPath = path.join(loadersDir, installerFileName)
  const downloadUrl = `https://maven.minecraftforge.net/net/minecraftforge/forge/${forgeVer}/forge-${forgeVer}-installer.jar`

  const fileResp = await fetch(downloadUrl)
  if (!fileResp.ok) throw new Error(`Failed to download Forge installer: ${fileResp.status}`)
  fs.writeFileSync(installerPath, Buffer.from(await fileResp.arrayBuffer()))

  const javaPath = findJava()
  if (!javaPath) throw new Error('Java not found. Please install Java to use Forge.')

  // Write launcher_profiles.json with the correct version so Forge installer recognizes it
  const profilesPath = path.join(novaDir, 'launcher_profiles.json')
  const profiles = {
    profiles: {
      Nova: {
        name: 'Nova',
        type: 'custom',
        created: new Date().toISOString(),
        lastUsed: new Date().toISOString(),
        lastVersionId: version,
        gameDir: novaDir,
      },
    },
    selectedProfile: 'Nova',
    clientToken: crypto.randomUUID(),
  }
  fs.writeFileSync(profilesPath, JSON.stringify(profiles, null, 2))

  try {
    await execAsync(javaPath, ['-jar', installerPath, '--installClient'], { timeout: 180_000, cwd: novaDir })
  } catch (err: any) {
    // Installer may exit non-zero but still install. Check below.
  } finally {
    try { fs.unlinkSync(installerPath) } catch {}
  }

  // Detect installation — check .nova first, then .minecraft
  let detected = findForgeVersion(versionsDir, version)
  if (detected) {
    return { success: true, version: detected }
  }

  // Try .minecraft fallback with full library copy
  const mcDetected = findForgeVersion(path.join(mcDir, 'versions'), version)
  if (mcDetected) {
    const copied = copyForgeToNova(novaDir, mcDir, mcDetected)
    if (copied) {
      return { success: true, version: mcDetected }
    }
  }

  throw new Error(`Forge installation failed for Minecraft ${version}.`)
})

ipcMain.handle('loader:install-optifine', async (_: any, opts: any) => {
  const { version } = opts
  const novaDir = path.join(app.getPath('appData'), '.nova')

  const optifineMap: Record<string, string> = {
    '1.7.10': 'OptiFine_1.7.10_HD_U_E7.jar',
    '1.8.9': 'OptiFine_1.8.9_HD_U_I7.jar',
    '1.12.2': 'OptiFine_1.12.2_HD_U_G5.jar',
    '1.16.5': 'OptiFine_1.16.5_HD_U_G8.jar',
  }

  const fileName = optifineMap[version]
  if (!fileName)
    throw new Error(
      `OptiFine is not available for Minecraft ${version}. Supported: ${Object.keys(optifineMap).join(', ')}`,
    )

  const localSource = path.join('F:\\Descargas 2\\optifine', fileName)
  if (!fs.existsSync(localSource))
    throw new Error(`OptiFine JAR not found at: ${localSource}`)

  const versionName = `${version}-OptiFine`
  const versionsDir = path.join(novaDir, 'versions')
  const versionDir = path.join(versionsDir, versionName)
  const versionJar = path.join(versionDir, `${versionName}.jar`)

  if (fs.existsSync(versionJar) && fs.statSync(versionJar).size > 0) {
    return { success: true, version: versionName }
  }

  const javaPath = findJava()
  if (!javaPath) throw new Error('Java not found. Please install Java to use OptiFine.')

  const tempDir = path.join(novaDir, 'loaders')
  if (!fs.existsSync(tempDir)) fs.mkdirSync(tempDir, { recursive: true })
  const tempInstaller = path.join(tempDir, fileName)
  fs.copyFileSync(localSource, tempInstaller)

  const { spawn } = require('child_process')

  try {
    await new Promise<void>((resolve, reject) => {
      const child = spawn(javaPath, ['-jar', tempInstaller], {
        cwd: novaDir,
        stdio: 'ignore',
      })
      child.on('close', (code: number | null) => {
        if (code === 0) resolve()
        else reject(new Error(`OptiFine installer exited with code ${code}`))
      })
      child.on('error', (err: Error) => reject(err))
    })
  } finally {
    try { fs.unlinkSync(tempInstaller) } catch {}
  }

  if (fs.existsSync(versionJar) && fs.statSync(versionJar).size > 0) {
    return { success: true, version: versionName }
  }

  const mcDir = path.join(app.getPath('appData'), '.minecraft')
  const mcVersionsDir = path.join(mcDir, 'versions')
  if (fs.existsSync(mcVersionsDir)) {
    const mcFound = fs.readdirSync(mcVersionsDir).find((v) => v.startsWith(`${version}-OptiFine`))
    if (mcFound) {
      const srcDir = path.join(mcVersionsDir, mcFound)
      const dstDir = path.join(versionsDir, mcFound)
      if (!fs.existsSync(dstDir)) fs.mkdirSync(dstDir, { recursive: true })
      for (const f of fs.readdirSync(srcDir)) {
        const src = path.join(srcDir, f)
        const dst = path.join(dstDir, f)
        if (!fs.existsSync(dst)) fs.copyFileSync(src, dst)
      }
      const dstJar = path.join(dstDir, `${mcFound}.jar`)
      if (fs.existsSync(dstJar) && fs.statSync(dstJar).size > 0) {
        return { success: true, version: mcFound }
      }
    }
  }

  throw new Error(`OptiFine installation failed. Click "Install" in the installer window next time.`)
})

// ── Helpers ─────────────────────────────────────────────────

function findJava(): string | null {
  const { execSync } = require('child_process')
  try {
    execSync('java -version', { encoding: 'utf8', timeout: 5000, stdio: 'pipe' })
    return 'java'
  } catch {
    // java not found on PATH
  }
  return null
}

async function downloadJavaRuntime(
  component: string,
  dir: string,
  onProgress: (msg: string) => void,
): Promise<void> {
  onProgress(`Downloading Java runtime (${component})...`)

  const manifestResp = await fetch(
    'https://launchermeta.mojang.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/all.json',
  )
  if (!manifestResp.ok) throw new Error('Failed to fetch Java runtime manifest')

  const manifest = await manifestResp.json()
  const windowsData = manifest['windows-x64']
  if (!windowsData?.[component])
    throw new Error(`Java component ${component} not available for windows-x64`)

  const componentData = Array.isArray(windowsData[component])
    ? windowsData[component][0]
    : windowsData[component]
  const componentManifestUrl = componentData.manifest?.url
  if (!componentManifestUrl) throw new Error('Java runtime manifest URL not found')

  const compManifestResp = await fetch(componentManifestUrl)
  if (!compManifestResp.ok) throw new Error('Failed to fetch Java component manifest')

  const files: Record<string, any> = (await compManifestResp.json()).files || {}
  const entries = Object.entries(files)

  // Create directories first
  for (const [filePath, info] of entries) {
    if (info.type === 'directory') {
      const fullPath = path.join(dir, filePath)
      if (!fs.existsSync(fullPath)) fs.mkdirSync(fullPath, { recursive: true })
    }
  }

  // Filter to files that need downloading
  const toDownload = entries.filter(
    ([filePath, info]: [string, any]) =>
      info.type === 'file' &&
      info.downloads?.raw?.url &&
      !fs.existsSync(path.join(dir, filePath)),
  )

  const total = toDownload.length
  if (total === 0) {
    onProgress('Java runtime already downloaded')
    return
  }

  onProgress(`Downloading Java runtime... 0/${total} files`)

  const concurrency = 10
  let completed = 0
  let failed = 0

  for (let i = 0; i < toDownload.length; i += concurrency) {
    const batch = toDownload.slice(i, i + concurrency)
    await Promise.allSettled(
      batch.map(async ([filePath, info]: [string, any]) => {
        const fullPath = path.join(dir, filePath)
        try {
          const resp = await fetch(info.downloads.raw.url)
          if (resp.ok) {
            const buf = Buffer.from(await resp.arrayBuffer())
            fs.writeFileSync(fullPath, buf)
          } else {
            failed++
          }
        } catch {
          failed++
        }
        completed++
      }),
    )
    if (completed % 50 === 0 || completed === total) {
      onProgress(`Downloading Java runtime... ${completed}/${total} files`)
    }
  }

  if (failed > 0) console.warn(`Java download: ${failed}/${total} files failed`)

  const javaExe = path.join(dir, 'bin', 'java.exe')
  if (!fs.existsSync(javaExe))
    throw new Error('Java runtime download incomplete - java.exe not found')

  onProgress('Java runtime downloaded successfully')
}

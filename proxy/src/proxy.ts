import { createServer, createClient } from 'minecraft-protocol'
import { AutoBoopModule, BedwarsStatsModule } from './modules'

interface SolarProxyOptions {
  host: string
  port: number
  targetHost: string
  targetPort: number
  autoBoop?: {
    enabled: boolean
    filters: { pattern: string; enabled: boolean }[]
    cooldown: number
  }
  bedwarsStats?: {
    enabled: boolean
  }
}

export class SolarProxy {
  private server: any = null
  private clients: Map<string, { clientToServer: any; serverToClient: any }> = new Map()
  private options: SolarProxyOptions
  private autoBoop: AutoBoopModule
  private bedwarsStats: BedwarsStatsModule

  constructor(options: SolarProxyOptions) {
    this.options = options
    this.autoBoop = new AutoBoopModule(
      options.autoBoop?.filters || [],
      options.autoBoop?.cooldown || 3
    )
    this.autoBoop.setEnabled(options.autoBoop?.enabled || false)
    this.bedwarsStats = new BedwarsStatsModule()
    this.bedwarsStats.setEnabled(options.bedwarsStats?.enabled || false)
  }

  start(): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        this.server = createServer({
          'online-mode': false,
          host: this.options.host,
          port: this.options.port,
          version: false,
          motd: '§bOrbit §7Proxy §8- §fSolarTweaks Engine',
          maxPlayers: 10,
          beforePing: (response: any) => {
            response.description = {
              text: '§bOrbit Client\n§7SolarTweaks Proxy Active'
            }
          }
        })

        this.server.on('connection', (client: any) => {
          const username = client.username || 'unknown'
          const clientId = `${username}-${Date.now()}`

          const serverClient = createClient({
            host: this.options.targetHost,
            port: this.options.targetPort,
            username: client.username,
            version: client.version,
            auth: 'offline',
          })

          this.clients.set(clientId, { clientToServer: serverClient, serverToClient: client })

          serverClient.on('connect', () => {
            client.write('login', {
              entityId: 0,
              isHardcore: false,
              gameMode: 0,
              previousGameMode: 255,
              worldNames: ['minecraft:overworld'],
              dimensionCodec: Buffer.alloc(0),
              dimension: { text: 'minecraft:overworld' },
              worldName: 'minecraft:overworld',
              hashedSeed: [0, 0],
              maxPlayers: 100,
              viewDistance: 10,
              reducedDebugInfo: false,
              enableRespawnScreen: true,
              isDebug: false,
              isFlat: false,
            })
          })

          client.on('packet', (data: any, meta: any) => {
            try {
              if (serverClient && serverClient.write) {
                serverClient.write(meta.name, data)
              }
            } catch (e) {
              // ignore write errors
            }
          })

          serverClient.on('packet', (data: any, meta: any) => {
            try {
              if (client && client.write) {
                client.write(meta.name, data)
              }

              // Process chat messages for modules
              if (meta.name === 'chat' || meta.name === 'system_chat' || meta.name === 'player_chat') {
                const message = data?.message || data?.contents?.text || data?.text || ''

                // AutoBoop
                if (this.autoBoop.isEnabled() && this.autoBoop.shouldBoop(message)) {
                  client.write('chat', { message: this.autoBoop.getBoopCommand() })
                }

                // Bedwars Stats
                if (this.bedwarsStats.isEnabled()) {
                  this.bedwarsStats.processChatMessage(message)
                }
              }
            } catch (e) {
              // ignore write errors
            }
          })

          client.on('end', () => {
            if (serverClient) serverClient.end()
            this.clients.delete(clientId)
          })

          serverClient.on('end', () => {
            client.end()
            this.clients.delete(clientId)
          })

          client.on('error', () => {})
          serverClient.on('error', () => {})
        })

        this.server.on('listening', () => {
          resolve()
        })

        this.server.on('error', (e: Error) => {
          reject(e)
        })
      } catch (e) {
        reject(e)
      }
    })
  }

  stop(): void {
    if (this.server) {
      this.server.close()
      this.server = null
    }
    for (const [, clients] of this.clients) {
      try { clients.clientToServer.end() } catch {}
      try { clients.serverToClient.end() } catch {}
    }
    this.clients.clear()
  }

  getClientCount(): number {
    return this.clients.size
  }

  getAutoBoop(): AutoBoopModule {
    return this.autoBoop
  }

  getBedwarsStats(): BedwarsStatsModule {
    return this.bedwarsStats
  }
}

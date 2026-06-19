export interface BedwarsSessionStats {
  kills: number
  deaths: number
  wins: number
  losses: number
  bedsDestroyed: number
  finalKills: number
  finalDeaths: number
}

export class BedwarsStatsModule {
  private session: BedwarsSessionStats = {
    kills: 0,
    deaths: 0,
    wins: 0,
    losses: 0,
    bedsDestroyed: 0,
    finalKills: 0,
    finalDeaths: 0,
  }
  private enabled: boolean = false
  private currentMode: string = ''

  setEnabled(enabled: boolean) {
    this.enabled = enabled
  }

  isEnabled(): boolean {
    return this.enabled
  }

  resetSession() {
    this.session = {
      kills: 0,
      deaths: 0,
      wins: 0,
      losses: 0,
      bedsDestroyed: 0,
      finalKills: 0,
      finalDeaths: 0,
    }
  }

  getSession(): BedwarsSessionStats {
    return { ...this.session }
  }

  getKDR(): number {
    return this.session.deaths > 0 ? this.session.kills / this.session.deaths : this.session.kills
  }

  getFKDR(): number {
    return this.session.finalDeaths > 0 ? this.session.finalKills / this.session.finalDeaths : this.session.finalKills
  }

  getWLR(): number {
    return this.session.losses > 0 ? this.session.wins / this.session.losses : this.session.wins
  }

  setCurrentMode(mode: string) {
    this.currentMode = mode
  }

  getCurrentMode(): string {
    return this.currentMode
  }

  processChatMessage(message: string): boolean {
    if (!this.enabled) return false

    const killPatterns = [
      /\[(.+)\] (.+?) (?:killed|eliminated) (.+)/i,
      /(.+?) (?:killed|eliminated) (.+)/i,
    ]
    for (const pattern of killPatterns) {
      const match = message.match(pattern)
      if (match) {
        this.session.kills++
        return true
      }
    }

    const deathPatterns = [
      /\[(.+)\] (.+?) was (?:killed|eliminated) by (.+)/i,
      /(.+?) was (?:killed|eliminated) by (.+)/i,
      /you (?:were|got) (?:killed|eliminated)/i,
    ]
    for (const pattern of deathPatterns) {
      if (message.match(pattern)) {
        this.session.deaths++
        return true
      }
    }

    if (/blue team has won/i.test(message) || /red team has won/i.test(message) ||
        /green team has won/i.test(message) || /yellow team has won/i.test(message)) {
      this.session.wins++
      return true
    }

    if (/you (?:lost|have been eliminated)/i.test(message)) {
      this.session.losses++
      return true
    }

    if (/bed (?:destroyed|broken)/i.test(message)) {
      this.session.bedsDestroyed++
      return true
    }

    if (/final (?:kill|hit)/i.test(message)) {
      this.session.finalKills++
      return true
    }

    if (/final death/i.test(message)) {
      this.session.finalDeaths++
      return true
    }

    return false
  }
}

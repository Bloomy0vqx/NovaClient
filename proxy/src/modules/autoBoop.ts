export interface AutoBoopFilter {
  pattern: string
  enabled: boolean
}

export class AutoBoopModule {
  private filters: AutoBoopFilter[] = []
  private cooldown: number = 3
  private lastBoop: number = 0
  private enabled: boolean = false

  constructor(filters: AutoBoopFilter[] = [], cooldown: number = 3) {
    this.filters = filters
    this.cooldown = cooldown
  }

  setEnabled(enabled: boolean) {
    this.enabled = enabled
  }

  setFilters(filters: AutoBoopFilter[]) {
    this.filters = filters
  }

  setCooldown(seconds: number) {
    this.cooldown = seconds
  }

  shouldBoop(chatMessage: string): boolean {
    if (!this.enabled) return false

    const now = Date.now()
    if (now - this.lastBoop < this.cooldown * 1000) return false

    for (const filter of this.filters) {
      if (!filter.enabled) continue
      try {
        const regex = new RegExp(filter.pattern, 'i')
        if (regex.test(chatMessage)) {
          this.lastBoop = now
          return true
        }
      } catch {
        if (chatMessage.toLowerCase().includes(filter.pattern.toLowerCase())) {
          this.lastBoop = now
          return true
        }
      }
    }

    return false
  }

  getBoopCommand(): string {
    return '/boop'
  }

  getFilters(): AutoBoopFilter[] {
    return this.filters
  }

  addFilter(pattern: string) {
    this.filters.push({ pattern, enabled: true })
  }

  removeFilter(index: number) {
    this.filters.splice(index, 1)
  }

  toggleFilter(index: number) {
    if (this.filters[index]) {
      this.filters[index].enabled = !this.filters[index].enabled
    }
  }
}

import { create } from 'zustand'
import type { MinecraftAccount } from '../types'

interface AuthState {
  account: MinecraftAccount | null
  accounts: MinecraftAccount[]
  isLoggedIn: boolean
  setAccount: (account: MinecraftAccount) => void
  addAccount: (account: MinecraftAccount) => void
  removeAccount: (id: string) => void
  switchAccount: (id: string) => void
  logout: () => void
  loadAccounts: () => void
}

export const useAuthStore = create<AuthState>((set, get) => ({
  account: null,
  accounts: [],
  isLoggedIn: false,

  setAccount: (account) => {
    localStorage.setItem('orbit_active_account', JSON.stringify(account))
    set({ account, isLoggedIn: true })
    const existing = get().accounts.filter(a => a.id !== account.id)
    set({ accounts: [...existing, account] })
    localStorage.setItem('orbit_accounts', JSON.stringify([...existing, account]))
  },

  addAccount: (account) => {
    const existing = get().accounts.filter(a => a.id !== account.id)
    const accounts = [...existing, account]
    set({ accounts })
    localStorage.setItem('orbit_accounts', JSON.stringify(accounts))
  },

  removeAccount: (id) => {
    const accounts = get().accounts.filter(a => a.id !== id)
    set({ accounts })
    localStorage.setItem('orbit_accounts', JSON.stringify(accounts))
    if (get().account?.id === id) {
      const next = accounts[0] || null
      set({ account: next, isLoggedIn: !!next })
      if (next) localStorage.setItem('orbit_active_account', JSON.stringify(next))
      else localStorage.removeItem('orbit_active_account')
    }
  },

  switchAccount: (id) => {
    const account = get().accounts.find(a => a.id === id)
    if (account) {
      set({ account, isLoggedIn: true })
      localStorage.setItem('orbit_active_account', JSON.stringify(account))
    }
  },

  logout: () => {
    set({ account: null, isLoggedIn: false })
    localStorage.removeItem('orbit_active_account')
  },

  loadAccounts: () => {
    const stored = localStorage.getItem('orbit_accounts')
    const active = localStorage.getItem('orbit_active_account')
    if (stored) {
      const accounts: MinecraftAccount[] = JSON.parse(stored)
      set({ accounts })
    }
    if (active) {
      const account: MinecraftAccount = JSON.parse(active)
      set({ account, isLoggedIn: true })
    }
  },
}))

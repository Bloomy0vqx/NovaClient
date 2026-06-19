import { create } from 'zustand'
import { translations, type Language, type TranslationKey } from './translations'

interface LanguageState {
  language: Language
  setLanguage: (lang: Language) => void
  t: (key: TranslationKey, params?: Record<string, string | number>) => string
}

function getInitialLanguage(): Language {
  const stored = localStorage.getItem('orbit_language')
  if (stored === 'es' || stored === 'en') return stored
  return 'en'
}

export const useLanguage = create<LanguageState>((set, get) => ({
  language: getInitialLanguage(),

  setLanguage: (lang) => {
    localStorage.setItem('orbit_language', lang)
    set({ language: lang })
  },

  t: (key, params) => {
    const { language } = get()
    const dict = translations[language] || translations.en
    let text: string = (dict as any)[key] || (translations.en as any)[key] || key
    if (params) {
      for (const [k, v] of Object.entries(params)) {
        text = text.replace(new RegExp(`\\{${k}\\}`, 'g'), String(v))
      }
    }
    return text
  },
}))

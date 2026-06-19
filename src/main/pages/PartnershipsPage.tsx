export interface Partnership {
  id: string
  name: string
  description: string
  logo: string
  discord: string
  discount?: string
  featured?: boolean
}
 
export const partnerships: Partnership[] = [
  {
    id: 'wildspire-studios',
    name: 'WildSpire Studios',
    description: 'Our Owners developed a studio where they will provide everything for the client.',
    logo: '/src/main/assets/wildspirelogo.png',
    discord: 'https://discord.gg/4d3X6kn2QG',
    discount: '',
    featured: true
  }
]
 

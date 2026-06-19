export interface Partnership {
  id: string
  name: string
  description: string
  logo: string
  website: string
  discount?: string
  featured?: boolean
}

export const partnerships: Partnership[] = [
  {
    id: 'example-partnership',
    name: 'Example Partner',
    description: 'A great partnership that offers exclusive benefits to Nova Client users.',
    logo: '/path/to/logo.png',
    website: 'https://example.com',
    discount: '20% off',
    featured: true
  }
]

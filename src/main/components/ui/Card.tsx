import { ReactNode } from 'react'

interface CardProps {
  icon: string
  title: string
  subtitle?: string
  background?: string
  contentClass?: string
  className?: string
  children: ReactNode
}

export function Card({ icon, title, subtitle, background, contentClass = '', className = '', children }: CardProps) {
  return (
    <div className={`card ${className}`} style={{ width: '90%', height: 'auto' }}>
      <div
        className="card-header"
        style={{
          width: '100%',
          height: '100px',
          borderTopRightRadius: '16px',
          borderTopLeftRadius: '16px',
          backgroundRepeat: 'no-repeat',
          backgroundSize: 'cover',
          backgroundImage: background
            ? `url('${background}')`
            : 'linear-gradient(135deg, #151525, #252535)',
        }}
      >
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            paddingTop: '22px',
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <i className={`${icon} card-icon`} style={{ fontSize: '25px', marginRight: '10px' }}></i>
            <h1 style={{ fontWeight: 200, fontSize: '25px', color: 'var(--text-primary)' }}>{title}</h1>
          </div>
          {subtitle && (
            <h4
              style={{
                fontWeight: 100,
                fontSize: '15px',
                letterSpacing: '3px',
                marginTop: '2px',
                color: 'var(--text-primary)',
              }}
            >
              {subtitle}
            </h4>
          )}
        </div>
      </div>
      <div
        className={`card-content ${contentClass}`}
        style={{
          height: 'auto',
          backgroundColor: '#181830',
          borderBottomRightRadius: '16px',
          borderBottomLeftRadius: '16px',
          overflow: 'hidden',
          display: 'flex',
          padding: '0.5rem',
        }}
      >
        {children}
      </div>
    </div>
  )
}

interface CardItemProps {
  icon?: string
  title: string
  subtitle?: string
  contentClass?: string
  className?: string
  children?: ReactNode
}

export function CardItem({ icon, title, subtitle, contentClass = '', className = '', children }: CardItemProps) {
  return (
    <div
      className={`card-item ${className}`}
        style={{
          backgroundColor: '#12121F',
          borderRadius: '12px',
          padding: '15px',
          margin: '5px',
          marginTop: '15px',
          flex: '1 1 0px',
          border: '1px solid #252540',
        }}
    >
      <div
        style={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          textAlign: 'center',
        }}
      >
        <div style={{ display: 'flex' }}>
          {icon && (
            <i className={`${icon} card-item-icon`} style={{ fontSize: '20px', marginRight: '10px' }}></i>
          )}
          <h1 style={{ fontWeight: 200, fontSize: '20px', color: 'var(--text-primary)' }}>{title}</h1>
        </div>
        {subtitle && (
          <h4 style={{ fontWeight: 200, fontSize: '12px', marginTop: '5px', color: 'var(--text-primary)' }}>
            {subtitle}
          </h4>
        )}
      </div>
      <div className={`card-item-content ${contentClass}`} style={{ marginTop: '10px' }}>
        {children}
      </div>
    </div>
  )
}

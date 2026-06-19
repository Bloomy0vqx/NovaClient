import { useEffect, useState, useRef } from 'react'

interface LogEntry {
  text: string
  type: 'info' | 'warn' | 'error' | 'debug'
  timestamp: string
}

function classify(line: string): LogEntry['type'] {
  const l = line.toLowerCase()
  if (l.includes('[error]') || l.includes('exception') || l.includes('caused by') || l.includes('fatal')) return 'error'
  if (l.includes('[warn]') || l.includes('warning')) return 'warn'
  if (l.includes('[debug]') || l.includes('nova client') || l.includes('[nova]')) return 'debug'
  return 'info'
}

function logColor(type: LogEntry['type']) {
  switch (type) {
    case 'error': return '#ff6b6b'
    case 'warn':  return '#ffd93d'
    case 'debug': return '#a78bfa'
    default:      return '#c9d1d9'
  }
}

function logBg(type: LogEntry['type']) {
  switch (type) {
    case 'error': return 'rgba(255,107,107,0.05)'
    case 'warn':  return 'rgba(255,217,61,0.04)'
    case 'debug': return 'rgba(167,139,250,0.04)'
    default:      return 'transparent'
  }
}

export default function ConsolePage({ standalone }: { standalone?: boolean }) {
  const [logs, setLogs] = useState<LogEntry[]>([])
  const bottomRef = useRef<HTMLDivElement>(null)
  const [autoScroll, setAutoScroll] = useState(true)

  useEffect(() => {
    if (!window.nova?.minecraft) return
    const now = () => new Date().toLocaleTimeString()
    const unsubData = window.nova.minecraft.onData((data: string) => {
      const lines = data.split('\n').filter(Boolean)
      setLogs(prev => [...prev, ...lines.map(text => ({ text, type: classify(text), timestamp: now() }))])
    })
    const unsubDebug = window.nova.minecraft.onDebug((data: string) => {
      const lines = data.split('\n').filter(Boolean)
      setLogs(prev => [...prev, ...lines.map(text => ({ text, type: classify(text), timestamp: now() }))])
    })
    return () => { if (unsubData) unsubData(); if (unsubDebug) unsubDebug() }
  }, [])

  useEffect(() => {
    if (autoScroll && bottomRef.current) {
      bottomRef.current.scrollIntoView({ behavior: 'smooth' })
    }
  }, [logs, autoScroll])

  const errorCount = logs.filter(l => l.type === 'error').length
  const warnCount  = logs.filter(l => l.type === 'warn').length

  const copyLogs = () => {
    const logText = logs.map(log => `[${log.timestamp}] ${log.text}`).join('\n')
    navigator.clipboard.writeText(logText).then(() => {
      // Could add a toast notification here
      console.log('Logs copied to clipboard')
    })
  }

  if (!standalone) {
    // Embedded version (inside the main window)
    return (
      <div style={{ width: '100%', display: 'flex', flexDirection: 'column', gap: 0 }}>
        <div style={{ background: '#12121f', borderRadius: 10, overflow: 'hidden', border: '1px solid rgba(255,255,255,0.06)' }}>
          <div style={{ padding: '10px 16px', borderBottom: '1px solid rgba(255,255,255,0.06)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: '#1a1a28' }}>
            <span style={{ fontFamily: 'Inter, sans-serif', fontWeight: 600, fontSize: 22, color: '#fff' }}>Console</span>
            <button
              onClick={copyLogs}
              style={{
                padding: '6px 12px',
                background: '#8B5CF6',
                border: 'none',
                borderRadius: 6,
                color: '#fff',
                cursor: 'pointer',
                fontSize: 12,
                fontWeight: 500,
                transition: 'all 0.2s ease',
              }}
              onMouseEnter={(e) => e.currentTarget.style.background = '#7C3AED'}
              onMouseLeave={(e) => e.currentTarget.style.background = '#8B5CF6'}
            >
              <i className="fa-solid fa-copy" style={{ marginRight: 6 }} />
              Copy Logs
            </button>
          </div>
          <LogArea logs={logs} bottomRef={bottomRef} />
        </div>
      </div>
    )
  }

  return (
    <div style={{
      width: '100vw',
      height: '100vh',
      display: 'flex',
      flexDirection: 'column',
      background: 'rgba(18, 18, 30, 0.97)',
      backdropFilter: 'blur(24px)',
      WebkitBackdropFilter: 'blur(24px)',
      fontFamily: 'Inter, sans-serif',
      overflow: 'hidden',
      borderRadius: 12,
      boxSizing: 'border-box',
    }}>
      {/* Custom Titlebar — draggable */}
      <div style={{
        WebkitAppRegion: 'drag',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '0 16px',
        height: 44,
        background: 'rgba(26,26,40,0.95)',
        borderBottom: '1px solid rgba(255,255,255,0.07)',
        flexShrink: 0,
        borderRadius: '12px 12px 0 0',
        userSelect: 'none',
      } as any}>
        {/* Left — status dot + title */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div style={{
            width: 8, height: 8, borderRadius: '50%',
            background: logs.length > 0 ? '#a78bfa' : '#5a5a6e',
            boxShadow: logs.length > 0 ? '0 0 8px #a78bfa88' : 'none',
            transition: 'all 0.3s',
            flexShrink: 0,
          }} />
          <span style={{ fontWeight: 600, fontSize: 13, color: '#e2e8f0', letterSpacing: '0.01em' }}>
            Nova Client — Console
          </span>
          <span style={{ fontSize: 11, color: '#5a5a6e', marginLeft: 2 }}>
            {logs.length} lines
          </span>
          {errorCount > 0 && (
            <span style={{ fontSize: 10, background: 'rgba(255,107,107,0.15)', color: '#ff6b6b', border: '1px solid rgba(255,107,107,0.25)', borderRadius: 4, padding: '1px 6px' }}>
              {errorCount} errors
            </span>
          )}
          {warnCount > 0 && (
            <span style={{ fontSize: 10, background: 'rgba(255,217,61,0.1)', color: '#ffd93d', border: '1px solid rgba(255,217,61,0.2)', borderRadius: 4, padding: '1px 6px' }}>
              {warnCount} warnings
            </span>
          )}
        </div>

        {/* Right — buttons (no-drag zone) */}
        <div style={{ display: 'flex', gap: 6, WebkitAppRegion: 'no-drag' } as any}>
          <button
            onClick={copyLogs}
            style={{
              background: 'rgba(139,92,246,0.15)',
              border: '1px solid rgba(139,92,246,0.3)',
              borderRadius: 6,
              padding: '4px 10px',
              color: '#8B5CF6',
              cursor: 'pointer',
              fontSize: 11,
              transition: 'all 0.2s',
              fontFamily: 'inherit',
            }}
            onMouseEnter={e => e.currentTarget.style.background = 'rgba(139,92,246,0.25)'}
            onMouseLeave={e => e.currentTarget.style.background = 'rgba(139,92,246,0.15)'}
          >
            <i className="fa-solid fa-copy" style={{ marginRight: 4 }} />
            Copy
          </button>
          <button
            onClick={() => setAutoScroll(p => !p)}
            style={{
              background: autoScroll ? 'rgba(167,139,250,0.12)' : 'rgba(255,255,255,0.05)',
              border: `1px solid ${autoScroll ? 'rgba(167,139,250,0.35)' : 'rgba(255,255,255,0.1)'}`,
              borderRadius: 6,
              padding: '4px 10px',
              color: autoScroll ? '#a78bfa' : '#7a7a8e',
              cursor: 'pointer',
              fontSize: 11,
              transition: 'all 0.2s',
              fontFamily: 'inherit',
            }}
          >
            ↓ {autoScroll ? 'Auto-scroll ON' : 'Auto-scroll OFF'}
          </button>
          <button
            onClick={() => setLogs([])}
            style={{
              background: 'rgba(255,255,255,0.04)',
              border: '1px solid rgba(255,255,255,0.09)',
              borderRadius: 6,
              padding: '4px 10px',
              color: '#999',
              cursor: 'pointer',
              fontSize: 11,
              fontFamily: 'inherit',
            }}
            onMouseEnter={e => e.currentTarget.style.color = '#fff'}
            onMouseLeave={e => e.currentTarget.style.color = '#999'}
          >
            Clear
          </button>

          {/* Window Controls */}
          <div style={{ width: 1, background: 'rgba(255,255,255,0.08)', margin: '8px 2px' }} />

          <button
            onClick={() => window.nova?.window?.minimize?.()}
            title="Minimize"
            style={{
              width: 28, height: 28, borderRadius: 6,
              background: 'rgba(255,255,255,0.05)',
              border: '1px solid rgba(255,255,255,0.08)',
              color: '#999', cursor: 'pointer', fontSize: 14,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              transition: 'all 0.15s',
            }}
            onMouseEnter={e => { e.currentTarget.style.background = 'rgba(255,255,255,0.1)'; e.currentTarget.style.color = '#fff' }}
            onMouseLeave={e => { e.currentTarget.style.background = 'rgba(255,255,255,0.05)'; e.currentTarget.style.color = '#999' }}
          >
            −
          </button>
          <button
            onClick={() => window.nova?.window?.close?.()}
            title="Close"
            style={{
              width: 28, height: 28, borderRadius: 6,
              background: 'rgba(255,100,100,0.08)',
              border: '1px solid rgba(255,100,100,0.18)',
              color: '#ff6b6b', cursor: 'pointer', fontSize: 14,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              transition: 'all 0.15s',
            }}
            onMouseEnter={e => { e.currentTarget.style.background = 'rgba(255,100,100,0.2)' }}
            onMouseLeave={e => { e.currentTarget.style.background = 'rgba(255,100,100,0.08)' }}
          >
            ✕
          </button>
        </div>
      </div>

      {/* Log Area */}
      <LogArea logs={logs} bottomRef={bottomRef} />
    </div>
  )
}

function LogArea({ logs, bottomRef }: { logs: LogEntry[]; bottomRef: React.RefObject<HTMLDivElement> }) {
  return (
    <div style={{
      flex: 1,
      overflowY: 'auto',
      fontFamily: '"Cascadia Code", "JetBrains Mono", "Fira Code", Consolas, monospace',
      fontSize: '12px',
      lineHeight: '1.7',
      padding: '8px 0',
      background: 'transparent',
    }}>
      {logs.length === 0 ? (
        <div style={{
          color: 'rgba(255,255,255,0.15)',
          textAlign: 'center',
          marginTop: 80,
          fontSize: 13,
        }}>
          <div style={{ fontSize: 32, marginBottom: 10, opacity: 0.3 }}>▮</div>
          Waiting for Minecraft to launch...
        </div>
      ) : (
        logs.map((log, i) => (
          <div key={i} style={{
            display: 'flex',
            gap: 12,
            padding: '1px 16px',
            background: logBg(log.type),
            borderLeft: log.type !== 'info' ? `2px solid ${logColor(log.type)}44` : '2px solid transparent',
          }}>
            <span style={{ color: '#36364a', flexShrink: 0, fontSize: 10, paddingTop: 3, userSelect: 'none', minWidth: 60 }}>
              {log.timestamp}
            </span>
            <span style={{ color: logColor(log.type), wordBreak: 'break-all', flex: 1 }}>
              {log.text}
            </span>
          </div>
        ))
      )}
      <div ref={bottomRef} />
    </div>
  )
}

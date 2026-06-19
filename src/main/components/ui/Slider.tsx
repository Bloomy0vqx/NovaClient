interface Props {
  label: string
  value: number
  min: number
  max: number
  step?: number
  suffix?: string
  onChange: (v: number) => void
}

export default function Slider({ label, value, min, max, step = 1, suffix = '', onChange }: Props) {
  const pct = ((value - min) / (max - min)) * 100

  return (
    <div className="flex flex-col gap-1.5">
      <div className="flex justify-between text-sm">
        <span className="text-white/60">{label}</span>
        <span className="text-white/80 font-mono">{value}{suffix}</span>
      </div>
      <div className="relative h-2 bg-white/[0.04] rounded-full">
        <div
          className="absolute h-full rounded-full bg-gradient-to-r from-cyan-500 to-purple-500 transition-all duration-150"
          style={{ width: `${pct}%` }}
        />
        <input
          type="range"
          min={min}
          max={max}
          step={step}
          value={value}
          onChange={(e) => onChange(Number(e.target.value))}
          className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
        />
      </div>
    </div>
  )
}

interface Props {
  checked: boolean
  onChange: (v: boolean) => void
  label?: string
}

export default function Toggle({ checked, onChange, label }: Props) {
  return (
    <button
      className={`toggle ${checked ? 'active' : ''}`}
      onClick={() => onChange(!checked)}
      title={label}
    />
  )
}

interface CircularProgressBarProps {
  value: number;
  size?: number;
  strokeWidth?: number;
}

export const CircularProgressBar = ({ 
  value, 
  size = 120, 
  strokeWidth = 8 
}: CircularProgressBarProps) => {
  const radius = (size - strokeWidth) / 2;
  const circumference = radius * 2 * Math.PI;
  const offset = circumference - (value / 100) * circumference;
  
  const getColor = (score: number) => {
    if (score >= 85) return '#22c55e';
    if (score >= 60) return '#eab308';
    return '#ef4444';
  };

  return (
    <div className="relative inline-flex items-center justify-center">
      <svg width={size} height={size} className="transform -rotate-90">
        {/* Círculo de fondo */}
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke="#e5e7eb"
          strokeWidth={strokeWidth}
        />
        {/* Círculo de progreso */}
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke={getColor(value)}
          strokeWidth={strokeWidth}
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          strokeLinecap="round"
          className="transition-all duration-500"
        />
      </svg>
      <div className="absolute text-center">
        <span className="text-3xl font-bold" style={{ color: getColor(value) }}>
          {value}
        </span>
        <span className="text-sm text-gray-500 block">puntos</span>
      </div>
    </div>
  );
};
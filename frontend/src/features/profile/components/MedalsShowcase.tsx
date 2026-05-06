interface MedalsShowcaseProps {
  badges: ('VERIFIED' | 'SILVER' | 'GOLD')[];
}

const medalConfig = {
  VERIFIED: { icon: '✅', label: 'Verificado', color: 'bg-green-100 text-green-700' },
  SILVER: { icon: '🥈', label: 'Plata', color: 'bg-gray-100 text-gray-700' },
  GOLD: { icon: '🥇', label: 'Oro', color: 'bg-yellow-100 text-yellow-700' },
};

export const MedalsShowcase = ({ badges }: MedalsShowcaseProps) => {
  if (badges.length === 0) {
    return (
      <div className="bg-gray-50 rounded-lg p-6 text-center">
        <p className="text-gray-400">🏅 Aún sin medallas</p>
        <p className="text-sm text-gray-400 mt-1">
          Completa trabajos para ganar medallas
        </p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-3 gap-3">
      {badges.map((badge) => {
        const config = medalConfig[badge];
        return (
          <div
            key={badge}
            className={`${config.color} rounded-lg p-3 text-center`}
          >
            <div className="text-3xl mb-1">{config.icon}</div>
            <div className="text-sm font-semibold">{config.label}</div>
          </div>
        );
      })}
    </div>
  );
};
import { CircularProgressBar } from './CircularProgressBar';

interface ConfidenceScoreProps {
  score: number;
  successfulJobs: number;
  cancelledJobs: number;
}

export const ConfidenceScore = ({ score, successfulJobs, cancelledJobs }: ConfidenceScoreProps) => {
  const totalJobs = successfulJobs + cancelledJobs;
  const successRate = totalJobs > 0 ? (successfulJobs / totalJobs) * 100 : 0;

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="flex flex-col items-center text-center">
        <CircularProgressBar value={score} />
        <h3 className="text-lg font-semibold text-gray-800 mt-4">
          Confianza Global
        </h3>
        
        <div className="grid grid-cols-2 gap-4 w-full mt-4 pt-4 border-t border-gray-100">
          <div>
            <p className="text-2xl font-bold text-green-600">{successfulJobs}</p>
            <p className="text-xs text-gray-500">Trabajos exitosos</p>
          </div>
          <div>
            <p className="text-2xl font-bold text-red-600">{cancelledJobs}</p>
            <p className="text-xs text-gray-500">Cancelados</p>
          </div>
        </div>
        
        <div className="w-full mt-3">
          <div className="flex justify-between text-sm text-gray-600 mb-1">
            <span>Tasa de éxito</span>
            <span>{successRate.toFixed(0)}%</span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div
              className="bg-green-600 h-2 rounded-full transition-all"
              style={{ width: `${successRate}%` }}
            />
          </div>
        </div>
      </div>
    </div>
  );
};
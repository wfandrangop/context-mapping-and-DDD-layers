interface PhotoPreviewProps {
  photos: string[];
  onDelete?: (index: number) => void;
}

export const PhotoPreview = ({ photos, onDelete }: PhotoPreviewProps) => {
  if (photos.length === 0) {
    return (
      <div className="text-center py-8 bg-gray-50 rounded-lg">
        <p className="text-gray-500">No hay fotos subidas aún</p>
        <p className="text-sm text-gray-400 mt-1">Sube fotos de tu trabajo como evidencia</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-2 gap-3">
      {photos.map((photo, index) => (
        <div key={index} className="relative group">
          <img
            src={photo}
            alt={`Evidencia ${index + 1}`}
            className="w-full h-32 object-cover rounded-lg border border-gray-200"
          />
          {onDelete && (
            <button
              onClick={() => onDelete(index)}
              className="absolute top-1 right-1 bg-red-600 text-white rounded-full w-6 h-6 flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition-opacity"
            >
              ✕
            </button>
          )}
        </div>
      ))}
    </div>
  );
};
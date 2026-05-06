import { useCallback, useState } from 'react';

interface EvidenceUploadAreaProps {
  onUpload: (files: FileList) => Promise<void>;
  uploading: boolean;
}

export const EvidenceUploadArea = ({ onUpload, uploading }: EvidenceUploadAreaProps) => {
  const [isDragOver, setIsDragOver] = useState(false);

  const handleDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(true);
  }, []);

  const handleDragLeave = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(false);
  }, []);

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(false);
    const files = e.dataTransfer.files;
    if (files.length > 0) {
      onUpload(files);
    }
  }, [onUpload]);

  const handleFileSelect = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      onUpload(files);
    }
  }, [onUpload]);

  return (
    <div
      onDragOver={handleDragOver}
      onDragLeave={handleDragLeave}
      onDrop={handleDrop}
      className={`border-2 border-dashed rounded-lg p-8 text-center transition-colors ${
        isDragOver
          ? 'border-blue-500 bg-blue-50'
          : 'border-gray-300 bg-gray-50'
      }`}
    >
      <div className="text-4xl mb-3">📸</div>
      <p className="text-gray-600 mb-2">
        Arrastra y suelta tus fotos aquí
      </p>
      <p className="text-sm text-gray-400 mb-4">
        o
      </p>
      <label className="inline-block">
        <input
          type="file"
          accept="image/*"
          multiple
          onChange={handleFileSelect}
          disabled={uploading}
          className="hidden"
        />
        <span className={`bg-blue-600 text-white px-4 py-2 rounded-lg cursor-pointer hover:bg-blue-700 transition-colors ${
          uploading ? 'opacity-50 cursor-not-allowed' : ''
        }`}>
          {uploading ? 'Subiendo...' : '📁 Seleccionar archivos'}
        </span>
      </label>
      <p className="text-xs text-gray-400 mt-4">
        Formatos permitidos: JPG, PNG. Tamaño máximo: 10MB
      </p>
    </div>
  );
};
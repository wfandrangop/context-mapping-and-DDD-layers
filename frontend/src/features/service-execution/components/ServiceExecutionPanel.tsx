import { useState } from 'react';
import {
  Camera,
  CheckCircle2,
  Clock3,
  FileWarning,
  ImagePlus,
  PlayCircle,
  Star,
  Upload,
  X,
} from 'lucide-react';
import { Button } from '@shared/ui/Button';
import { Card } from '@shared/ui/Card';
import { serviceExecutionService } from '@features/service-execution/services/serviceExecutionService';
import { sessionService } from '@shared/services/session/sessionService';
import type { ServiceExecution } from '@features/service-execution/types/serviceExecution.types';

interface ServiceExecutionPanelProps {
  title?: string;
  executions: ServiceExecution[];
  currentUserRole: 'customer' | 'worker';
  emptyMessage: string;
  onChanged: () => Promise<void> | void;
}

const statusContent: Record<string, { label: string; description: string; className: string }> = {
  STARTED: {
    label: 'Servicio iniciado',
    description: 'La orden fue creada y está lista para comenzar.',
    className: 'bg-blue-50 text-blue-700 border-blue-200',
  },
  IN_PROCESS: {
    label: 'Servicio en proceso',
    description: 'El trabajo está en ejecución. Se pueden cargar evidencias.',
    className: 'bg-amber-50 text-amber-700 border-amber-200',
  },
  FINALIZED: {
    label: 'Esperando validacion',
    description: 'El trabajador marco el trabajo como terminado. Falta validacion del cliente.',
    className: 'bg-green-50 text-green-700 border-green-200',
  },
  VALIDATED: {
    label: 'Trabajo finalizado',
    description: 'El cliente valido el resultado final y dejo su calificacion.',
    className: 'bg-emerald-50 text-emerald-700 border-emerald-200',
  },
  DISPUTED: {
    label: 'En litigio',
    description: 'El cliente reporto un problema con el resultado.',
    className: 'bg-red-50 text-red-700 border-red-200',
  },
};

const getStatus = (status: string) =>
  statusContent[status] ?? {
    label: status,
    description: 'Estado recibido desde backend.',
    className: 'bg-gray-50 text-gray-700 border-gray-200',
  };

const getTodayLabel = () =>
  new Intl.DateTimeFormat('es-EC', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date());

export const ServiceExecutionPanel = ({
  title = 'Estado de órdenes de servicio',
  executions,
  currentUserRole,
  emptyMessage,
  onChanged,
}: ServiceExecutionPanelProps) => {
  const [selectedFileByExecution, setSelectedFileByExecution] = useState<Record<string, File | null>>({});
  const [feedbackByExecution, setFeedbackByExecution] = useState<Record<string, { rating: number; comment: string }>>({});
  const [messageByExecution, setMessageByExecution] = useState<Record<string, string>>({});
  const [busyExecutionId, setBusyExecutionId] = useState<string | null>(null);
  const [finalizingExecution, setFinalizingExecution] = useState<ServiceExecution | null>(null);

  const setMessage = (id: string, message: string) => {
    setMessageByExecution(prev => ({ ...prev, [id]: message }));
  };

  const getClientDisplayName = (clientId: string) =>
    sessionService.getRegisteredProfileNameById(clientId) ?? 'Cliente asignado';

  const getWorkerDisplayName = (workerId: string) => {
    const worker = sessionService.getWorkerProfileSummary(workerId);
    return worker.fullName || 'Trabajador asignado';
  };

  const beginExecution = async (execution: ServiceExecution) => {
    setBusyExecutionId(execution.id);
    try {
      await serviceExecutionService.begin(execution.id);
      setMessage(execution.id, 'Servicio marcado como en proceso.');
      await onChanged();
    } catch (error: any) {
      const backendMessage = error.response?.data?.error || error.response?.data?.message;
      setMessage(
        execution.id,
        backendMessage
          ? `No se pudo marcar en proceso: ${backendMessage}`
          : 'No se pudo marcar en proceso en el backend. Intenta nuevamente.'
      );
      await onChanged();
    } finally {
      setBusyExecutionId(null);
    }
  };

  const uploadEvidence = async (execution: ServiceExecution) => {
    const file = selectedFileByExecution[execution.id];

    if (!file) {
      setMessage(execution.id, 'Selecciona una foto o video antes de cargar evidencia.');
      return;
    }

    if (!file.type.startsWith('image/') && !file.type.startsWith('video/')) {
      setMessage(execution.id, 'Selecciona una foto o video válido.');
      return;
    }

    setBusyExecutionId(execution.id);
    try {
      if (file.type.startsWith('video/')) {
        throw new Error('Video evidence is local-only with the current backend');
      }

      await serviceExecutionService.uploadPhoto(execution.id, file);
      setSelectedFileByExecution(prev => ({ ...prev, [execution.id]: null }));
      setMessage(execution.id, `Evidencia cargada el ${getTodayLabel()}.`);
      await onChanged();
    } catch {
      sessionService.addExecutionEvidence(execution.id, {
        id: crypto.randomUUID(),
        fileName: file.name,
        mediaType: file.type.startsWith('video/') ? 'video' : 'photo',
        uploadedAt: new Date().toISOString(),
        source: 'local',
      });
      setSelectedFileByExecution(prev => ({ ...prev, [execution.id]: null }));
      setMessage(execution.id, 'Evidencia registrada localmente para pruebas; el backend actual no pudo persistir esta actualización.');
      await onChanged();
    } finally {
      setBusyExecutionId(null);
    }
  };

  const saveOptionalEvidence = async (execution: ServiceExecution, file: File | null) => {
    if (!file) return execution;

    if (!file.type.startsWith('image/') && !file.type.startsWith('video/')) {
      throw new Error('Selecciona una foto o video valido.');
    }

    if (file.type.startsWith('video/')) {
      sessionService.addExecutionEvidence(execution.id, {
        id: crypto.randomUUID(),
        fileName: file.name,
        mediaType: 'video',
        uploadedAt: new Date().toISOString(),
        source: 'local',
      });
      return execution;
    }

    try {
      return await serviceExecutionService.uploadPhoto(execution.id, file);
    } catch {
      sessionService.addExecutionEvidence(execution.id, {
        id: crypto.randomUUID(),
        fileName: file.name,
        mediaType: 'photo',
        uploadedAt: new Date().toISOString(),
        source: 'local',
      });
      return execution;
    }
  };

  const markWorkerFinalized = async (execution: ServiceExecution) => {
    const file = selectedFileByExecution[execution.id] ?? null;

    setBusyExecutionId(execution.id);
    try {
      await saveOptionalEvidence(execution, file);
      setSelectedFileByExecution(prev => ({ ...prev, [execution.id]: null }));
      sessionService.addExecutionForParticipants(execution.id, execution.clientId, execution.workerId);
      sessionService.setExecutionStatus(execution.id, 'FINALIZED');
      setMessage(execution.id, 'Trabajo finalizado. Ahora el cliente debe validar y calificar.');
      setFinalizingExecution(null);
      await onChanged();
    } catch (error: any) {
      setMessage(execution.id, error.message || 'No se pudo finalizar el trabajo.');
    } finally {
      setBusyExecutionId(null);
    }
  };

  const validateExecution = async (execution: ServiceExecution) => {
    const feedback = feedbackByExecution[execution.id] ?? { rating: 5, comment: '' };
    const file = selectedFileByExecution[execution.id];

    if (!feedback.comment.trim()) {
      setMessage(execution.id, 'Agrega un comentario para validar el trabajo.');
      return;
    }

    setBusyExecutionId(execution.id);
    try {
      let latestExecution = await serviceExecutionService.getById(execution.id);

      if (latestExecution.status === 'STARTED') {
        latestExecution = await serviceExecutionService.begin(execution.id);
      }

      if (latestExecution.status !== 'IN_PROCESS') {
        setMessage(
          execution.id,
          'La orden debe estar en proceso antes de finalizarse.'
        );
        return;
      }

      if (file) {
        latestExecution = await saveOptionalEvidence(latestExecution, file);
        setSelectedFileByExecution(prev => ({ ...prev, [execution.id]: null }));
      }

      const canPersistCompletion = latestExecution.photoUrls.length > 0;
      if (canPersistCompletion) {
        await serviceExecutionService.complete(execution.id, {
          clientRating: Number(feedback.rating),
          clientComment: feedback.comment.trim(),
        });
      }
      sessionService.addExecutionForParticipants(execution.id, execution.clientId, execution.workerId);
      sessionService.setExecutionFeedback(execution.id, Number(feedback.rating), feedback.comment.trim());
      sessionService.recordWorkerReview(
        execution.workerId,
        Number(feedback.rating),
        feedback.comment.trim(),
        execution.id
      );
      setMessage(
        execution.id,
        canPersistCompletion
          ? 'Trabajo finalizado, validado y calificado por el cliente.'
          : 'Trabajo validado localmente. El backend actual exige al menos una foto para persistir la finalizacion.'
      );
      setFinalizingExecution(null);
      await onChanged();
    } catch (error: any) {
      const backendMessage = error.response?.data?.error || error.response?.data?.message;
      setMessage(
        execution.id,
        backendMessage
          ? `No se pudo validar en base de datos: ${backendMessage}`
          : 'No se pudo validar en base de datos. Revisa que la orden tenga evidencia y este en proceso.'
      );
      await onChanged();
    } finally {
      setBusyExecutionId(null);
    }
  };

  const getStepState = (
    executionStatus: string,
    step: 'STARTED' | 'IN_PROCESS' | 'FINALIZED' | 'VALIDATED'
  ) => {
    const order = ['STARTED', 'IN_PROCESS', 'FINALIZED', 'VALIDATED'];
    return order.indexOf(executionStatus) >= order.indexOf(step);
  };

  return (
    <Card id="servicios" hover={false}>
      <div className="flex items-center justify-between gap-4 mb-5">
        <h2 className="text-xl font-bold text-[#1A5276]">{title}</h2>
        <span className="text-sm text-gray-500">{executions.length} órdenes</span>
      </div>

      {executions.length === 0 ? (
        <p className="text-gray-500">{emptyMessage}</p>
      ) : (
        <div className="grid gap-4">
          {executions.map(execution => {
            const status = getStatus(execution.status);
            const feedback = feedbackByExecution[execution.id] ?? { rating: 5, comment: '' };
            const selectedFile = selectedFileByExecution[execution.id];
            const isBusy = busyExecutionId === execution.id;

            return (
              <article key={execution.id} className="rounded-xl border border-gray-100 bg-white p-5 shadow-sm">
                <div className="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-4">
                  <div>
                    <p className="text-sm font-semibold text-[#F39C12]">Trabajo en ejecución</p>
                    <p className="text-2xl font-bold text-[#1A5276]">Orden {execution.id.slice(0, 8)}</p>
                    <p className="text-sm text-gray-500">Cliente: {getClientDisplayName(execution.clientId)}</p>
                    <p className="text-sm text-gray-500">Trabajador: {getWorkerDisplayName(execution.workerId)}</p>
                    <p className="text-xs text-gray-400 mt-1">Consultado: {getTodayLabel()}</p>
                  </div>
                  <div className={`rounded-lg border px-3 py-2 text-sm ${status.className}`}>
                    <div className="flex items-center gap-2 font-semibold">
                      {execution.status === 'FINALIZED' || execution.status === 'VALIDATED' ? <CheckCircle2 className="w-4 h-4" /> : <Clock3 className="w-4 h-4" />}
                      {status.label}
                    </div>
                    <p className="mt-1">{status.description}</p>
                  </div>
                </div>

                <div className="mt-5 grid gap-2 sm:grid-cols-4">
                  {[
                    { key: 'STARTED', label: 'Iniciado' },
                    { key: 'IN_PROCESS', label: 'En proceso' },
                    { key: 'FINALIZED', label: 'Finalizado' },
                    { key: 'VALIDATED', label: 'Validado' },
                  ].map((step, index) => {
                    const active = getStepState(execution.status, step.key as 'STARTED' | 'IN_PROCESS' | 'FINALIZED' | 'VALIDATED');
                    return (
                      <div key={step.key} className="flex items-center gap-2">
                        <div
                          className={`w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold ${
                            active ? 'bg-[#F39C12] text-white' : 'bg-gray-200 text-gray-500'
                          }`}
                        >
                          {index + 1}
                        </div>
                        <div className="min-w-0">
                          <p className={`text-sm font-semibold ${active ? 'text-[#1A5276]' : 'text-gray-400'}`}>
                            {step.label}
                          </p>
                        </div>
                      </div>
                    );
                  })}
                </div>

                <div className="mt-4 grid gap-3 md:grid-cols-3">
                  <div className="rounded-lg bg-white border border-gray-100 p-3">
                    <p className="text-sm font-semibold text-[#1A5276] flex items-center gap-2">
                      <Camera className="w-4 h-4" />
                      Evidencias
                    </p>
                    <p className="text-sm text-gray-500 mt-1">
                      Fotos backend: {execution.photoUrls.length}
                    </p>
                    <p className="text-xs text-gray-400 mt-1">
                      Evidencias locales: {execution.localEvidence?.length ?? 0}
                    </p>
                  </div>

                  <div className="rounded-lg bg-white border border-gray-100 p-3 md:col-span-2">
                    {execution.photoUrls.length > 0 || execution.localEvidence?.length ? (
                      <div className="flex flex-wrap gap-2">
                        {execution.photoUrls.map((url, index) => (
                          <a
                            key={`${url}-${index}`}
                            href={`/api/${url.replace(/^\//, '')}`}
                            target="_blank"
                            rel="noreferrer"
                            className="rounded-lg bg-[#1A5276]/10 text-[#1A5276] px-3 py-2 text-sm hover:bg-[#1A5276]/15"
                          >
                            Foto {index + 1}
                          </a>
                        ))}
                        {execution.localEvidence?.map(evidence => (
                          <span
                            key={evidence.id}
                            className="rounded-lg bg-[#F39C12]/10 text-[#1A5276] px-3 py-2 text-sm"
                          >
                            {evidence.mediaType === 'video' ? 'Video' : 'Foto'} local · {evidence.fileName} ·{' '}
                            {new Intl.DateTimeFormat('es-EC', {
                              dateStyle: 'short',
                              timeStyle: 'short',
                            }).format(new Date(evidence.uploadedAt))}
                          </span>
                        ))}
                      </div>
                    ) : (
                      <p className="text-sm text-gray-500 flex items-center gap-2">
                        <FileWarning className="w-4 h-4" />
                        Sin evidencias cargadas.
                      </p>
                    )}
                  </div>
                </div>

                <div className="mt-4 grid gap-4">
                  {execution.status === 'STARTED' && (
                    <div className="rounded-lg bg-blue-50 border border-blue-100 p-4">
                      <p className="font-semibold text-[#1A5276] mb-2">El trabajo aún no ha comenzado</p>
                      <p className="text-sm text-gray-600 mb-4">
                        El trabajador debe iniciar la ejecucion operativa. El cliente solo visualiza el estado.
                      </p>
                      {currentUserRole === 'worker' && (
                        <Button
                          variant="secondary"
                          onClick={() => beginExecution(execution)}
                          disabled={isBusy}
                        >
                          <span className="inline-flex items-center gap-2">
                            <PlayCircle className="w-4 h-4" />
                            Iniciar servicio
                          </span>
                        </Button>
                      )}
                    </div>
                  )}

                  {execution.status === 'IN_PROCESS' && (
                    <div className="rounded-lg bg-amber-50 border border-amber-100 p-4">
                      <p className="font-semibold text-[#1A5276] mb-2">Trabajo en proceso</p>
                      <p className="text-sm text-gray-600 mb-4">
                        El trabajador puede subir evidencias y marcar el trabajo como finalizado.
                      </p>
                      {currentUserRole === 'worker' ? (
                        <Button variant="secondary" onClick={() => setFinalizingExecution(execution)}>
                          <span className="inline-flex items-center gap-2">
                            <Upload className="w-4 h-4" />
                            Evidencias y finalizar
                          </span>
                        </Button>
                      ) : (
                        <p className="text-sm text-gray-600">Esperando evidencias y finalizacion del trabajador.</p>
                      )}
                    </div>
                  )}

                  {execution.status === 'FINALIZED' && (
                    <div className="rounded-lg bg-green-50 border border-green-200 p-4 text-sm text-green-800">
                      <p className="font-semibold">Trabajo finalizado por el trabajador</p>
                      <p className="mb-3">El cliente debe revisar evidencias, validar y calificar el resultado.</p>
                      {currentUserRole === 'customer' ? (
                        <div className="flex flex-wrap gap-2">
                          <Button variant="primary" onClick={() => setFinalizingExecution(execution)}>
                            Validar y calificar
                          </Button>
                          <Button
                            variant="secondary"
                            onClick={async () => {
                              sessionService.setExecutionStatus(execution.id, 'DISPUTED');
                              setMessage(execution.id, 'Problema reportado. La orden paso a litigio.');
                              await onChanged();
                            }}
                          >
                            Reportar problema
                          </Button>
                        </div>
                      ) : (
                        <p>Esperando validacion del cliente.</p>
                      )}
                    </div>
                  )}

                  {execution.status === 'VALIDATED' && (
                    <div className="rounded-lg bg-emerald-50 border border-emerald-200 p-4 text-sm text-emerald-800">
                      <p className="font-semibold">Trabajo validado</p>
                      {execution.clientRating && <p>Calificacion: {execution.clientRating} estrellas</p>}
                      {execution.clientComment && <p>Comentario: {execution.clientComment}</p>}
                    </div>
                  )}

                  {execution.status === 'DISPUTED' && (
                    <div className="rounded-lg bg-red-50 border border-red-200 p-4 text-sm text-red-800">
                      <p className="font-semibold">Trabajo en litigio</p>
                      <p>El cliente reporto un problema. Conserva evidencias y comentarios.</p>
                    </div>
                  )}

                  {messageByExecution[execution.id] && (
                    <div className="rounded-lg bg-[#F39C12]/10 text-[#1A5276] px-3 py-2 text-sm">
                      {messageByExecution[execution.id]}
                    </div>
                  )}
                </div>
              </article>
            );
          })}
        </div>
      )}

      {finalizingExecution && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="w-full max-w-2xl rounded-2xl bg-white shadow-2xl">
            <div className="flex items-center justify-between border-b border-gray-100 p-5">
              <div>
                <p className="text-sm font-semibold text-[#F39C12]">
                  {currentUserRole === 'customer' ? 'Validacion del trabajo' : 'Evidencias del trabajo'}
                </p>
                <h3 className="text-2xl font-bold text-[#1A5276]">
                  Orden {finalizingExecution.id.slice(0, 8)}
                </h3>
              </div>
              <button
                type="button"
                onClick={() => setFinalizingExecution(null)}
                className="rounded-lg p-2 text-gray-500 hover:bg-gray-100"
                aria-label="Cerrar"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="grid gap-5 p-5">
              <div className="rounded-lg bg-[#F4F7F6] p-4">
                <p className="font-semibold text-[#1A5276]">Datos de la orden</p>
                <p className="text-sm text-gray-600">Cliente: {getClientDisplayName(finalizingExecution.clientId)}</p>
                <p className="text-sm text-gray-600">Trabajador: {getWorkerDisplayName(finalizingExecution.workerId)}</p>
                <p className="text-sm text-gray-600">Estado actual: {getStatus(finalizingExecution.status).label}</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Evidencia del trabajo
                </label>
                <label className="flex min-h-32 cursor-pointer flex-col items-center justify-center rounded-xl border-2 border-dashed border-gray-300 bg-gray-50 p-4 text-center hover:border-[#F39C12]">
                  <ImagePlus className="w-8 h-8 text-[#F39C12] mb-2" />
                  <span className="font-semibold text-[#1A5276]">
                    Selecciona una foto o video
                  </span>
                  <span className="text-sm text-gray-500">
                    Opcional. Las fotos se guardan en backend; los videos quedan como evidencia local.
                  </span>
                  <input
                    type="file"
                    accept="image/*,video/*"
                    className="hidden"
                    onChange={event =>
                      setSelectedFileByExecution(prev => ({
                        ...prev,
                        [finalizingExecution.id]: event.target.files?.[0] ?? null,
                      }))
                    }
                  />
                </label>
                {selectedFileByExecution[finalizingExecution.id] && (
                  <p className="text-xs text-gray-500 mt-2">
                    Seleccionado: {selectedFileByExecution[finalizingExecution.id]?.name} · {getTodayLabel()}
                  </p>
                )}
              </div>

              {currentUserRole === 'customer' && (
                <>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Calificación del cliente
                    </label>
                    <select
                      value={(feedbackByExecution[finalizingExecution.id] ?? { rating: 5, comment: '' }).rating}
                      onChange={event => {
                        const feedback = feedbackByExecution[finalizingExecution.id] ?? { rating: 5, comment: '' };
                        setFeedbackByExecution(prev => ({
                          ...prev,
                          [finalizingExecution.id]: {
                            ...feedback,
                            rating: Number(event.target.value),
                          },
                        }));
                      }}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#F39C12]"
                    >
                      {[5, 4, 3, 2, 1].map(value => (
                        <option key={value} value={value}>
                          {value} estrellas
                        </option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Comentario de validacion
                    </label>
                    <textarea
                      value={(feedbackByExecution[finalizingExecution.id] ?? { rating: 5, comment: '' }).comment}
                      onChange={event => {
                        const feedback = feedbackByExecution[finalizingExecution.id] ?? { rating: 5, comment: '' };
                        setFeedbackByExecution(prev => ({
                          ...prev,
                          [finalizingExecution.id]: {
                            ...feedback,
                            comment: event.target.value,
                          },
                        }));
                      }}
                      className="w-full min-h-28 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#F39C12]"
                      placeholder="Describe la calidad del trabajo, cumplimiento y observaciones..."
                    />
                  </div>
                </>
              )}
            </div>

            <div className="flex flex-col-reverse gap-3 border-t border-gray-100 p-5 sm:flex-row sm:justify-end">
              <Button variant="secondary" onClick={() => setFinalizingExecution(null)}>
                Cancelar
              </Button>
              {currentUserRole === 'customer' ? (
                <Button
                  variant="primary"
                  onClick={() => validateExecution(finalizingExecution)}
                  disabled={busyExecutionId === finalizingExecution.id}
                >
                  Validar y calificar
                </Button>
              ) : (
                <div className="flex flex-col gap-2 sm:flex-row">
                  <Button
                    variant="secondary"
                    onClick={() => uploadEvidence(finalizingExecution)}
                    disabled={busyExecutionId === finalizingExecution.id}
                  >
                    Guardar evidencia opcional
                  </Button>
                  <Button
                    variant="primary"
                    onClick={() => markWorkerFinalized(finalizingExecution)}
                    disabled={busyExecutionId === finalizingExecution.id}
                  >
                    Finalizar trabajo
                  </Button>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </Card>
  );
};

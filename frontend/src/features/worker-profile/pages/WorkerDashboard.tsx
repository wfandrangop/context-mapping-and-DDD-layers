import { Navigate } from 'react-router-dom';
import { BadgeCheck, BriefcaseBusiness, ClipboardList, Send, Star, Wrench } from 'lucide-react';
import { DashboardShell } from '@shared/layout/DashboardShell';
import { JobPostList } from '@features/job-marketplace/components/JobPostList';
import { MetricCard } from '@shared/ui/MetricCard';
import { ServiceExecutionPanel } from '@features/service-execution/components/ServiceExecutionPanel';
import { Card } from '@shared/ui/Card';
import { useWorkerDashboardData } from '@shared/hooks/useDashboardData';
import { jobMarketplaceService } from '@features/job-marketplace/services/jobMarketplaceService';
import { sessionService } from '@shared/services/session/sessionService';
import type { JobPost } from '@features/job-marketplace/types/jobMarketplace.types';

export const WorkerDashboard = () => {
  const profileId = sessionService.getWorkerProfileId();
  const workerName = sessionService.getWorkerName() ?? 'Trabajador';
  const isWorker = sessionService.getRole() === 'worker';
  const profileSummary = profileId ? sessionService.getWorkerProfileSummary(profileId) : null;
  const { availableJobs, recentJobs, applicationStatusByJobId, activeExecutions, reputation, isLoading, refresh } =
    useWorkerDashboardData(profileId);
  const isFinalizedByClient = (status: string, hasRating?: number) =>
    status === 'VALIDATED' || (status === 'FINALIZED' && Boolean(hasRating));
  const runningExecutions = activeExecutions.filter(execution =>
    !isFinalizedByClient(execution.status, execution.clientRating) &&
    !(execution.status === 'FINALIZED' && !execution.clientRating)
  );
  const pendingValidationExecutions = activeExecutions.filter(execution =>
    execution.status === 'FINALIZED' && !execution.clientRating
  );
  const finalizedExecutions = activeExecutions.filter(execution =>
    isFinalizedByClient(execution.status, execution.clientRating)
  );

  const locallyRatedExecutions = activeExecutions.filter(
    execution => isFinalizedByClient(execution.status, execution.clientRating) && execution.clientRating
  );
  const localAverageRating =
    locallyRatedExecutions.length > 0
      ? locallyRatedExecutions.reduce((total, execution) => total + (execution.clientRating ?? 0), 0) /
        locallyRatedExecutions.length
      : null;
  const displayedConfidence =
    localAverageRating !== null ? Math.round(localAverageRating * 20) : reputation?.confidenceScore ?? 0;
  const displayedSuccessfulJobs = (reputation?.successfulJobs ?? 0) + locallyRatedExecutions.length;
  const displayedReviewCount = (reputation?.reviewCount ?? 0) + locallyRatedExecutions.length;
  const workerBadge =
    displayedReviewCount > 0 ? reputation?.badges?.[0] ?? profileSummary?.badge ?? 'Perfil verificado' : 'Nuevo trabajador';
  const localReviews = profileSummary?.reviews ?? [];
  const getClientDisplayName = (clientId: string) =>
    sessionService.getRegisteredProfileNameById(clientId) ?? 'Cliente asignado';

  if (!sessionService.isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  if (!isWorker || !profileId) {
    return <Navigate to="/registro/trabajador" replace />;
  }

  const applyToJob = async (job: JobPost) => {
    await jobMarketplaceService.apply(job.id, { workerProfileId: profileId });
    sessionService.addScopedAppliedJobId(job.id, profileId);
    await refresh();
  };

  return (
    <DashboardShell role="worker" displayName={workerName}>
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-8">
        <div>
          <p className="text-sm font-semibold text-[#F39C12]">Dashboard de trabajador</p>
          <h1 className="text-3xl md:text-4xl font-bold text-[#1A5276]">Hola, {workerName}</h1>
          <p className="text-gray-600 mt-1">Revisa tu reputación, oportunidades y servicios activos.</p>
        </div>
      </div>

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4 mb-6">
        <MetricCard
          title="Confianza"
          value={displayedConfidence}
          description={localAverageRating !== null ? `Promedio local: ${localAverageRating.toFixed(1)} estrellas` : 'Sin reputación registrada aún'}
          icon={<Star />}
        />
        <MetricCard
          title="Trabajos exitosos"
          value={displayedSuccessfulJobs}
          description={locallyRatedExecutions.length > 0 ? 'Incluye validaciones locales' : 'Historial completado'}
          icon={<BriefcaseBusiness />}
        />
        <MetricCard
          title="Disponibles"
          value={availableJobs.length}
          description="Trabajos abiertos para postular"
          icon={<Send />}
        />
        <MetricCard
          title="Servicios activos"
          value={runningExecutions.length}
          description="Órdenes de servicio asignadas"
          icon={<ClipboardList />}
        />
      </div>

      <div className="grid gap-6 xl:grid-cols-[1fr_380px]">
        <section className="grid gap-6">
          <Card hover={false}>
            <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
              <div>
                <p className="text-sm font-semibold text-[#F39C12]">Perfil laboral</p>
                <h2 className="text-2xl font-bold text-[#1A5276]">{profileSummary?.specialty ?? 'Especialidad no registrada'}</h2>
                <p className="mt-2 text-gray-600">
                  {profileSummary?.professionalDescription ?? 'Completa tu descripcion profesional para que los clientes entiendan tu experiencia.'}
                </p>
              </div>
              <span className="inline-flex w-fit items-center gap-2 rounded-full bg-green-50 px-3 py-1 text-sm font-semibold text-green-700">
                <BadgeCheck className="h-4 w-4" />
                {workerBadge}
              </span>
            </div>

            <div className="mt-5 grid gap-4 md:grid-cols-2">
              <div>
                <p className="mb-2 text-sm font-semibold text-[#1A5276]">Categorias y especialidades</p>
                <div className="flex flex-wrap gap-2">
                  {(profileSummary?.categories?.length ? profileSummary.categories : ['Sin categorias registradas']).map(category => (
                    <span key={category} className="rounded-full bg-[#1A5276]/10 px-3 py-1 text-sm text-[#1A5276]">
                      {category}
                    </span>
                  ))}
                </div>
              </div>

              <div>
                <p className="mb-2 text-sm font-semibold text-[#1A5276]">Experiencia</p>
                <p className="text-sm text-gray-600">
                  {profileSummary?.yearsOfExperience ? `${profileSummary.yearsOfExperience} anos de experiencia` : 'Sin anos detectados'}
                </p>
                <p className="text-sm text-gray-600">
                  Calificacion: {localAverageRating !== null ? `${localAverageRating.toFixed(1)} estrellas` : 'Nuevo trabajador'}
                </p>
              </div>
            </div>

            {localReviews.length > 0 && (
              <div className="mt-5 rounded-lg border border-gray-100 bg-gray-50 p-4">
                <p className="mb-3 flex items-center gap-2 text-sm font-semibold text-[#1A5276]">
                  <Star className="h-4 w-4 text-[#F39C12]" />
                  Ultima reseña recibida
                </p>
                <p className="text-sm font-semibold text-gray-700">
                  {localReviews[0].rating} estrellas
                </p>
                <p className="mt-1 text-sm text-gray-600">{localReviews[0].comment}</p>
              </div>
            )}

            <div className="mt-5 grid gap-4 md:grid-cols-2">
              <div>
                <p className="mb-2 flex items-center gap-2 text-sm font-semibold text-[#1A5276]">
                  <Wrench className="h-4 w-4" />
                  Habilidades tecnicas
                </p>
                <div className="flex flex-wrap gap-2">
                  {(profileSummary?.technicalSkills?.length ? profileSummary.technicalSkills : ['Pendiente de deteccion']).map(skill => (
                    <span key={skill} className="rounded-full bg-gray-100 px-3 py-1 text-sm text-gray-700">
                      {skill}
                    </span>
                  ))}
                </div>
              </div>

              <div>
                <p className="mb-2 text-sm font-semibold text-[#1A5276]">Herramientas propias</p>
                <div className="flex flex-wrap gap-2">
                  {(profileSummary?.tools?.length ? profileSummary.tools : ['No registradas']).map(tool => (
                    <span key={tool} className="rounded-full bg-[#F39C12]/10 px-3 py-1 text-sm text-[#1A5276]">
                      {tool}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          </Card>

          <div id="disponibles">
            <JobPostList
              title="Trabajos disponibles"
              jobs={availableJobs}
              emptyMessage={isLoading ? 'Cargando oportunidades...' : 'No hay trabajos disponibles por ahora.'}
              actionLabel="Postular"
              onAction={applyToJob}
            />
          </div>

          <JobPostList
            title="Mis postulaciones"
            jobs={recentJobs}
            emptyMessage="Aún no tienes postulaciones."
            applicationStatusByJobId={applicationStatusByJobId}
          />

          <div id="procesos">
            <ServiceExecutionPanel
              title="Servicios activos"
              executions={runningExecutions}
              currentUserRole="worker"
              emptyMessage="No tienes órdenes de servicio registradas para tu perfil."
              onChanged={refresh}
            />
          </div>

          <ServiceExecutionPanel
            title="Pendientes de validacion"
            executions={pendingValidationExecutions}
            currentUserRole="worker"
            emptyMessage="No tienes trabajos esperando validacion del cliente."
            onChanged={refresh}
          />

          <Card hover={false}>
            <div className="mb-5 flex items-center justify-between gap-4">
              <h2 className="text-xl font-bold text-[#1A5276]">Historial de trabajos</h2>
              <span className="text-sm text-gray-500">{finalizedExecutions.length} finalizados</span>
            </div>
            {finalizedExecutions.length === 0 ? (
              <p className="rounded-lg border border-dashed border-gray-300 p-5 text-center text-gray-500">
                Aun no tienes trabajos validados por clientes.
              </p>
            ) : (
              <div className="grid gap-3">
                {finalizedExecutions.map(execution => (
                  <article key={execution.id} className="rounded-lg border border-gray-100 bg-gray-50 p-4">
                    <p className="font-semibold text-[#1A5276]">Orden {execution.id.slice(0, 8)}</p>
                    <p className="text-sm text-gray-600">Cliente: {getClientDisplayName(execution.clientId)}</p>
                    {execution.clientRating && <p className="text-sm text-gray-600">Calificacion: {execution.clientRating} estrellas</p>}
                    {execution.clientComment && <p className="text-sm text-gray-600">Comentario: {execution.clientComment}</p>}
                  </article>
                ))}
              </div>
            )}
          </Card>
        </section>

        <aside className="grid gap-6">
          <Card id="reputacion" hover={false}>
            <h2 className="text-xl font-bold text-[#1A5276] mb-4">Resumen de reputación</h2>
            <div className="flex items-center justify-center mb-5">
              <div className="w-32 h-32 rounded-full border-8 border-[#F39C12] flex items-center justify-center">
                <span className="text-3xl font-bold text-[#1A5276]">
                  {displayedConfidence}
                </span>
              </div>
            </div>
            <div className="grid gap-2 text-sm text-gray-600">
              <p>Reseñas: {displayedReviewCount}</p>
              <p>Promedio: {localAverageRating !== null ? `${localAverageRating.toFixed(1)} estrellas` : 'Sin calificaciones locales'}</p>
              <p>Cancelados: {reputation?.cancelledJobs ?? 0}</p>
              <p>Insignias: {reputation?.badges?.length ? reputation.badges.join(', ') : 'Sin insignias aún'}</p>
            </div>
            {localReviews.length > 0 && (
              <div className="mt-5 grid gap-3">
                <p className="text-sm font-semibold text-[#1A5276]">Comentarios recientes</p>
                {localReviews.slice(0, 3).map(review => (
                  <article key={review.executionId} className="rounded-lg bg-gray-50 p-3 text-sm text-gray-600">
                    <p className="font-semibold text-[#1A5276]">{review.rating} estrellas</p>
                    <p>{review.comment}</p>
                  </article>
                ))}
              </div>
            )}
          </Card>
        </aside>
      </div>
    </DashboardShell>
  );
};

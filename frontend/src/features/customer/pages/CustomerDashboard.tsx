import { useMemo, useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { BriefcaseBusiness, ClipboardList, PlusCircle, UsersRound } from 'lucide-react';
import { DashboardShell } from '@shared/layout/DashboardShell';
import { JobPostList } from '@features/job-marketplace/components/JobPostList';
import { MetricCard } from '@shared/ui/MetricCard';
import { ServiceExecutionPanel } from '@features/service-execution/components/ServiceExecutionPanel';
import { Button } from '@shared/ui/Button';
import { Card } from '@shared/ui/Card';
import { Input } from '@shared/ui/Input';
import { useCustomerDashboardData } from '@shared/hooks/useDashboardData';
import { jobMarketplaceService } from '@features/job-marketplace/services/jobMarketplaceService';
import { serviceExecutionService } from '@features/service-execution/services/serviceExecutionService';
import { sessionService } from '@shared/services/session/sessionService';
import type { JobApplicantSummary, JobPost, JobUrgency } from '@features/job-marketplace/types/jobMarketplace.types';

export const CustomerDashboard = () => {
  const customerId = sessionService.getCustomerId();
  const customerName = sessionService.getCustomerName() ?? 'Cliente';
  const isCustomer = sessionService.getRole() === 'customer';
  const { publishedJobs, openJobs, activeExecutions, applicantsByJobId, isLoading, refresh } =
    useCustomerDashboardData(customerId);
  const [formData, setFormData] = useState({
    technicalRequirements: '',
    minimumBudget: 50,
    maximumBudget: 500,
    urgency: 'MEDIUM' as JobUrgency,
  });
  const [isPublishing, setIsPublishing] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const hiredWorkers = useMemo(
    () => new Set(activeExecutions.map(execution => execution.workerId)).size,
    [activeExecutions]
  );
  const isFinalizedByClient = (status: string, hasRating?: number) =>
    status === 'VALIDATED' || (status === 'FINALIZED' && Boolean(hasRating));
  const finalizedExecutions = activeExecutions.filter(execution =>
    isFinalizedByClient(execution.status, execution.clientRating)
  );
  const pendingValidationExecutions = activeExecutions.filter(execution =>
    execution.status === 'FINALIZED' && !execution.clientRating
  );
  const runningExecutions = activeExecutions.filter(execution =>
    !isFinalizedByClient(execution.status, execution.clientRating) &&
    !(execution.status === 'FINALIZED' && !execution.clientRating)
  );
  const getWorkerDisplayName = (workerId: string) => {
    const worker = sessionService.getWorkerProfileSummary(workerId);
    return worker.fullName || 'Trabajador asignado';
  };

  if (!sessionService.isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  if (!isCustomer || !customerId) {
    return <Navigate to="/registro/cliente" replace />;
  }

  const handleCreateJob = async (event: React.FormEvent) => {
    event.preventDefault();
    setIsPublishing(true);
    setMessage(null);

    try {
      const created = await jobMarketplaceService.create({
        clientId: customerId,
        technicalRequirements: formData.technicalRequirements
          .split(',')
          .map(requirement => requirement.trim())
          .filter(Boolean),
        minimumBudget: Number(formData.minimumBudget),
        maximumBudget: Number(formData.maximumBudget),
        urgency: formData.urgency,
      });

      sessionService.addScopedPublishedJobId(created.id, customerId);
      setFormData({
        technicalRequirements: '',
        minimumBudget: 50,
        maximumBudget: 500,
        urgency: 'MEDIUM',
      });
      setMessage('Empleo publicado correctamente.');
      await refresh();
    } catch {
      setMessage('No se pudo publicar el empleo. Revisa presupuesto y requisitos.');
    } finally {
      setIsPublishing(false);
    }
  };

  const handleSelectApplicant = async (job: JobPost, applicant: JobApplicantSummary) => {
    if (!applicant.profileId) {
      setMessage('Este empleo aún no tiene postulantes para seleccionar.');
      return;
    }

    try {
      await jobMarketplaceService.selectWorker(job.id, { workerProfileId: applicant.profileId });
      sessionService.setApplicationStatus(job.id, applicant.profileId, 'APROBADO');
      job.applicantProfileIds
        .filter(profileId => profileId !== applicant.profileId)
        .forEach(profileId => {
          sessionService.setApplicationStatus(job.id, profileId, 'RECHAZADO');
        });
      const execution = await serviceExecutionService.create({
        clientId: customerId,
        workerId: applicant.profileId,
      });

      sessionService.addExecutionForParticipants(execution.id, customerId, applicant.profileId);
      setMessage(
        `Trabajador seleccionado: ${applicant.fullName}. El trabajo pasó a órdenes de servicio.`
      );
      await refresh();
    } catch {
      setMessage('No se pudo seleccionar el trabajador o crear la orden de servicio.');
    }
  };

  return (
    <DashboardShell role="customer" displayName={customerName}>
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-8">
        <div>
          <p className="text-sm font-semibold text-[#F39C12]">Dashboard de cliente</p>
          <h1 className="text-3xl md:text-4xl font-bold text-[#1A5276]">Hola, {customerName}</h1>
          <p className="text-gray-600 mt-1">Publica trabajos, revisa postulantes y gestiona servicios.</p>
        </div>
        <a href="#publicar">
          <Button variant="primary">Publicar empleo</Button>
        </a>
      </div>

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4 mb-6">
        <MetricCard title="Trabajos publicados" value={publishedJobs.length} description="Creados desde tu cuenta" icon={<BriefcaseBusiness />} />
        <MetricCard title="Servicios activos" value={runningExecutions.length} description="Ejecuciones en seguimiento" icon={<ClipboardList />} />
        <MetricCard title="Trabajadores contratados" value={hiredWorkers} description="Perfiles vinculados" icon={<UsersRound />} />
        <MetricCard title="Oportunidades abiertas" value={openJobs.length} description="Demanda visible en marketplace" icon={<PlusCircle />} />
      </div>

      {message && <div className="mb-6 rounded-lg bg-[#F39C12]/10 text-[#1A5276] px-4 py-3">{message}</div>}

      <div className="grid gap-6 xl:grid-cols-[1fr_380px]">
        <section className="grid gap-6">
          <JobPostList
            title="Trabajos publicados"
            jobs={publishedJobs}
            emptyMessage={isLoading ? 'Cargando trabajos...' : 'Aún no tienes trabajos publicados.'}
            applicantsByJobId={applicantsByJobId}
            onSelectApplicant={handleSelectApplicant}
          />

          <div>
            <ServiceExecutionPanel
              title="Servicios activos"
              executions={runningExecutions}
              currentUserRole="customer"
              emptyMessage="No hay órdenes de servicio registradas para tu cuenta."
              onChanged={refresh}
            />
          </div>

          <div id="validacion">
            <ServiceExecutionPanel
              title="Validar trabajos finalizados"
              executions={pendingValidationExecutions}
              currentUserRole="customer"
              emptyMessage="No hay trabajos pendientes de validacion."
              onChanged={refresh}
            />
          </div>

          <Card hover={false}>
            <div className="mb-5 flex items-center justify-between gap-4">
              <h2 className="text-xl font-bold text-[#1A5276]">Trabajos finalizados</h2>
              <span className="text-sm text-gray-500">{finalizedExecutions.length} trabajos</span>
            </div>
            {finalizedExecutions.length === 0 ? (
              <p className="rounded-lg border border-dashed border-gray-300 p-5 text-center text-gray-500">
                Aun no tienes trabajos finalizados o validados.
              </p>
            ) : (
              <div className="grid gap-3">
                {finalizedExecutions.map(execution => (
                  <article key={execution.id} className="rounded-lg border border-gray-100 bg-gray-50 p-4">
                    <p className="font-semibold text-[#1A5276]">Orden {execution.id.slice(0, 8)}</p>
                    <p className="text-sm text-gray-600">Trabajador: {getWorkerDisplayName(execution.workerId)}</p>
                    <p className="text-sm text-gray-600">Estado: Finalizado</p>
                    {execution.clientRating && <p className="text-sm text-gray-600">Calificacion: {execution.clientRating} estrellas</p>}
                    {execution.clientComment && <p className="text-sm text-gray-600">Comentario: {execution.clientComment}</p>}
                  </article>
                ))}
              </div>
            )}
          </Card>
        </section>

        <aside className="grid gap-6">
          <Card id="publicar" hover={false}>
            <h2 className="text-xl font-bold text-[#1A5276] mb-4">Publicar empleo</h2>
            <form onSubmit={handleCreateJob}>
              <Input
                label="Descripcion y habilidades requeridas"
                value={formData.technicalRequirements}
                onChange={event => setFormData(prev => ({ ...prev, technicalRequirements: event.target.value }))}
                placeholder="Reparacion de fugas, instalacion de tuberias, mantenimiento de banos"
                required
              />
              <div className="grid grid-cols-2 gap-3">
                <Input
                  label="Mínimo"
                  type="number"
                  value={formData.minimumBudget}
                  onChange={event => setFormData(prev => ({ ...prev, minimumBudget: Number(event.target.value) }))}
                  min={0}
                />
                <Input
                  label="Máximo"
                  type="number"
                  value={formData.maximumBudget}
                  onChange={event => setFormData(prev => ({ ...prev, maximumBudget: Number(event.target.value) }))}
                  min={0}
                />
              </div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Urgencia</label>
              <select
                value={formData.urgency}
                onChange={event => setFormData(prev => ({ ...prev, urgency: event.target.value as JobUrgency }))}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#F39C12] mb-4"
              >
                <option value="LOW">Baja</option>
                <option value="MEDIUM">Media</option>
                <option value="IMMEDIATE">Inmediata</option>
              </select>
              <Button type="submit" variant="primary" className="w-full" disabled={isPublishing}>
                {isPublishing ? 'Publicando...' : 'Publicar'}
              </Button>
            </form>
          </Card>

          <Card hover={false}>
            <h2 className="text-xl font-bold text-[#1A5276] mb-3">Acciones rápidas</h2>
            <div className="grid gap-3">
              <Link to="/registro/cliente">
                <Button variant="secondary" className="w-full">Actualizar datos</Button>
              </Link>
              <a href="#servicios">
                <Button variant="secondary" className="w-full">Ver servicios activos</Button>
              </a>
            </div>
          </Card>
        </aside>
      </div>
    </DashboardShell>
  );
};

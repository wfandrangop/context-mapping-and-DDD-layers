import { BriefcaseBusiness, CheckCircle2, Star, UsersRound } from 'lucide-react';
import { Card } from '@shared/ui/Card';
import { Button } from '@shared/ui/Button';
import type { JobApplicantSummary, JobPost } from '@features/job-marketplace/types/jobMarketplace.types';

interface JobPostListProps {
  title: string;
  jobs: JobPost[];
  emptyMessage: string;
  actionLabel?: string;
  onAction?: (job: JobPost) => void;
  applicantsByJobId?: Record<string, JobApplicantSummary[]>;
  onSelectApplicant?: (job: JobPost, applicant: JobApplicantSummary) => void;
  applicationStatusByJobId?: Record<string, string>;
}

const urgencyLabel: Record<string, string> = {
  LOW: 'Baja',
  MEDIUM: 'Media',
  IMMEDIATE: 'Inmediata',
};

const urgencyClass: Record<string, string> = {
  LOW: 'bg-green-50 text-green-700 border-green-200',
  MEDIUM: 'bg-yellow-50 text-yellow-700 border-yellow-200',
  IMMEDIATE: 'bg-red-50 text-red-700 border-red-200',
};

const applicationStatusContent: Record<string, { label: string; className: string }> = {
  POSTULADO: {
    label: 'Postulado',
    className: 'bg-blue-50 text-blue-700 border-blue-200',
  },
  EN_REVISION: {
    label: 'En revision',
    className: 'bg-yellow-50 text-yellow-700 border-yellow-200',
  },
  APROBADO: {
    label: 'Aprobado',
    className: 'bg-green-50 text-green-700 border-green-200',
  },
  RECHAZADO: {
    label: 'Rechazado',
    className: 'bg-red-50 text-red-700 border-red-200',
  },
};

export const JobPostList = ({
  title,
  jobs,
  emptyMessage,
  actionLabel,
  onAction,
  applicantsByJobId = {},
  onSelectApplicant,
  applicationStatusByJobId = {},
}: JobPostListProps) => (
  <Card hover={false}>
    <div className="flex items-center justify-between gap-4 mb-5">
      <h2 className="text-xl font-bold text-[#1A5276]">{title}</h2>
      <span className="text-sm text-gray-500">{jobs.length} items</span>
    </div>

    {jobs.length === 0 ? (
      <div className="rounded-lg border border-dashed border-gray-300 p-6 text-center text-gray-500">
        {emptyMessage}
      </div>
    ) : (
      <div className="grid gap-3">
        {jobs.map(job => {
          const applicants = applicantsByJobId[job.id] ?? [];
          const applicationStatus = applicationStatusByJobId[job.id];
          const statusContent = applicationStatus ? applicationStatusContent[applicationStatus] : null;

          return (
          <article key={job.id} className="rounded-lg border border-gray-100 bg-gray-50 p-4">
            <div className="flex flex-col md:flex-row md:items-start md:justify-between gap-4">
              <div className="min-w-0">
                <div className="flex items-center gap-2 text-[#1A5276] font-semibold">
                  <BriefcaseBusiness className="w-4 h-4" />
                  {job.technicalRequirements.join(', ') || 'Servicio solicitado'}
                </div>
                <p className="text-sm text-gray-500 mt-2">
                  Presupuesto: ${job.minimumBudget} - ${job.maximumBudget}
                </p>
                <div className="mt-3 flex flex-wrap gap-2">
                  <span className={`rounded-full border px-2.5 py-1 text-xs font-semibold ${urgencyClass[job.urgency] ?? 'bg-gray-50 text-gray-700 border-gray-200'}`}>
                    Urgencia {urgencyLabel[job.urgency] ?? job.urgency}
                  </span>
                  {job.technicalRequirements.map(requirement => (
                    <span key={requirement} className="rounded-full bg-white px-2.5 py-1 text-xs text-gray-600">
                      {requirement}
                    </span>
                  ))}
                  {statusContent && (
                    <span className={`rounded-full border px-2.5 py-1 text-xs font-semibold ${statusContent.className}`}>
                      {statusContent.label}
                    </span>
                  )}
                </div>
                <p className="text-sm text-gray-500 mt-1 flex items-center gap-1">
                  <UsersRound className="w-4 h-4" />
                  {job.applicantProfileIds.length} postulantes
                </p>
              </div>
              {actionLabel && onAction && (
                <Button variant="secondary" onClick={() => onAction(job)}>
                  {actionLabel}
                </Button>
              )}
            </div>

            {onSelectApplicant && (
              <div className="mt-4 border-t border-gray-200 pt-4">
                <p className="mb-3 text-sm font-semibold text-[#1A5276]">
                  Trabajadores postulados
                </p>
                {applicants.length === 0 ? (
                  <p className="rounded-lg border border-dashed border-gray-300 bg-white p-3 text-sm text-gray-500">
                    Aún no hay postulantes para este trabajo.
                  </p>
                ) : (
                  <div className="grid gap-3">
                    {applicants.map(applicant => (
                      <div
                        key={applicant.profileId}
                        className="grid gap-3 rounded-lg border border-gray-100 bg-white p-3 md:grid-cols-[1fr_auto] md:items-center"
                      >
                        <div>
                          <p className="font-semibold text-[#1A5276]">{applicant.fullName}</p>
                          <p className="text-sm text-gray-600">
                            {applicant.specialty || 'Especialidad no registrada'} ·{' '}
                            {applicant.yearsOfExperience ? `${applicant.yearsOfExperience} anos de experiencia` : 'Nuevo trabajador'}
                          </p>
                          <div className="mt-2 flex flex-wrap gap-2">
                            {applicant.categories.map(category => (
                              <span key={category} className="rounded-full bg-[#1A5276]/10 px-2 py-1 text-xs font-semibold text-[#1A5276]">
                                {category}
                              </span>
                            ))}
                            {applicant.technicalSkills.slice(0, 4).map(skill => (
                              <span key={skill} className="rounded-full bg-gray-100 px-2 py-1 text-xs text-gray-700">
                                {skill}
                              </span>
                            ))}
                          </div>
                          <div className="mt-2 flex flex-wrap gap-2 text-xs text-gray-500">
                            <span className="inline-flex items-center gap-1 rounded-full bg-green-50 px-2 py-1 text-green-700">
                              <CheckCircle2 className="h-3.5 w-3.5" />
                              {applicant.successfulJobs > 0 ? `${applicant.successfulJobs} trabajos completados` : 'Nuevo trabajador'}
                            </span>
                            <span className="inline-flex items-center gap-1 rounded-full bg-[#F39C12]/10 px-2 py-1 text-[#1A5276]">
                              <Star className="h-3.5 w-3.5" />
                              {applicant.averageRating ? `${applicant.averageRating.toFixed(1)} estrellas` : `Score ${applicant.score}`}
                            </span>
                            <span className="inline-flex items-center gap-1 rounded-full bg-blue-50 px-2 py-1 text-blue-700">
                              {applicant.badge}
                            </span>
                          </div>
                        </div>
                        <Button variant="primary" onClick={() => onSelectApplicant(job, applicant)}>
                          Seleccionar
                        </Button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}
          </article>
          );
        })}
      </div>
    )}
  </Card>
);

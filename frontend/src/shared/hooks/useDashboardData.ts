import { useCallback, useEffect, useState } from 'react';
import { jobMarketplaceService } from '@features/job-marketplace/services/jobMarketplaceService';
import { reputationService } from '@features/reputation/services/reputationService';
import { serviceExecutionService } from '@features/service-execution/services/serviceExecutionService';
import { workerProfileService } from '@features/worker-profile/services/workerProfileService';
import { sessionService } from '@shared/services/session/sessionService';
import type { JobApplicantSummary, JobPost } from '@features/job-marketplace/types/jobMarketplace.types';
import type { TradeReputation } from '@features/reputation/types/reputation.types';
import type { ServiceExecution } from '@features/service-execution/types/serviceExecution.types';

type ApplicationStatus = 'POSTULADO' | 'EN_REVISION' | 'APROBADO' | 'RECHAZADO';

const loadKnownJobs = async (ids: string[]) => {
  const jobs = await Promise.allSettled(ids.map(id => jobMarketplaceService.getById(id)));
  return jobs
    .filter((job): job is PromiseFulfilledResult<JobPost> => job.status === 'fulfilled')
    .map(job => job.value);
};

const loadKnownExecutions = async (ids: string[]) => {
  const executions = await Promise.allSettled(ids.map(id => serviceExecutionService.getById(id)));
  return executions
    .filter((execution): execution is PromiseFulfilledResult<ServiceExecution> => execution.status === 'fulfilled')
    .map(execution => sessionService.applyExecutionOverrides(execution.value));
};

export function useCustomerDashboardData(customerId: string | null) {
  const [publishedJobs, setPublishedJobs] = useState<JobPost[]>([]);
  const [openJobs, setOpenJobs] = useState<JobPost[]>([]);
  const [activeExecutions, setActiveExecutions] = useState<ServiceExecution[]>([]);
  const [applicantsByJobId, setApplicantsByJobId] = useState<Record<string, JobApplicantSummary[]>>({});
  const [isLoading, setIsLoading] = useState(true);

  const refresh = useCallback(async () => {
    if (!customerId) {
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    const [knownJobs, allOpenJobs, executions] = await Promise.all([
      loadKnownJobs(sessionService.getPublishedJobIds()),
      jobMarketplaceService.getOpen(),
      loadKnownExecutions(sessionService.getActiveExecutionIds()),
    ]);

    const visibleOpenJobs = allOpenJobs.filter(job => !job.selectedWorkerProfileId);
    const relatedOpenJobs = visibleOpenJobs.filter(job => job.clientId === customerId);
    const jobsById = new Map([...knownJobs, ...relatedOpenJobs].map(job => [job.id, job]));

    const nextPublishedJobs = Array.from(jobsById.values()).filter(job => !job.selectedWorkerProfileId);
    const applicantEntries = await Promise.all(
      nextPublishedJobs.map(async job => {
        const applicants = await Promise.all(
          job.applicantProfileIds.map(async profileId => {
            const profile = sessionService.getWorkerProfileSummary(profileId);
            const reputation = await reputationService.getByProfileId(profileId).catch(() => null);
            return {
              profileId,
              fullName: profile.fullName,
              specialty: profile.specialty,
              categories: profile.categories ?? [],
              technicalSkills: profile.technicalSkills ?? [],
              tools: profile.tools ?? [],
              yearsOfExperience: profile.yearsOfExperience ?? null,
              badge: reputation?.badges?.[0] ?? profile.badge ?? 'Nuevo trabajador',
              averageRating: profile.averageRating ?? null,
              successfulJobs: reputation?.successfulJobs ?? 0,
              score: reputation?.confidenceScore ?? 0,
            };
          })
        );
        return [job.id, applicants] as const;
      })
    );

    setPublishedJobs(nextPublishedJobs);
    setApplicantsByJobId(Object.fromEntries(applicantEntries));
    setOpenJobs(visibleOpenJobs);
    setActiveExecutions(executions.filter(execution => execution.clientId === customerId));
    setIsLoading(false);
  }, [customerId]);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  return { publishedJobs, openJobs, activeExecutions, applicantsByJobId, isLoading, refresh };
}

export function useWorkerDashboardData(profileId: string | null) {
  const [availableJobs, setAvailableJobs] = useState<JobPost[]>([]);
  const [recentJobs, setRecentJobs] = useState<JobPost[]>([]);
  const [applicationStatusByJobId, setApplicationStatusByJobId] = useState<Record<string, string>>({});
  const [activeExecutions, setActiveExecutions] = useState<ServiceExecution[]>([]);
  const [reputation, setReputation] = useState<TradeReputation | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const refresh = useCallback(async () => {
    if (!profileId) {
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    const [allOpenJobs, appliedJobs, executions, reputationData, profileData] = await Promise.all([
      jobMarketplaceService.getOpen(),
      loadKnownJobs(sessionService.getAppliedJobIds()),
      loadKnownExecutions(sessionService.getActiveExecutionIds()),
      reputationService.getByProfileId(profileId),
      workerProfileService.getMe().catch(() => null),
    ]);

    if (profileData) {
      const email = sessionService.getAuthEmail() ?? profileData.id;
      sessionService.rememberWorkerProfileFromBackend(email, profileData);
    }

    const visibleOpenJobs = allOpenJobs.filter(job => !job.selectedWorkerProfileId);
    const applicationJobs = [...appliedJobs, ...allOpenJobs.filter(job => job.applicantProfileIds.includes(profileId))]
      .filter((job, index, jobs) => jobs.findIndex(item => item.id === job.id) === index);
    const nextApplicationStatuses = Object.fromEntries(
      applicationJobs.map(job => {
        const localStatus = sessionService.getApplicationStatus(job.id, profileId);
        const inferredStatus =
          job.selectedWorkerProfileId === profileId
            ? 'APROBADO'
            : job.selectedWorkerProfileId
              ? 'RECHAZADO'
              : localStatus ?? 'EN_REVISION';

        if (inferredStatus !== localStatus) {
          sessionService.setApplicationStatus(job.id, profileId, inferredStatus as ApplicationStatus);
        }

        return [job.id, inferredStatus];
      })
    );

    setAvailableJobs(
      visibleOpenJobs.filter(job => !job.applicantProfileIds.includes(profileId))
    );
    setRecentJobs(applicationJobs);
    setApplicationStatusByJobId(nextApplicationStatuses);
    setActiveExecutions(executions.filter(execution => execution.workerId === profileId));
    setReputation(reputationData);
    setIsLoading(false);
  }, [profileId]);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  return { availableJobs, recentJobs, applicationStatusByJobId, activeExecutions, reputation, isLoading, refresh };
}

import type { AppRole, AuthResponse, BackendRole } from '@features/identity-access/types/auth.types';

export type { AppRole };

import type {
  ServiceExecution,
  ServiceExecutionEvidence,
  ServiceExecutionStatus,
} from '@features/service-execution/types/serviceExecution.types';
import type {
  WorkerProfileResponse,
  WorkerProfileSummary,
  WorkerReview,
} from '@features/worker-profile/types/workerProfile.types';

const ROLE_KEY = 'appRole';
const AUTH_TOKEN_KEY = 'authToken';
const AUTH_USER_ID_KEY = 'authUserId';
const AUTH_EMAIL_KEY = 'authEmail';
const AUTH_ROLES_KEY = 'authRoles';
const CUSTOMER_ID_KEY = 'customerId';
const CUSTOMER_NAME_KEY = 'customerName';
const PROFILE_ID_KEY = 'profileId';
const PROFILE_NAME_KEY = 'profileName';
const PUBLISHED_JOBS_KEY = 'publishedJobIds';
const ACTIVE_EXECUTIONS_KEY = 'activeExecutionIds';
const APPLIED_JOBS_KEY = 'appliedJobIds';
const APPLICATION_STATUSES_KEY = 'jobApplicationStatuses';
const EXECUTION_OVERRIDES_KEY = 'serviceExecutionOverrides';
const REGISTERED_PROFILES_KEY = 'registeredProfilesByEmail';
const WORKER_SUMMARIES_KEY = 'workerProfileSummaries';

const SPECIALTY_KEYWORDS: Array<{
  category: string;
  keywords: string[];
  skills: string[];
  tools: string[];
}> = [
  {
    category: 'Electricidad',
    keywords: ['electric', 'cable', 'tomacorriente', 'breaker', 'luminaria', 'instalacion electrica'],
    skills: ['Instalaciones electricas', 'Mantenimiento electrico', 'Revision de cableado'],
    tools: ['Multimetro', 'Alicate', 'Tester electrico'],
  },
  {
    category: 'Plomeria',
    keywords: ['plomer', 'tuberia', 'griferia', 'fuga', 'bano', 'lavabo', 'caneria'],
    skills: ['Reparacion de fugas', 'Instalacion de tuberias', 'Mantenimiento de banos'],
    tools: ['Llaves inglesas', 'Cortadora de tubos', 'Kit de reparacion'],
  },
  {
    category: 'Albanileria',
    keywords: ['albanil', 'cemento', 'pared', 'ladrillo', 'enlucido', 'concreto'],
    skills: ['Levantamiento de paredes', 'Reparacion de grietas', 'Enlucido'],
    tools: ['Palustre', 'Nivel', 'Mezcladora'],
  },
  {
    category: 'Pintura',
    keywords: ['pintur', 'brocha', 'rodillo', 'empaste', 'acabado'],
    skills: ['Pintura interior', 'Empaste', 'Acabados'],
    tools: ['Rodillo', 'Brochas', 'Bandeja de pintura'],
  },
  {
    category: 'Jardineria',
    keywords: ['jardin', 'cesped', 'poda', 'plantas', 'riego'],
    skills: ['Poda', 'Mantenimiento de jardines', 'Riego'],
    tools: ['Tijeras de poda', 'Cortacesped', 'Manguera'],
  },
  {
    category: 'Carpinteria',
    keywords: ['carpinter', 'madera', 'mueble', 'puerta', 'repisas'],
    skills: ['Reparacion de muebles', 'Instalacion de puertas', 'Trabajo en madera'],
    tools: ['Serrucho', 'Taladro', 'Lijadora'],
  },
  {
    category: 'Limpieza',
    keywords: ['limpieza', 'desinfeccion', 'aseo', 'profunda'],
    skills: ['Limpieza profunda', 'Desinfeccion', 'Organizacion de espacios'],
    tools: ['Aspiradora', 'Hidrolavadora', 'Kit de limpieza'],
  },
  {
    category: 'Ninero',
    keywords: ['nino', 'ninera', 'cuidado infantil', 'bebes', 'tareas escolares'],
    skills: ['Cuidado infantil', 'Apoyo en tareas', 'Rutinas de cuidado'],
    tools: ['Botiquin basico', 'Material didactico'],
  },
  {
    category: 'Mecanica',
    keywords: ['mecan', 'motor', 'vehiculo', 'aceite', 'frenos'],
    skills: ['Mantenimiento de vehiculos', 'Revision de frenos', 'Cambio de aceite'],
    tools: ['Gata hidraulica', 'Llaves mecanicas', 'Scanner basico'],
  },
  {
    category: 'Soldadura',
    keywords: ['soldadur', 'metal', 'estructura metalica', 'reja', 'electrodo'],
    skills: ['Soldadura metalica', 'Reparacion de rejas', 'Estructuras metalicas'],
    tools: ['Maquina de soldar', 'Careta', 'Electrodos'],
  },
];

const readJsonArray = (key: string, storage: Storage = sessionStorage): string[] => {
  try {
    const value = storage.getItem(key);
    return value ? JSON.parse(value) : [];
  } catch {
    return [];
  }
};

const writeJsonArray = (key: string, values: string[]) => {
  sessionStorage.setItem(key, JSON.stringify(values));
  localStorage.setItem(key, JSON.stringify(values));
};

const appendUnique = (key: string, value: string) => {
  const values = Array.from(new Set([
    ...readJsonArray(key),
    ...readJsonArray(key, localStorage),
  ]));
  if (!values.includes(value)) {
    writeJsonArray(key, [...values, value]);
  }
};

const readPersistedArray = (key: string): string[] => {
  const sessionValues = readJsonArray(key);
  const localValues = readJsonArray(key, localStorage);

  const values = Array.from(new Set([...sessionValues, ...localValues]));
  if (values.length > 0) {
    writeJsonArray(key, values);
  }
  return values;
};

const mergeUnique = (...groups: string[][]) =>
  Array.from(new Set(groups.flat()));

const scopedCollectionKey = (baseKey: string, role: AppRole, id: string) =>
  `${baseKey}:${role}:${id}`;

type ExecutionOverride = Partial<Pick<ServiceExecution, 'status' | 'clientRating' | 'clientComment'>> & {
  localEvidence?: ServiceExecutionEvidence[];
};

type ApplicationStatus = 'POSTULADO' | 'EN_REVISION' | 'APROBADO' | 'RECHAZADO';

const readExecutionOverrides = (): Record<string, ExecutionOverride> => {
  try {
    const sessionValue = sessionStorage.getItem(EXECUTION_OVERRIDES_KEY);
    const localValue = localStorage.getItem(EXECUTION_OVERRIDES_KEY);
    const sessionOverrides = sessionValue ? JSON.parse(sessionValue) : {};
    const localOverrides = localValue ? JSON.parse(localValue) : {};
    const overrides = {
      ...sessionOverrides,
      ...localOverrides,
    };
    const serialized = JSON.stringify(overrides);
    sessionStorage.setItem(EXECUTION_OVERRIDES_KEY, serialized);
    localStorage.setItem(EXECUTION_OVERRIDES_KEY, serialized);
    return overrides;
  } catch {
    return {};
  }
};

const writeExecutionOverride = (id: string, override: ExecutionOverride) => {
  const overrides = readExecutionOverrides();
  const next = JSON.stringify({
    ...overrides,
    [id]: {
      ...overrides[id],
      ...override,
    },
  });
  sessionStorage.setItem(EXECUTION_OVERRIDES_KEY, next);
  localStorage.setItem(EXECUTION_OVERRIDES_KEY, next);
};

const applicationStatusKey = (jobId: string, profileId: string) => `${jobId}:${profileId}`;

const readApplicationStatuses = (): Record<string, ApplicationStatus> => {
  try {
    const value =
      sessionStorage.getItem(APPLICATION_STATUSES_KEY) ??
      localStorage.getItem(APPLICATION_STATUSES_KEY);
    return value ? JSON.parse(value) : {};
  } catch {
    return {};
  }
};

const writeApplicationStatus = (
  jobId: string,
  profileId: string,
  status: ApplicationStatus
) => {
  const statuses = readApplicationStatuses();
  const next = JSON.stringify({
    ...statuses,
    [applicationStatusKey(jobId, profileId)]: status,
  });
  sessionStorage.setItem(APPLICATION_STATUSES_KEY, next);
  localStorage.setItem(APPLICATION_STATUSES_KEY, next);
};

const toAppRole = (role: BackendRole | string): AppRole =>
  role === 'WORKER' ? 'worker' : 'customer';

type RegisteredProfile = {
  role: AppRole;
  id: string;
  name: string;
};

const normalizeEmail = (email: string) => email.trim().toLowerCase();

const readRegisteredProfiles = (): Record<string, RegisteredProfile> => {
  try {
    const value = localStorage.getItem(REGISTERED_PROFILES_KEY);
    return value ? JSON.parse(value) : {};
  } catch {
    return {};
  }
};

const writeRegisteredProfile = (
  email: string,
  profile: RegisteredProfile
) => {
  const profiles = readRegisteredProfiles();
  localStorage.setItem(
    REGISTERED_PROFILES_KEY,
    JSON.stringify({
      ...profiles,
      [normalizeEmail(email)]: profile,
    })
  );
};

const readWorkerSummaries = (): Record<string, WorkerProfileSummary> => {
  try {
    const value = localStorage.getItem(WORKER_SUMMARIES_KEY);
    return value ? JSON.parse(value) : {};
  } catch {
    return {};
  }
};

const writeWorkerSummary = (summary: WorkerProfileSummary) => {
  const summaries = readWorkerSummaries();
  localStorage.setItem(
    WORKER_SUMMARIES_KEY,
    JSON.stringify({
      ...summaries,
      [summary.profileId]: summary,
    })
  );
};

const updateWorkerSummary = (
  profileId: string,
  updater: (summary: WorkerProfileSummary) => WorkerProfileSummary
) => {
  const current = readWorkerSummaries()[profileId] ?? {
    profileId,
    fullName: `Trabajador ${profileId.slice(0, 8)}`,
    specialty: 'Especialidad no registrada',
    categories: [],
    technicalSkills: [],
    tools: [],
    yearsOfExperience: null,
    badge: 'Nuevo trabajador',
    averageRating: null,
    reviewCount: 0,
    reviews: [],
  };
  writeWorkerSummary(updater(current));
};

const normalizeReviews = (reviews: WorkerReview[] = []) =>
  reviews.filter(review => review.comment.trim());

const inferSpecialty = (experienceDescription: string) => {
  const clean = experienceDescription.trim();
  if (!clean) return 'Especialidad no registrada';

  const firstPhrase = clean.split(/[,.]/)[0]?.trim() ?? clean;
  return firstPhrase.length > 64 ? `${firstPhrase.slice(0, 61)}...` : firstPhrase;
};

const unique = (values: string[]) => Array.from(new Set(values.filter(Boolean)));

const inferYearsOfExperience = (description: string): number | null => {
  const match = description.match(/(\d+)\s*(?:anos|a\u00f1os|anios|years)/i);
  return match ? Number(match[1]) : null;
};

const inferToolsFromProfileTerms = (terms: string[]) => {
  const normalized = terms
    .join(' ')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase();
  const matched = SPECIALTY_KEYWORDS.filter(item =>
    item.keywords.some(keyword => normalized.includes(keyword))
  );
  return unique(matched.flatMap(item => item.tools)).slice(0, 6);
};

const inferWorkerSummary = (
  id: string,
  fullName: string,
  experienceDescription: string,
  selectedCategories: string[] = []
): WorkerProfileSummary => {
  const normalized = experienceDescription
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase();
  const matched = SPECIALTY_KEYWORDS.filter(item =>
    item.keywords.some(keyword => normalized.includes(keyword))
  );
  const categories = unique([
    ...selectedCategories,
    ...matched.map(item => item.category),
  ]);
  const technicalSkills = unique(matched.flatMap(item => item.skills)).slice(0, 8);
  const tools = unique(matched.flatMap(item => item.tools)).slice(0, 6);

  return {
    profileId: id,
    fullName,
    specialty: categories[0] ?? inferSpecialty(experienceDescription),
    professionalDescription: experienceDescription.trim(),
    categories,
    technicalSkills,
    tools,
    yearsOfExperience: inferYearsOfExperience(normalized),
    badge: categories.length > 0 ? 'Perfil verificado' : 'Nuevo trabajador',
    averageRating: null,
    reviewCount: 0,
    reviews: [],
  };
};

export const sessionService = {
  setAuth: (auth: AuthResponse) => {
    sessionStorage.setItem(AUTH_TOKEN_KEY, auth.token);
    sessionStorage.setItem(AUTH_USER_ID_KEY, auth.userId);
    sessionStorage.setItem(AUTH_EMAIL_KEY, auth.email);
    sessionStorage.setItem(AUTH_ROLES_KEY, JSON.stringify(auth.roles));

    if (auth.roles[0]) {
      sessionStorage.setItem(ROLE_KEY, toAppRole(auth.roles[0]));
    }
  },
  getToken: () => sessionStorage.getItem(AUTH_TOKEN_KEY),
  getAuthUserId: () => sessionStorage.getItem(AUTH_USER_ID_KEY),
  getAuthEmail: () => sessionStorage.getItem(AUTH_EMAIL_KEY),
  getAuthRoles: (): BackendRole[] => {
    try {
      const roles = sessionStorage.getItem(AUTH_ROLES_KEY);
      return roles ? JSON.parse(roles) : [];
    } catch {
      return [];
    }
  },
  isAuthenticated: () => Boolean(sessionStorage.getItem(AUTH_TOKEN_KEY)),
  rememberCustomerProfile: (email: string, id: string, name: string) => {
    writeRegisteredProfile(email, { role: 'customer', id, name });
  },
  getRegisteredProfileNameById: (id: string) =>
    Object.values(readRegisteredProfiles()).find(profile => profile.id === id)?.name ?? null,
  rememberWorkerProfile: (email: string, id: string, name: string) => {
    writeRegisteredProfile(email, { role: 'worker', id, name });
    const current = readWorkerSummaries()[id];
    writeWorkerSummary({
      ...current,
      profileId: id,
      fullName: name,
      specialty: current?.specialty ?? 'Especialidad no registrada',
    });
  },
  rememberWorkerProfileFromBackend: (email: string, profile: WorkerProfileResponse) => {
    writeRegisteredProfile(email, { role: 'worker', id: profile.id, name: profile.fullName });
    const current = readWorkerSummaries()[profile.id];
    const occupations = profile.occupations ?? [];
    const technicalSkills = profile.technicalSkills ?? [];
    const specialty = occupations[0] ?? current?.specialty ?? 'Especialidad no registrada';
    const inferredTools = inferToolsFromProfileTerms([...occupations, ...technicalSkills]);

    writeWorkerSummary({
      ...current,
      profileId: profile.id,
      fullName: profile.fullName,
      specialty,
      categories: occupations.length > 0 ? occupations : current?.categories ?? [],
      technicalSkills: technicalSkills.length > 0 ? technicalSkills : current?.technicalSkills ?? [],
      tools: current?.tools?.length ? current.tools : inferredTools,
      professionalDescription:
        current?.professionalDescription ??
        (occupations.length > 0
          ? `Perfil profesional en ${occupations.join(', ')}.`
          : undefined),
      badge: current?.badge ?? (profile.reputationScore > 0 ? 'Perfil verificado' : 'Nuevo trabajador'),
    });
  },
  rememberWorkerProfileDetails: (
    id: string,
    fullName: string,
    experienceDescription: string,
    selectedCategories: string[] = []
  ) => {
    writeWorkerSummary(inferWorkerSummary(id, fullName, experienceDescription, selectedCategories));
  },
  restoreProfileForAuth: (auth: AuthResponse) => {
    const remembered = readRegisteredProfiles()[normalizeEmail(auth.email)];
    if (!remembered) return null;

    if (remembered.role === 'customer' && auth.roles.includes('CUSTOMER')) {
      sessionStorage.setItem(CUSTOMER_ID_KEY, remembered.id);
      sessionStorage.setItem(CUSTOMER_NAME_KEY, remembered.name);
      sessionStorage.setItem(ROLE_KEY, 'customer');
      return remembered;
    }

    if (remembered.role === 'worker' && auth.roles.includes('WORKER')) {
      sessionStorage.setItem(PROFILE_ID_KEY, remembered.id);
      sessionStorage.setItem(PROFILE_NAME_KEY, remembered.name);
      sessionStorage.setItem(ROLE_KEY, 'worker');
      return remembered;
    }

    return null;
  },
  ensureProfileFallbackForAuth: (auth: AuthResponse) => {
    if (auth.roles.includes('WORKER') && !sessionStorage.getItem(PROFILE_ID_KEY)) {
      sessionStorage.setItem(PROFILE_ID_KEY, auth.userId);
      sessionStorage.setItem(PROFILE_NAME_KEY, auth.email);
      sessionStorage.setItem(ROLE_KEY, 'worker');
    }

    if (auth.roles.includes('CUSTOMER') && !sessionStorage.getItem(CUSTOMER_ID_KEY)) {
      sessionStorage.setItem(CUSTOMER_ID_KEY, auth.userId);
      sessionStorage.setItem(CUSTOMER_NAME_KEY, auth.email);
      sessionStorage.setItem(ROLE_KEY, 'customer');
    }
  },
  clear: () => {
    [
      AUTH_TOKEN_KEY,
      AUTH_USER_ID_KEY,
      AUTH_EMAIL_KEY,
      AUTH_ROLES_KEY,
      ROLE_KEY,
      CUSTOMER_ID_KEY,
      CUSTOMER_NAME_KEY,
      PROFILE_ID_KEY,
      PROFILE_NAME_KEY,
      PUBLISHED_JOBS_KEY,
      ACTIVE_EXECUTIONS_KEY,
      APPLIED_JOBS_KEY,
    ].forEach(key => {
      sessionStorage.removeItem(key);
    });
  },

  setRole: (role: AppRole) => sessionStorage.setItem(ROLE_KEY, role),
  getRole: (): AppRole | null => sessionStorage.getItem(ROLE_KEY) as AppRole | null,

  setCustomer: (id: string, name: string) => {
    sessionStorage.setItem(CUSTOMER_ID_KEY, id);
    sessionStorage.setItem(CUSTOMER_NAME_KEY, name);
    sessionStorage.setItem(ROLE_KEY, 'customer');
  },
  getCustomerId: () => sessionStorage.getItem(CUSTOMER_ID_KEY),
  getCustomerName: () => sessionStorage.getItem(CUSTOMER_NAME_KEY),

  setWorkerProfile: (id: string, name?: string) => {
    sessionStorage.setItem(PROFILE_ID_KEY, id);
    if (name) sessionStorage.setItem(PROFILE_NAME_KEY, name);
    sessionStorage.setItem(ROLE_KEY, 'worker');
  },
  getWorkerProfileId: () => sessionStorage.getItem(PROFILE_ID_KEY),
  getWorkerName: () => sessionStorage.getItem(PROFILE_NAME_KEY),

  addPublishedJobId: (id: string) => appendUnique(PUBLISHED_JOBS_KEY, id),
  getPublishedJobIds: () => {
    const customerId = sessionStorage.getItem(CUSTOMER_ID_KEY);
    if (customerId) {
      return mergeUnique(
        readPersistedArray(scopedCollectionKey(PUBLISHED_JOBS_KEY, 'customer', customerId)),
        readPersistedArray(PUBLISHED_JOBS_KEY)
      );
    }
    return readPersistedArray(PUBLISHED_JOBS_KEY);
  },

  addActiveExecutionId: (id: string) => appendUnique(ACTIVE_EXECUTIONS_KEY, id),
  addExecutionForParticipants: (id: string, clientId: string, workerId: string) => {
    appendUnique(ACTIVE_EXECUTIONS_KEY, id);
    appendUnique(scopedCollectionKey(ACTIVE_EXECUTIONS_KEY, 'customer', clientId), id);
    appendUnique(scopedCollectionKey(ACTIVE_EXECUTIONS_KEY, 'worker', workerId), id);
  },
  getActiveExecutionIds: () => {
    const role = sessionStorage.getItem(ROLE_KEY) as AppRole | null;
    const id =
      role === 'customer'
        ? sessionStorage.getItem(CUSTOMER_ID_KEY)
        : sessionStorage.getItem(PROFILE_ID_KEY);

    if (role && id) {
      return mergeUnique(
        readPersistedArray(scopedCollectionKey(ACTIVE_EXECUTIONS_KEY, role, id)),
        readPersistedArray(ACTIVE_EXECUTIONS_KEY)
      );
    }

    return readPersistedArray(ACTIVE_EXECUTIONS_KEY);
  },

  addAppliedJobId: (id: string) => appendUnique(APPLIED_JOBS_KEY, id),
  getAppliedJobIds: () => {
    const profileId = sessionStorage.getItem(PROFILE_ID_KEY);
    if (profileId) {
      return mergeUnique(
        readPersistedArray(scopedCollectionKey(APPLIED_JOBS_KEY, 'worker', profileId)),
        readPersistedArray(APPLIED_JOBS_KEY)
      );
    }
    return readPersistedArray(APPLIED_JOBS_KEY);
  },

  addScopedPublishedJobId: (id: string, customerId: string) => {
    appendUnique(scopedCollectionKey(PUBLISHED_JOBS_KEY, 'customer', customerId), id);
  },

  addScopedAppliedJobId: (id: string, profileId: string) => {
    appendUnique(scopedCollectionKey(APPLIED_JOBS_KEY, 'worker', profileId), id);
    writeApplicationStatus(id, profileId, 'POSTULADO');
  },

  setApplicationStatus: writeApplicationStatus,

  getApplicationStatus: (jobId: string, profileId: string): ApplicationStatus | null =>
    readApplicationStatuses()[applicationStatusKey(jobId, profileId)] ?? null,

  getApplicationStatusesForProfile: (profileId: string) => {
    const statuses = readApplicationStatuses();
    return Object.fromEntries(
      Object.entries(statuses)
        .filter(([key]) => key.endsWith(`:${profileId}`))
        .map(([key, status]) => [key.replace(`:${profileId}`, ''), status])
    ) as Record<string, ApplicationStatus>;
  },

  getWorkerProfileSummary: (profileId: string): WorkerProfileSummary => {
    const summary = readWorkerSummaries()[profileId];
    return (
      summary ?? {
        profileId,
        fullName: `Trabajador ${profileId.slice(0, 8)}`,
        specialty: 'Especialidad no registrada',
        categories: [],
        technicalSkills: [],
        tools: [],
        yearsOfExperience: null,
        badge: 'Nuevo trabajador',
        averageRating: null,
        reviewCount: 0,
        reviews: [],
      }
    );
  },

  recordWorkerReview: (
    profileId: string,
    rating: number,
    comment: string,
    executionId: string
  ) => {
    updateWorkerSummary(profileId, summary => {
      const previousReviews = normalizeReviews(summary.reviews);
      const reviews = [
        {
          executionId,
          rating,
          comment: comment.trim(),
          reviewedAt: new Date().toISOString(),
        },
        ...previousReviews.filter(review => review.executionId !== executionId),
      ];
      const averageRating =
        reviews.reduce((total, review) => total + review.rating, 0) / reviews.length;

      return {
        ...summary,
        averageRating: Number(averageRating.toFixed(2)),
        reviewCount: reviews.length,
        reviews,
        badge: reviews.length > 0 ? summary.badge ?? 'Perfil verificado' : 'Nuevo trabajador',
      };
    });
  },

  applyExecutionOverrides: (execution: ServiceExecution): ServiceExecution => {
    const override = readExecutionOverrides()[execution.id];
    if (!override) return execution;

    return {
      ...execution,
      ...override,
      photoUrls: execution.photoUrls,
      localEvidence: override.localEvidence ?? execution.localEvidence,
    };
  },

  setExecutionStatus: (id: string, status: ServiceExecutionStatus) => {
    writeExecutionOverride(id, { status });
  },

  addExecutionEvidence: (id: string, evidence: ServiceExecutionEvidence) => {
    const current = readExecutionOverrides()[id]?.localEvidence ?? [];
    writeExecutionOverride(id, { localEvidence: [...current, evidence] });
  },

  setExecutionFeedback: (id: string, clientRating: number, clientComment: string) => {
    appendUnique(ACTIVE_EXECUTIONS_KEY, id);
    writeExecutionOverride(id, { clientRating, clientComment, status: 'VALIDATED' });
  },
};

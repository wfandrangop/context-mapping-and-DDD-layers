package com.veritrabajo.backend.jobmarketplace.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veritrabajo.backend.jobmarketplace.domain.exception.JobPostNotFoundException;
import com.veritrabajo.backend.jobmarketplace.domain.model.EstimatedBudget;
import com.veritrabajo.backend.jobmarketplace.domain.model.JobApplication;
import com.veritrabajo.backend.jobmarketplace.domain.model.JobPost;
import com.veritrabajo.backend.jobmarketplace.domain.model.JobPostCreation;
import com.veritrabajo.backend.jobmarketplace.domain.model.JobPostData;
import com.veritrabajo.backend.jobmarketplace.domain.model.TechnicalRequirements;
import com.veritrabajo.backend.jobmarketplace.domain.model.Urgency;
import com.veritrabajo.backend.shared.api.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class JobMarketplaceQueryControllerWebTest {

    private static final int NOT_FOUND_STATUS = 404;
    private static final int MIN_BUDGET = 100;
    private static final int MAX_BUDGET = 500;
    private static final String CLIENT_ID = "client-1";
    private static final Instant JOB_CREATED_AT = Instant.parse("2026-01-01T12:00:00Z");

    @Mock
    private JobMarketplaceApplicationService applicationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UUID jobPostId;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new JobMarketplaceQueryController(applicationService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
        this.jobPostId = UUID.randomUUID();
    }

    @Test
    void publishDemandShouldReturnCreatedWithBody() throws Exception {
        stubPublishReturnsOpenJobPost();
        ResultActions response = postValidCreateJobPost();
        assertCreatedJobPostPayload(response);
        assertCapturedCreationMatchesRequest();
    }

    @Test
    void getDemandShouldReturnOkWhenFound() throws Exception {
        JobPost post = openJobPost(jobPostId);
        when(applicationService.getById(jobPostId)).thenReturn(post);

        mockMvc.perform(get("/api/job-posts/{id}", jobPostId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(jobPostId.toString()))
                .andExpect(jsonPath("$.clientId").value(CLIENT_ID));
    }

    @Test
    void getDemandShouldReturnNotFoundWhenMissing() throws Exception {
        when(applicationService.getById(jobPostId))
                .thenThrow(new JobPostNotFoundException(jobPostId));

        mockMvc.perform(get("/api/job-posts/{id}", jobPostId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(NOT_FOUND_STATUS))
                .andExpect(jsonPath("$.message").value("Job demand not found: " + jobPostId))
                .andExpect(jsonPath("$.path").value("/api/job-posts/" + jobPostId));
    }

    @Test
    void openDemandsShouldReturnList() throws Exception {
        JobPost post = openJobPost(jobPostId);
        when(applicationService.getOpenDemands()).thenReturn(List.of(post));

        mockMvc.perform(get("/api/job-posts/open"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(jobPostId.toString()))
                .andExpect(jsonPath("$[0].urgency").value("MEDIUM"));
    }

    @Test
    void applyShouldReturnUpdatedJobPost() throws Exception {
        JobApplication application = sampleApplication("worker-1");
        JobPost updated = jobPostWithApplicants(jobPostId, List.of(application));
        when(applicationService.apply(eq(jobPostId), eq("worker-1"))).thenReturn(updated);

        String body = objectMapper.writeValueAsString(new ApplyJobPostRequest("worker-1"));

        mockMvc.perform(post("/api/job-posts/{id}/applications", jobPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicantProfileIds[0]").value("worker-1"));
    }

    @Test
    void selectEmployeeWithExplicitWorkerShouldReturnOk() throws Exception {
        JobApplication application = sampleApplication("worker-1");
        JobPost selected = jobPostWithSelection(jobPostId, List.of(application), "worker-1");
        when(applicationService.select(eq(jobPostId), eq("worker-1"))).thenReturn(selected);

        String body = objectMapper.writeValueAsString(new SelectEmployeeRequest("worker-1"));

        mockMvc.perform(post("/api/job-posts/{id}/select", jobPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selectedWorkerProfileId").value("worker-1"));
    }

    @Test
    void selectEmployeeWithoutBodyShouldDelegateWithNullWorkerId() throws Exception {
        JobApplication application = sampleApplication("worker-2");
        JobPost selected = jobPostWithSelection(jobPostId, List.of(application), "worker-2");
        when(applicationService.select(eq(jobPostId), eq(null))).thenReturn(selected);

        mockMvc.perform(post("/api/job-posts/{id}/select", jobPostId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selectedWorkerProfileId").value("worker-2"));

        verify(applicationService).select(eq(jobPostId), eq(null));
    }

    private void stubPublishReturnsOpenJobPost() {
        JobPost created = jobPostDraft(jobPostId, List.of("java", "spring"));
        when(applicationService.publishDemand(any(JobPostCreation.class))).thenReturn(created);
    }

    private ResultActions postValidCreateJobPost() throws Exception {
        String body = sampleCreateJobPostJson();
        return mockMvc.perform(post("/api/job-posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private void assertCreatedJobPostPayload(ResultActions response) throws Exception {
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(jobPostId.toString()))
                .andExpect(jsonPath("$.clientId").value(CLIENT_ID))
                .andExpect(jsonPath("$.technicalRequirements[0]").value("java"))
                .andExpect(jsonPath("$.technicalRequirements[1]").value("spring"))
                .andExpect(jsonPath("$.minimumBudget").value(MIN_BUDGET))
                .andExpect(jsonPath("$.maximumBudget").value(MAX_BUDGET))
                .andExpect(jsonPath("$.urgency").value("MEDIUM"))
                .andExpect(jsonPath("$.applicantProfileIds").isEmpty())
                .andExpect(jsonPath("$.selectedWorkerProfileId").value(nullValue()));
    }

    private void assertCapturedCreationMatchesRequest() {
        ArgumentCaptor<JobPostCreation> captor = ArgumentCaptor.forClass(JobPostCreation.class);
        verify(applicationService).publishDemand(captor.capture());
        JobPostCreation creation = captor.getValue();
        assertThat(creation.clientId()).isEqualTo(CLIENT_ID);
        assertThat(creation.technicalRequirements().skills()).containsExactly("java", "spring");
        assertThat(creation.estimatedBudget().minimum())
                .isEqualByComparingTo(String.valueOf(MIN_BUDGET));
        assertThat(creation.estimatedBudget().maximum())
                .isEqualByComparingTo(String.valueOf(MAX_BUDGET));
        assertThat(creation.urgency()).isEqualTo(Urgency.MEDIUM);
    }

    private String sampleCreateJobPostJson() {
        return """
                {
                  "clientId": "%s",
                  "technicalRequirements": ["java", "spring"],
                  "minimumBudget": %d,
                  "maximumBudget": %d,
                  "urgency": "MEDIUM"
                }
                """.formatted(CLIENT_ID, MIN_BUDGET, MAX_BUDGET);
    }

    private static JobApplication sampleApplication(String workerProfileId) {
        UUID appId = UUID.randomUUID();
        Instant appliedAt = Instant.parse("2026-01-15T10:00:00Z");
        return JobApplication.rehydrate(appId, workerProfileId, appliedAt);
    }

    private static EstimatedBudget sampleBudget() {
        return new EstimatedBudget(
                BigDecimal.valueOf(MIN_BUDGET),
                BigDecimal.valueOf(MAX_BUDGET));
    }

    private JobPost openJobPost(UUID id) {
        return jobPostDraft(id, List.of("java"));
    }

    private JobPost jobPostDraft(UUID id, List<String> skills) {
        return JobPost.rehydrate(new JobPostData(
                id,
                CLIENT_ID,
                TechnicalRequirements.of(skills),
                sampleBudget(),
                Urgency.MEDIUM,
                JOB_CREATED_AT,
                List.of(),
                null
        ));
    }

    private JobPost jobPostWithApplicants(UUID id, List<JobApplication> applications) {
        return JobPost.rehydrate(new JobPostData(
                id,
                CLIENT_ID,
                TechnicalRequirements.of(List.of("java")),
                sampleBudget(),
                Urgency.MEDIUM,
                JOB_CREATED_AT,
                applications,
                null
        ));
    }

    private JobPost jobPostWithSelection(
            UUID id,
            List<JobApplication> applications,
            String selectedWorkerId
    ) {
        return JobPost.rehydrate(new JobPostData(
                id,
                CLIENT_ID,
                TechnicalRequirements.of(List.of("java")),
                sampleBudget(),
                Urgency.MEDIUM,
                JOB_CREATED_AT,
                applications,
                selectedWorkerId
        ));
    }
}

package com.veritrabajo.backend.workerprofile.infrastructure.acl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veritrabajo.backend.workerprofile.domain.model.AnalysisResult;
import com.veritrabajo.backend.workerprofile.domain.model.Occupation;
import com.veritrabajo.backend.workerprofile.domain.model.Occupation.ExpertiseLevel;
import com.veritrabajo.backend.workerprofile.domain.model.RawDescription;
import com.veritrabajo.backend.workerprofile.domain.model.TechnicalSkill;
import com.veritrabajo.backend.workerprofile.domain.service.IAAnalysisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Anti-corruption layer for {@link IAAnalysisService} using Groq LLM.
 * Maps the remote JSON payload into domain objects without leaking infra types upward.
 */
@Component
public class IAAnalysisServiceAdapter implements IAAnalysisService {

    private static final String DEFAULT_BASE_URL = "https://api.groq.com/openai";
    private static final String DEFAULT_MODEL = "llama3-70b-8192";

    private static final String ANALYSIS_PROMPT =
            "Analyze the following work experience text and respond ONLY with valid JSON "
                    + "using exactly this shape:\n"
                    + "{\n"
                    + "  \"occupations\": [{\"tradeName\": \"string\", "
                    + "\"level\": \"BEGINNER|INTERMEDIATE|ADVANCED|EXPERT\"}],\n"
                    + "  \"technicalSkills\": [\"string\"]\n"
                    + "}\n"
                    + "Text to analyze:\n";

    private final String apiKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public IAAnalysisServiceAdapter(
            @Value("${spring.ai.openai.api-key:NOT_CONFIGURED}") String apiKey,
            RestClient.Builder restClientBuilder
    ) {
        this.apiKey = apiKey;
        this.restClient = restClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public AnalysisResult analyze(RawDescription description) {
        try {
            String prompt = buildPrompt(description.getText());
            String modelResponse = callGroq(prompt);
            return parseModelResponse(modelResponse);
        } catch (Exception exception) {
            // External AI can fail (quota, connectivity, provider errors).
            // keep registration flow alive with empty AI enrichment.
            return AnalysisResult.of(new ArrayList<>(), new ArrayList<>());
        }
    }

    private String buildPrompt(String workerText) {
        return ANALYSIS_PROMPT + workerText;
    }

    private String callGroq(String prompt) {
        String requestBody = "{"
                + "\"model\":\"" + escapeJson(DEFAULT_MODEL) + "\","
                + "\"messages\":["
                + "{\"role\":\"system\",\"content\":\"You are an information extraction assistant. "
                + "Always return only valid JSON with no markdown.\"},"
                + "{\"role\":\"user\",\"content\":\"" + escapeJson(prompt) + "\"}"
                + "]"
                + "}";

        String rawResponse = restClient.post()
                .uri(DEFAULT_BASE_URL + "/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return extractChatContent(rawResponse);
    }

    private AnalysisResult parseModelResponse(String modelResponse) {
        try {
            JsonNode parsed = extractJson(modelResponse);
            List<Occupation> occupations = extractOccupations(parsed);
            List<TechnicalSkill> skills = extractSkills(parsed);
            return AnalysisResult.of(occupations, skills);
        } catch (Exception e) {
            return AnalysisResult.of(new ArrayList<>(), new ArrayList<>());
        }
    }

    private JsonNode extractJson(String modelResponse) throws Exception {
        String cleanJson = modelResponse
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();
        return objectMapper.readTree(cleanJson);
    }

    private String extractChatContent(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            return root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception exception) {
            return "";
        }
    }

    private List<Occupation> extractOccupations(JsonNode parsed) {
        List<Occupation> occupations = new ArrayList<>();
        JsonNode occupationsNode = parsed.path("occupations");

        for (JsonNode node : occupationsNode) {
            String tradeName = node.path("tradeName").asText();
            String levelStr = node.path("level").asText("INTERMEDIATE");
            ExpertiseLevel level = parseLevel(levelStr);

            if (!tradeName.isBlank()) {
                occupations.add(Occupation.of(tradeName, level));
            }
        }
        return occupations;
    }

    private List<TechnicalSkill> extractSkills(JsonNode parsed) {
        List<TechnicalSkill> skills = new ArrayList<>();
        JsonNode skillsNode = parsed.path("technicalSkills");

        for (JsonNode node : skillsNode) {
            String skillName = node.asText();
            if (!skillName.isBlank()) {
                skills.add(TechnicalSkill.of(skillName));
            }
        }
        return skills;
    }

    /**
     * Parses Gemini level strings; unknown values fall back to {@link ExpertiseLevel#INTERMEDIATE}.
     */
    private ExpertiseLevel parseLevel(String levelStr) {
        try {
            return ExpertiseLevel.valueOf(levelStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ExpertiseLevel.INTERMEDIATE;
        }
    }

    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

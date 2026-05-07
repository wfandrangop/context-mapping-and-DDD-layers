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
 * Anti-corruption layer for {@link IAAnalysisService} using Google Gemini.
 * Maps the remote JSON payload into domain objects without leaking infra types upward.
 */
@Component
public class IAAnalysisServiceAdapter implements IAAnalysisService {

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
                    + "gemini-2.0-flash:generateContent";

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
            @Value("${gemini.api.key:NOT_CONFIGURED}") String apiKey,
            RestClient.Builder restClientBuilder
    ) {
        this.apiKey = apiKey;
        this.restClient = restClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public AnalysisResult analyze(RawDescription description) {
        validateApiKey();

        String requestBody = buildRequestBody(description.getText());
        String rawResponse = callGeminiApi(requestBody);

        return parseGeminiResponse(rawResponse);
    }

    private void validateApiKey() {
        if ("NOT_CONFIGURED".equals(apiKey) || apiKey.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Gemini API key is not configured. "
                            + "Set 'gemini.api.key=YOUR_KEY' in application.properties"
            );
        }
    }

    private String buildRequestBody(String workerText) {
        String promptText = ANALYSIS_PROMPT + workerText;
        return "{"
                + "\"contents\": [{"
                + "\"parts\": [{\"text\": \""
                + escapeJson(promptText)
                + "\"}]"
                + "}]"
                + "}";
    }

    private String callGeminiApi(String requestBody) {
        return restClient.post()
                .uri(GEMINI_URL + "?key=" + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(String.class);
    }

    private AnalysisResult parseGeminiResponse(String rawResponse) {
        try {
            JsonNode parsed = extractInnerJson(rawResponse);
            List<Occupation> occupations = extractOccupations(parsed);
            List<TechnicalSkill> skills = extractSkills(parsed);
            return AnalysisResult.of(occupations, skills);
        } catch (Exception e) {
            return AnalysisResult.of(new ArrayList<>(), new ArrayList<>());
        }
    }

    private JsonNode extractInnerJson(String rawResponse) throws Exception {
        JsonNode root = objectMapper.readTree(rawResponse);
        String jsonText = root
                .path("candidates").get(0)
                .path("content")
                .path("parts").get(0)
                .path("text")
                .asText();
        String cleanJson = jsonText
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();
        return objectMapper.readTree(cleanJson);
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

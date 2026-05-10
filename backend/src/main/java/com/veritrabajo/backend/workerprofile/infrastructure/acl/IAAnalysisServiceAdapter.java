package com.veritrabajo.backend.workerprofile.infrastructure.acl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.veritrabajo.backend.workerprofile.domain.model.AnalysisResult;
import com.veritrabajo.backend.workerprofile.domain.model.Occupation;
import com.veritrabajo.backend.workerprofile.domain.model.Occupation.ExpertiseLevel;
import com.veritrabajo.backend.workerprofile.domain.model.RawDescription;
import com.veritrabajo.backend.workerprofile.domain.model.TechnicalSkill;
import com.veritrabajo.backend.workerprofile.domain.service.IAAnalysisService;
import org.springframework.http.MediaType;
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

    private static final String SYSTEM_PROMPT =
            "You are an information extraction assistant. " +
                    "Always return only valid JSON with no markdown.";
    private static final String ANALYSIS_PROMPT = """
            Analyze the following work experience text and 
            respond ONLY with valid JSON using exactly this shape:
            {
              "occupations": [{"tradeName": "string", "level": 
              "BEGINNER|INTERMEDIATE|ADVANCED|EXPERT"}],
              "technicalSkills": ["string"]
            }
            Text to analyze:
            """;

    private final String model;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public IAAnalysisServiceAdapter(GroqProperties groqProperties, RestClient groqRestClient) {
        this.model = groqProperties.model();
        this.restClient = groqRestClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public AnalysisResult analyze(RawDescription description) {
        try {
            String modelResponse = callGroq(ANALYSIS_PROMPT + description.getText());
            return parseModelResponse(modelResponse);
        } catch (Exception e) {
            return AnalysisResult.of(new ArrayList<>(), new ArrayList<>());
        }
    }

    private String callGroq(String prompt) throws Exception {
        String requestBody = buildRequestBody(prompt);

        String rawResponse = restClient.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return extractChatContent(rawResponse);
    }

    private String buildRequestBody(String prompt) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("model", model);

        ArrayNode messages = request.putArray("messages");
        messages.addObject().put("role", "system").put("content", SYSTEM_PROMPT);
        messages.addObject().put("role", "user").put("content", prompt);

        return objectMapper.writeValueAsString(request);
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
        String cleanJson = modelResponse.replaceAll("```json", "").replaceAll("```", "").trim();
        return objectMapper.readTree(cleanJson);
    }

    private String extractChatContent(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
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

    private ExpertiseLevel parseLevel(String levelStr) {
        try {
            return ExpertiseLevel.valueOf(levelStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ExpertiseLevel.INTERMEDIATE;
        }
    }
}

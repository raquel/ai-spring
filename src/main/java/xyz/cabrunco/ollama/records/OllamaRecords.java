package xyz.cabrunco.ollama.records;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import xyz.cabrunco.ollama.utils.FileUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OllamaRecords {

    public record ModelList(List<OllamaModel> models) {

        public record OllamaModel(String name, OffsetDateTime modifiedDate, long size, String digest, Details details) {}

        public record Details(String format, String family, List<String> families, String parameterSize, String quantizationLevel) {}

    }

    public sealed interface OllamaRequest permits OllamaTextRequest, OllamaImageRequest {
        String model();
        String prompt();
        boolean stream();
    }

    public record OllamaTextRequest(String model, String prompt, boolean stream) implements OllamaRequest {}

    public record OllamaImageRequest( String model, String prompt, List<String> images, boolean stream)
            implements OllamaRequest {

        // Transform file names into base64-encoded strings
        public OllamaImageRequest {
            images = images.stream()
                    .map(image -> isBase64Encoded(image) ? image : FileUtils.encodeImage(image))
                    .collect(Collectors.toList());
        }

        private boolean isBase64Encoded(String image) {
            // Implement Base64 format validation.
            return image.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");
        }
    }

    public record OllamaResponse(String model, String createdAt, String response, boolean done, long totalDuration,
                                 int promptEvalCount, int evalCount) {}

    public record Message(String role, String content) {
    }

    public record OllamaChatRequest(
            String model,
            List<Message> messages,
            boolean stream) {
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record OllamaChatResponse(
            String model,
            String createdAt,
            Message message,
            boolean done,
            long totalDuration,
            int promptEvalCount,
            int evalCount) {
    }

}

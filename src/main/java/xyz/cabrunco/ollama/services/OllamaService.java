package xyz.cabrunco.ollama.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static xyz.cabrunco.ollama.records.OllamaRecords.*;
import static xyz.cabrunco.ollama.records.OllamaRecords.ModelList.OllamaModel;

@Service
public class OllamaService {

    public static final String DEFAULT_MODEL = OllamaModels.PHI;
    private static final Logger log = LoggerFactory.getLogger(OllamaService.class);

    private final OllamaInterface ollamaInterface;

    public OllamaService(OllamaInterface ollamaInterface) {
        this.ollamaInterface = ollamaInterface;
    }

    public List<OllamaModel> getModels() {
        return ollamaInterface.getModels().models();
    }

    public String chat(String model, String question) {
        var request = new OllamaChatRequest(model, List.of(new Message("user", question)), false);
        var response = ollamaInterface.chat(request);
        return response.message().content();
    }

    public String chatWithDefaultModel(String question) {
        return chat(DEFAULT_MODEL, question);
    }

    public String conversation(String model, String... strings) {
        if (strings.length % 2 == 0) {
            throw new IllegalArgumentException(
                    """
                    Odd number of strings required, alternating
                    messages between 'user' and 'assistant'.""");
        }
        List<Message> messages = IntStream.range(0, strings.length)
                .mapToObj(i -> new Message(
                        i % 2 == 0 ? "user" : "assistant", strings[i]))
                .collect(Collectors.toList());
        var request = new OllamaChatRequest(model, messages, false);
        var response = ollamaInterface.chat(request);
        log.info("Conversation response: {}", response);
        return response.message().content();
    }

    public String generate(OllamaRequest request) {
        switch (request) {
            case OllamaImageRequest imageRequest ->
                    System.out.printf("Reading image using %s%n", imageRequest.model());
            case OllamaTextRequest textRequest ->
                    System.out.printf("Generating text response from %s...%n", textRequest.model());
        }
        return ollamaInterface.generate(request).response();
    }
}

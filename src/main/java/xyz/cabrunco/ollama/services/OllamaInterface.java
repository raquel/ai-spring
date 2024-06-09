package xyz.cabrunco.ollama.services;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static xyz.cabrunco.ollama.records.OllamaRecords.*;

@HttpExchange("/api")
public interface OllamaInterface {

    @GetExchange("/tags")
    ModelList getModels();

    @PostExchange("/generate")
    OllamaResponse generate(@RequestBody OllamaRequest question);

    @PostExchange("/chat")
    OllamaResponse chat(@RequestBody OllamaChatRequest question);
}

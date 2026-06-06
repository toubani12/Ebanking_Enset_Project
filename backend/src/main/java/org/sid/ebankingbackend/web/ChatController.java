package org.sid.ebankingbackend.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sid.ebankingbackend.agent.BankAiAgent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ai")
@CrossOrigin("*")
@AllArgsConstructor
public class ChatController {

    private final BankAiAgent bankAiAgent;

    /**
     * Chat endpoint consumed by the Angular frontend and the Telegram bot.
     * Any authenticated user (SCOPE_USER or SCOPE_ADMIN) can call this endpoint —
     * authorization is already enforced globally in SecurityConfig (all routes require auth).
     *
     * Request body: { "query": "...", "conversationId": "..." }
     * Response:     { "response": "..." }
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> body) {
        String query = body.get("query");
        String conversationId = body.get("conversationId");

        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Query must not be empty."));
        }

        if (conversationId == null || conversationId.isBlank()) {
            conversationId = "default";
        }

        log.info("Chat request — conversationId={}, query length={}", conversationId, query.length());

        String response = bankAiAgent.askAgent(query, conversationId);
        return ResponseEntity.ok(Map.of("response", response));
    }
}

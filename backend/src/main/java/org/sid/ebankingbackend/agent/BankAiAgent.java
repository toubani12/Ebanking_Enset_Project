package org.sid.ebankingbackend.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Component;

@Component
public class BankAiAgent {

    private static final String DEFAULT_CONVERSATION_ID = "default";

    private static final String SYSTEM_PROMPT = """
            Tu es un assistant bancaire professionnel et courtois d'une plateforme e-banking.

            Tu disposes de deux sources d'information complémentaires :

            1. OUTILS (données en temps réel) — utilise-les pour toute question sur un compte ou un client :
               - getCustomerInfo      : informations d'un client (nom, email) à partir de son ID numérique.
               - getAccountBalance    : solde, type et statut d'un compte à partir de son ID (UUID).
               - getAccountHistory    : les 10 dernières opérations d'un compte à partir de son ID (UUID).

            2. RECHERCHE DOCUMENTAIRE (RAG) — utilise-la pour toute question sur les règles, \
            politiques, frais ou conditions générales de la banque. \
            Le contexte pertinent est fourni automatiquement dans chaque message.

            Règles absolues :
            - Réponds TOUJOURS dans la langue utilisée par l'utilisateur.
            - N'invente, n'estime ou ne suppose JAMAIS des données financières (soldes, montants, dates).
            - Appelle toujours l'outil approprié avant de répondre à une question sur un compte.
            - Si un outil échoue, informe l'utilisateur clairement et demande-lui de vérifier l'identifiant.
            - Utilise tes outils pour les données des comptes, et le contexte fourni par la recherche \
            documentaire (RAG) pour répondre aux questions sur les règles de la banque.
            - Si l'information n'est dans aucun des deux, dis que tu ne sais pas.
            """;

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public BankAiAgent(ChatClient.Builder chatClientBuilder,
                       BankAiTools bankAiTools,
                       SimpleVectorStore vectorStore) {
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .build();
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(bankAiTools)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new QuestionAnswerAdvisor(vectorStore)
                )
                .build();
    }

    /**
     * Ask the agent a question using the default shared conversation session.
     * Suitable for single-user or stateless contexts.
     */
    public String askAgent(String query) {
        return askAgent(query, DEFAULT_CONVERSATION_ID);
    }

    /**
     * Ask the agent a question within a specific conversation session.
     * Use this overload in multi-user contexts (e.g. one conversationId per Telegram chat ID)
     * so that each user's history is isolated in memory.
     */
    public String askAgent(String query, String conversationId) {
        return chatClient.prompt()
                .user(query)
                .advisors(spec -> spec.param(
                        ChatMemory.CONVERSATION_ID,
                        conversationId))
                .call()
                .content();
    }
}

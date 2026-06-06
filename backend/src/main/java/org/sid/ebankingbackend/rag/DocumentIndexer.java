package org.sid.ebankingbackend.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * RAG ingestion pipeline.
 *
 * Declares the SimpleVectorStore bean, configures its persistence to a local JSON file,
 * and exposes loadBankDocuments() to index PDF documents into the vector store.
 *
 * The store file is loaded automatically at startup if it already exists, so previously
 * indexed documents survive application restarts without re-ingestion.
 */
@Slf4j
@Configuration
public class DocumentIndexer {

    // Path of the JSON file used to persist the vector store between restarts.
    // Configurable via application.properties: app.vectorstore.path
    @Value("${app.vectorstore.path:src/main/resources/store/bank-store.json}")
    private String vectorStorePath;

    // @Lazy breaks the potential circular reference: this @Configuration class declares
    // the SimpleVectorStore bean and also injects it for use in loadBankDocuments().
    @Lazy
    private final SimpleVectorStore vectorStore;

    public DocumentIndexer(@Lazy SimpleVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    // -------------------------------------------------------------------------
    // Bean declaration
    // -------------------------------------------------------------------------

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();

        File storeFile = new File(vectorStorePath);
        if (storeFile.exists()) {
            log.info("Vector store file found — loading embeddings from: {}", storeFile.getAbsolutePath());
            store.load(storeFile);
        } else {
            log.info("No existing vector store file — starting with empty store ({})", vectorStorePath);
        }

        return store;
    }

    // -------------------------------------------------------------------------
    // Ingestion pipeline
    // -------------------------------------------------------------------------

    /**
     * Indexes a PDF document into the vector store and persists the result to disk.
     *
     * Pipeline:
     *   PDF (MultipartFile)
     *     → PagePdfDocumentReader   (one Document per page)
     *     → TokenTextSplitter       (800-token chunks, ~400-token overlap)
     *     → SimpleVectorStore.add() (embeddings computed via OpenAI)
     *     → SimpleVectorStore.save() (JSON file on disk)
     *
     * @param file the PDF file uploaded by the user or loaded from disk
     * @throws IOException if reading the file fails
     */
    public void loadBankDocuments(MultipartFile file) throws IOException {
        log.info("Starting ingestion of document: {}", file.getOriginalFilename());

        // Step 1 — Read the PDF (one Document per page)
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(file.getResource());
        List<Document> pages = pdfReader.get();
        log.info("PDF read: {} page(s) extracted", pages.size());

        // Step 2 — Split pages into overlapping chunks for better RAG recall
        // chunkSize=800 tokens, minChunkSize=400 chars, minChunkLength=10,
        // maxChunks=5000, keepSeparator=true
        TokenTextSplitter splitter = new TokenTextSplitter(800, 400, 10, 5000, true);
        List<Document> chunks = splitter.apply(pages);
        log.info("Splitting done: {} chunk(s) produced", chunks.size());

        // Step 3 — Compute embeddings and store in the vector store
        vectorStore.add(chunks);
        log.info("Embeddings added to vector store");

        // Step 4 — Persist to the JSON file so the store survives restarts
        File storeFile = new File(vectorStorePath);
        storeFile.getParentFile().mkdirs();
        vectorStore.save(storeFile);
        log.info("Vector store persisted to: {}", storeFile.getAbsolutePath());
    }
}

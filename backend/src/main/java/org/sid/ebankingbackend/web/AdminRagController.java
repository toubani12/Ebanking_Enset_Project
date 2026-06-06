package org.sid.ebankingbackend.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sid.ebankingbackend.rag.DocumentIndexer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/rag")
@CrossOrigin("*")
@AllArgsConstructor
public class AdminRagController {

    private final DocumentIndexer documentIndexer;

    /**
     * Uploads a PDF and indexes its content into the vector store.
     * Restricted to administrators (SCOPE_ADMIN authority, granted via JWT scope claim).
     *
     * Usage (curl example):
     *   curl -X POST http://localhost:8080/rag/upload \
     *        -H "Authorization: Bearer <token>" \
     *        -F "file=@conditions-generales.pdf"
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, String>> uploadDocument(
            @RequestPart("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "The uploaded file is empty."));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Only PDF files are accepted."));
        }

        try {
            log.info("Admin requested indexing of document: {}", originalFilename);
            documentIndexer.loadBankDocuments(file);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Document '" + originalFilename + "' has been indexed successfully."
            ));
        } catch (IOException e) {
            log.error("Failed to index document '{}': {}", originalFilename, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to process the document: " + e.getMessage()));
        }
    }
}

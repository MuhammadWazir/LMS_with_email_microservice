package com.example.LMS.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenLibraryService {

    private final RestTemplate restTemplate;
    private static final String OPEN_LIBRARY_BASE_URL = "https://openlibrary.org/api/books";

    public String fetchAuthorName(String isbn) {
        try {
            String url = String.format("%s?bibkeys=ISBN:%s&format=json&jscmd=data",
                    OPEN_LIBRARY_BASE_URL, isbn);

            log.info("Fetching author information from Open Library API for ISBN: {}", isbn);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseAuthorFromResponse(response.getBody(), isbn);
            }

            log.warn("Failed to fetch author information for ISBN: {}", isbn);
            return null;

        } catch (Exception e) {
            log.error("Error fetching author information for ISBN: {}", isbn, e);
            return null;
        }
    }

    private String parseAuthorFromResponse(String responseBody, String isbn) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody);

            String key = "ISBN:" + isbn;
            JsonNode bookNode = rootNode.get(key);

            if (bookNode != null && bookNode.has("authors")) {
                JsonNode authorsNode = bookNode.get("authors");
                if (authorsNode.isArray() && authorsNode.size() > 0) {
                    JsonNode firstAuthor = authorsNode.get(0);
                    if (firstAuthor.has("name")) {
                        return firstAuthor.get("name").asText();
                    }
                }
            }

            log.warn("No author information found in response for ISBN: {}", isbn);
            return null;

        } catch (Exception e) {
            log.error("Error parsing author information from API response for ISBN: {}", isbn, e);
            return null;
        }
    }
}


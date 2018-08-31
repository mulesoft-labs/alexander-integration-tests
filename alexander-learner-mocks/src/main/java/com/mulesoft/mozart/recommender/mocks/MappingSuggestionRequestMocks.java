package com.mulesoft.mozart.recommender.mocks;

import com.mulesoft.mozart.commons.mapping.MappingUtils;
import com.mulesoft.recommenderapiservice.rest.MappingSuggestionRequest;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public enum MappingSuggestionRequestMocks {

    SIMPLE_SUGGESTION("suggestion-request-input.json"),
    SIMPLE_USE_CASE("simple-use-case-request-input.json");

    private final String requestInput;

    MappingSuggestionRequestMocks(String requestInput) {
        this.requestInput = requestInput;
    }

    public MappingSuggestionRequest getRequestInput() throws IOException {
        return MappingUtils.convertJsonTo(getRequestInputAsString(), MappingSuggestionRequest.class);
    }

    public String getRequestInputAsString() throws IOException {
        return readFile(this.requestInput);
    }

    private String readFile(String fileName) throws IOException {
        return IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName), "UTF-8");
    }

}

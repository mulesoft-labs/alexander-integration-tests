package com.mulesoft.mozart.recommender.tests;

import com.mulesoft.mozart.recommender.learner.client.AlexanderLearnerAPI;
import com.mulesoft.mozart.recommender.mocks.MappingSuggestionRequestMocks;
import com.mulesoft.mozart.recommender.rest.FitResponse;
import com.mulesoft.mozart.tests.platform.utils.TestConfigurationProperties;
import com.mulesoft.recommenderapiservice.rest.MappingSuggestionRequest;
import com.mulesoft.recommenderapiservice.rest.MappingSuggestionResponse;
import com.mulesoft.recommenderapiservice.restclient.RecommenderAPIServiceClient;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.junit.Assert.assertFalse;

public class IntegrationTest {

    static Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    private final String organizationId = AlexanderEnvironmentConfiguration.getConfiguration().getProperty("anypoint.organizationId");
    AlexanderLearnerAPI alexanderLearnerAPI = AlexanderLearnerAPI
            .connect(AlexanderEnvironmentConfiguration.getConfiguration().getProperty("alexander-learner-url"), 2000, 40000);
    RecommenderAPIServiceClient recommenderAPIServiceClient = RecommenderAPIServiceClient
            .connect(AlexanderEnvironmentConfiguration.getConfiguration().getProperty("recommender-api-url"), 2000, 40000);

    @Before
    public void setUp() throws Exception {
        TestConfigurationProperties testConfig = new TestConfigurationProperties();
        testConfig.setBaseUrl(AlexanderEnvironmentConfiguration.getConfiguration().getProperty("anypoint.baseurl"));
        testConfig.setUser(AlexanderEnvironmentConfiguration.getConfiguration().getProperty("anypoint.username"));
        testConfig.setPassword(AlexanderEnvironmentConfiguration.getConfiguration().getProperty("anypoint.password"));
        testConfig.setDefaultTimeout(15000);
        testConfig.setOrganizationId(organizationId);
    }

    @Test(timeout = 40000)
    public void canStoreInitialSuggestion() throws Exception {
        String targetDirectory = "integration-tests/" + UUID.randomUUID().toString();
        logger.info("Using directory {} ", targetDirectory);
        FitResponse fitResponse = alexanderLearnerAPI.fit(targetDirectory)
                .toBlocking().value();
        assertFalse(fitResponse.getScores().isEmpty());

        MappingSuggestionRequest requestInput = MappingSuggestionRequestMocks.SIMPLE_SUGGESTION.getRequestInput();
        requestInput.setModelVersion(targetDirectory);
        MappingSuggestionResponse suggestionResponse = recommenderAPIServiceClient.getSuggestion(targetDirectory, requestInput).toBlocking().value();

        assertFalse(suggestionResponse.getSuggestions().isEmpty());
    }

}

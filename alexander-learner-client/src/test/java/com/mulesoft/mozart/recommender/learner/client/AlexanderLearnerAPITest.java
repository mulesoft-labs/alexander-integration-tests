package com.mulesoft.mozart.recommender.learner.client;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.mulesoft.mozart.recommender.rest.FitResponse;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AlexanderLearnerAPITest {

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(wireMockConfig().dynamicPort());

    @Rule
    public WireMockClassRule instanceRule = wireMockRule;

    AlexanderLearnerAPI alexanderLearnerAPI = AlexanderLearnerAPI.connect(this.getUrl(), 1000, 1000);

    private void mockRequest(String method, UrlPattern pattern) {
        wireMockRule.stubFor(request(method, pattern)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "    \"scores\": [\n" +
                                "        {\n" +
                                "            \"val_loss\": [\n" +
                                "                0.2706550981\n" +
                                "            ],\n" +
                                "            \"val_acc\": [\n" +
                                "                0.8904109589\n" +
                                "            ],\n" +
                                "            \"loss\": [\n" +
                                "                0.291004407\n" +
                                "            ],\n" +
                                "            \"acc\": [\n" +
                                "                0.8819679054\n" +
                                "            ]\n" +
                                "        }\n" +
                                "    ]\n" +
                                "}")
                )
        );
    }

    private String getUrl() {
        return format("http://localhost:%s", wireMockRule.port());
    }

    @Before
    public void setUp() throws Exception {
        mockRequest("POST", urlEqualTo("/api/v1/fit?targetDirectory=pepe"));
    }

    @Test
    public void fit() throws Exception {
        FitResponse fitResponse = alexanderLearnerAPI.fit("pepe").toBlocking().value();
        assertFalse(fitResponse.getScores().isEmpty());
    }

}

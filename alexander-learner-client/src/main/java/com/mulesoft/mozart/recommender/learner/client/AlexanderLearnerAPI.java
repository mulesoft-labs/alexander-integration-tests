package com.mulesoft.mozart.recommender.learner.client;

import com.mulesoft.mozart.recommender.rest.FitResponse;
import feign.*;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.rxjava.RxJavaFeign;
import feign.slf4j.Slf4jLogger;
import rx.Single;

public interface AlexanderLearnerAPI {

    @RequestLine("POST /api/v1/fit?targetDirectory={targetDirectory}")
    @Headers({
            "Content-Type: application/json",
    })
    Single<FitResponse> fit(@Param("targetDirectory") String targetDirectory);

    @RequestLine("GET /api/v1/fit/status")
    Single<FitResponse> fitStatus();

    static AlexanderLearnerAPI connect(String baseUrl, Integer connectTimeout, Integer readTimeout){
        return connect(baseUrl, connectTimeout, readTimeout, new OkHttpClient());
    }

    static AlexanderLearnerAPI connect(String baseUrl, Integer connectTimeout, Integer readTimeout, Client client){
        return RxJavaFeign.builder()
                .retryer(Retryer.NEVER_RETRY)
                .client(client)
                .options(new Request.Options(connectTimeout, readTimeout))
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder())
                .logLevel(feign.Logger.Level.FULL)
                .logger(new Slf4jLogger(AlexanderLearnerAPI.class))
                .target(AlexanderLearnerAPI.class, baseUrl);
    }
}

package com.mulesoft.mozart.recommender.rest;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class FitResponse {
    private List<Score> scores;

    @Getter
    @Setter
    public static class Score {
        String algorithm;
        double[] acc;
        double[] val_acc;
        double[] loss;
        double[] val_loss;
    }
}

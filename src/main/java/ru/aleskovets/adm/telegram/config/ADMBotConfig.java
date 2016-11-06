package ru.aleskovets.adm.telegram.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by ad on 10/30/2016.
 */
@Component
public class ADMBotConfig {

    @Value("${participants.file}")
    private String participantsFilePath;
    @Value("${participants.expectedNumber}")
    private Integer expectedNumber;

    public String getParticipantsFilePath() {
        return participantsFilePath;
    }

    public Integer getExpectedNumber() {
        return expectedNumber;
    }
}

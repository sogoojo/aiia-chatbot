package com.mastercard.bot.dev.engage;

import com.microsoft.bot.ai.luis.LuisApplication;
import com.microsoft.bot.ai.luis.LuisRecognizer;
import com.microsoft.bot.ai.luis.LuisRecognizerOptionsV3;
import com.microsoft.bot.builder.Recognizer;
import com.microsoft.bot.builder.RecognizerResult;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.integration.Configuration;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;

public class EngageRecognizer implements Recognizer {


    private LuisRecognizer recognizer;

    public EngageRecognizer(Configuration configuration){
        Boolean luisIsConfigured = StringUtils.isNotBlank(configuration.getProperty("LuisAppId"))
                && StringUtils.isNotBlank(configuration.getProperty("LuisAPIKey"))
                && StringUtils.isNotBlank(configuration.getProperty("LuisAPIHostName"));
        if (luisIsConfigured) {
            LuisApplication luisApplication = new LuisApplication(
                    configuration.getProperty("LuisAppId"),
                    configuration.getProperty("LuisAPIKey"),
                    String.format("https://%s", configuration.getProperty("LuisAPIHostName"))
            );
            LuisRecognizerOptionsV3 recognizerOptions = new LuisRecognizerOptionsV3(luisApplication);
            recognizerOptions.setIncludeInstanceData(true);

            this.recognizer = new LuisRecognizer(recognizerOptions);
        }
    }

    public Boolean isConfigured(){
        return this.recognizer !=null;
    }

    public CompletableFuture<RecognizerResult>executeLuisQuery(TurnContext context){
        return this.recognizer.recognize(context);
    }


    @Override
    public CompletableFuture<RecognizerResult> recognize(TurnContext turnContext) {
        return this.recognizer.recognize(turnContext);
    }
}

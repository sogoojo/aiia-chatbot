// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.mastercard.bot.dev.engage;

import com.google.common.collect.Iterators;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.integration.Configuration;
import com.microsoft.bot.schema.ActionTypes;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.CardAction;
import com.microsoft.bot.schema.CardImage;
import com.microsoft.bot.schema.ConversationReference;
import com.microsoft.bot.schema.SuggestedActions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This controller will receive GET requests at /api/notify and send a message
 * to all ConversationReferences.
 *
 * @see ConversationReferences
 * @see Bot
 * @see Application
 */
@RestController
public class NotifyController {

    private final BotFrameworkHttpAdapter adapter;

    private ConversationReferences conversationReferences;
    private String appId;


    @Autowired
    public NotifyController(
        BotFrameworkHttpAdapter withAdapter,
        Configuration withConfiguration,
        ConversationReferences withReferences
    ) {
        adapter = withAdapter;
        conversationReferences = withReferences;
        appId = withConfiguration.getProperty("MicrosoftAppId");
    }

    @GetMapping("/api/notify")
    public ResponseEntity<Object> proactiveMessage() {
        for (ConversationReference reference : conversationReferences.values()) {
            adapter.continueConversation(
                appId, reference, turnContext -> turnContext.sendActivity("proactive hello").thenApply(resourceResponse -> null)
            );
        }
        return new ResponseEntity<>(
            "<html><body><h1>Proactive messages have been sent.</h1></body></html>",
            HttpStatus.ACCEPTED
        );
    }

    @GetMapping("/")
    public String hello(){
        return "Application is up and running";
    }


    @GetMapping("/callback")
    public ResponseEntity<Object> processItems(@RequestParam(value = "code") String code) {
        try {
            NetworkCall.onBoardToken(code);
            for (ConversationReference reference : conversationReferences.values()) {
                adapter.continueConversation(
                        appId, reference, turnContext -> sendSuggestedActions(turnContext))
                                .thenApply(result -> null);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  null;
    }

    private static CompletableFuture<Void> sendSuggestedActions(TurnContext turnContext) {
        return turnContext.sendActivity(
                MessageFactory.text("Account linking successfully completed, what would you like to do?")
        ).thenApply(sendResult -> null);
    }



}

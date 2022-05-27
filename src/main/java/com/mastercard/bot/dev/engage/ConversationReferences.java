// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.mastercard.bot.dev.engage;

import com.microsoft.bot.schema.ConversationReference;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A Map of ConversationReference object the bot handling.
 *
 * @see NotifyController
 * @see Bot
 */
public class ConversationReferences extends ConcurrentHashMap<String, ConversationReference> {
}

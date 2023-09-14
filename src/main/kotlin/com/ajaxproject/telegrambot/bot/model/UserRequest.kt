package com.ajaxproject.telegrambot.bot.model

import org.telegram.telegrambots.meta.api.objects.Update


data class UserRequest(
    val update: Update,
    val chatId: Long,
    val userSession: UserSession
)


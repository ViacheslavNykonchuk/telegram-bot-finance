package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.enums.Commands.ADD_INCOME
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_INCOMES
import com.ajaxproject.telegrambot.bot.enums.Commands.MENU
import com.ajaxproject.telegrambot.bot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.bot.enums.ConversationState.WAITING_FOR_ADD_INCOME
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.ADD_FINANCE
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.BACK_TO_MENU
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.FAILED_ADD_FINANCE
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.SUCCESSFUL_ADD_FINANCE
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.models.MongoFinance
import com.ajaxproject.telegrambot.bot.service.FinanceService
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.isTextMessage
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateSession
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

@Component
class AddIncomesButtonHandler(
    private val telegramService: TelegramService,
    private val textService: TextService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, ADD_INCOME.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(ADD_FINANCE.name)
        )
        val session: UpdateSession = dispatchRequest.updateSession.apply {
            state = WAITING_FOR_ADD_INCOME
        }
        userSessionService.saveSession(dispatchRequest.chatId, session)
    }
}

@Component
class AddIncomesHandler(
    private val telegramService: TelegramService,
    private val financeService: FinanceService,
    private val textService: TextService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return WAITING_FOR_ADD_INCOME == request.updateSession.state &&
            request.update.isTextMessage()
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        val income = dispatchRequest.update.message.text
        val chatId = dispatchRequest.chatId

        if (!income.contains(":")) {
            telegramService.sendMessage(chatId, textService.readText(FAILED_ADD_FINANCE.name))
            return
        }

        financeService.addFinance(
            MongoFinance(
                id = ObjectId(),
                userId = dispatchRequest.chatId,
                financeType = FinanceService.INCOME,
                amount = income.split(":")[0].toDouble(),
                description = income.split(":")[1]
            )
        )

        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(SUCCESSFUL_ADD_FINANCE.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(
                        inlineButton("Add income again", ADD_INCOME.command),
                        inlineButton("Return to menu", MENU.command)
                    )
                )
            }
        )
        val session: UpdateSession = dispatchRequest.updateSession.apply {
            state = CONVERSATION_STARTED
        }
        userSessionService.saveSession(dispatchRequest.chatId, session)
    }
}

@Component
class GetIncomesHandler(
    private val telegramService: TelegramService,
    private val financeService: FinanceService,
    private val textService: TextService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, GET_INCOMES.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        financeService.getIncomeByUserId(dispatchRequest.chatId)
            ?.forEach {
                telegramService.sendMessage(
                    chatId = dispatchRequest.chatId,
                    text = it.toString()
                )
            }
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(BACK_TO_MENU.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(
                        inlineButton("Return to menu", MENU.command)
                    )
                )
            }
        )
        val session: UpdateSession = dispatchRequest.updateSession.apply {
            state = CONVERSATION_STARTED
        }
        userSessionService.saveSession(dispatchRequest.chatId, session)
    }
}

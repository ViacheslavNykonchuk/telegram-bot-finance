package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.dto.ExpenseResponse
import com.ajaxproject.telegrambot.bot.dto.IncomeResponse
import com.ajaxproject.telegrambot.bot.dto.toExpenseResponse
import com.ajaxproject.telegrambot.bot.dto.toIncomeResponse
import com.ajaxproject.telegrambot.bot.models.MongoFinance
import com.ajaxproject.telegrambot.bot.repository.FinanceRepositoryImpl
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FinanceService(
    private val financeRepositoryImpl: FinanceRepositoryImpl,
) {

    fun getIncomeByUserId(userId: Long): List<IncomeResponse>? {
        return financeRepositoryImpl.findByUserId(userId, INCOME)
            ?.map { it.toIncomeResponse() }
            ?: let {
                log.warn("Income with userId {} not found", userId)
                emptyList()
            }
    }

    fun getExpenseByUserId(userId: Long): List<ExpenseResponse>? {
        return financeRepositoryImpl.findByUserId(userId, EXPENSE)
            ?.map { it.toExpenseResponse() }
            ?: let {
                log.warn("Expense with userId {} not found", userId)
                emptyList()
            }
    }

    fun addFinance(finance: MongoFinance): MongoFinance {
        return financeRepositoryImpl.save(finance)
    }

    fun getCurrencyBalance(userId: Long): Double {
        return getExpenseByUserId(userId)?.sumOf { it.amount }
            ?.let { getIncomeByUserId(userId)?.sumOf { it.amount }?.minus(it) }
            ?: 0.0
    }

    companion object {
        const val INCOME = "income"
        const val EXPENSE = "expense"
        private val log = LoggerFactory.getLogger(FinanceService::class.java)
    }
}

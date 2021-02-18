package ru.gromov.serverControlTgBot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.logging.LogLevel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.gromov.serverControlTgBot.service.BotCommandsService

@Component
class BotInitComponent @Autowired constructor(
    @Value("\${bot.token:NONE}") private var botToken: String,
    var botCommandsService: BotCommandsService
) {

    @EventListener(value = [ApplicationStartedEvent::class])
    fun startPolling() {
        if (botToken == "NONE") {
            throw IllegalStateException("Setup bot token!")
        }

        bot {
            token = botToken
            timeout = 30
            logLevel = LogLevel.All()
            dispatch {

                command("start") {
                    botCommandsService.startBot(message)
                }

                text("Команды управления") {
                    val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                        listOf(
                            InlineKeyboardButton.CallbackData(
                                text = "Запустить сервер",
                                callbackData = "startServer"
                            ),
                            InlineKeyboardButton.CallbackData(
                                text = "Сделать бекап",
                                callbackData = "backupWorld"
                            ),
                            InlineKeyboardButton.CallbackData(
                                text = "Статус сервера",
                                callbackData = "serverStatus"
                            )
                        ),
                        listOf(
                            InlineKeyboardButton.CallbackData(
                                text = "Остановить сервер",
                                callbackData = "stopServer"
                            ),
                            InlineKeyboardButton.CallbackData(
                                text = "Обновить сервер",
                                callbackData = "updateServer"
                            )
                        )
                    )
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Команды:",
                        replyMarkup = inlineKeyboardMarkup
                    )
                }


                callbackQuery(
                    callbackData = "startServer",
                    callbackAnswerShowAlert = true
                ) {
                    callbackQuery.message?.chat?.id ?: return@callbackQuery
                    botCommandsService.startServer(callbackQuery)
                }
                callbackQuery(
                    callbackData = "stopServer",
                    callbackAnswerShowAlert = true
                ) {
                    callbackQuery.message?.chat?.id ?: return@callbackQuery
                    botCommandsService.stopServer(callbackQuery)
                }
                callbackQuery(
                    callbackData = "updateServer",
                    callbackAnswerShowAlert = true
                ) {
                    callbackQuery.message?.chat?.id ?: return@callbackQuery
                    botCommandsService.updateServer(callbackQuery)
                }
                callbackQuery(
                    callbackData = "backupWorld",
                    callbackAnswerShowAlert = true
                ) {
                    callbackQuery.message?.chat?.id ?: return@callbackQuery
                    botCommandsService.backupServer(callbackQuery)
                }
                callbackQuery(
                    callbackData = "serverStatus",
                    callbackAnswerShowAlert = true
                ) {
                    callbackQuery.message?.chat?.id ?: return@callbackQuery
                    botCommandsService.serverStatus(callbackQuery)
                }


                telegramError {
                    println(error.getErrorMessage())
                }


            }
        }.startPolling()
    }

}


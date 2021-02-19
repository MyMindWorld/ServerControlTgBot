package ru.gromov.serverControlTgBot.service

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.*
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.gromov.serverControlTgBot.utils.Messages
import java.lang.IllegalStateException
import java.rmi.UnexpectedException


@Service
class BotCommandsService @Autowired constructor(
    @Value("\${bot.serverSuccessfullyUpdatedMessage}") private var serverSuccessfullyUpdated: String,
    @Value("\${bot.serverDoesntNeedUpdateMessage}") private var serverDoesntNeedUpdate: String,
    private val consoleService: ConsoleService,
    private val adminService: AdminService,
    private var bot: Bot
) {

    val welcomeKeyboardMarkup = KeyboardReplyMarkup(
        keyboard = listOf(
            listOf(KeyboardButton("Команды управления"))
        ), resizeKeyboard = true
    )

    fun startBot(message: Message) {
        adminService.denyNotPrivilegedAndNotifyAdmin(message.from!!, message.text!!)

        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = Messages.welcomeMessage(),
            replyMarkup = welcomeKeyboardMarkup,
            parseMode = ParseMode.MARKDOWN_V2
        )

    }

    fun startServer(callbackQuery: CallbackQuery) {
        adminService.denyNotPrivilegedAndNotifyAdmin(callbackQuery.from, callbackQuery.message!!.text!!)

        denyIfServerIsRunning(callbackQuery)

        consoleService.startServer()

        if (!adminService.isReportUser(callbackQuery.from)) {
            adminService.sendStartToAdmin(callbackQuery.from)
        }

        bot.answerCallbackQuery(
            callbackQueryId = callbackQuery.id,
            showAlert = true,
            text = Messages.serverStarted()
        )
    }

    fun stopServer(callbackQuery: CallbackQuery) {
        adminService.denyNotPrivilegedAndNotifyAdmin(callbackQuery.from, callbackQuery.message!!.text!!)

        consoleService.stopServer()

        if (!adminService.isReportUser(callbackQuery.from)) {
            adminService.sendStopToAdmin(callbackQuery.from)
        }

        bot.answerCallbackQuery(
            callbackQueryId = callbackQuery.id,
            showAlert = true,
            text = Messages.serverStopped()
        )
    }

    fun updateServer(callbackQuery: CallbackQuery) {
        adminService.denyNotPrivilegedAndNotifyAdmin(callbackQuery.from, callbackQuery.message!!.text!!)

        denyIfServerIsRunning(callbackQuery)

        val updateResponse = consoleService.updateServer()

        if (!updateResponse.contains(serverSuccessfullyUpdated)) {
            bot.sendMessage(
                chatId = ChatId.fromId(callbackQuery.from.id),
                text = Messages.serverUpdateFailed(),
                parseMode = ParseMode.MARKDOWN_V2
            )
            adminService.sendUpdateFailedToAdmin(updateResponse)
            throw UnexpectedException("Server update failed!")
        }

        if (updateResponse.contains(serverDoesntNeedUpdate)) {
            bot.sendMessage(
                chatId = ChatId.fromId(callbackQuery.from.id),
                text = Messages.serverDoesntNeedUpdate(),
                parseMode = ParseMode.MARKDOWN_V2
            )
        } else {
            bot.sendMessage(
                chatId = ChatId.fromId(callbackQuery.from.id),
                text = Messages.serverUpdated(),
                parseMode = ParseMode.MARKDOWN_V2
            )
        }

    }

    fun serverStatus(callbackQuery: CallbackQuery) {
        adminService.denyNotPrivilegedAndNotifyAdmin(callbackQuery.from, callbackQuery.message!!.text!!)

        bot.answerCallbackQuery(
            callbackQueryId = callbackQuery.id,
            showAlert = true,
            text = Messages.serverStatus(consoleService.isServerRunning())
        )
    }

    fun backupServer(callbackQuery: CallbackQuery) {
        adminService.denyNotPrivilegedAndNotifyAdmin(callbackQuery.from, callbackQuery.message!!.text!!)

        denyIfServerIsRunning(callbackQuery)

        consoleService.backupServer()

        bot.answerCallbackQuery(
            callbackQueryId = callbackQuery.id,
            showAlert = true,
            text = Messages.serverBackedUp()
        )
    }

    private fun denyIfServerIsRunning(callbackQuery: CallbackQuery) {
        if (consoleService.isServerRunning()) {
            bot.answerCallbackQuery(
                callbackQueryId = callbackQuery.id,
                showAlert = true,
                text = Messages.serverAlreadyRunning()
            )
            throw IllegalStateException("Server should be stopped before update")
        }
    }

    fun showServerCommandsMarkup(message: Message) {
        adminService.denyNotPrivilegedAndNotifyAdmin(message.from!!, message.text!!)

        val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
            listOf(
                InlineKeyboardButton.CallbackData(
                    text = "Запустить",
                    callbackData = "startServer"
                ),
                InlineKeyboardButton.CallbackData(
                    text = "Сделать бекап",
                    callbackData = "backupWorld"
                ),
                InlineKeyboardButton.CallbackData(
                    text = "Статус",
                    callbackData = "serverStatus"
                )
            ),
            listOf(
                InlineKeyboardButton.CallbackData(
                    text = "Остановить",
                    callbackData = "stopServer"
                ),
                InlineKeyboardButton.CallbackData(
                    text = "Обновить",
                    callbackData = "updateServer"
                )
            )
        )
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "Команды управления сервером:",
            replyMarkup = inlineKeyboardMarkup
        )
    }
}
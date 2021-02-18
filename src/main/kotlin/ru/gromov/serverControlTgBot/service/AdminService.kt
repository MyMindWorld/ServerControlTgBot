package ru.gromov.serverControlTgBot.service

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.gromov.serverControlTgBot.utils.Messages


@Service
class AdminService @Autowired constructor(
    @Value("\${bot.permittedChatIDs}") private var permittedChatIDs: List<String>,
    @Value("\${bot.reportUserId}") private var reportUserId: String,
    private val bot: Bot
) {


    private fun isPrivileged(user: User) = permittedChatIDs.contains(user.id.toString())

    fun denyNotPrivilegedAndNotifyAdmin(user: User, commandText: String) {
        if (!isPrivileged(user)) {
            bot.sendMessage(
                chatId = ChatId.fromId(user.id),
                text = Messages.unauthorized(),
                parseMode = ParseMode.MARKDOWN_V2
            )
            sendReportToAdmin(user, commandText)
            throw IllegalAccessError("User doesn't have role to use this")
        }

    }

    fun isReportUser(user: User) = user.id.toString() == reportUserId

    fun sendStartToAdmin(startedBy: User) {
        bot.sendMessage(
            chatId = ChatId.fromId(reportUserId.toLong()),
            text = Messages.serverStartedReport(startedBy),
            parseMode = ParseMode.MARKDOWN_V2
        )
    }


    fun sendStopToAdmin(stoppedBy: User) {
        bot.sendMessage(
            chatId = ChatId.fromId(reportUserId.toLong()),
            text = Messages.serverStoppedReport(stoppedBy),
            parseMode = ParseMode.MARKDOWN_V2
        )
    }

    fun sendUpdateFailedToAdmin(updateResponse: String) {
        bot.sendMessage(
            chatId = ChatId.fromId(reportUserId.toLong()),
            text = Messages.serverUpdateFailedReport(updateResponse),
            parseMode = ParseMode.MARKDOWN_V2
        )
    }

    private fun sendReportToAdmin(actionMadeBy: User, commandText: String) {
        bot.sendMessage(
            chatId = ChatId.fromId(reportUserId.toLong()),
            text = Messages.reportNonAdminRequest(actionMadeBy, commandText),
            parseMode = ParseMode.MARKDOWN_V2
        )
    }

}
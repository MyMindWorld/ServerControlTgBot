package ru.gromov.serverControlTgBot.service

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import org.junit.jupiter.api.*
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import ru.gromov.serverControlTgBot.ServerControlTgBot
import ru.gromov.serverControlTgBot.testData.*
import ru.gromov.serverControlTgBot.utils.Messages
@Import(BotCommandsServiceTest.BotCommandsServiceConfig::class)
class BotCommandsServiceTest : BaseServiceTest() {

    companion object {
        private val adminService: AdminService = Mockito.mock(AdminService::class.java)
        private val consoleService: ConsoleService = Mockito.mock(ConsoleService::class.java)

    }

    @TestConfiguration
    @Import(ServerControlTgBot::class)
    open class BotCommandsServiceConfig {
        @Bean
        @Primary
        open fun adminService(): AdminService {
            return adminService
        }
        @Bean
        @Primary
        open fun consoleService(): ConsoleService {
            return consoleService
        }
    }

    @Autowired
    lateinit var botCommandsService: BotCommandsService

    @Nested
    inner class ShouldProcessStartMessage {
        @Test
        fun `should not show keyboard to random user`() {
            Mockito.doThrow(IllegalAccessError::class.java).`when`(adminService).denyNotPrivilegedAndNotifyAdmin(
                restrictedUserAlice.user, "Can i use this bot?"
            )

            assertThrows<IllegalAccessError> {
                botCommandsService.startBot(messageWithText(restrictedUserAlice, "Can i use this bot?"))
            }

            Mockito.verify(bot, Mockito.never()).sendMessage(
                chatId = ChatId.fromId(restrictedUserAlice.chat.id),
                text = Messages.welcomeMessage(),
                replyMarkup = botCommandsService.welcomeKeyboardMarkup,
                parseMode = ParseMode.MARKDOWN_V2
            )
        }

        @Test
        fun `should show keyboard to allowed User`() {
            Mockito.doNothing().`when`(adminService).denyNotPrivilegedAndNotifyAdmin(
                allowedUserMarshall.user, "Hello"
            )

            botCommandsService.startBot(messageWithText(allowedUserMarshall, "Hello"))

            Mockito.verify(bot, Mockito.times(1)).sendMessage(
                chatId = ChatId.fromId(allowedUserMarshall.chat.id),
                text = Messages.welcomeMessage(),
                replyMarkup = botCommandsService.welcomeKeyboardMarkup,
                parseMode = ParseMode.MARKDOWN_V2
            )
        }
    }

    @Nested
    inner class WhenServerIsRunning {

        private val callBackMessage = callBackMessage(allowedUserMarshall, "Hello", data = "Any")

        @BeforeEach
        fun allowJockeyUsageOfBot() {
            Mockito.doNothing().`when`(adminService).denyNotPrivilegedAndNotifyAdmin(allowedUserJockey.user, "Hello")
            Mockito.doReturn(true).`when`(consoleService).isServerRunning()
        }

        @AfterEach
        fun resetCalls() {
            Mockito.clearInvocations(bot, adminService, consoleService)
        }


        @Test
        fun `should not start server`() {
            assertThrows<IllegalStateException> {
                botCommandsService.startServer(callBackMessage)
            }

            Mockito.verify(consoleService, Mockito.never()).startServer()

            Mockito.verify(bot, Mockito.times(1)).answerCallbackQuery(
                callbackQueryId = callBackMessage.id,
                showAlert = true,
                text = Messages.serverAlreadyRunning()
            )
        }

        @Test
        fun `should not update server`() {
            assertThrows<IllegalStateException> {
                botCommandsService.updateServer(callBackMessage)
            }

            Mockito.verify(consoleService, Mockito.never()).updateServer()

            Mockito.verify(bot, Mockito.times(1)).answerCallbackQuery(
                callbackQueryId = callBackMessage.id,
                showAlert = true,
                text = Messages.serverAlreadyRunning()
            )
        }

        @Test
        fun `should not backup server`() {
            assertThrows<IllegalStateException> {
                botCommandsService.backupServer(callBackMessage)
            }

            Mockito.verify(consoleService, Mockito.never()).backupServer()

            Mockito.verify(bot, Mockito.times(1)).answerCallbackQuery(
                callbackQueryId = callBackMessage.id,
                showAlert = true,
                text = Messages.serverAlreadyRunning()
            )
        }

        @Test
        fun `should stop server`() {
            botCommandsService.stopServer(callBackMessage)

            Mockito.verify(consoleService, Mockito.times(1)).stopServer()

            Mockito.verify(bot, Mockito.times(1)).answerCallbackQuery(
                callbackQueryId = callBackMessage.id,
                showAlert = true,
                text = Messages.serverStopped()
            )
        }

        @Test
        fun `should show server status`() {
            botCommandsService.serverStatus(callBackMessage)

            Mockito.verify(consoleService, Mockito.times(1)).isServerRunning()

            Mockito.verify(bot, Mockito.times(1)).answerCallbackQuery(
                callbackQueryId = callBackMessage.id,
                showAlert = true,
                text = Messages.serverStatus(true)
            )
        }
    }

    @Nested
    inner class WhenServerIsStopped {

        private val callbackQuery = callBackMessage(allowedUserMarshall, "Hello", data = "Any")

        @BeforeEach
        fun allowMarshallUsageOfBot() {
            Mockito.doNothing().`when`(adminService).denyNotPrivilegedAndNotifyAdmin(allowedUserMarshall.user, "Hello")
            Mockito.doReturn(false).`when`(consoleService).isServerRunning()
        }

        @AfterEach
        fun resetCalls() {
            Mockito.clearInvocations(bot, adminService, consoleService)
        }

        @Test
        fun `should start server`() {
            botCommandsService.startServer(callbackQuery)

            Mockito.verify(consoleService, Mockito.times(1)).startServer()

            Mockito.verify(bot, Mockito.times(1)).answerCallbackQuery(
                callbackQueryId = callbackQuery.id,
                showAlert = true,
                text = Messages.serverStarted()
            )
        }

        @Test
        fun `should update server`() {
            Mockito.doReturn("Success! App ").`when`(consoleService).updateServer()
            botCommandsService.updateServer(callbackQuery)

            Mockito.verify(consoleService, Mockito.times(1)).updateServer()

            Mockito.verify(bot, Mockito.times(1)).sendMessage(
                chatId = ChatId.fromId(callbackQuery.from.id),
                text = Messages.serverUpdated(),
                parseMode = ParseMode.MARKDOWN_V2
            )
        }

        @Test
        fun `should backup server`() {
            botCommandsService.backupServer(callbackQuery)

            Mockito.verify(consoleService, Mockito.times(1)).backupServer()

            Mockito.verify(bot, Mockito.times(1)).answerCallbackQuery(
                callbackQueryId = callbackQuery.id,
                showAlert = true,
                text = Messages.serverBackedUp()
            )
        }

        @Test
        fun `should stop server`() {
            botCommandsService.stopServer(callbackQuery)

            Mockito.verify(consoleService, Mockito.times(1)).stopServer()

            Mockito.verify(bot, Mockito.times(1)).answerCallbackQuery(
                callbackQueryId = callbackQuery.id,
                showAlert = true,
                text = Messages.serverStopped()
            )
        }

        @Test
        fun `should show server status`() {
            botCommandsService.serverStatus(callbackQuery)

            Mockito.verify(consoleService, Mockito.times(1)).isServerRunning()

            Mockito.verify(bot, Mockito.times(1)).answerCallbackQuery(
                callbackQueryId = callbackQuery.id,
                showAlert = true,
                text = Messages.serverStatus(false)
            )
        }
    }


    @Nested
    inner class Any {

    }


}
package ru.gromov.serverControlTgBot.service

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import ru.gromov.serverControlTgBot.testData.allowedUserJockey
import ru.gromov.serverControlTgBot.testData.allowedUserMarshall
import ru.gromov.serverControlTgBot.testData.reportUserAegis
import ru.gromov.serverControlTgBot.testData.restrictedUserAlice
import ru.gromov.serverControlTgBot.utils.Messages

class AdminServiceTest : BaseServiceTest() {


    @Autowired
    lateinit var adminService: AdminService

    @Nested
    inner class ShouldRecognizeReportUsers {
        @Test
        fun `user from reportUserId variable should be recognized as reportUserId`() {
            Assertions.assertThat(adminService.isReportUser(reportUserAegis.user)).isTrue
        }

        @Test
        fun `allowed but not report user should not be recognized as reportUserId`() {
            Assertions.assertThat(adminService.isReportUser(allowedUserMarshall.user)).isFalse
        }
    }

    @Nested
    inner class ShouldRecognizePrivilegedUsers {
        @Test
        fun `action from Non privileged user should trigger report to admin`() {
            assertThrows<IllegalAccessError> {
                adminService.denyNotPrivilegedAndNotifyAdmin(
                    restrictedUserAlice.user,
                    "LET ME IIIIIN!"
                )
            }

            Mockito.verify(bot, Mockito.atLeastOnce()).sendMessage(
                chatId = ChatId.fromId(reportUserAegis.chat.id),
                text = Messages.reportNonAdminRequest(restrictedUserAlice.user, "LET ME IIIIIN!"),
                parseMode = ParseMode.MARKDOWN_V2
            )
        }

        @Test
        fun `action from privileged user should not trigger report to admin`() {
            adminService.denyNotPrivilegedAndNotifyAdmin(
                allowedUserMarshall.user,
                "Im home"
            )

            Mockito.verify(bot, Mockito.never()).sendMessage(
                chatId = ChatId.fromId(reportUserAegis.chat.id),
                text = Messages.reportNonAdminRequest(allowedUserMarshall.user, "Im home"),
                parseMode = ParseMode.MARKDOWN_V2
            )
        }
    }


}
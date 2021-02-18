package ru.gromov.serverControlTgBot.utils

import com.github.kotlintelegrambot.entities.User

class Messages {
    companion object ResponseMessages {
        fun welcomeMessage() = "Привет! У тебя есть все нужные права, пользуйся ответственно!)".toValidTgMessage()

        fun unauthorized() = "У тебя нет прав на использование этого бота!".toValidTgMessage()

        fun serverStartedReport(user: User) =
            "Сервер запущен пользователем : ".toValidTgMessage() + "[${user.getUsernameOrInitials()}](tg://user?id=${user.id}) ".toValidInlineLink()
                .toValidInlineLink()

        fun serverStarted() = "Сервер запущен"

        fun serverAlreadyRunning() = "Действие возможно только при остановленном сервере".toValidTgMessage()

        fun serverStoppedReport(user: User) =
            "Сервер остановлен пользователем : ".toValidTgMessage() + "[${user.getUsernameOrInitials()}](tg://user?id=${user.id}) ".toValidInlineLink()
                .toValidInlineLink()

        fun serverStopped() = "Сервер остановлен"

        fun serverUpdateFailed() = "Обновление сервера произошло с ошибкой! Свяжитесь с администратором.".toValidTgMessage()

        fun serverUpdateFailedReport(updateResponse:String) = "Обновление сервера произошло с ошибкой! Проверьте логи и при необходимости откатите всё назад. Логи : $updateResponse".toValidTgMessage()

        fun serverUpdated() = "Сервер обновлён"

        fun serverDoesntNeedUpdate() = "Сервер уже последней версии"

        fun serverStatus(serverRunning: Boolean): String {
            return if (serverRunning) {
                "Сервер сейчас работает"
            } else {
                "Сервер сейчас не запущен"
            }
        }

        fun serverBackedUp() = "Бекап успешно создан"

        fun reportNonAdminRequest(user: User,message: String) =
            "[${user.getUsernameOrInitials()}](tg://user?id=${user.id}) ".toValidInlineLink() + "Отправил : \n$message".toValidTgMessage()

    }
}
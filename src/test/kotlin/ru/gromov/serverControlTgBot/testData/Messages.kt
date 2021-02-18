package ru.gromov.serverControlTgBot.testData

import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.Message

fun messageWithAnyText(userAndChat: UserAndChat) = Message(
    1L,
    from = userAndChat.user,
    chat = userAndChat.chat,
    date = 1,
    text = "Some nonsense"
)

fun messageWithText(userAndChat: UserAndChat, text: String) = Message(
    1L,
    from = userAndChat.user,
    chat = userAndChat.chat,
    date = 1,
    text = text
)

fun callBackMessage(userAndChat: UserAndChat, text: String, data: String) = CallbackQuery(
    "934436877766118451",
    from = userAndChat.user,
    message = Message(
        messageId = 123L,
        from = botUser.user,
        chat = userAndChat.chat,
        date = 1,
        text = text
    ),
    data = data,
    chatInstance = "NONCORRELATEDNUMBER"
)

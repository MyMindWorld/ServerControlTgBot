package ru.gromov.serverControlTgBot.testData

import com.github.kotlintelegrambot.entities.Chat
import com.github.kotlintelegrambot.entities.User

data class UserAndChat(val user: User, val chat: Chat)

val reportUserAegis =
    UserAndChat(
        User(id = 1L, firstName = "Aegis", lastName = "Person", username = "@Aegis", isBot = false),
        Chat(1, type = "private")
    )

val allowedUserMarshall =
    UserAndChat(
        User(id = 2L, firstName = "Marshall", lastName = "Broadson", username = "@Aegis", isBot = false),
        Chat(2, type = "private")
    )

val allowedUserJockey =
    UserAndChat(
        User(id = 3L, firstName = "Jockey", lastName = "Spider", username = "@Aegis", isBot = false),
        Chat(3, type = "private")
    )

val restrictedUserBob =
    UserAndChat(
        User(id = 4L, firstName = "Bob", lastName = "Tester", username = "@ANON", isBot = false),
        Chat(4, type = "private")
    )

val restrictedUserAlice =
    UserAndChat(
        User(id = 5L, firstName = "Alice", lastName = "PenTester", username = "@NOTANON", isBot = false),
        Chat(5, type = "private")
    )


fun randomUser(id: Long) =
    UserAndChat(
        User(id = id, firstName = "rand$id", lastName = "rand$id", username = "@RandUser", isBot = false),
        Chat(id, type = "private")
    )


val botUser = UserAndChat(
    User(id = 666L, firstName = "Bot", lastName = "", username = "@TestBot", isBot = true),
    Chat(666, type = "private")
)
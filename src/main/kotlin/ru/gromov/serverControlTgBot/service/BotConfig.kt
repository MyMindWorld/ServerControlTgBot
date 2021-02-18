package ru.gromov.serverControlTgBot.service

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.logging.LogLevel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BotConfig @Autowired constructor(
    @Value("\${bot.token:NONE}") private var botToken: String
) {

    @Bean
    open fun bot(): Bot {
        if (botToken == "NONE") {
            throw IllegalStateException("Setup bot token!")
        }

        return bot {
            token = botToken
            timeout = 30
            logLevel = LogLevel.All()
        }
    }
}
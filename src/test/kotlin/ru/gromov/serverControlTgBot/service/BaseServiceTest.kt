package ru.gromov.serverControlTgBot.service

import com.github.kotlintelegrambot.Bot
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import ru.gromov.serverControlTgBot.BotInitComponent
import ru.gromov.serverControlTgBot.ServerControlTgBot


@SpringBootTest
@ExtendWith(SpringExtension::class)
@TestPropertySource("classpath:application-test.properties")
@Import(BaseServiceTest.TestConfig::class)
open class BaseServiceTest {

    @MockBean
    lateinit var botInitComponent: BotInitComponent

    @MockBean
    lateinit var botConfig: BotConfig

    companion object {
        val bot: Bot = Mockito.mock(Bot::class.java)
    }

    @TestConfiguration
    @Import(ServerControlTgBot::class)
    open class TestConfig {
        @Bean
        @Primary
        open fun bot(): Bot {
            return bot
        }
    }

}
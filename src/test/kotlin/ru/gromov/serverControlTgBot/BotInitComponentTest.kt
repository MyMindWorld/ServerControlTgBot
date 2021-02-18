package ru.gromov.serverControlTgBot

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

import com.github.tomakehurst.wiremock.WireMockServer
import org.junit.jupiter.api.AfterEach

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled

@Disabled // TODO finish wiremock stubbing
@EntityScan(basePackages = ["ru.gromov.guessWhoTgBot.db.model"])
@PropertySource("classpath:application-test.properties")
@SpringBootTest
@ExtendWith(SpringExtension::class)
internal class BotInitComponentTest {

    var wireMockServer: WireMockServer? = null

    @BeforeEach
    fun setup() {
        wireMockServer = WireMockServer(8090)
        wireMockServer!!.start()
        setupStub()
    }

    @AfterEach
    fun teardown() {
        wireMockServer!!.stop()
    }

    fun setupStub() {
        wireMockServer!!.stubFor(
            get(urlEqualTo("https://api.telegram.org/bot1123123123:ASDQWERTY/getUpdates?timeout=30"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(
                            "{\"ok\":true,\"result\":[{\"update_id\":621499188,\n" +
                                    "\"message\":" +
                                    "{\"message_id\":333,\"from\":" +
                                    "{\"id\":123,\"is_bot\":false,\"first_name\":\"ASD\",\"last_name\":\"ASDA\",\"username\":\"ASDASD\",\"language_code\":\"ru\"}," +
                                    "\"chat\":{\"id\":123,\"first_name\":\"ASD\",\"last_name\":\"ASDA\",\"username\":\"ASDASD\",\"type\":\"private\"},\"date\":1612709138,\"text\":\"Create Game\"}}]}"
                        )
                )
        )
    }


//    var pollingUrl = "https://api.telegram.org/bot${getProp("bot.token")}/getUpdates?timeout=30"
//
//    @Test
//    fun pollingUrlShouldBeFromTestResources() {
//        assertThat(pollingUrl).isEqualTo("https://api.telegram.org/bot1123123123:ASDQWERTY/getUpdates?timeout=30")
//    }

    @Test
    fun getUpdatesShouldPointAtMock() {

    }
}


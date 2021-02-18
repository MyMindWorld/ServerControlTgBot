package ru.gromov.serverControlTgBot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource

@EntityScan(basePackages = ["ru.gromov.serverControlTgBot.db.model"])
@PropertySource("classpath:application.properties")
@SpringBootApplication
open class ServerControlTgBot

fun main(args: Array<String>) {
    runApplication<ServerControlTgBot>(*args)
}



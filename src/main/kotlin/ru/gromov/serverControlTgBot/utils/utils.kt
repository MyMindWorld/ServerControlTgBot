package ru.gromov.serverControlTgBot.utils

import com.github.kotlintelegrambot.entities.User

fun String.toValidTgMessage() = this
    .replace(".", "\\.")
    .replace("=", "\\=")
    .replace("-", "\\-")
    .replace("_", "\\_")
    .replace("*", "\\*")
    .replace("[", "\\[")
    .replace("]", "\\]")
    .replace("(", "\\(")
    .replace(")", "\\)")
    .replace("!", "\\!")
    .replace("            ", "")
    .trimIndent()

fun String.toValidInlineLink() = this
    .replace("\\", "\\\\")
    .replace("            ", "")

fun User.getUsernameOrInitials() = username?: firstName + lastName
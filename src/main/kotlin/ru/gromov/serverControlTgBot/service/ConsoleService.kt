package ru.gromov.serverControlTgBot.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.rmi.UnexpectedException
import java.util.concurrent.TimeUnit


@Service
class ConsoleService constructor(
    @Value("\${bot.runPath}") private var steamCmdPath: String,
    @Value("\${bot.startCommand}") private var startCommand: String,
    @Value("\${bot.updateCommand}") private var updateCommand: String,
    @Value("\${bot.updateExecutableLocation}") private var updateExecutableLocation: String,
    @Value("\${bot.listProcessesExecutable}") private var listProcessesExecutable: String,
    @Value("\${bot.serverName}") private var serverName: String,
    @Value("\${bot.stopCommand}") private var stopCommand: String,
    @Value("\${bot.backupCommand}") private var backupCommand: String
) {

    fun startServer() {
        startCommand.runCommand(File(steamCmdPath))
    }

    fun stopServer() {
        stopCommand.runCommand(File(steamCmdPath))
    }

    fun updateServer(): String {

        val response = updateCommand.runAndWaitCaptureCommand(File(updateExecutableLocation))
        if (response != null) {
            return response
        } else {
            throw UnexpectedException("Server status not executed")
        }
    }

    fun backupServer() {
        backupCommand.runCommand(File(steamCmdPath))
    }

    fun isServerRunning(): Boolean {
        val response = listProcessesExecutable.runCommandCapture(File(steamCmdPath))
        if (response != null) {
            return response.contains(serverName)
        } else {
            throw UnexpectedException("Server status not executed")
        }
    }

    private fun String.runCommand(workingDir: File) {
        ProcessBuilder(*split(" ").toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor(15, TimeUnit.SECONDS)
    }

    private fun String.runAndWaitCaptureCommand(workingDir: File): String? {
        return try {
            val parts = this.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            proc.waitFor(15, TimeUnit.MINUTES)
            proc.inputStream.bufferedReader().readText()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun String.runCommandCapture(workingDir: File): String? {
        return try {
            val parts = this.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            proc.waitFor(5, TimeUnit.SECONDS)
            proc.inputStream.bufferedReader().readText()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
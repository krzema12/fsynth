package it.krzeminski.fsynth

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.float
import com.github.ajalt.clikt.parameters.types.path
import it.krzeminski.fsynth.generated.gitInfo
import it.krzeminski.fsynth.songs.allSongs
import it.krzeminski.fsynth.types.Song
import java.nio.file.Path
import java.time.Instant

fun main(args: Array<String>) = Synthesize().main(args)

private class Synthesize : CliktCommand(name = "fsynth") {
    val song: Song by option(
            "--song",
            help = "Name of the song to play",
            metavar = "NAME")
            .choice(allSongs.map { it.name to it }.toMap())
            .required()

    val startTime: Float by option(
            help = "Optional. Starts the payback omitting the given amount of seconds",
            metavar = "SECONDS")
            .float()
            .default(0.0f)
            .validate {
                require(it >= 0.0f) {
                    "Start time should be positive!"
                }
            }

    val outputFile: Path? by option(
            help = "Optional. If given, the song will not be played, but instead, written as a WAVE file",
            metavar = "PATH")
            .path()

    override fun run() {
        printIntroduction()

        val audioStream = song.asAudioStream(samplesPerSecond = 44100, sampleSizeInBits = 8, startTime = startTime)

        val outputFileFinal = outputFile
        if (outputFileFinal != null) {
            audioStream?.saveAsWaveFile(outputFileFinal)
        } else {
            audioStream?.playAndBlockUntilFinishes()
        }
    }
}

private fun printIntroduction() {
    println("fsynth by Piotr Krzemiński")
    println("Version ${gitInfo.latestCommit.sha1.substring(0, 8)} " +
            "from ${Instant.ofEpochSecond(gitInfo.latestCommit.timeUnixTimestamp)}")
}

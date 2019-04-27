package it.krzeminski.fsynth.synthesis

import it.krzeminski.fsynth.squareWave
import it.krzeminski.fsynth.synthesis.types.SongForSynthesis
import it.krzeminski.fsynth.synthesis.types.TrackForSynthesis
import it.krzeminski.fsynth.types.BoundedWaveform
import it.krzeminski.testutils.plotassert.assertFunctionConformsTo
import kotlin.test.Test
import kotlin.test.assertEquals

/* ktlint-disable no-multi-spaces paren-spacing */

class SynthesisTest {
    @Test
    fun singleTrackWithSingleSegment() {
        val testSong = SongForSynthesis(
                tracks = listOf(
                        TrackForSynthesis(
                                segments = listOf(
                                        BoundedWaveform(squareWave(4.0f), 0.5f)),
                                volume = 1.0f)))

        val songEvaluationFunction = testSong.buildSongEvaluationFunction()
        assertEquals(0.5f, testSong.durationInSeconds)
        assertFunctionConformsTo(songEvaluationFunction) {
            row(1.0f,   "XXXXXXXXI      IXXXXXXXI                                     ")
            row(0.0f,   "        I      I       I      IXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
            row(-1.0f,  "        IXXXXXXI       IXXXXXXI                              ")
            xAxis {
                markers("|                             |                             |")
                values( 0.0f,                         0.5f,                         1.0f)
            }
        }
    }

    @Test
    fun durationIsCorrectlyCalculated() {
        val testSong = SongForSynthesis(
                tracks = listOf(
                        TrackForSynthesis(
                                segments = listOf(
                                        BoundedWaveform(squareWave(4.0f), 0.1f),
                                        BoundedWaveform(squareWave(4.0f), 0.2f),
                                        BoundedWaveform(squareWave(4.0f), 0.3f)),
                                volume = 1.0f),
                        TrackForSynthesis(
                                segments = listOf(
                                        BoundedWaveform(squareWave(4.0f), 0.5f),
                                        BoundedWaveform(squareWave(4.0f), 2.5f),
                                        BoundedWaveform(squareWave(4.0f), 1.2f)),
                                volume = 1.0f),
                        TrackForSynthesis(
                                segments = listOf(
                                        BoundedWaveform(squareWave(4.0f), 1.0f),
                                        BoundedWaveform(squareWave(4.0f), 0.2f),
                                        BoundedWaveform(squareWave(4.0f), 0.2f)),
                                volume = 1.0f)))

        assertEquals(0.5f + 2.5f + 1.2f, testSong.durationInSeconds)
    }

    @Test
    fun durationForEmptySongForSynthesis() {
        val testSong = SongForSynthesis(
                tracks = emptyList())

        assertEquals(0.0f, testSong.durationInSeconds)
    }

    @Test
    fun multipleTracksWithEqualVolumesAreCorrectlySynthesized() {
        val testSong = SongForSynthesis(
                tracks = listOf(
                        TrackForSynthesis(
                                segments = listOf(
                                        BoundedWaveform(squareWave(8.0f), 0.5f)),
                                volume = 0.5f),
                        TrackForSynthesis(
                                segments = listOf(
                                        BoundedWaveform(squareWave(2.0f), 1.0f)),
                                volume = 0.5f)))

        val songEvaluationFunction = testSong.buildSongEvaluationFunction()
        assertFunctionConformsTo(songEvaluationFunction) {
            row(1.0f,   "XXXXI   IXXXI                                                ")
            row(0.5f,   "    I   I   I                  IXXXXXXXXXXXXXXI              ")
            row(0.0f,   "    IXXXI   IXXXXXXI   IXXXI   I              I             I")
            row(-0.5f,  "                   I   I   I   I              IXXXXXXXXXXXXXI")
            row(-1.0f,  "                   IXXXI   IXXXI                             ")
            xAxis {
                markers("|                             |                             |")
                values( 0.0f,                         0.5f,                         1.0f)
            }
        }
    }

    @Test
    fun multipleTracksWithDifferentVolumesAreCorrectlySynthesized() {
        val testSong = SongForSynthesis(
                tracks = listOf(
                        TrackForSynthesis(
                                segments = listOf(
                                        BoundedWaveform(squareWave(8.0f), 0.5f)),
                                volume = 0.5f),
                        TrackForSynthesis(
                                segments = listOf(
                                        BoundedWaveform(squareWave(2.0f), 1.0f)),
                                volume = 0.25f)))

        val songEvaluationFunction = testSong.buildSongEvaluationFunction()
        assertFunctionConformsTo(songEvaluationFunction) {
            row(1.0f,   "                                                             ")
            row(0.75f,  "XXXXI   IXXXI                                                ")
            row(0.5f,   "    I   I   I                                                ")
            row(0.25f,  "    I   I   I   IXXI   IXXXI   IXXXXXXXXXXXXXXI              ")
            row(0.0f,   "    I   I   I   I  I   I   I   I              I             I")
            row(-0.25f, "    IXXXI   IXXXI  I   I   I   I              IXXXXXXXXXXXXXI")
            row(-0.5f,  "                   I   I   I   I                             ")
            row(-0.75f, "                   IXXXI   IXXXI                             ")
            row(-1.0f,  "                                                             ")
            xAxis {
                markers("|                             |                             |")
                values( 0.0f,                         0.5f,                         1.0f)
            }
        }
    }

    @Test
    fun multipleTracksAreCorrectlySynthesizedForSongLengthBeyondBucketSize() {
        val testSong = SongForSynthesis(
                tracks = listOf(
                        TrackForSynthesis(
                                segments = listOf(
                                        BoundedWaveform(squareWave(4.0f), 1.0f)),
                                volume = 0.5f),
                        TrackForSynthesis(
                                segments = listOf(
                                        BoundedWaveform(squareWave(1.0f), 2.0f)),
                                volume = 0.5f)))

        val songEvaluationFunction = testSong.buildSongEvaluationFunction()
        assertFunctionConformsTo(songEvaluationFunction) {
            row(1.0f,   "XXXXI   IXXXI                                                ")
            row(0.5f,   "    I   I   I                 IXXXXXXXXXXXXXXXI              ")
            row(0.0f,   "    IXXXI   IXXXXXXI   IXXXI  I               I             I")
            row(-0.5f,  "                   I   I   I  I               IXXXXXXXXXXXXXI")
            row(-1.0f,  "                   IXXXI   IXXI                              ")
            xAxis {
                markers("|              |              |              |              |")
                values( 0.0f,          0.5f,          1.0f,          1.5f,          2.0f)
            }
        }
    }
}

/* ktlint-disable no-multi-spaces paren-spacing */

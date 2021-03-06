package it.krzeminski.fsynth.synthesis

import it.krzeminski.fsynth.squareWave
import it.krzeminski.fsynth.synthesis.types.SongForSynthesis
import it.krzeminski.fsynth.synthesis.types.TrackForSynthesis
import it.krzeminski.fsynth.types.BoundedWaveform
import it.krzeminski.fsynth.types.PositionedBoundedWaveform
import it.krzeminski.visassert.assertFunctionConformsTo
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
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 0.5f), 0.0f)),
                                volume = 1.0f)))

        val songEvaluationFunction = testSong.buildSongEvaluationFunction()
        assertEquals(0.5f, testSong.durationInSeconds)
        assertFunctionConformsTo(songEvaluationFunction) {
            row(1.0f,   "XXXXXXXXi      iXXXXXXXi                                     ")
            row(0.0f,   "        i      i       i      iXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
            row(-1.0f,  "        iXXXXXXi       iXXXXXXi                              ")
            xAxis {
                markers("|                             |                             |")
                values( 0.0f,                         0.5f,                         1.0f)
            }
        }
    }

    @Test
    fun durationIsCorrectlyCalculatedAmongMultipleTracksForNonOverlappingSegments() {
        val testSong = SongForSynthesis(
                tracks = listOf(
                        TrackForSynthesis(
                                segments = listOf(
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 0.1f), 0.0f),
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 0.2f), 0.1f),
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 0.3f), 0.3f)),
                                volume = 1.0f),
                        TrackForSynthesis(
                                segments = listOf(
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 0.5f), 0.0f),
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 2.5f), 0.5f),
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 1.2f), 3.0f)),
                                volume = 1.0f),
                        TrackForSynthesis(
                                segments = listOf(
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 1.0f), 0.0f),
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 0.2f), 1.0f),
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 0.2f), 1.2f)),
                                volume = 1.0f)))

        assertEquals(0.5f + 2.5f + 1.2f, testSong.durationInSeconds)
    }

    @Test
    fun durationIsCorrectlyCalculatedForOverlappingSegments() {
        val testSong = SongForSynthesis(
                tracks = listOf(
                        TrackForSynthesis(
                                segments = listOf(
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 0.5f), 0.0f),
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 2.0f), 0.25f),
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 0.5f), 0.5f)),
                                volume = 1.0f)))

        assertEquals(2.0f + 0.25f, testSong.durationInSeconds)
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
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(8.0f), 0.5f), 0.0f)),
                                volume = 0.5f),
                        TrackForSynthesis(
                                segments = listOf(
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(2.0f), 1.0f), 0.0f)),
                                volume = 0.5f)))

        val songEvaluationFunction = testSong.buildSongEvaluationFunction()
        assertFunctionConformsTo(songEvaluationFunction) {
            row(1.0f,   "XXXXi   iXXXi                                                ")
            row(0.5f,   "    i   i   i                  iXXXXXXXXXXXXXXi              ")
            row(0.0f,   "    iXXXi   iXXXXXXi   iXXXi   i              i             i")
            row(-0.5f,  "                   i   i   i   i              iXXXXXXXXXXXXXi")
            row(-1.0f,  "                   iXXXi   iXXXi                             ")
            xAxis {
                markers("|                             |                             |")
                values( 0.0f,                         0.5f,                         1.0f)
            }
        }
    }

    @Test
    fun overlappingSegmentsAreAreCorrectlySynthesized() {
        val testSong = SongForSynthesis(
                tracks = listOf(
                        TrackForSynthesis(
                                segments = listOf(
                                        PositionedBoundedWaveform(BoundedWaveform({ 1.0f }, 0.75f), 0.0f),
                                        PositionedBoundedWaveform(BoundedWaveform({ 0.5f }, 0.25f), 0.25f)),
                                volume = 1.0f)))

        val songEvaluationFunction = testSong.buildSongEvaluationFunction()
        assertFunctionConformsTo(songEvaluationFunction) {
            row(1.5f,   "               XXXXXXXXXXXXXXXX               ")
            row(1.0f,   "XXXXXXXXXXXXXXX                XXXXXXXXXXXXXXX")
            xAxis {
                markers("|              |              |              |")
                values( 0.0f,          0.25f,         0.5f,          0.75f)
            }
        }
    }

    @Test
    fun multipleTracksWithDifferentVolumesAreCorrectlySynthesized() {
        val testSong = SongForSynthesis(
                tracks = listOf(
                        TrackForSynthesis(
                                segments = listOf(
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(8.0f), 0.5f), 0.0f)),
                                volume = 0.5f),
                        TrackForSynthesis(
                                segments = listOf(
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(2.0f), 1.0f), 0.0f)),
                                volume = 0.25f)))

        val songEvaluationFunction = testSong.buildSongEvaluationFunction()
        assertFunctionConformsTo(songEvaluationFunction) {
            row(1.0f,   "                                                             ")
            row(0.75f,  "XXXXi   iXXXi                                                ")
            row(0.5f,   "    i   i   i                                                ")
            row(0.25f,  "    i   i   i   iXXi   iXXXi   iXXXXXXXXXXXXXXi              ")
            row(0.0f,   "    i   i   i   i  i   i   i   i              i             i")
            row(-0.25f, "    iXXXi   iXXXi  i   i   i   i              iXXXXXXXXXXXXXi")
            row(-0.5f,  "                   i   i   i   i                             ")
            row(-0.75f, "                   iXXXi   iXXXi                             ")
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
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(4.0f), 1.0f), 0.0f)),
                                volume = 0.5f),
                        TrackForSynthesis(
                                segments = listOf(
                                        PositionedBoundedWaveform(BoundedWaveform(squareWave(1.0f), 2.0f), 0.0f)),
                                volume = 0.5f)))

        val songEvaluationFunction = testSong.buildSongEvaluationFunction()
        assertFunctionConformsTo(songEvaluationFunction) {
            row(1.0f,   "XXXXi   iXXXi                                                ")
            row(0.5f,   "    i   i   i                 iXXXXXXXXXXXXXXXi              ")
            row(0.0f,   "    iXXXi   iXXXXXXi   iXXXi  i               i             i")
            row(-0.5f,  "                   i   i   i  i               iXXXXXXXXXXXXXi")
            row(-1.0f,  "                   iXXXi   iXXi                              ")
            xAxis {
                markers("|              |              |              |              |")
                values( 0.0f,          0.5f,          1.0f,          1.5f,          2.0f)
            }
        }
    }
}

/* ktlint-disable no-multi-spaces paren-spacing */

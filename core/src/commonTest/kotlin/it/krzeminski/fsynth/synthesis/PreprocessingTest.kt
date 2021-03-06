package it.krzeminski.fsynth.synthesis

import it.krzeminski.fsynth.effects.envelope.AdsrEnvelopeDefinition
import it.krzeminski.fsynth.effects.envelope.buildEnvelopeFunction
import it.krzeminski.fsynth.instruments.Instrument
import it.krzeminski.fsynth.silence
import it.krzeminski.fsynth.sineWave
import it.krzeminski.fsynth.testutils.assertValuesEqual
import it.krzeminski.fsynth.types.BoundedWaveform
import it.krzeminski.fsynth.types.MusicNote.* // ktlint-disable no-wildcard-imports
import it.krzeminski.fsynth.types.MusicNoteTransition
import it.krzeminski.fsynth.types.NoteValue
import it.krzeminski.fsynth.types.Song
import it.krzeminski.fsynth.types.Track
import it.krzeminski.fsynth.types.TrackSegment
import it.krzeminski.fsynth.types.times
import it.krzeminski.visassert.assertFunctionConformsTo
import kotlin.test.Test
import kotlin.test.assertEquals

class PreprocessingTest {
    companion object {
        val testInstrumentForNoteC4 = { _: Float -> 123.0f }
        val testInstrumentForNoteE4 = { _: Float -> 456.0f }
        val testInstrumentForNoteG4 = { _: Float -> 789.0f }
        val testInstrument = Instrument(
                waveform = { frequency: Float ->
                    when (frequency) {
                        1.0f -> sineWave(1.0f)
                        C4.frequency -> testInstrumentForNoteC4
                        E4.frequency -> testInstrumentForNoteE4
                        G4.frequency -> testInstrumentForNoteG4
                        else -> throw IllegalStateException("Only the tree above notes should be used in this test!")
                    }
                },
                envelope = buildEnvelopeFunction(
                        AdsrEnvelopeDefinition(
                                attackTime = 0.0f,
                                decayTime = 0.0f,
                                sustainLevel = 1.0f,
                                releaseTime = 0.0f)))
        val testInstrumentWithRelease = testInstrument.copy(
                envelope = buildEnvelopeFunction(
                        AdsrEnvelopeDefinition(
                                attackTime = 0.0f,
                                decayTime = 0.0f,
                                sustainLevel = 1.0f,
                                releaseTime = 0.5f)))
    }

    @Test
    fun severalSimpleNotes() {
        val testSong = Song(
                name = "Test song",
                beatsPerMinute = 240,
                tracks = listOf(
                        Track(
                                name = "Test track",
                                instrument = testInstrument,
                                segments = listOf(
                                        TrackSegment.SingleNote(NoteValue(1, 4), C4),
                                        TrackSegment.SingleNote(NoteValue(1, 8), E4),
                                        TrackSegment.SingleNote(NoteValue(1, 2), G4)
                                ),
                                volume = 1.0f
                        )
                )
        )

        val preprocessedTestSong = testSong.preprocessForSynthesis()

        assertEquals(preprocessedTestSong.tracks.size, 1)
        assertEquals(preprocessedTestSong.tracks[0].volume, 1.0f)
        assertEquals(preprocessedTestSong.tracks[0].segments.size, 3)
        with(preprocessedTestSong.tracks[0]) {
            with(segments[0]) {
                assertEquals(startTime, 0.0f)
                assertValuesEqual(boundedWaveform, BoundedWaveform(testInstrumentForNoteC4, 0.25f), delta = 0.001f)
            }
            with(segments[1]) {
                assertEquals(startTime, 0.25f)
                assertValuesEqual(boundedWaveform, BoundedWaveform(testInstrumentForNoteE4, 0.125f), delta = 0.001f)
            }
            with(segments[2]) {
                assertEquals(startTime, 0.375f)
                assertValuesEqual(boundedWaveform, BoundedWaveform(testInstrumentForNoteG4, 0.5f), delta = 0.001f)
            }
        }
    }

    @Test
    fun severalOverlappingNotes() {
        val testSong = Song(
                name = "Test song",
                beatsPerMinute = 240,
                tracks = listOf(
                        Track(
                                name = "Test track",
                                instrument = testInstrumentWithRelease,
                                segments = listOf(
                                        TrackSegment.SingleNote(NoteValue(1, 4), C4),
                                        TrackSegment.SingleNote(NoteValue(1, 8), E4),
                                        TrackSegment.SingleNote(NoteValue(1, 2), G4)
                                ),
                                volume = 1.0f
                        )
                )
        )

        val preprocessedTestSong = testSong.preprocessForSynthesis()

        assertEquals(preprocessedTestSong.tracks.size, 1)
        assertEquals(preprocessedTestSong.tracks[0].volume, 1.0f)
        assertEquals(preprocessedTestSong.tracks[0].segments.size, 3)
        with(preprocessedTestSong.tracks[0]) {
            with(segments[0]) {
                assertEquals(startTime, 0.0f)
                assertValuesEqual(boundedWaveform,
                        testInstrumentForNoteC4 * testInstrumentWithRelease.envelope(0.25f),
                        delta = 0.001f)
            }
            with(segments[1]) {
                assertEquals(startTime, 0.25f)
                assertValuesEqual(boundedWaveform,
                        testInstrumentForNoteE4 * testInstrumentWithRelease.envelope(0.125f),
                        delta = 0.001f)
            }
            with(segments[2]) {
                assertEquals(startTime, 0.375f)
                assertValuesEqual(boundedWaveform,
                        testInstrumentForNoteG4 * testInstrumentWithRelease.envelope(0.5f),
                        delta = 0.001f)
            }
        }
    }

    /* ktlint-disable no-multi-spaces paren-spacing */

    @Test
    fun glissando() {
        val testSong = Song(
                name = "Test song",
                beatsPerMinute = 240,
                tracks = listOf(
                        Track(
                                name = "Test track",
                                instrument = testInstrument,
                                segments = listOf(
                                        TrackSegment.Glissando(NoteValue(1, 4),
                                                MusicNoteTransition(VeryLowForTesting, A0))
                                ),
                                volume = 1.0f
                        )
                )
        )

        val preprocessedTestSong = testSong.preprocessForSynthesis()

        with (preprocessedTestSong.tracks[0]) {
            assertEquals(1, segments.size)
            assertEquals(0.0f, segments[0].startTime)
            assertEquals(0.25f, segments[0].boundedWaveform.duration)
            assertFunctionConformsTo(segments[0].boundedWaveform.waveform) {
                row(1.0f,   "        IIII                          III                   II              I          ")
                row(        "       I    II                       I   I                 I               I I         ")
                row(        "     II       I                           I               I   I               I        ")
                row(        "    I          I                    I                                     I            ")
                row(        "   I            I                  I       I             I     I                       ")
                row(        "                                            I                                          ")
                row(        "  I              I                I                                      I     I       ")
                row(        " I                I                                     I       I                      ")
                row(0.0f,   "X                                I           I                                         ")
                row(        "                   I                                   I         I              I     I")
                row(        "                    I           I             I                         I              ")
                row(        "                                                                                       ")
                row(        "                     I         I               I      I           I                    ")
                row(        "                      I       I                                        I         I   I ")
                row(        "                       I     I                  I    I             I                   ")
                row(        "                        I   I                    I  I                 I           I I  ")
                row(-1.0f,  "                         III                      II                II             I   ")
                xAxis {
                    markers("|                                                                                     |")
                    values( 0.0f,                                                                                0.25f)
                }
            }
        }
    }

    /* ktlint-disable no-multi-spaces paren-spacing */

    @Test
    fun chord() {
        val testSong = Song(
                name = "Test song",
                beatsPerMinute = 240,
                tracks = listOf(
                        Track(
                                name = "Test track",
                                instrument = testInstrument,
                                segments = listOf(
                                        TrackSegment.Chord(NoteValue(1, 4), listOf(C4, E4, G4))
                                ),
                                volume = 1.0f
                        )
                )
        )

        val preprocessedTestSong = testSong.preprocessForSynthesis()

        with (preprocessedTestSong.tracks[0]) {
            assertEquals(1, segments.size)
            assertEquals(0.0f, segments[0].startTime)
            assertEquals(0.25f, segments[0].boundedWaveform.duration)
            assertEquals(123.0f + 456.0f + 789.0f, segments[0].boundedWaveform.waveform(0.0f))
        }
    }

    @Test
    fun pause() {
        val testSong = Song(
                name = "Test song",
                beatsPerMinute = 240,
                tracks = listOf(
                        Track(
                                name = "Test track",
                                instrument = testInstrument,
                                segments = listOf(
                                        TrackSegment.Pause(NoteValue(1, 4))
                                ),
                                volume = 1.0f
                        )
                )
        )

        val preprocessedTestSong = testSong.preprocessForSynthesis()

        assertEquals(preprocessedTestSong.tracks.size, 1)
        assertEquals(preprocessedTestSong.tracks[0].volume, 1.0f)
        assertEquals(preprocessedTestSong.tracks[0].segments.size, 1)
        with(preprocessedTestSong.tracks[0]) {
            with(segments[0]) {
                assertEquals(startTime, 0.0f)
                assertValuesEqual(boundedWaveform, BoundedWaveform(silence, 0.25f), delta = 0.001f)
            }
        }
    }

    @Test
    fun severalTracks() {
        val testSong = Song(
                name = "Test song",
                beatsPerMinute = 240,
                tracks = listOf(
                        Track(
                                name = "Test track",
                                instrument = testInstrument,
                                segments = listOf(
                                        TrackSegment.SingleNote(NoteValue(1, 4), C4)
                                ),
                                volume = 1.0f
                        ),
                        Track(
                                name = "Test track 2",
                                instrument = testInstrument,
                                segments = listOf(
                                        TrackSegment.SingleNote(NoteValue(1, 8), E4)
                                ),
                                volume = 1.0f
                        )
                )
        )

        val preprocessedTestSong = testSong.preprocessForSynthesis()

        assertEquals(preprocessedTestSong.tracks.size, 2)
        with(preprocessedTestSong.tracks[0]) {
            assertEquals(volume, 1.0f)
            assertEquals(segments.size, 1)
            with(segments[0]) {
                assertEquals(startTime, 0.0f)
                assertValuesEqual(boundedWaveform, BoundedWaveform(testInstrumentForNoteC4, 0.25f), delta = 0.001f)
            }
        }
        with(preprocessedTestSong.tracks[1]) {
            assertEquals(volume, 1.0f)
            assertEquals(segments.size, 1)
            with(segments[0]) {
                assertEquals(startTime, 0.0f)
                assertValuesEqual(boundedWaveform, BoundedWaveform(testInstrumentForNoteE4, 0.125f), delta = 0.001f)
            }
        }
    }
}

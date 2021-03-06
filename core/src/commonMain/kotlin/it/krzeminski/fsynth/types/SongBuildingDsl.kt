package it.krzeminski.fsynth.types

import it.krzeminski.fsynth.instruments.Instrument

fun song(name: String, beatsPerMinute: Int, init: SongBuilder.() -> Unit): Song {
    val songBuilder = SongBuilder(name, beatsPerMinute)
    songBuilder.init()
    return songBuilder.build()
}

@DslMarker
annotation class SongDslMarker

@SongDslMarker
class SongBuilder(
    name: String,
    private val beatsPerMinute: Int
) {
    private var song = Song(name = name, beatsPerMinute = beatsPerMinute, tracks = emptyList())

    fun track(
        name: String? = null,
        instrument: Instrument,
        volume: Float,
        init: TrackBuilder.() -> Unit
    ) {
        val trackBuilder = TrackBuilder(instrument, volume, name)
        initAndAppendTrack(trackBuilder, init)
    }

    private fun initAndAppendTrack(trackBuilder: TrackBuilder, init: TrackBuilder.() -> Unit) {
        trackBuilder.init()
        song = song.copy(tracks = song.tracks + trackBuilder.build())
    }

    fun build(): Song {
        return song
    }
}

@SongDslMarker
class TrackBuilder(
    private val instrument: Instrument,
    private val volume: Float,
    private val name: String?
) {
    private var track = Track(name = name, instrument = instrument, volume = volume, segments = emptyList())

    fun note(value: NoteValue, pitch: MusicNote) {
        track = track.copy(segments = track.segments + TrackSegment.SingleNote(value, pitch))
    }

    fun glissando(value: NoteValue, transition: MusicNoteTransition) {
        track = track.copy(segments = track.segments + TrackSegment.Glissando(value, transition))
    }

    fun chord(value: NoteValue, vararg pitches: MusicNote) {
        track = track.copy(segments = track.segments + TrackSegment.Chord(value, pitches.toList()))
    }

    fun pause(value: NoteValue) {
        track = track.copy(segments = track.segments + TrackSegment.Pause(value))
    }

    fun build(): Track {
        return track
    }
}

infix fun Int.by(denominator: Int): NoteValue = NoteValue(this, denominator)

infix fun MusicNote.to(endPitch: MusicNote): MusicNoteTransition = MusicNoteTransition(this, endPitch)

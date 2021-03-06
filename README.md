[![Build Status](https://circleci.com/gh/krzema12/fsynth.svg?style=svg)](https://app.circleci.com/pipelines/github/krzema12/fsynth) [![codecov](https://codecov.io/gh/krzema12/fsynth/branch/master/graph/badge.svg)](https://codecov.io/gh/krzema12/fsynth) [![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

<img src="https://raw.githubusercontent.com/krzema12/fsynth/master/web/src/main/resources/Logo.svg" width="200" height="200" />

# What is fsynth? 

It's a simple music synthesizer. It focuses on generating the waveforms from scratch, no samples are used. You can listen to it via the Web player, or using the cross-platform Java CLI. Read on to learn more.

Secondly, this project is my playground for learning Kotlin, functional programming (hence the "f" in "fsynth"), front-end development, Gradle, multiplatform projects, and other. It's also a place where I can focus more on quality than I normally could afford in a professional environment, because here I don't have a pressure to deliver on time. That's why my aim here is also to have as little technical debt as possible, and have as clean code as I can write.

Songs are described with such DSL:

```kotlin
val simpleDemoSong = song(
        name = "Simple demo song",
        beatsPerMinute = 120) {
    track(instrument = organs, volume = 0.3f) {
        note(1 by 8, D4)
        note(1 by 16, Csharp4)
        note(1 by 16, D4)
        note(1 by 8, E4)
        note(1 by 8, D4)

        pause(1 by 8)
        chord(1 by 8, A3, D4, Fsharp4)
        chord(1 by 4, B3, D4, G4)
    }
}
```

# How to listen

## Web

https://fsynthlib.github.io/fsynth/

## Java CLI

The CLI uses system sound output to play music.

You can use the CLI from the distribution package:

```
./gradlew :cli:installDist
cli/build/install/fsynth/bin/fsynth --song 'Van Halen - Jump (intro)'
```

or during development, you can call the CLI through Gradle:

```
./gradlew :cli:run --args="--song 'Van Halen - Jump (intro)'"
```

To see a list of available songs, call the CLI without arguments. 

# Talk on [Tricity Kotlin User Group](https://www.facebook.com/groups/tricity.kotlin.user.group)

[![[TKUG #1] Piotr Krzemiński, 'Project fsynth: show and tell'](http://img.youtube.com/vi/mQEIn7Eqsio/0.jpg)](http://www.youtube.com/watch?v=mQEIn7Eqsio "[TKUG #1] Piotr Krzemiński, 'Project fsynth: show and tell'")

# Build prerequisites

The below dependencies won't be installed by Gradle:

* JDK + path to it in `JAVA_HOME`
  (warning: use Oracle's JDK for now, there are some issue with OpenJDK; see #31 for details)
* Android SDK + path to it in `ANDROID_HOME`

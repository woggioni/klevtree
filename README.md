# Kotlin multiplatform library for Levenshtein distance

THis library is used to find the closest matches of a word in a predefined set of word, according to
[Levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance) or [Damerau-Levenshtein distance](https://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance).

### Build
The library uses Gradle as the build system, so the only required dependency is a valid JDK 17 implementation.
Then it can be built using

```bash
./gradlew build
```

### Usage

The library is avaliable from "https://woggioni.net/mvn" Maven repository and can ba consumed by another Gradle
project simply by adding

```Groovy
repositories {
    maven {
        url = "https://woggioni.net/mvn"
    }
}
dependencies {
    implementation group: 'net.woggioni', name: 'klevtree', version: '2023.03'
}
```

As a Kotlin multiplatform library it currently supports the jvm, js and linuxX64 targets

### Example code

```kotlin
val words = listOf(
    "tired",
    "authorise",
    "exercise",
    "bloody",
    "ritual",
    "trail",
    "resort",
    "landowner",
    "navy",
    "captivate",
    "captivity",
    "north")
val tree = LevTrie().apply {
    algorithm = LevTrie.Algorithm.DAMERAU_LEVENSHTEIN
    caseSensitive = false
    words.forEach(this::add)
}
val result = tree.fuzzySearch("fired", 1)
result.forEach {
    println("Word: ${it.first}, distance: ${it.second}")
}

// Word: tired, distance: 1

```


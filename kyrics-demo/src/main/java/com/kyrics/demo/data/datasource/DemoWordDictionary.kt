package com.kyrics.demo.data.datasource

import com.kyrics.demo.data.model.WordDefinition
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Curated vocabulary dictionary for the Word Tap demo.
 * Only contains key content words worth learning — common function words
 * (the, is, a, so, will) are intentionally excluded.
 */
@Singleton
class DemoWordDictionary
    @Inject
    constructor() {
        fun lookup(word: String): WordDefinition? = definitions[word.lowercase().trim()]

        fun hasWord(word: String): Boolean = definitions.containsKey(word.lowercase().trim())

        companion object {
            private val definitions: Map<String, WordDefinition> =
                buildMap {
                    put(
                        "setting",
                        WordDefinition(
                            word = "setting",
                            phonetic = "/\u02C8s\u025Bt\u026A\u014B/",
                            partOfSpeech = "verb",
                            definition = "Going down below the horizon",
                            example = "The sun is setting over the ocean.",
                        ),
                    )
                    put(
                        "golden",
                        WordDefinition(
                            word = "golden",
                            phonetic = "/\u02C8\u0261o\u028Ald\u0259n/",
                            partOfSpeech = "adjective",
                            definition = "Of the colour of gold; shining brightly",
                            example = "A golden sunset lit the valley.",
                        ),
                    )
                    put(
                        "across",
                        WordDefinition(
                            word = "across",
                            phonetic = "/\u0259\u02C8kr\u0252s/",
                            partOfSpeech = "preposition",
                            definition = "From one side to the other",
                            example = "She walked across the bridge.",
                        ),
                    )
                    put(
                        "shine",
                        WordDefinition(
                            word = "shine",
                            phonetic = "/\u0283a\u026An/",
                            partOfSpeech = "verb",
                            definition = "To emit or reflect light; to glow",
                            example = "The stars shine at night.",
                        ),
                    )
                    put(
                        "bright",
                        WordDefinition(
                            word = "bright",
                            phonetic = "/bra\u026At/",
                            partOfSpeech = "adjective",
                            definition = "Giving out or reflecting much light",
                            example = "The bright moon lit the path.",
                        ),
                    )
                    put(
                        "morning",
                        WordDefinition(
                            word = "morning",
                            phonetic = "/\u02C8m\u0254\u02D0rn\u026A\u014B/",
                            partOfSpeech = "noun",
                            definition = "The period from sunrise to noon",
                            example = "She jogs every morning.",
                        ),
                    )
                }
        }
    }

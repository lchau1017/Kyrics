package com.kyrics.demo.data.model

import androidx.compose.runtime.Immutable

/**
 * Definition data for a single word.
 */
@Immutable
data class WordDefinition(
    val word: String,
    val phonetic: String? = null,
    val partOfSpeech: String? = null,
    val definition: String,
    val example: String? = null,
)

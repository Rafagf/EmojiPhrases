package com.rafag.repository

import com.rafag.model.*

interface Repository {
    suspend fun add(emojiValue: String, phraseValue: String)
    suspend fun phrase(id: String): EmojiPhrase?
    suspend fun phrase(id: Int): EmojiPhrase?
    suspend fun phrases(): List<EmojiPhrase>
    suspend fun remove(id: String)
    suspend fun remove(id: Int)
    suspend fun clear()
}
package com.rafag.repository

import com.rafag.model.*

interface Repository {
    suspend fun add(userId: String, emojiValue: String, phraseValue: String)
    suspend fun phrase(id: String): EmojiPhrase?
    suspend fun phrase(id: Int): EmojiPhrase?
    suspend fun phrases(): List<EmojiPhrase>
    suspend fun phrases(userId: String): List<EmojiPhrase>
    suspend fun remove(id: String)
    suspend fun remove(id: Int)
    suspend fun clear()
    suspend fun user(userId: String, hash: String? = null): User?
    suspend fun userByEmail(email: String): User?
    suspend fun createUser(user: User)
}
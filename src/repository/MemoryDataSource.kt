package com.rafag.repository

import com.rafag.model.*
import java.util.concurrent.atomic.*

class MemoryDataSource : DataSource {

    private val counterId = AtomicInteger()
    private val phrases = ArrayList<EmojiPhrase>()

    override suspend fun add(phrase: EmojiPhrase): EmojiPhrase {
        if (phrases.contains(phrase)) {
            return phrases.find { it == phrase }!!
        }
        phrase.id = counterId.incrementAndGet()
        phrases.add(phrase)
        return phrase
    }

    override suspend fun phrase(id: String): EmojiPhrase = phrase(id.toInt())

    override suspend fun phrase(id: Int): EmojiPhrase {
        return phrases.find { it.id == id } ?: throw IllegalArgumentException("No phrase found with id $id")
    }

    override suspend fun phrases(): ArrayList<EmojiPhrase> = phrases

    override suspend fun remove(phrase: EmojiPhrase) {
        if (!phrases.contains(phrase)) {
            throw IllegalArgumentException("No phrase found with id ${phrase.id}")
        } else {
            phrases.remove(phrase)
        }
    }

    override suspend fun remove(id: String) = remove(phrase(id))

    override suspend fun remove(id: Int) = remove(phrase(id))

    override suspend fun clear() = phrases.clear()

}
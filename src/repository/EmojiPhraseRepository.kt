package com.rafag.repository

import com.rafag.model.*
import com.rafag.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

class EmojiPhraseRepository : Repository {
    override suspend fun add(emojiValue: String, phraseValue: String) {
        transaction {
            EmojiPhrases.insert {
                it[emoji] = emojiValue
                it[phrase] = phraseValue
            }
        }
    }

    override suspend fun phrase(id: Int): EmojiPhrase? = dbQuery {
        EmojiPhrases.select {
            (EmojiPhrases.id eq id)
        }.mapNotNull {
            it.toEmojiPhrase()
        }.singleOrNull()
    }

    override suspend fun phrase(id: String): EmojiPhrase? = phrase(id.toInt())

    override suspend fun phrases(): List<EmojiPhrase> = dbQuery {
        EmojiPhrases.selectAll().map {
            it.toEmojiPhrase()
        }
    }

    override suspend fun remove(id: Int) {
        if (phrase(id) == null) {
            throw IllegalArgumentException("No emojiphrase for id :$id")
        }

        return dbQuery {
            EmojiPhrases.deleteWhere {
                (EmojiPhrases.id eq id)
            } > 0
        }
    }

    override suspend fun remove(id: String) = remove(id.toInt())

    override suspend fun clear() {
        dbQuery {
            EmojiPhrases.deleteAll()
        }
    }

    private fun ResultRow.toEmojiPhrase(): EmojiPhrase {
        return EmojiPhrase(
            id = this[EmojiPhrases.id].value,
            emoji = this[EmojiPhrases.emoji],
            phrase = this[EmojiPhrases.phrase]
        )
    }
}
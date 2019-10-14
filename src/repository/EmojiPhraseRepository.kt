package com.rafag.repository

import com.rafag.model.*
import com.rafag.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

class EmojiPhraseRepository : Repository {

    override suspend fun add(userId: String, emojiValue: String, phraseValue: String) {
        transaction {
            EmojiPhrases.insert {
                it[user] = userId
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

    override suspend fun user(userId: String, hash: String?): User? {
        val user = dbQuery {
            Users.select {
                (Users.id eq userId)
            }.mapNotNull {
                it.toUser()
            }.singleOrNull()
        }

        return when {
            user == null -> null
            hash == null -> user
            user.passwordHash == hash -> user
            else -> null
        }
    }

    override suspend fun userByEmail(email: String) = dbQuery {
        Users.select { Users.email.eq(email) }
            .mapNotNull {
                it.toUser()
            }.singleOrNull()
    }

    override suspend fun createUser(user: User) = dbQuery {
        Users.insert {
            it[id] = user.userId
            it[displayName] = user.displayName
            it[email] = user.email
            it[passwordHash] = user.passwordHash
        }
        Unit
    }

    private fun ResultRow.toEmojiPhrase(): EmojiPhrase {
        return EmojiPhrase(
            id = this[EmojiPhrases.id].value,
            userId = this[EmojiPhrases.user],
            emoji = this[EmojiPhrases.emoji],
            phrase = this[EmojiPhrases.phrase]
        )
    }

    private fun ResultRow.toUser(): User =
        User(
            userId = this[Users.id],
            email = this[Users.email],
            displayName = this[Users.displayName],
            passwordHash = this[Users.passwordHash]
        )
}
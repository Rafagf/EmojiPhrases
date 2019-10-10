package com.rafag.repository

import com.rafag.model.*
import com.zaxxer.hikari.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

object DatabaseFactory {

    fun init() {
        Database.connect(hiraki())

        transaction {
            SchemaUtils.create(EmojiPhrases)

            EmojiPhrases.insert {
                it[emoji] = "e1"
                it[phrase] = "p1"
            }

            EmojiPhrases.insert {
                it[emoji] = "e2"
                it[phrase] = "p2"
            }
        }
    }

    private fun hiraki(): HikariDataSource {
        val config = HikariConfig()
        config.apply {
            driverClassName = "org.h2.Driver"
            jdbcUrl = "jdbc:h2:mem:test"
            maximumPoolSize = 3
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            config.validate()
        }

        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction {
            block()
        }
    }
}
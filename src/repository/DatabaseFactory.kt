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
            SchemaUtils.create(Users)
        }
    }

    private fun hiraki(): HikariDataSource {
        val config = HikariConfig()
        config.apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = System.getenv("JDBC_DATABASE_URL")
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
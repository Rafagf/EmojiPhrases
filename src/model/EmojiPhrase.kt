package com.rafag.model

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.*
import java.io.*

data class EmojiPhrase(
    var id: Int,
    val userId: String,
    val emoji: String,
    val phrase: String
) : Serializable

object EmojiPhrases : IntIdTable() {
    val user: Column<String> = varchar("user_id", 20).index()
    val emoji: Column<String> = varchar("emoji", 255)
    val phrase: Column<String> = varchar("phrases", 255)
}
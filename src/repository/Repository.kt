package com.rafag.repository

import com.rafag.model.*

class Repository(private val memoryDataSource: MemoryDataSource) : DataSource {

    override suspend fun add(phrase: EmojiPhrase): EmojiPhrase = memoryDataSource.add(phrase)
    override suspend fun phrase(id: String): EmojiPhrase = memoryDataSource.phrase(id)
    override suspend fun phrase(id: Int): EmojiPhrase = memoryDataSource.phrase(id)
    override suspend fun phrases(): ArrayList<EmojiPhrase> = memoryDataSource.phrases()
    override suspend fun remove(phrase: EmojiPhrase) = memoryDataSource.remove(phrase)
    override suspend fun remove(id: String) = memoryDataSource.remove(id)
    override suspend fun remove(id: Int) = memoryDataSource.remove(id)
    override suspend fun clear() = memoryDataSource.clear()
}
package com.example.data.repository

import com.example.data.local.database.CardDao
import com.example.data.local.entity.CardEntity
import com.example.domain.repository.ICardRepository
import kotlinx.coroutines.flow.Flow

class CardRepository(private val cardDao: CardDao) : ICardRepository {
    override val allCards: Flow<List<CardEntity>> = cardDao.getAllCards()

    override suspend fun insertCards(cards: List<CardEntity>) {
        cardDao.insertAll(cards)
    }

    override suspend fun deleteAll() {
        cardDao.deleteAll()
    }

    override suspend fun getCardCount(): Int {
        return cardDao.getCardCount()
    }
}

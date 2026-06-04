package com.example.domain.repository

import com.example.data.local.entity.CardEntity
import kotlinx.coroutines.flow.Flow

interface ICardRepository {
    val allCards: Flow<List<CardEntity>>
    suspend fun insertCards(cards: List<CardEntity>)
    suspend fun deleteAll()
    suspend fun getCardCount(): Int
}

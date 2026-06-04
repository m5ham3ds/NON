package com.example.domain.usecase

import com.example.data.local.entity.CardEntity
import com.example.domain.model.Card
import com.example.domain.repository.ICardRepository
import java.security.SecureRandom

class GenerateCardsUseCase(private val cardRepository: ICardRepository) {
    suspend operator fun invoke(
        prefix: String,
        length: Int,
        count: Int,
        charset: String
    ): List<Card> {
        val chars = charset.ifEmpty { "0123456789" }
        val random = SecureRandom()
        val cards = (1..count).map {
            val suffix = (1..length).map { chars[random.nextInt(chars.length)] }.joinToString("")
            Card(code = "$prefix$suffix")
        }
        cardRepository.deleteAll()
        cardRepository.insertCards(cards.map { CardEntity(code = it.code, prefix = prefix, length = length, charset = charset) })
        return cards
    }
}

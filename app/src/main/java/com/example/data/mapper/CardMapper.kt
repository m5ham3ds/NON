package com.example.data.mapper

import com.example.data.local.entity.CardEntity
import com.example.domain.model.Card

object CardMapper {
    fun CardEntity.toDomain(): Card = Card(
        id = id,
        code = code,
        prefix = prefix,
        length = length,
        charset = charset,
        createdAt = createdAt
    )

    fun Card.toEntity(): CardEntity = CardEntity(
        id = id,
        code = code,
        prefix = prefix,
        length = length,
        charset = charset,
        createdAt = createdAt
    )

    fun List<CardEntity>.toDomainList(): List<Card> = map { it.toDomain() }
}

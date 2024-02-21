package com.example.backend.db

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Table

interface DAOTable<M, I : Comparable<I>> {
    suspend fun create(entity: M): M?
    suspend fun getAll(): List<M>
    suspend fun getBy(id: I): M?
    suspend fun getBy(equals: (Table) -> Op<Boolean>): List<M>
}

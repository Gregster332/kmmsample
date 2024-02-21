package com.example.mykmmtest.utils

import app.cash.sqldelight.db.SqlDriver
import com.example.corenetwork.api.auth.DBUser
import com.example.corenetwork.api.auth.LocalCache
import com.example.mykmmtest.MainDatabase
import comexamplemykmmtest.Users
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

class LocalCacheImpl : LocalCache, KoinComponent {
    private val db: MainDatabase by inject()

    override fun getAllUsers() = db.mainQueries.selectAll().executeAsList().map { it.mapTo() }

    override fun getUserBy(id: String) = try {
            db.mainQueries.selectUserById(id).executeAsList().first().mapTo()
        } catch (e: NoSuchElementException) {
            null
        }

    override fun deleteAllUsers() {
        db.mainQueries.deleteAll()
    }

    override fun saveNewUser(user: DBUser) {
        try {
            db.mainQueries.saveNewUser(
                id = user.id,
                nickname = user.nickname,
                email = user.email,
                photoUrl = user.photoUrl,
                bio = user.bio,
                current = user.current,
            )
        } catch (e: Exception) {
            println(e)
        }
    }

    override fun getCurrentUser() = try {
            db.mainQueries.selectCurrentUser().executeAsList().first().mapTo()
        } catch (e: Exception) {
            println(e)
            null
        }

    private fun Users.mapTo() = DBUser(
            id = id,
            nickname = nickname,
            email = email,
            photoUrl = photoUrl,
            bio = bio,
            current = current,
        )
}

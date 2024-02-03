package com.example.mykmmtest.Utils

import app.cash.sqldelight.db.SqlDriver
import com.example.corenetwork.api.Auth.LocalCache
import com.example.corenetwork.api.Auth.UserBaseInfo
import com.example.mykmmtest.MainDatabase
import comexamplemykmmtest.Users
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

class LocalCacheImpl: LocalCache, KoinComponent {

    private val db: MainDatabase by inject()

    override fun getAllUsers(): List<UserBaseInfo> {
        return db.mainQueries.selectAll().executeAsList().map { it.mapTo() }
    }

    override fun getUserBy(id: String): UserBaseInfo? = try {
        db.mainQueries.selectUserById(id).executeAsList().first().mapTo()
    } catch(e: NoSuchElementException) {
        null
    }

    override fun deleteAllUsers() {
        db.mainQueries.deleteAll()
    }

    override fun saveNewUser(user: UserBaseInfo) {
        try {
            db.mainQueries.saveNewUser(
                id = user.id,
                nickname = user.nickname,
                email = user.email,
                password = user.password,
                photoUrl = user.photoUrl,
                bio = user.bio
            )
        } catch(e: Exception) {
          println(e)
        }
    }


    private fun Users.mapTo() = UserBaseInfo(
        id = id,
        nickname = nickname,
        email = email,
        password = password,
        photoUrl = photoUrl,
        bio = bio
    )
}
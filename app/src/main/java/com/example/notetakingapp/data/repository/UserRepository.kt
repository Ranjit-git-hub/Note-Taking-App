package com.example.notetakingapp.data.repository

import androidx.lifecycle.LiveData
import com.example.notetakingapp.data.UserDao
import com.example.notetakingapp.data.model.User

class UserRepository(private val userDao: UserDao) {

    val readAllData: LiveData<List<User>> = userDao.readAllData()
    val readAllDataByRecent: LiveData<List<User>> = userDao.readAllDataByRecent()


    suspend fun addUser(user: User){
        userDao.addUser(user)
    }

    suspend fun updateUser(user: User){
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User){
        userDao.deleteUser(user)
    }

    suspend fun deleteAllUsers(){
        userDao.deleteAllUsers()
    }

    suspend fun updateLastOpened(userId: Int, timestamp: Long) {
        userDao.updateLastOpened(userId, timestamp)
    }

    fun searchDatabase(searchQuery: String): LiveData<List<User>> {
        return userDao.searchDatabase(searchQuery)
    }


}
package com.example.notetakingapp.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.notetakingapp.data.UserDatabase
import com.example.notetakingapp.data.model.User
import com.example.notetakingapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

     val readAllData: LiveData<List<User>>
    val readAllDataByRecent: LiveData<List<User>>

    private val repository: UserRepository

    init {
        val userDao = UserDatabase.Companion.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        readAllData = repository.readAllData
        readAllDataByRecent = repository.readAllDataByRecent

    }

    fun addUser(user: User){
        viewModelScope.launch(Dispatchers.IO){
            repository.addUser(user)
        }
    }

    fun updateUser(user: User){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateUser(user)
        }
    }

    fun deleteUser(user: User){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteUser(user)
        }
    }

    fun deleteAllUsers(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAllUsers()
        }
    }

    fun updateLastOpened(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLastOpened(userId, System.currentTimeMillis())
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<User>> {
        return repository.searchDatabase(searchQuery)
    }


}
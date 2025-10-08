package com.example.notetakingapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notetakingapp.data.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("Delete FROM user_table")
    suspend fun deleteAllUsers()

    @Query("Select * FROM user_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<User>>

    // Returns notes sorted by most recently opened
    @Query("SELECT * FROM user_table ORDER BY lastOpened DESC")
    fun readAllDataByRecent(): LiveData<List<User>>

    // Updates the lastOpened timestamp for a specific note
    @Query("UPDATE user_table SET lastOpened = :timestamp WHERE id = :noteId")
    suspend fun updateLastOpened(noteId: Int, timestamp: Long)

    @Query("SELECT * FROM user_table WHERE title LIKE :searchQuery OR description LIKE :searchQuery ORDER BY lastOpened DESC")
    fun searchDatabase(searchQuery: String): LiveData<List<User>>

}
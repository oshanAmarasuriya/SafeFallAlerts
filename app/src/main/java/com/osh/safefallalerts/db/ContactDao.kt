package com.osh.safefallalerts.db

import androidx.room.*

@Dao
interface ContactDao {
    @Insert
    suspend fun insert(contact: Contact)

    @Query("SELECT * FROM contacts")
    suspend fun getAll(): List<Contact>

    @Delete
    suspend fun delete(contact: Contact)
}

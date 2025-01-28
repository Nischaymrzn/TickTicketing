package com.example.tickticketing.repository

import com.example.tickticketing.model.UserModel
import com.google.firebase.auth.FirebaseUser


interface UserRepository {

    //after login what do we want to return inside the callback like {success : true, message: "Login success")
    fun login(email : String, password: String, callback:(Boolean, String) -> Unit)

    fun signup(email: String, password: String, callback: (Boolean, String, String) -> Unit) //success, message, userId

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit)

    fun addUserToDatabase(userModel: UserModel, callback: (Boolean, String) -> Unit)

    fun getCurrentUser() : FirebaseUser? //nullable

    fun getDataFromDatabase(userId : String, callback: (UserModel?, Boolean, String) -> Unit)

    fun logout(callback: (Boolean, String) -> Unit)

    fun editProfile(userId: String, data : MutableMap<String, Any>, callback: (Boolean, String) -> Unit)
}
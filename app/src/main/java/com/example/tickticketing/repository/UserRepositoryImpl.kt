package com.example.tickticketing.repository

import android.widget.Toast
import com.example.tickticketing.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepositoryImpl :UserRepository {
    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference : DatabaseReference = database.reference.child("users")
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
            if(it.isSuccessful){
                callback(true, "Login successful")
            }else{
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun signup(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
            if(it.isSuccessful){
                callback(true, "Register success", auth.currentUser?.uid.toString())
            }else{
                callback(false, it.exception?.message.toString(), "")
            }
        }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener{
            if(it.isSuccessful){
                callback(true, "Password reset mail has been sent")
            }else{
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun addUserToDatabase(userModel: UserModel, callback: (Boolean, String) -> Unit) {
        reference.child(userModel.userId).setValue(userModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "User added to database")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun getDataFromDatabase(
        userId: String,
        callback: (UserModel?, Boolean, String) -> Unit
    ) {
        reference.child(userId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val model = snapshot.getValue(UserModel::class.java)
                    callback(model, true, "Details fetched successfully")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try{
            auth.signOut()
            callback(true,"Sign-out successfully")
        }catch (e: Exception){
            callback(false,e.message.toString())
        }
    }

    override fun editProfile(
        userId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        reference.child(userId).updateChildren(data).addOnCompleteListener {
            if (it.isSuccessful){
                callback(true, "Profile edited successfully")
            }else{
                callback(false, "Unable to edited profile")
            }
        }
    }
}
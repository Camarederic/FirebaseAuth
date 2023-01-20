package com.example.firebaseauth

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebaseauth.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


        binding.buttonRegister.setOnClickListener {
            registerUser()
        }

        binding.buttonLogin.setOnClickListener {
            loginUser()
        }

        binding.buttonUpdateProfile.setOnClickListener {
            updateProfile()
        }

    }

    private fun updateProfile() {
        auth.currentUser?.let { user ->
            val userName = binding.edUserName.text.toString()
            val photoURI = Uri.parse("android.resource://$packageName/${R.drawable.picture_4}")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .setPhotoUri(photoURI)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main) {
                        checkLoggedInState()
                        Toast.makeText(
                            this@MainActivity,
                            "Successfully updated user profile",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: java.lang.Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }

    private fun registerUser() {
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        checkLoggedInState()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun loginUser() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        checkLoggedInState()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun checkLoggedInState() {
        val user = auth.currentUser
        if (user == null) {
            binding.tvLoggedIn.text = "You are not logged in"
        } else {
            binding.tvLoggedIn.text = "You are logged in!"
            binding.edUserName.setText(user.displayName)
            binding.imageViewProfile.setImageURI(user.photoUrl)
        }
    }
}
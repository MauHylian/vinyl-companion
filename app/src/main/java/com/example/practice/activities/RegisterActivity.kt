package com.example.practice.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practice.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        findViewById<Button>(R.id.registerBtn).setOnClickListener {
            performRegister()
        }

        findViewById<TextView>(R.id.yaTienesCuenta).setOnClickListener {
            Log.d("RegisterActivity", "Login activity")
            // Launch login activity somehow
            finish()
        }
    }

    private fun performRegister() {
        val username = findViewById<TextView>(R.id.usernameEdit).text.toString()
        val email = findViewById<TextView>(R.id.emailEdit).text.toString()
        val password = findViewById<TextView>(R.id.passwordEdit).text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa los datos faltantes.", Toast.LENGTH_SHORT).show()
            return
        }

        if (passwordEdit.text.toString() == confirmPasswordEdit.text.toString()) {
            Log.d("RegisterActivity", "Username es: $password")
            Log.d("RegisterActivity", "Email es: $email")
            Log.d("RegisterActivity", "Contrasena es: $password")

            // FIREBASE AUTH REGISTER
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener
                        // else if successful
                        finish()
                        //Log.d("Main", "Successfully created user with uid: ${it.result!!.user!!.uid}")
                    }
                    .addOnFailureListener {
                        Log.d("Main", "Failed to create user: ${it.message}")
                        Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
        } else {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        }
    }
}
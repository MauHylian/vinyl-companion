package com.example.practice.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practice.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.loginBtn).setOnClickListener {
            performLogin()
        }

        findViewById<TextView>(R.id.regresaRegistro).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun performLogin() {
        val email = findViewById<TextView>(R.id.emailEditLogin).text.toString()
        val password = findViewById<TextView>(R.id.passwordEditLogin).text.toString()
        Log.d("LoginActivity", "Se intento iniciar con el email: $email y contrasena: $password")

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa los datos faltantes.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("LoginActivity", "Email es: $email")
        Log.d("LoginActivity", "Contrasena es: $password")

        // FIREBASE AUTH
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if(!it.isSuccessful) return@addOnCompleteListener
                    else {
                        val intent = Intent(this, ScanActivity::class.java)
                        startActivity(intent)
                    }
                    //Log.d("LoginActivity", "Successful log-in with email: $email and password: $password and uis: ${it.result!!.user!!.uid}")
                }
                .addOnFailureListener {
                    Log.d("LoginActivity", "Failed to log user: ${it.message}")
                    Toast.makeText(this, "Failed to log user: ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }
}

package com.example.market

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val signInId = findViewById<EditText>(R.id.sign_in_id)
        val signInPassword = findViewById<EditText>(R.id.sign_in_password)
        val buttonSignIn = findViewById<Button>(R.id.sign_in)

        buttonSignIn.setOnClickListener {
            val id = signInId.text.toString()
            val password = signInPassword.text.toString()
            auth?.signInWithEmailAndPassword(id, password)
                ?.addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공
                        Toast.makeText(this@SignInActivity, "로그인 성공", Toast.LENGTH_SHORT).show()

                        // 원하는 액티비티로 이동
                        val intent = Intent(this,ListActivity::class.java)
                        Log.d("SignInActivity", "Before starting ListActivity")
                        startActivity(intent)
                        Log.d("SignInActivity", "After starting ListActivity")
                        // 현재 액티비티를 종료합니다.
                    } else {
                        // 로그인 실패
                        Toast.makeText(this@SignInActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}

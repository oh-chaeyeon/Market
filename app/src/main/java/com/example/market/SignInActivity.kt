package com.example.market

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

class SignInActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance();


        val SignInId = findViewById<EditText>(R.id.sign_in_id)
        val SignInPassword = findViewById<EditText>(R.id.sign_in_password)
        val buttonSignIn = findViewById<Button>(R.id.sign_in)

        buttonSignIn.setOnClickListener{
            val id=SignInId.text.toString()
            val password = SignInPassword.text.toString()
            auth?.signInWithEmailAndPassword(id,password) ?.
                    addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            // 로그인 성공
                            Toast.makeText(this@SignInActivity, "로그인 성공", Toast.LENGTH_SHORT).show()

                            // 원하는 액티비티로 이동
                            val intent = Intent(this@SignInActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // 로그인 실패
                            Toast.makeText(this@SignInActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                        })
        }

    }
}
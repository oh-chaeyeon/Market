package com.example.market

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

class SignUpActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance();


        val signupId = findViewById<EditText>(R.id.sign_up_id)
        val signupPassword = findViewById<EditText>(R.id.sign_up_password)

        val sign_up = findViewById(R.id.sign_up) as Button
        sign_up.setOnClickListener {
            val id = signupId.text.toString()
            val password = signupPassword.text.toString()

            auth?.createUserWithEmailAndPassword(id, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 등록 성공
                        val user = auth?.currentUser
                        val intent = Intent(this, ListActivity::class.java)
                        intent.putExtra("userEmail", id)
                        startActivity(intent)
                    } else {
                        // 등록 실패
                        // 에러 처리
                    }
                }
        }
    }
}
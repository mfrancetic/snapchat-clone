package com.mfrancetic.snapchatclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        setLoginView()
        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        sign_up_login_button.setOnClickListener(View.OnClickListener {
            val email = email_edit_text.text.toString()
            val password = password_edit_text.text.toString()
            if (isLoginMode()) {
                loginUser(email, password)
            } else {
                val password2 = password2_edit_text.text.toString()
                signUpUser(email, password, password2)
            }
        })
        switch_to_sign_up_login_text_view.setOnClickListener(View.OnClickListener {
            if (isLoginMode()) {
                setSignUpView()
            } else {
                setLoginView()
            }
        })
    }

    private fun signUpUser(email: String, password: String, password2: String) {
        if (password != password2) {
            Toast.makeText(baseContext, getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
            return
        }
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(baseContext, getString(R.string.email_password_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 3) {
            Toast.makeText(baseContext, getString(R.string.email_password_too_short), Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(this.localClassName, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext, getString(R.string.sign_up_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUi(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(this.localClassName, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUi(null)
                }
            }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(this.localClassName, "signInWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext, getString(R.string.login_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUi(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(this.localClassName, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUi(null)
                    // ...
                }
            }
            }

    private fun setLoginView() {
        sign_up_login_button.text = getString(R.string.login)
        switch_to_sign_up_login_text_view.text = getString(R.string.switch_to_sign_up)
        password2_edit_text.visibility = View.GONE
    }

    private fun setSignUpView() {
        sign_up_login_button.text = getString(R.string.sign_up)
        switch_to_sign_up_login_text_view.text = getString(R.string.switch_to_login)
        password2_edit_text.visibility = View.VISIBLE
    }

    private fun isLoginMode(): Boolean {
        return sign_up_login_button.text == getString(R.string.login)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUi(currentUser)
    }

    private fun updateUi(currentUser: FirebaseUser?) {
        println("currentUser : $currentUser")
    }
}
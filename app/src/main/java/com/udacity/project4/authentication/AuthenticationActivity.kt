package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private val viewModel by viewModels<LoginViewModelAuth>()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)
        auth = FirebaseAuth.getInstance()


        viewModel.authenticationState.observe(this) { authenticationState ->
            when (authenticationState) {
                LoginViewModelAuth.AuthenticationState.AUTHENTICATED -> {
                    val intent = Intent(this, RemindersActivity::class.java)
                    startActivity(intent)
                }
                else -> Log.d("TAG", "Failed to login")
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == REQUEST_CODE_SIGN_IN_WITH_GOOGLE_From_LogIn){
//            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
//            account?.let {
//                googleAuthForFirebase(it)
//            }
//        }
//    }
//
//    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
//        val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
//        CoroutineScope(Dispatchers.Main).launch{
//            try {
//                withContext(Dispatchers.IO) {
//                    auth.signInWithCredential(credentials).await()
//                }
//                if(auth.currentUser != null)
//                    startActivity(Intent(this@AuthenticationActivity, RemindersActivity::class.java))
//            }catch (exe:Exception){
//                Toast.makeText(this@AuthenticationActivity,exe.message, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
}

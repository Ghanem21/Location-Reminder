package com.udacity.project4.authentication


import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LogInViewModel : ViewModel() {
    lateinit var activity:Activity
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var errorMessage = MutableLiveData<String>()

    var emptyEmailListener = MutableLiveData<Boolean>()
    var emptyPasswordListener = MutableLiveData<Boolean>()
    var showError = MutableLiveData<Boolean>()
    var navigateToHome = MutableLiveData<Boolean>()
    var navigateToSignUp = MutableLiveData<Boolean>()
    var verificationDialog = MutableLiveData<Boolean>()

    var signInWithGoogleListener = MutableLiveData<Boolean>()
    lateinit var googleSignInClient : GoogleSignInClient
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        email.value = ""
        password.value = ""
        errorMessage.value = ""

        emptyEmailListener.value = false
        emptyPasswordListener.value = false
        showError.value = false
        navigateToHome.value = false
        navigateToSignUp.value = false
        verificationDialog.value = false
    }

    fun logIn() {
        validation()
    }

    private fun validation() {
        if (email.value.equals("")) {
            setEmptyEmailError()
            return
        }
        if (password.value.equals("")) {
            setEmptyPasswordError()
            return
        }
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email.value ?: "", password.value ?: "").await()
                checkLoggedInState()
            } catch (e: Exception) {
                toastError(e.message)
            }
        }
    }

    private fun checkLoggedInState() {
        if (auth.currentUser == null)
            toastError("You have no account")
        else {
            if(auth.currentUser!!.isEmailVerified) {
                navigateToHome.value = true
            }else{
                showDialog()
            }
        }
    }

    private fun showDialog() {
        verificationDialog.value = true
    }

    fun showDialogDone(){
        verificationDialog.value = false
    }

    private fun toastError(message: String?) {
        errorMessage.value = message ?: ""
        showError.value = true
    }

    private fun setEmptyPasswordError() {
        emptyPasswordListener.value = true
    }

    private fun setEmptyEmailError() {
        emptyEmailListener.value = true
    }

    fun navigateToHomeDone() {
        navigateToHome.value = false
    }

    fun navigateToSignUp() {
        navigateToSignUp.value = true
    }

    fun navigateToSignUpDone() {
        navigateToSignUp.value = false
    }

    fun showErrorDone() {
        showError.value = false
    }

    fun emptyPasswordListenerDone() {
        emptyPasswordListener.value = false
    }

    fun emptyEmailListenerDone() {
        emptyEmailListener.value = false
    }

    fun logInWithGoogle(){
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity,options)
        signInWithGoogleListener.value = true
    }

    fun signInWithGoogleDone(){
        signInWithGoogleListener.value = false
    }
}
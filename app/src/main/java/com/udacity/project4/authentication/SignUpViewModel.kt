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

class SignUpViewModel : ViewModel() {

    lateinit var activity: Activity
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var confirmPassword = MutableLiveData<String>()
    var errorMessage = MutableLiveData<String>()

    var emptyEmailListener = MutableLiveData<Boolean>()
    var emptyPasswordListener = MutableLiveData<Boolean>()
    var emptyConfirmPasswordListener = MutableLiveData<Boolean>()
    var showError = MutableLiveData<Boolean>()
    var navigateToHome = MutableLiveData<Boolean>()
    var navigateToLogIn = MutableLiveData<Boolean>()
    var unEqualPasswordListener = MutableLiveData<Boolean>()
    var verificationDialog = MutableLiveData<Boolean>()

    var signInWithGoogleListener = MutableLiveData<Boolean>()
    lateinit var googleSignInClient : GoogleSignInClient
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        email.value = ""
        password.value = ""
        confirmPassword.value = ""
        errorMessage.value = ""

        emptyEmailListener.value = false
        emptyPasswordListener.value = false
        emptyConfirmPasswordListener.value = false
        showError.value = false
        navigateToHome.value = false
        navigateToLogIn.value = false
        verificationDialog.value = false
    }
    fun signUp(){
        validation()
    }
    fun navigateToLogIn(){
        navigateToLogIn.value = true
    }

    fun navigateToLogInDone(){
        navigateToLogIn.value = false
    }
    private fun validation(){
        if (email.value.equals("")) {
            setEmptyEmailError()
            return
        }
        if(password.value.equals("")) {
            setEmptyPasswordError()
            return
        }
        if(confirmPassword.value.equals("")) {
            setEmptyConfirmPasswordError()
            return
        }
        if(!password.value.equals(confirmPassword.value)) {
            setUnEqualPassword()
            return
        }
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email.value ?: "",password.value ?: "").await()
                checkLoggedInState()
            }catch (e : Exception){
                toastError(e.message)
            }
        }
    }

    private fun setEmptyConfirmPasswordError() {
        emptyConfirmPasswordListener.value = true
    }

    private fun setUnEqualPassword() {
        unEqualPasswordListener.value = true
    }

    fun unEqualPasswordDone(){
        unEqualPasswordListener.value = false
    }

    private fun checkLoggedInState(){
        if(auth.currentUser == null)
            toastError("You have already an account")
        else {
            auth.currentUser!!.sendEmailVerification().addOnCompleteListener {
                if(it.isSuccessful){
                    showDialog()
                }else{
                    toastError(it.exception?.message ?: "")
                }
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

    fun showErrorDone() {
        showError.value = false
    }

    fun emptyPasswordListenerDone() {
        emptyPasswordListener.value = false
    }

    fun emptyEmailListenerDone() {
        emptyEmailListener.value = false
    }

    fun emptyConfirmPasswordListenerDone() {
        emptyConfirmPasswordListener.value = false
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
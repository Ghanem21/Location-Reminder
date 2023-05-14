package com.udacity.project4.authentication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentLoginBinding
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

const val REQUEST_CODE_SIGN_IN_WITH_GOOGLE_From_LogIn = 0
class LoginFragment : Fragment() {
    private val viewModel :LogInViewModel by viewModels()
    private lateinit var binding : FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        auth = FirebaseAuth.getInstance()


        //viewModel.activity = requireActivity()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigateToHome.observe(viewLifecycleOwner){
            if(it) {
                requireActivity().startActivity(Intent(requireActivity(), RemindersActivity::class.java))
                viewModel.navigateToHomeDone()
            }
        }

        viewModel.verificationDialog.observe(viewLifecycleOwner){
            if(it){
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Verification")
                    .setMessage("You have to verify your email")
                    .setPositiveButton("OK"){ dialog,_ ->
                        dialog.dismiss()
                    }.show()

                dialog.setCancelable(false)

                viewModel.showDialogDone()
            }
        }

        viewModel.showError.observe(viewLifecycleOwner){
            if(it){
                Toast.makeText(requireContext(),viewModel.errorMessage.value,Toast.LENGTH_SHORT).show()
                viewModel.showErrorDone()
            }
        }

        viewModel.emptyPasswordListener.observe(viewLifecycleOwner){
            if(it){
                binding.passwordEdt.error = "You must enter your password"
                viewModel.emptyPasswordListenerDone()
            }
        }
        viewModel.emptyEmailListener.observe(viewLifecycleOwner){
            if(it){
                binding.emailEdt.error = "You must enter your email"
                viewModel.emptyEmailListenerDone()
            }
        }
        viewModel.navigateToSignUp.observe(viewLifecycleOwner){
            if(it){
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
                viewModel.navigateToSignUpDone()
            }
        }

        viewModel.signInWithGoogleListener.observe(viewLifecycleOwner){
            if(it){
                viewModel.googleSignInClient.signInIntent.also { intent ->
                    intent.putExtra("requestCode", REQUEST_CODE_SIGN_IN_WITH_GOOGLE_From_LogIn)
                    resultLauncher.launch(intent)
                    viewModel.signInWithGoogleDone()
                }
            }
        }
    }


    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == REQUEST_CODE_SIGN_IN_WITH_GOOGLE_From_LogIn){
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
            account?.let {
                googleAuthForFirebase(it)
            }
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
        lifecycleScope.launch{

            try {
                withContext(Dispatchers.IO) {
                    auth.signInWithCredential(credentials).await()
                }
                if(auth.currentUser != null)
                    startActivity(Intent(requireContext(), RemindersActivity::class.java))
            }catch (exe:Exception){
                Toast.makeText(requireContext(),exe.message, Toast.LENGTH_SHORT).show()
            }
        }
    }



}
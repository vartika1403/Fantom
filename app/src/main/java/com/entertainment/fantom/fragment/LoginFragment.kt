package com.entertainment.fantom.fragment

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull

import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.entertainment.fantom.DetailObject
import com.entertainment.fantom.R
import com.entertainment.fantom.utils.Utils
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LoginFragment : Fragment() {
    private var isLogin: Boolean = false
    private val isVerified: Boolean = false
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String
    private lateinit var enterPhoneNo: EditText
    private lateinit var logInButton: Button
    private lateinit var progressDialog: ProgressDialog
    private val sharedPreferences: SharedPreferences? by lazy {
        context?.getSharedPreferences("app", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        Log.d(TAG, "sharedPref, " + context)
        auth = FirebaseAuth.getInstance()
        isLogin = sharedPreferences?.getBoolean("isLogin", false) ?: false
        logInButton = view.findViewById(R.id.logInButton)
        enterPhoneNo = view.findViewById(R.id.enterPhoneNo)
        logInButton.text = "Get Otp"
        Log.d(TAG, "Login 1: " + isLogin)
        logInButton.setOnClickListener {
            if (TextUtils.isEmpty(enterPhoneNo.getText().toString())) {
                Toast.makeText(context, "Please enter a valid phone number.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val phone = "+91" + enterPhoneNo.getText().toString()
                val text = if (isLogin) "Welcome to Fraternity" else "Sending verification code"
                progressDialog = Utils.showProgressDialog(context, text)
                if (!isLogin) {
                    sendVerificationCode(phone)
                } else {
                    progressDialog.dismiss()
                }
            }
        }

        return view
    }

    private fun sendVerificationCode(number: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number,
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallBack
        );
    }

    // callback method is called on Phone auth provider.
    private val mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                Log.d(TAG, "Login unique id: " + s)
                verificationId = s
            }

            // this method is called when user receive OTP from Firebase.
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                Log.d(TAG, "Login otp code: " + code)

                if (code != null) {
                    enterPhoneNo.setText(code)
                    logInButton.visibility = View.GONE
                    Toast.makeText(context, "verification completed:  ${code}", Toast.LENGTH_SHORT)
                        .show();
                    progressDialog.dismiss()
                    verifyCode(code)
                }
            }

            // this method is called when firebase doesn't
            // sends our OTP code due to any error or issue.
            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                progressDialog.dismiss()
                Log.d(TAG, "Login error: " + e.message)
            }
        }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        Log.d(TAG, "Login user credentials received: " + credential.smsCode)

        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val num = task.result?.user?.phoneNumber
                    val userId = task.result?.user?.uid
                    Log.d(TAG, "Login user no: " + num + " user Id : " + userId)
                    FirebaseDatabase.getInstance().getReference("users").orderByChild("phoneNumber")
                        .equalTo(num).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    openHomeFragment(userId, num)
                                } else {
                                    val detailObject = DetailObject()
                                    detailObject.userId = userId
                                    detailObject.phoneNum = num
                                    FirebaseDatabase.getInstance().getReference("users").push()
                                        .child("phoneNumber").setValue(num).addOnSuccessListener {
                                            openHomeFragment(userId, num)
                                        }
                                }
                            }

                            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
                        })
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.signin_failure_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun openHomeFragment(userId: String?, phoneNum: String?) {
        isLogin = true
        sharedPreferences?.edit()?.apply {
            putBoolean("isLogin", isLogin)
            putString("phoneNumber", phoneNum)
            putString("userId", userId)
            apply()
        }

        findNavController().navigate(R.id.homeFragment, null,
            NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build()
        )
    }

    companion object {
        private const val TAG = "LoginFragment"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
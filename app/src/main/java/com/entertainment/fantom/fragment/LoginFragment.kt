package com.entertainment.fantom.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.entertainment.fantom.R
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseApp
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
    private var isLogin: Boolean? = false
    private val isVerified: Boolean = false
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String
    private lateinit var enterPhoneNo: EditText
    private lateinit var logInButton : Button
    private var sharedPreferences: SharedPreferences? =
        context?.getSharedPreferences("app", Context.MODE_PRIVATE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        Log.d(TAG, "sharedPref, " + context)
        auth = FirebaseAuth.getInstance()
        isLogin = sharedPreferences?.getBoolean("isLogin", false)
        logInButton = view.findViewById<Button>(R.id.logInButton)
        enterPhoneNo = view.findViewById(R.id.enterPhoneNo)
        logInButton.text = "Get Otp"
        logInButton.setOnClickListener {
            if (TextUtils.isEmpty(enterPhoneNo.getText().toString())) {
                Toast.makeText(context, "Please enter a valid phone number.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val phone = "+91" + enterPhoneNo.getText().toString()
                sendVerificationCode(phone)
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
                    logInButton.text = "Verify Code"
                    Toast.makeText(context, "verification completed:  ${code}", Toast.LENGTH_SHORT)
                        .show();

                    verifyCode(code)
                }
            }

            // this method is called when firebase doesn't
            // sends our OTP code due to any error or issue.
            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
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
                    Log.d(TAG, "Login user no: " + num)
                    FirebaseDatabase.getInstance().getReference("users").orderByChild("phoneNumber")
                        .equalTo(num).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    openHomeFragment()
                                } else {
                                    FirebaseDatabase.getInstance().getReference("users").push()
                                        .child("phoneNumber").setValue(num).addOnSuccessListener {
                                           openHomeFragment()
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

    private fun openHomeFragment() {
        val fragmentManager = activity?.supportFragmentManager
        fragmentManager?.let {
            val fragmentTransaction = it.beginTransaction()
            val fragment = HomeFragment.newInstance("isLoggedIn", "");
            fragmentTransaction.replace(R.id.fragment, fragment)
            fragmentTransaction.commit()
        }
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
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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var isLogin:Boolean? = false
    private val isVerified:Boolean = false
    private lateinit var auth:FirebaseAuth
    private lateinit var verificationId:String
    private lateinit var enterPhoneNo:EditText
    private var sharedPreferences: SharedPreferences? = context?.getSharedPreferences("app", Context.MODE_PRIVATE)

    //Creating editor to store values to shared preferences
    var editor = sharedPreferences?.edit()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_login, container, false)
        ButterKnife.bind(this, view);
        Log.d(TAG, "sharedPref, " + context)
        auth = FirebaseAuth.getInstance()
        isLogin = sharedPreferences?.getBoolean("isLogin", false)
        val logInButton = view.findViewById<Button>(R.id.logInButton)
        enterPhoneNo = view.findViewById<EditText>(R.id.enterPhoneNo)
        logInButton.text = "Get Otp"
        //called when text is entered
       // addEnterTextListener()
        logInButton.setOnClickListener {
            // below line is for checking weather the user
            // has entered his mobile number or not.
            if (TextUtils.isEmpty(enterPhoneNo.getText().toString())) {
                // when mobile number text field is empty
                // displaying a toast message.
                Toast.makeText(context, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show()
            } else {
                // if the text field is not empty we are calling our
                // send OTP method for getting OTP from Firebase.
                val phone = "+91" + enterPhoneNo.getText().toString()
                sendVerificationCode(phone)
            }
        }

        return view
    }

    private fun sendVerificationCode(number: String) {
// this method is used for getting
        // OTP on user phone number.
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number, // first parameter is user's mobile number
                60, // second parameter is time limit for OTP
                // verification which is 60 seconds in our case.
                TimeUnit.SECONDS, // third parameter is for initializing units
                // for time period which is in seconds in our case.
                TaskExecutors.MAIN_THREAD, // this task will be excuted on Main thread.
                mCallBack // we are calling callback method when we recieve OTP for
                // auto verification of user.
        );
    }

    private fun addEnterTextListener() {
        enterPhoneNo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    // callback method is called on Phone auth provider.
    private val   // initializing our callbacks for on
    // verification callback method.
            mCallBack: OnVerificationStateChangedCallbacks = object : OnVerificationStateChangedCallbacks() {
        // below method is used when
        // OTP is sent from Firebase
        override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
            super.onCodeSent(s, forceResendingToken)
            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            verificationId = s
        }

        // this method is called when user
        // receive OTP from Firebase.
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            val code = phoneAuthCredential.smsCode

            // checking if the code
            // is null or not.
            if (code != null) {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.
                enterPhoneNo.setText(code)
                Toast.makeText(context,"verification completed:  ${code}",Toast.LENGTH_SHORT).show();

                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code)
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        override fun onVerificationFailed(e: FirebaseException) {
            // displaying error message with firebase exception.
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun verifyCode(code: String) {
// below line is used for getting getting
        // credentials from our verification id and code.
        // below line is used for getting getting
        // credentials from our verification id and code.
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        // after getting credential we are
        // calling sign in method.

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val num = task.result?.user?.phoneNumber
                    FirebaseDatabase.getInstance().getReference("users").orderByChild("phoneNumber")
                        .equalTo(num).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                   /* val intent = Intent(
                                        this@VerifyPhoneActivity,
                                        ProfileActivity::class.java
                                    )
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)*/
                                } else {
                                    FirebaseDatabase.getInstance().getReference("users").push()
                                        .child("phoneNumber").setValue(num).addOnSuccessListener {
                                          /*  val intent = Intent(
                                                this@VerifyPhoneActivity,
                                                WelcomeActivity::class.java
                                            )
                                            startActivity(intent)*/
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


    companion object {
        private const val TAG = "LoginFragment"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
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
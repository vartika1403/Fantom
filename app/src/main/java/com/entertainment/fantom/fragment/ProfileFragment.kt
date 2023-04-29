package com.entertainment.fantom.fragment

import com.entertainment.fantom.DetailObject
import com.google.firebase.analytics.FirebaseAnalytics
import com.entertainment.fantom.R

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.entertainment.fantom.databinding.FragmentDetailsNewBinding
import com.entertainment.fantom.viewmodel.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var detailObject: DetailObject? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var binding: FragmentDetailsNewBinding
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            detailObject = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailsNewBinding.inflate(inflater)
        retainInstance = true
        //logs view event for search fragment
        firebaseAnalytics = activity?.let {
            FirebaseAnalytics.getInstance(it)
        }
        //logs view event
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, TAG)

        firebaseAnalytics?.let {
            it.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)
            it.setAnalyticsCollectionEnabled(true)
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()

        displayUi()
        collectFlow()
    }

    private fun collectFlow() {
        lifecycleScope.launch(Dispatchers.Main) {
            profileViewModel.detailsEntered.collectLatest { value ->
                binding.saveProfile.isEnabled = value
             }
        }
    }

    private fun initListeners() {
        binding.userDetail.userName.nameInputEditText.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
              profileViewModel.setUserName(it.toString())
            }
        }

        binding.userDetail.emailDetails.nameInputEditText.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                profileViewModel.setEmailAddress(it.toString())
            }
        }

        binding.userDetail.webLink.nameInputEditText.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                profileViewModel.setWebLink(it.toString())
            }
        }

        binding.userDetail.instagramDetails.nameInputEditText.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                profileViewModel.setInstagramLink(it.toString())
            }
        }
    }

    private fun displayUi() {
        detailObject?.let {
            binding.userDetail.apply {
                userName.apply {
                    name.text = getString(R.string.user_name)
                    if(it.name.isNullOrEmpty()) nameInputText.hint = getString(R.string.enter_user_name)
                    else nameInputEditText.setText(it.name)
                }

                emailDetails.apply {
                    name.text = getString(R.string.user_email)
                    if (it.email.isNullOrEmpty()) nameInputText.hint = getString(R.string.enter_email_address)
                    else nameInputEditText.setText(it.email)
                }

                webLink.apply {
                    name.text = getString(R.string.user_web_link)
                    if (it.webLink.isNullOrEmpty()) nameInputText.hint = getString(R.string.enter_web_link)
                    else nameInputEditText.setText(it.webLink)
                }

                instagramDetails.apply {
                    name.text = getString(R.string.user_instagram_link)
                    if (it.fbLink.isNullOrEmpty()) nameInputText.hint = getString(R.string.enter_instagram)
                    else nameInputEditText.setText(it.fbLink)
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (activity != null && (activity as AppCompatActivity?)!!.supportActionBar != null) (activity as AppCompatActivity?)!!.supportActionBar!!
            .hide()
    }

    companion object {
        private val TAG = ProfileFragment::class.java.simpleName
        private const val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(detailObject: DetailObject): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, detailObject)
            fragment.arguments = args
            return fragment
        }
    }
}
package com.entertainment.fantom.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.entertainment.fantom.DetailObject
import com.entertainment.fantom.R
import com.entertainment.fantom.data.Resource
import com.entertainment.fantom.data.repository.ProfileRepository
import com.entertainment.fantom.databinding.FragmentDetailsNewBinding
import com.entertainment.fantom.utils.Utils
import com.entertainment.fantom.viewmodel.ProfileViewModel
import com.entertainment.fantom.viewmodel.ProfileViewModelFactory
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var detailObject: DetailObject? = null
    private var isUserProfile: Boolean = false
    private var selectedItem = ""
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var binding: FragmentDetailsNewBinding
    private val profileRepository by lazy {
        ProfileRepository()
    }
    private var categories_list_items = listOf("Female Singer", "Male Singer", "Guitarist", "Band")
    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(
            context as Activity?,
            profileRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide the Action Bar
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailsNewBinding.inflate(inflater)
        retainInstance = true
        //logs view event for search fragment
        firebaseAnalytics = context?.let {
            FirebaseAnalytics.getInstance(requireContext())
        }
        //logs view event
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, TAG)

        firebaseAnalytics?.let {
            it.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)
            it.setAnalyticsCollectionEnabled(true)
        }

        //setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            detailObject = it.getParcelable(DETAIL_OBJECT)
            isUserProfile = it.getBoolean(IS_USER_PROFILE)
        }

        initListeners()

        displayUi()
        collectFlow()
    }

    private fun collectFlow() {
        binding.saveProfile.isVisible = isUserProfile
        var value = false
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                profileViewModel.detailsEnteredAreCorrect.collectLatest {
                    value = it
                }
            }
        }

        binding.saveProfile.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                if (value) {
                    profileViewModel.saveData()
                } else {
                    Toast.makeText(context,
                        getString(R.string.please_enter_correct_details), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        profileViewModel.detailObjectLiveData.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Error -> {
                    if (resource.error.isNotEmpty() && resource.emailError) {
                        Toast.makeText(
                            context,
                            getString(R.string.please_enter_correct_email_address),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (resource.error.isNotEmpty()) {
                        Toast.makeText(
                            context,
                            getString(R.string.please_try_again_later), Toast.LENGTH_LONG
                        ).show()
                    }
                }

                is Resource.Success -> {
                    Log.d("vartika", "result is successfully saved")
                    val dialog = Utils.showProgressDialog(activity, resource.data)
                    dialog.show()
                    dialog.setCanceledOnTouchOutside(true)
                    requireActivity().getSupportFragmentManager().popBackStack()
                }

                is Resource.Loading -> {
                    val dialog = Utils.showProgressDialog(activity, getString(R.string.saving_data))
                    dialog.show()
                    dialog.setCanceledOnTouchOutside(true)

                }
            }
        })
    }

    private fun initListeners() {

        binding.userDetail.userName.userDetailEditText.apply {
            hint = context.getString(R.string.enter_user_band_name)
            addTextChangedListener {
                if (it != null && it.toString().isNotEmpty()) {
                    profileViewModel.setUserName(it.toString())
                }
            }
        }

        binding.userDetail.emailDetails.userDetailEditText.apply {
            hint = context.getString(R.string.enter_email_address)
            addTextChangedListener {
                if (it != null && it.toString().trim().isNotEmpty()) {
                    profileViewModel.setEmailAddress(it.toString())
                }
            }
        }

        binding.userDetail.webLink.userDetailEditText.apply {
            hint = context.getString(R.string.enter_weblink)
            addTextChangedListener {
                if (it != null && it.toString().isNotEmpty()) {
                    profileViewModel.setWebLink(it.toString())
                }
            }
        }

        binding.userDetail.instagramDetails.userDetailEditText.apply {
            hint = context.getString(R.string.enter_instagram_link)
            addTextChangedListener {
                if (it != null && it.toString().isNotEmpty()) {
                    profileViewModel.setInstagramLink(it.toString())
                }
            }
        }
    }

    private fun displayUi() {
        detailObject?.let {
            binding.userDetail.apply {
                userName.apply {
                    if (!isUserProfile && it.name.isNullOrEmpty()) {
                        this.root.visibility = View.GONE
                    }
                    //name.text = getString(R.string.user_name)
                    userDetailEditText.setEnabled(isUserProfile)
                    if (it.name.isNullOrEmpty()) nameInputText.hint =
                        getString(R.string.enter_user_name)
                    else userDetailEditText.setText(it.name)
                }

                emailDetails.apply {
                    if (!isUserProfile && it.email.isNullOrEmpty()) {
                        this.root.visibility = View.GONE
                    }
                    userDetailEditText.setEnabled(isUserProfile)
                    if (it.email.isNullOrEmpty()) nameInputText.hint =
                        getString(R.string.enter_email_address)
                    else userDetailEditText.setText(it.email)
                }

                webLink.apply {
                    if (!isUserProfile && it.webLink.isNullOrEmpty()) {
                        this.root.visibility = View.GONE
                    }

                    userDetailEditText.setEnabled(isUserProfile)
                    if (it.webLink.isNullOrEmpty()) nameInputText.hint =
                        getString(R.string.enter_web_link)
                    else userDetailEditText.setText(it.webLink)
                }

                instagramDetails.apply {
                    if (!isUserProfile && it.fbLink.isNullOrEmpty()) {
                        this.root.visibility = View.GONE
                    }
                    userDetailEditText.setEnabled(isUserProfile)
                    if (it.fbLink.isNullOrEmpty()) nameInputText.hint =
                        getString(R.string.enter_instagram)
                    else userDetailEditText.setText(it.fbLink)
                }

                if (isUserProfile) {
                 /*   val array_adapter = activity?.let { activity ->
                        ArrayAdapter(
                            activity,
                            android.R.layout.simple_spinner_item,
                            categories_list_items
                        )
                    }
                    array_adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categories.root.visibility = View.VISIBLE
                    categories.userDetailEditText.visibility = View.GONE
                    categories.spinner.visibility = View.VISIBLE
                    categories.spinner.adapter = array_adapter
                    selectedItem = categories.spinner.selectedItem as String
                    profileViewModel.setCategory(selectedItem)*/

                    setupCategorySpinner(isUserProfile)
                }
            }
        }
    }


    private fun setupCategorySpinner(isSpinnerVisible: Boolean) {
        binding.userDetail.categories.apply {
            root.visibility = View.VISIBLE

            if (isSpinnerVisible) {
                userDetailEditText.visibility = View.GONE
                spinnerLayout?.visibility = View.VISIBLE
                spinner.visibility = View.VISIBLE

                val activityContext = activity ?: return

                val arrayAdapter = ArrayAdapter(
                    activityContext,
                    R.layout.spinner_text,
                    categories_list_items
                )

                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = arrayAdapter
                spinner.setSelection(0) // Default selection

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedItem = categories_list_items[position]
                        profileViewModel.setCategory(selectedItem)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

            } else {
                userDetailEditText.visibility = View.VISIBLE
                spinnerLayout?.visibility = View.GONE
            }
        }
    }

    companion object {
        val TAG = ProfileFragment::class.java.simpleName
        private const val DETAIL_OBJECT = "detail_obj"
        private const val IS_USER_PROFILE = "isUserProfile"

        @JvmStatic
        fun newInstance(detailObject: DetailObject?, isUserProfile: Boolean): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putParcelable(DETAIL_OBJECT, detailObject)
            args.putBoolean(IS_USER_PROFILE, isUserProfile)
            fragment.arguments = args
            return fragment
        }
    }

}
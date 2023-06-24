package com.entertainment.fantom.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.entertainment.fantom.DetailObject
import com.entertainment.fantom.R
import com.entertainment.fantom.databinding.FragmentDetailsNewBinding
import com.entertainment.fantom.repository.ProfileRepository
import com.entertainment.fantom.utils.Resource
import com.entertainment.fantom.utils.Utils
import com.entertainment.fantom.viewmodel.ProfileViewModel
import com.entertainment.fantom.viewmodel.ProfileViewModelFactory
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var detailObject: DetailObject? = null
    private var isUserProfile: Boolean = false
    private var selectedItem = ""
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var binding: FragmentDetailsNewBinding
    private val profileRepository by lazy {
        ProfileRepository()
    }
    private var categories_list_items = arrayOf("Female Singer", "Male Singer", "Guitarist", "Band")
    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(
            context as Activity?,
            profileRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            detailObject = it.getParcelable(DETAIL_OBJECT)
            isUserProfile = it.getBoolean(IS_USER_PROFILE)
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
        binding.saveProfile.isVisible = isUserProfile
        var value = false
        lifecycleScope.launch(Dispatchers.Main) {
            profileViewModel.detailsEntered.collectLatest {
                value = it
            }
        }

        binding.saveProfile.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                if (value) {
                    profileViewModel.saveData()
                } else {
                    Toast.makeText(context, "Please enter all details: ", Toast.LENGTH_SHORT).show()
                }
            }
        }
        profileViewModel.detailObjectLiveData.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Error -> {
                    if (resource.error.isNotEmpty() && resource.emailError) {
                        Toast.makeText(context, "Please enter correct email address ", Toast.LENGTH_LONG).show()
                    } else if (resource.error.isNotEmpty()) {
                        Toast.makeText(context, "Please try again later", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Success -> {
                    val dialog = Utils.progressDialog(activity, resource.data)
                    dialog.show()
                }
                else -> Resource.Loading
            }
        })
    }

    private fun initListeners() {
        binding.userDetail.userName.nameInputEditText.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                profileViewModel.setUserName(it.toString())
            }
        }

        binding.userDetail.emailDetails.nameInputEditText.addTextChangedListener {
            if (it.toString().trim().isNotEmpty()) {
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
                    if (!isUserProfile && it.name.isNullOrEmpty()) {
                        this.root.visibility = View.GONE
                    }
                    name.text = getString(R.string.user_name)
                    nameInputEditText.setEnabled(isUserProfile)
                    if (it.name.isNullOrEmpty()) nameInputText.hint =
                        getString(R.string.enter_user_name)
                    else nameInputEditText.setText(it.name)
                }

                emailDetails.apply {
                    if (!isUserProfile && it.email.isNullOrEmpty()) {
                        this.root.visibility = View.GONE
                    }
                    name.text = getString(R.string.user_email)
                    nameInputEditText.setEnabled(isUserProfile)
                    if (it.email.isNullOrEmpty()) nameInputText.hint =
                        getString(R.string.enter_email_address)
                    else nameInputEditText.setText(it.email)
                }

                webLink.apply {
                    if (!isUserProfile && it.webLink.isNullOrEmpty()) {
                        this.root.visibility = View.GONE
                    }
                    name.text = getString(R.string.user_web_link)
                    nameInputEditText.setEnabled(isUserProfile)
                    if (it.webLink.isNullOrEmpty()) nameInputText.hint =
                        getString(R.string.enter_web_link)
                    else nameInputEditText.setText(it.webLink)
                }

                instagramDetails.apply {
                    if (!isUserProfile && it.fbLink.isNullOrEmpty()) {
                        this.root.visibility = View.GONE
                    }
                    name.text = getString(R.string.user_instagram_link)
                    nameInputEditText.setEnabled(isUserProfile)
                    if (it.fbLink.isNullOrEmpty()) nameInputText.hint =
                        getString(R.string.enter_instagram)
                    else nameInputEditText.setText(it.fbLink)
                }

                if (isUserProfile) {
                    val array_adapter = activity?.let { activity ->
                        ArrayAdapter(
                            activity,
                            android.R.layout.simple_spinner_item,
                            categories_list_items
                        )
                    }
                    array_adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categories.root.visibility = View.VISIBLE
                    categories.name.text = getString(R.string.user_categories)
                    categories.nameInputEditText.visibility = View.GONE
                    categories.spinner.visibility = View.VISIBLE
                    categories.spinner.adapter = array_adapter
                    selectedItem = categories.spinner.selectedItem as String
                    profileViewModel.setCategory(selectedItem)
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedItem = parent?.getItemAtPosition(position) as String
        profileViewModel.setCategory(selectedItem)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}
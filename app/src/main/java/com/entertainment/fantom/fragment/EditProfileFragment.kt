package com.entertainment.fantom.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.entertainment.fantom.R
import com.entertainment.fantom.data.Resource
import com.entertainment.fantom.data.repository.ProfileRepository
import com.entertainment.fantom.databinding.FragmentEditProfileBinding
import com.entertainment.fantom.utils.UiUtils.getCategories
import com.entertainment.fantom.utils.Utils.hideProgressDialog
import com.entertainment.fantom.utils.Utils.showProgressDialog
import com.entertainment.fantom.viewmodel.ProfileViewModel
import com.entertainment.fantom.viewmodel.ProfileViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private var dialog: ProgressDialog? = null
    private val profileRepository by lazy {
        ProfileRepository()
    }
    private var categoriesList = listOf("Female Singer", "Male Singer", "Guitarist", "Band")
    private val profileViewModel by viewModels<ProfileViewModel> {
        ProfileViewModelFactory(
            context as Activity?,
            profileRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentEditProfileBinding.bind(view)

        val userName = arguments?.let { EditProfileFragmentArgs.fromBundle(it).userName }
        val userEmail = arguments?.let { EditProfileFragmentArgs.fromBundle(it).userEmail }
        val userPhone = arguments?.let { EditProfileFragmentArgs.fromBundle(it).userPhone }
        val userWebsite = arguments?.let { EditProfileFragmentArgs.fromBundle(it).userWebsite }
        val userFacebook = arguments?.let { EditProfileFragmentArgs.fromBundle(it).userFacebook }
        val userCategory = arguments?.let { EditProfileFragmentArgs.fromBundle(it).userCategory }

        binding.userName.isVisible = userName.isNullOrEmpty().not()
        binding.userName.setText(userName)
        if (userName != null) {
            profileViewModel.setUserName(userName)
        }
        binding.userEmail.isVisible = userEmail.isNullOrEmpty().not()
        if (userEmail != null) {
            profileViewModel.setEmailAddress(userEmail)
        }
        binding.userEmail.setText(userEmail)
        binding.userPhone.isVisible = userPhone.isNullOrEmpty().not()
        binding.userPhone.setText(userPhone)
        binding.userWebsite.isVisible = userWebsite.isNullOrEmpty().not()
        binding.userWebsite.setText(userWebsite)
        if (userWebsite != null) {
            profileViewModel.setWebLink(userWebsite)
        }
        binding.userFacebook.isVisible = userFacebook.isNullOrEmpty().not()
        if (userFacebook != null) {
            profileViewModel.setInstagramLink(userFacebook)
        }
        binding.userFacebook.setText(userFacebook)

        initListeners()

        setupCategorySpinner()

        collectFlow()
    }

    private fun initListeners() {

        binding.userName.addTextChangedListener {
            if (it != null && it.toString().trim().isNotEmpty()) {
                profileViewModel.setUserName(it.toString())
            }
        }

        binding.userEmail.addTextChangedListener {
            if (it != null && it.toString().trim().isNotEmpty()) {
                profileViewModel.setEmailAddress(it.toString())
            }
        }

        binding.userFacebook.addTextChangedListener {
            if (it != null && it.toString().trim().isNotEmpty()) {
                profileViewModel.setInstagramLink(it.toString())
            }
        }

        binding.userWebsite.addTextChangedListener {
            if (it != null && it.toString().trim().isNotEmpty()) {
                profileViewModel.setWebLink(it.toString())
            }
        }
    }

    private fun collectFlow() {
        var value = false
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                profileViewModel.detailsEnteredAreCorrect.collectLatest {
                    value = it
                }
            }
        }

        binding.btnSaveProfile.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                if (value) {
                    profileViewModel.saveData()
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.please_enter_correct_details), Toast.LENGTH_SHORT
                    )
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
                    hideProgressDialog(dialog)
                }

                is Resource.Success -> {
                    Log.d("vartika", "result is successfully saved: " + resource.data)
                    hideProgressDialog(dialog)
                    // dialog.show()
                    // dialog.setCanceledOnTouchOutside(true)
                    requireActivity().getSupportFragmentManager().popBackStack()
                }

                is Resource.Loading -> {
                    dialog = showProgressDialog(activity, getString(R.string.saving_data))
                    dialog?.setCanceledOnTouchOutside(true)
                }
            }
        })
    }

    private fun setupCategorySpinner() {
        if (getCategories().isNotEmpty()) {
            categoriesList = getCategories()
        }
        val activityContext = activity ?: return
        val categorySpinner: Spinner = binding.userCategorySpinner

        val arrayAdapter = ArrayAdapter<String>(
            activityContext,
            R.layout.spinner_text,
            categoriesList
        )

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = arrayAdapter
        categorySpinner.setSelection(0) // Default selection

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = categoriesList[position]
                profileViewModel.setCategory(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.entertainment.fantom.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.entertainment.fantom.R
import com.entertainment.fantom.data.repository.ProfileRepository
import com.entertainment.fantom.databinding.FragmentEditProfileBinding
import com.entertainment.fantom.utils.UiUtils.getCategories
import com.entertainment.fantom.viewmodel.ProfileViewModel
import com.entertainment.fantom.viewmodel.ProfileViewModelFactory


class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
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

        binding.userName.setText(userName)
        binding.userEmail.setText(userEmail)
        binding.userPhone.setText(userPhone)
        binding.userWebsite.setText(userWebsite)
        binding.userFacebook.setText(userFacebook)
        setupCategorySpinner()
    }

    fun saveUserData() {

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
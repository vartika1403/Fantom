package com.entertainment.fraternity.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import androidx.navigation.fragment.findNavController
import com.entertainment.fraternity.R
import com.entertainment.fraternity.data.Resource
import com.entertainment.fraternity.data.repository.FetchUserDetailsRepository
import com.entertainment.fraternity.databinding.FragmentUserProfileBinding
import com.entertainment.fraternity.utils.Utils
import com.entertainment.fraternity.viewmodel.UserProfileViewModel
import com.entertainment.fraternity.viewmodel.UserProfileViewModelFactory


class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private val fetchUserDetailsRepository by lazy {
        FetchUserDetailsRepository()
    }
    private val userProfileViewModel by viewModels<UserProfileViewModel> {
        UserProfileViewModelFactory(
            context as Activity?,
            fetchUserDetailsRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutEditProfile.setOnClickListener {
            val action = UserProfileFragmentDirections
                .actionUserProfileFragmentToEditProfileFragment(
                    userName = binding.tvName.text.toString(),
                    userEmail = binding.tvEmailValue.text.toString(),
                    userPhone = binding.tvPhone.text.toString(),
                    userWebsite = binding.tvWebsiteValue.text.toString(),
                    userFacebook = binding.tvFacebookValue.text.toString(),
                    userCategory = binding.tvCategory.text.toString()
                )
            findNavController().navigate(action)
        }

        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        userProfileViewModel.fetchUserDetails()
        var progressDialog = Utils.showProgressDialog(context, "")
        userProfileViewModel.userDetailObjectLiveData.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressDialog = Utils.showProgressDialog(context, "")
                }

                is Resource.Success -> {
                    binding.layoutEditProfile.visibility = View.VISIBLE
                    binding.tvName.isVisible = !resource.data.name.isNullOrEmpty()
                    binding.tvName.text = resource.data.name
                    binding.tvEmailName.isVisible = !resource.data.email.isNullOrEmpty()
                    binding.tvEmailValue.isVisible = !resource.data.email.isNullOrEmpty()
                    binding.tvEmailValue.text = resource.data.email
                    binding.tvWebsiteName.isVisible = !resource.data.webLink.isNullOrEmpty()
                    binding.tvWebsiteValue.isVisible = !resource.data.webLink.isNullOrEmpty()
                    binding.tvWebsiteValue.text = resource.data.webLink
                    binding.tvFacebookName.isVisible = !resource.data.fbLink.isNullOrEmpty()
                    binding.tvFacebookValue.isVisible = !resource.data.fbLink.isNullOrEmpty()
                    binding.tvFacebookValue.text = resource.data.fbLink
                    binding.tvCategory.isVisible = !resource.data.category.isNullOrEmpty()
                    binding.tvCategory.text = resource.data.category

                    Utils.hideProgressDialog(progressDialog)
                }

                is Resource.Error -> {
                    Utils.hideProgressDialog(progressDialog)
                    Toast.makeText(
                        context,
                        getString(R.string.please_try_again_later), Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
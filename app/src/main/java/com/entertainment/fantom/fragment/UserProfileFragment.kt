package com.entertainment.fantom.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.entertainment.fantom.databinding.FragmentUserProfileBinding


class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

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
                    userEmail = binding.tvEmail.text.toString(),
                    userPhone = binding.tvPhone.text.toString(),
                    userWebsite = binding.tvWebsite.text.toString(),
                    userFacebook = binding.tvFacebook.text.toString(),
                    userCategory = binding.tvCategory.text.toString()
                )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
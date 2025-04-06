package com.entertainment.fantom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.entertainment.fantom.databinding.FragmentEditProfileBinding


class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

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
        binding.userCategory.setText(userCategory)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
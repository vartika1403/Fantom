package com.entertainment.fraternity.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.entertainment.fraternity.DetailObject
import com.entertainment.fraternity.data.repository.ProfileRepository
import com.entertainment.fraternity.databinding.FragmentDetailsNewBinding
import com.entertainment.fraternity.utils.Constant.DETAIL_OBJECT
import com.google.firebase.analytics.FirebaseAnalytics

class ProfileFragment : Fragment() {

    companion object {
        val TAG = ProfileFragment::class.java.simpleName
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

    private var detailObject: DetailObject? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var binding: FragmentDetailsNewBinding
    private val profileRepository by lazy {
        ProfileRepository()
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            detailObject = it.getParcelable(DETAIL_OBJECT)
        }


        displayUi()
    }

    @SuppressLint("SetTextI18n")
    private fun displayUi() {
        detailObject?.let {
            binding.apply {
                tvNameValue.isVisible = !it.name.isNullOrEmpty()
                tvNameValue.text = "Name: ${it.name}"
                tvWebsiteLink.isVisible = !it.webLink.isNullOrEmpty()
                tvWebsiteLink.text = "Website: ${it.webLink}"
                tvCategory.isVisible = !it.category.isNullOrEmpty()
                tvCategory.text = "Category: ${it.category}"
                tvFacebookLink.isVisible = !it.fbLink.isNullOrEmpty()
                tvFacebookLink.text = "Facebook or Instagram: ${it.fbLink}"
            }
        }
    }
}





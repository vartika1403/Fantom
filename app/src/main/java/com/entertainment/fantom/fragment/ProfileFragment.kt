package com.entertainment.fantom.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.entertainment.fantom.DetailObject
import com.entertainment.fantom.data.repository.ProfileRepository
import com.entertainment.fantom.databinding.FragmentDetailsNewBinding
import com.entertainment.fantom.utils.Constant.DETAIL_OBJECT
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

    private fun displayUi() {
        detailObject?.let {
            binding.apply {
                tvName.isVisible = !it.name.isNullOrEmpty()
                tvName.text = it.name
                tvWebsite.isVisible = !it.webLink.isNullOrEmpty()
                tvWebsiteLink.text = it.webLink
                tvCategory.isVisible = !it.category.isNullOrEmpty()
                tvCategory.text = it.category
                tvFacebook.isVisible = !it.fbLink.isNullOrEmpty()
                tvFacebookLink.text = it.fbLink
            }
        }
    }
}





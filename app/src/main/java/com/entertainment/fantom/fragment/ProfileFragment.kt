package com.entertainment.fantom.fragment

import com.entertainment.fantom.DetailObject
import com.google.firebase.analytics.FirebaseAnalytics
import com.entertainment.fantom.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.View.VISIBLE
import android.view.View.GONE
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import com.bumptech.glide.Glide
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.entertainment.fantom.databinding.FragmentDetailBinding

class ProfileFragment : Fragment() {
    private var detailObject: DetailObject? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var binding: FragmentDetailBinding

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
        binding = FragmentDetailBinding.inflate(inflater)
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
        //set image
        setImageToView()

        // set data
        setData()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setData() {
        detailObject?.let {
            binding.nameTextValue.visibility = if (!it.name.isNullOrEmpty()) VISIBLE else GONE
            binding.nameTextValue.text = it.name
            binding.emailText.visibility = if (!it.email.isNullOrEmpty()) VISIBLE else GONE
            binding.emailText.text = it.name
            binding.fbLinkText.visibility = if (!it.fbLink.isNullOrEmpty()) VISIBLE else GONE
            binding.fbLinkText.text = it.fbLink
            binding.webLinkText.visibility =
                if (!it.webLink.isNullOrEmpty()) VISIBLE else GONE
            binding.webLinkText.text = it.webLink
        }
    }

    private fun setImageToView() {
        detailObject?.let {
            if (!it.image.isNullOrEmpty()) {
                binding.image.visibility = VISIBLE

                // Create a storage reference from our app
                val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                    it.imageUrl
                )

                Log.d(TAG, "storageRef, " + storageRef.downloadUrl)
                storageRef.downloadUrl.addOnCompleteListener { }
                Glide.with(this)
                    .load(storageRef)
                    .centerCrop()
                    .placeholder(R.drawable.splash)
                    .into(binding.image)
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
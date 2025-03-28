package com.entertainment.fantom.fragment;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.entertainment.fantom.DetailObject;
import com.entertainment.fantom.R;
import com.entertainment.fantom.databinding.FragmentHomeBinding;
import com.entertainment.fantom.utils.Utils;
import com.entertainment.fantom.viewmodel.HomeViewModel;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements MenuProvider {
    public static final String TAG = "HomeFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAnalytics firebaseAnalytics;
    private HomeViewModel homeViewModel;
    private ProgressDialog dialog;

    private String selectedItem;
    private List<String> itemsList;
    private FragmentHomeBinding fragmentHomeBinding;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater);
        dialog = Utils.progressDialog(getActivity(), "");
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, "HomeFragment");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.setMinimumSessionDuration(1000);
        fragmentHomeBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        homeViewModel.loadDataFromFirebase();
        dialog.dismiss();
        fetchItemsFromFirebase();

        fragmentHomeBinding.joinButton.setOnClickListener(v -> {
            clickButton();
        });


        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onCreateMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.add_profile) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = ProfileFragment.newInstance(new DetailObject(), true);
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(TAG);
            fragmentTransaction.commit();
        }
        return true;
    }

    public void fetchItemsFromFirebase() {
        // dialog.show();
        homeViewModel.getItemsFromFromFirebase().observe(getViewLifecycleOwner(), items -> {
            // dialog.dismiss();
            if (items != null) {
                if (!items.isEmpty()) {
                    fragmentHomeBinding.spinner.setVisibility(View.VISIBLE);
                    fragmentHomeBinding.joinButton.setVisibility(View.VISIBLE);
                    setDataToSpinner(items);
                } else {
                    fragmentHomeBinding.spinner.setVisibility(View.GONE);
                    fragmentHomeBinding.joinButton.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(requireContext(), "Network issue , please try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setDataToSpinner(List<String> categories) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_text, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);

        // attaching data adapter to spinner
        fragmentHomeBinding.spinner.setAdapter(dataAdapter);
       /* if (getActivity() != null) {
            Drawable mIcon = ContextCompat.getDrawable(getActivity(), R.mipmap.drop_down);
            mIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_green_dark),
                    PorterDuff.Mode.MULTIPLY);
            fragmentHomeBinding. .setImageDrawable(mIcon);
        }*/
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            getActivity().invalidateOptionsMenu();
        }
    }

    //move to searchFragment
    public void clickButton() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, "HomeFragment");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, selectedItem);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Fragment fragment = SearchFragment.newInstance(selectedItem, "");
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String tag = fragment.getClass().getName();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d(TAG, "onPrepartionMenu of Home: ");
        ActionBar actionBar = Objects.requireNonNull(getActivity()).getActionBar();
        Log.d(TAG, "onPrepartionMenu action bar, " + actionBar);
        if (actionBar != null) {
            actionBar.show();
        }
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }*/
}

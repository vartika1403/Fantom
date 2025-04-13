package com.entertainment.fantom.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.entertainment.fantom.R;
import com.entertainment.fantom.databinding.FragmentHomeBinding;
import com.entertainment.fantom.utils.UiUtils;
import com.entertainment.fantom.utils.Utils;
import com.entertainment.fantom.viewmodel.HomeViewModel;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

public class HomeFragment extends Fragment implements MenuProvider {
    public static final String TAG = "HomeFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAnalytics firebaseAnalytics;
    private HomeViewModel homeViewModel;
    private ProgressDialog dialog;

    private String selectedItem;
    private FragmentHomeBinding fragmentHomeBinding;


    public HomeFragment() {
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
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater);
        dialog = Utils.showProgressDialog(getActivity(), "");
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, TAG);
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
       // menu.clear();
        menuInflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        if (menuItem.getItemId() == R.id.add_profile) {
            navController.navigate(R.id.profileFragment);
            return true;
        }
        return false;
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
                Toast.makeText(requireContext(), R.string.network_issue_please_try_again, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setDataToSpinner(List<String> categories) {
     //        categories.add(0, requireActivity().getString(R.string.spinner_title));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_text, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentHomeBinding.spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof AppCompatActivity) {
            UiUtils.showToolbarAndDrawer((AppCompatActivity) getActivity());
        }
    }

    //move to searchFragment
    public void clickButton() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, "HomeFragment");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, selectedItem);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        Bundle args = new Bundle();
        args.putString("selectedItem", selectedItem);
        navController.navigate(R.id.searchFragment, args);
    }

}

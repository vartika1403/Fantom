package com.entertainment.fantom.fragment;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.entertainment.fantom.R;
import com.entertainment.fantom.utils.Utils;
import com.entertainment.fantom.viewmodel.HomeViewModel;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAnalytics firebaseAnalytics;
    private HomeViewModel homeViewModel;
    private ProgressDialog dialog;

    private String selectedItem;
    private List<String> itemsList;

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.button)
    Button button;

    @BindView(R.id.spinner_image)
    ImageView spinnerDropDownImage;

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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        ButterKnife.bind(this,view);
        dialog = Utils.progressDialog(getActivity());
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        firebaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getActivity()));
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, "HomeFragment");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.setMinimumSessionDuration(1000);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fetchItemsFromFirebase();

        return view;
    }

    public void fetchItemsFromFirebase() {
        homeViewModel.getItemsFromFromFirebase().observe(getViewLifecycleOwner(), items -> {
              if (items != null && !items.isEmpty()) {
                  spinner.setVisibility(View.VISIBLE);
                  setDataToSpinner(items);
                  dialog.dismiss();
              } else {
                  dialog.dismiss();
              }
        });
    }

    public void setDataToSpinner(List<String> categories) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_text, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        Drawable mIcon= ContextCompat.getDrawable(getActivity(), R.mipmap.drop_down);
        assert mIcon != null;
        mIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_green_dark),
                PorterDuff.Mode.MULTIPLY);
        spinnerDropDownImage.setImageDrawable(mIcon);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            getActivity().invalidateOptionsMenu();
        }
    }

    //move to searchFragment
    @OnClick(R.id.button)
    public void clickButton() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, "HomeFragment");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, selectedItem);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        Fragment fragment = SearchFragment.newInstance(selectedItem, "");
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String tag = fragment.getClass().getName();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d(TAG, "onPrepartionMenu of Home: " );
        ActionBar actionBar= Objects.requireNonNull(getActivity()).getActionBar();
        Log.d(TAG, "onPrepartionMenu action bar, " + actionBar);
        if (actionBar != null) {
            actionBar.show();
        }
    }
}

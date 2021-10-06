package com.entertainment.fantom.fragment;

import android.app.ActionBar;
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
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAnalytics firebaseAnalytics;

    private String selectedItem;

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
        ButterKnife.bind(this,view);

        //R.id.spinner_layout;
        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        //logs view event
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

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Looking for Bands for Events");
        categories.add("Looking for Guitarist");
        categories.add("Looking for male singer");
        categories.add("Looking for female singer");
        categories.add("Flute");
        categories.add("Guitarist");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_text, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        Drawable mIcon= ContextCompat.getDrawable(getActivity(), R.mipmap.drop_down);
        mIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_green_dark),
                PorterDuff.Mode.MULTIPLY);
        spinnerDropDownImage.setImageDrawable(mIcon);


        return view;
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
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment =  SearchFragment.newInstance(selectedItem, "");
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String tag = fragment.getClass().getName();
        fragmentTransaction.replace(R.id.home_fragment, fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d(TAG, "onPrepartionMenu of Home: " );
        ActionBar actionBar= getActivity().getActionBar();
        Log.d(TAG, "onPrepartionMenu action bar, " + actionBar);
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.drawable.splash);
            actionBar.show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



}

package com.entertainment.fantom.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.entertainment.fantom.DetailObject;
import com.entertainment.fantom.adapter.EntityListAdapter;
import com.entertainment.fantom.R;
import com.entertainment.fantom.SearchInterface;
import com.entertainment.fantom.utils.Utils;
import com.entertainment.fantom.viewmodel.HomeViewModel;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;


/**
 * created by vartika sharma
 * create an instance of this Search fragment.
 */
public class SearchFragment extends HomeFragment implements SearchInterface {
    private static final String TAG = SearchFragment.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private HomeViewModel homeViewModel;
    private String entityName;
    private RecyclerView recyclerView;
    private EntityListAdapter adapter;
    private SearchFragment context;
    private ProgressDialog dialog;
    private FirebaseAnalytics firebaseAnalytics;
    private Parcelable recyclerViewState;
    private LinearLayoutManager linearLayoutManager;
    private TextView notAvailableText;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            context = this;
            entityName = getArguments().getString(ARG_PARAM1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        setRetainInstance(true);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setEntityName(entityName);
        recyclerView = view.findViewById(R.id.recycler_view);
        notAvailableText = view.findViewById(R.id.not_available_text);

        dialog = Utils.progressDialog(getActivity(), "");
        subscribeToLiveData();
        //logs view event for search fragment
        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        //logs view event
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, TAG);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.setMinimumSessionDuration(1000);
        setHasOptionsMenu(true);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void subscribeToLiveData() {
        homeViewModel.getDataFromFirebase().observe(getViewLifecycleOwner(), detailObjectList -> {
            if (detailObjectList ==null || detailObjectList.size() == 0) {
                notAvailableText.setVisibility(View.VISIBLE);
            } else {
                notAvailableText.setVisibility(View.GONE);
                setDataToAdapter(detailObjectList);
            }
            dialog.dismiss();
        });
    }

    private void setDataToAdapter(List<DetailObject> detailObjectList) {
        adapter = new EntityListAdapter(detailObjectList, context) ;
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.onRestoreInstanceState(recyclerViewState);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get item selected and deal with it
        switch (item.getItemId()) {
            case android.R.id.home:
                //called when the up affordance/carat in actionbar is pressed
                return true;
        }
        return false;
    }

    @Override
    public void setData(DetailObject detailObject) {
         if (detailObject != null) {
             Bundle bundle = new Bundle();
             bundle.putString(FirebaseAnalytics.Param.ORIGIN, TAG);
             bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, detailObject.getName());
             firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
             FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
             FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
             Fragment fragment = ProfileFragment.newInstance(detailObject);
             fragmentTransaction.replace(R.id.fragment, fragment);
             fragmentTransaction.addToBackStack(TAG);
             fragmentTransaction.commit();
         }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d(TAG, "onPrepartionMenu of Search: " );
        if (getActivity() != null &&  ((AppCompatActivity) getActivity()).getSupportActionBar() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save list state
        if (linearLayoutManager != null) {
            recyclerViewState = linearLayoutManager.onSaveInstanceState();
            state.putParcelable("state", recyclerViewState);
        }
    }
}


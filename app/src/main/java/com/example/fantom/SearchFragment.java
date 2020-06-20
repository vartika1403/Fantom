package com.example.fantom;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;



/**
 * created by vartika sharma
 * create an instance of this Search fragment.
 */
public class SearchFragment extends Fragment{
    private static final String TAG = SearchFragment.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private HomeViewModel homeViewModel;
    private String entityName;
    private RecyclerView recyclerView;
    private  EntityListAdapter adapter;
    private Context context;

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
            context = getActivity();
            entityName = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.setEntityName(entityName);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // subscribe live data to get data from ViewModel
        subscribeToLiveData();

        return view;
    }

    private void subscribeToLiveData() {
        homeViewModel.getDataFromFirebase().observe(this, detailObjectList -> {
            adapter = new EntityListAdapter(detailObjectList, context) ;
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        });

       // recyclerView.
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

    private void printBackStack() {
    }

    public void removeOrHideCurFragOnBack(FragmentTransaction fragmentTransaction, Fragment fragment) {
        if(fragment == null)
            return;
        fragmentTransaction.remove(fragment);
    }

    }


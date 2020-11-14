package com.entertainment.fantom;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;


/**
 * created by vartika sharma
 * create an instance of this Search fragment.
 */
public class SearchFragment extends Fragment implements SearchInterface{
    private static final String TAG = SearchFragment.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private HomeViewModel homeViewModel;
    private String entityName;
    private RecyclerView recyclerView;
    private  EntityListAdapter adapter;
    private SearchFragment context;
    private ProgressDialog dialog;
    private FirebaseAnalytics firebaseAnalytics;
    private List<DetailObject> detailObjectList;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        setRetainInstance(true);

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.setEntityName(entityName);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        notAvailableText =  (TextView)view.findViewById(R.id.not_available_text);
        detailObjectList = new ArrayList<>();

        //show progress dialog
      //  showProgressDialog();
        //dialog = new ProgressDialog(getActivity(),R.style.AppCompatAlertDialogStyle);
        dialog = ProgressDialog.show(getActivity(), "",
                "Loading. Please wait...", true);
       // dialog.addContentView();
        //initialize recyclerview and adapter
     //   initRecyclerView();
        // subscribe live data to get data from ViewModel
        subscribeToLiveData();
        //logs view event for search fragment
        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        //logs view event
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, TAG);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.setMinimumSessionDuration(1000);
        return view;
    }

/*
    private void showProgressDialog() {
        ProgressDialogLayout progressDialogLayout = (ProgressDialogLayout) getActivity().getLayoutInflater().inflate(R.layout.progress_dialog_layout, null);

        progressDialog.setCancelable(false);

        progressDialogLayout.setTitle(title);
        progressDialogLayout.setMessage(message);
        progressDialogLayout.setButton(getString(R.string.word_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceConnection.scheduleTask(new Runnable() {
                    @Override
                    public void run() {
                        serviceConnection.getConnectionService().endService();
                    }
                });
                progressDialog.dismiss();
                generateInvalidSnackBar(view, getString(R.string.connection_cancelled)).show();
                loginProgressDialog = null;
            }
        });
        progressDialog.show();
        progressDialog.setContentView(progressDialogLayout);
    }
*/

    private void initRecyclerView() {
        adapter = new EntityListAdapter(detailObjectList, context) ;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void subscribeToLiveData() {
        homeViewModel.getDataFromFirebase().observe(this, detailObjectList -> {
            if (detailObjectList ==null || detailObjectList.size() == 0) {
                notAvailableText.setVisibility(View.VISIBLE);
            } else {
                notAvailableText.setVisibility(View.GONE);
            }
            adapter = new EntityListAdapter(detailObjectList, context) ;
            recyclerView.setHasFixedSize(true);
          // detailObjectList.addAll(detailObjectList);
             linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setSmoothScrollbarEnabled(true);
            linearLayoutManager.onRestoreInstanceState(recyclerViewState);
            recyclerView.setLayoutManager(linearLayoutManager);

            recyclerView.setAdapter(adapter);
            dialog.dismiss();
           // recyclerView.setAdapter(adapter);
        });

/*
        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

            }
        });
*/
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

    @Override
    public void setData(DetailObject detailObject) {
         if (detailObject != null) {
             Bundle bundle = new Bundle();
             bundle.putString(FirebaseAnalytics.Param.ORIGIN, TAG);
             bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, detailObject.getName());
             firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
             FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
             FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
             Fragment fragment = DetailFragment.newInstance(detailObject);
             fragmentTransaction.replace(R.id.home_fragment, fragment);
             fragmentTransaction.addToBackStack(null);
             fragmentTransaction.commit();
         }
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

   /* @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if(state != null)
            mListState = state.getParcelable(LIST_STATE_KEY);
    }*/

   // @Override
    //public void onRestoreInstanceState() {
      //  super.onViewStateRestored();
    //}
}


package com.entertainment.fantom.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.entertainment.fantom.DetailObject;
import com.entertainment.fantom.R;
import com.entertainment.fantom.SearchInterface;
import com.entertainment.fantom.adapter.EntityListAdapter;
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
    private static final String ARG_PARAM1 = "selectedRole";
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

    private EditText searchEditText;

    private List<DetailObject> fullItemList = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        setRetainInstance(true);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setEntityName(entityName);
        recyclerView = view.findViewById(R.id.recycler_view);
        notAvailableText = view.findViewById(R.id.not_available_text);
        searchEditText = view.findViewById(R.id.searchEditText);

        dialog = Utils.showProgressDialog(getActivity(), "");
        subscribeToLiveData();
        //logs view event for search fragment
        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        //logs view event
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, TAG);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.setMinimumSessionDuration(1000);


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterList(s.toString());
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void subscribeToLiveData() {
        homeViewModel.getDataFromFirebase().observe(getViewLifecycleOwner(), detailObjectList -> {
            if (detailObjectList == null || detailObjectList.size() == 0) {
                notAvailableText.setVisibility(View.VISIBLE);
            } else {
                notAvailableText.setVisibility(View.GONE);
                fullItemList.clear();
                fullItemList.addAll(detailObjectList);
                setDataToAdapter(detailObjectList);
            }
            dialog.dismiss();
        });
    }

    private void setDataToAdapter(List<DetailObject> detailObjectList) {
        adapter = new EntityListAdapter(detailObjectList, context);
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
    public void setData(DetailObject detailObject) {
        if (detailObject != null && getView() != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ORIGIN, TAG);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, detailObject.getName());
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            Bundle navBundle = new Bundle();
            navBundle.putParcelable("detail_object", detailObject);
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_searchFragment_to_profileFragment, bundle);
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

    private void filterList(String query) {
        List<DetailObject> filteredList = new ArrayList<>();

        for (DetailObject item : fullItemList) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.updateList(filteredList);

        if (filteredList.isEmpty()) {
            notAvailableText.setVisibility(View.VISIBLE);
        } else {
            notAvailableText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().addMenuProvider(this, getViewLifecycleOwner());
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.add_profile) {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_searchFragment_to_profileFragment);
            return true;
        }
        return false;
    }
}


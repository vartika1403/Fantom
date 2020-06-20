package com.example.fantom;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailFragment extends Fragment {
    private static final String TAG = DetailFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DetailObject detailObject;

    @BindView(R.id.image)
    ImageView bandImage;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.emailText)
    TextView emailText;
    @BindView(R.id.entityName)
    TextView entityName;
    @BindView(R.id.nameTextValue)
    TextView name;
    @BindView(R.id.fbLink)
    TextView fbLink;
    @BindView(R.id.fbLinkText)
    TextView fbLinkText;
    @BindView(R.id.webLink)
    TextView webLink;
    @BindView(R.id.webLinkText)
    TextView webLinkText;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(DetailObject detailObject) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, detailObject);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //if (savedInstanceState != null)
            detailObject = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        if (savedInstanceState != null)
        detailObject = savedInstanceState.getParcelable(ARG_PARAM1);
        //set image
        setImageToView();

        // set data
        setData();
        return view;
    }

    private void setData() {
        if (detailObject != null && detailObject.getName() != null
                && !detailObject.getName().isEmpty()) {
            entityName.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            name.setText(detailObject.getName());
        } else {
            name.setVisibility(View.GONE);
            entityName.setVisibility(View.GONE);
        }

        if (detailObject != null && detailObject.getEmail() != null
                && !detailObject.getEmail().isEmpty()) {
            email.setVisibility(View.VISIBLE);
            emailText.setVisibility(View.VISIBLE);
            emailText.setText(detailObject.getEmail());
        } else {
            emailText.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        }

        if (detailObject != null && detailObject.getFbLink() != null
                && !detailObject.getFbLink().isEmpty()) {
            fbLink.setVisibility(View.VISIBLE);
            fbLinkText.setVisibility(View.VISIBLE);
            fbLinkText.setText(detailObject.getFbLink());
        } else {
            fbLinkText.setVisibility(View.GONE);
            fbLink.setVisibility(View.GONE);
        }

        if (detailObject != null && detailObject.getWebLink() != null
                &&!detailObject.getWebLink().isEmpty()) {
            webLink.setVisibility(View.VISIBLE);
            webLinkText.setVisibility(View.VISIBLE);
            webLinkText.setText(detailObject.getWebLink());
        } else {
            webLinkText.setVisibility(View.GONE);
            webLink.setVisibility(View.GONE);
        }
    }

    private void setImageToView() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

package com.entertainment.fantom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.entertainment.fantom.ProfileObject;
import com.entertainment.fantom.R;
import com.entertainment.fantom.SearchInterface;
import com.entertainment.fantom.fragment.SearchFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityListAdapter extends RecyclerView.Adapter<EntityListAdapter.EntityViewHolder> {
    private static final String TAG = EntityListAdapter.class.getSimpleName();
    private final List<ProfileObject> profileObjectList;
    private SearchFragment context;

    public EntityListAdapter(List<ProfileObject> profileObjectList, SearchFragment context) {
        this.profileObjectList = profileObjectList;
        this.context = context;
    }
    @NotNull
    @Override
    public EntityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_item, parent,  false);
        return new EntityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull EntityViewHolder holder, int position) {
        if (profileObjectList.get(position) != null)
         holder.entityName.setText(profileObjectList.get(position).getName());

        holder.itemView.setOnClickListener(view -> {
            SearchInterface searchInterface = (SearchInterface)context;
            searchInterface.setData(profileObjectList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return profileObjectList.size();
    }

    protected static class EntityViewHolder extends RecyclerView.ViewHolder {
        public TextView entityName;

        public EntityViewHolder(View itemView) {
            super(itemView);
            entityName = (TextView)itemView.findViewById(R.id.item_text);
        }
    }
}

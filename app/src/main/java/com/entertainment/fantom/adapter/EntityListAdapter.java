package com.entertainment.fantom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.entertainment.fantom.DetailObject;
import com.entertainment.fantom.R;
import com.entertainment.fantom.SearchInterface;
import com.entertainment.fantom.fragment.SearchFragment;

import java.util.List;

public class EntityListAdapter extends RecyclerView.Adapter<EntityListAdapter.EntityViewHolder> {
    private static final String TAG = EntityListAdapter.class.getSimpleName();
    private List<DetailObject> detailObjectList;
    private SearchFragment context;

    public EntityListAdapter(List<DetailObject> detailObjectList, SearchFragment context) {
        this.detailObjectList = detailObjectList;
        this.context = context;
    }
    @Override
    public EntityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_item, parent,  false);
        return new EntityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EntityViewHolder holder, int position) {
        if (holder != null) {
            if (detailObjectList.get(position) != null)
                holder.entityName.setText(detailObjectList.get(position).getName());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchInterface searchInterface = (SearchInterface) context;
                searchInterface.setData(detailObjectList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return detailObjectList.size();
    }

    protected class EntityViewHolder extends RecyclerView.ViewHolder {
        public TextView entityName;

        public EntityViewHolder(View itemView) {
            super(itemView);
            entityName = (TextView)itemView.findViewById(R.id.item_text);
        }
    }
}

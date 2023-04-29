package com.entertainment.fantom.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.entertainment.fantom.Conf;
import com.entertainment.fantom.DetailObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    public static final String TAG = HomeViewModel.class.getSimpleName();
    private MutableLiveData<List<DetailObject>> entityData;
    private MutableLiveData<List<String>> itemsLiveData;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public String entityName;

    public HomeViewModel() {
        entityData = new MutableLiveData<>();
        itemsLiveData = new MutableLiveData<>();
        // instance of our Firebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReferenceFromUrl(Conf.firebaseDomainUri());

        // load data from firebase
        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        List<String> entityList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.d("Get Data", postSnapshot.getKey());
                    entityList.add(postSnapshot.getKey());
                }
                itemsLiveData.setValue(entityList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                itemsLiveData.setValue(entityList);
                Log.d("Count " ,"databaseError " + databaseError.getMessage());
            }
        });
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
        loadEntityDataFromFirebase();
        Log.d(TAG, "entity name and databaseref, " + entityName + ", " + databaseReference.child(entityName));
    }

    private void loadEntityDataFromFirebase() {
        List<DetailObject> entityList = new ArrayList<>();
        databaseReference.child(entityName).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for (DataSnapshot data : dataSnapshot.getChildren()) {
                     Log.d(TAG, "snapshot data 1,"  + ", key," + data.getKey());
                     try {
                         DetailObject detailObject = data.getValue(DetailObject.class);
                         //JSONObject jsonObject = new JSONObject(data.getValue(DetailObject.class));
                         assert detailObject != null;
                         detailObject.setName(data.getKey());
                         String fbLink = detailObject.getFbLink();
                         String webLink = detailObject.getWebLink();
                         String imageUrl = detailObject.getImage();
                         Log.d(TAG, "snapshot data 1 2," + fbLink+ ", webLink," + webLink +
                                 " image url," + imageUrl);
                         entityList.add(detailObject);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                     //DetailObject detailObject = new DetailObject(data.getKey(), data.getValue().toString())
                    Log.d(TAG, "snapshot data 2, " + data.toString());
                 }
                 entityData.setValue(entityList);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
                 Log.d("Count " ,"databaseError " + databaseError.getMessage());
                 entityData.setValue(entityList);
             }
        });
    }

    public LiveData<List<DetailObject>> getDataFromFirebase(){
          return entityData;
    }

    public LiveData<List<String>> getItemsFromFromFirebase() {
        return itemsLiveData;
    }

}

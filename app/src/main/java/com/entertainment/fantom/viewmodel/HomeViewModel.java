package com.entertainment.fantom.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.entertainment.fantom.Conf;
import com.entertainment.fantom.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    public static final String TAG = HomeViewModel.class.getSimpleName();
    public MutableLiveData<List<User>> entityData;

    // Write a message to the database
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    public String entityName;

    public HomeViewModel() {
        entityData = new MutableLiveData<>();

    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
      //  database = FirebaseDatabase.getInstance();

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
       // databaseReference.child()

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Conf.firebaseDomainUri());
        loadDataFromFirebase();
        Log.d(TAG, "entity name and databaseref, " + entityName + ", " + databaseReference.child(entityName));
    }


    private void loadDataFromFirebase() {
         databaseReference.child(entityName).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 List<User> entityList = new ArrayList<>();
                 for (DataSnapshot data : dataSnapshot.getChildren()) {
                     Log.d(TAG, "snapshot data 1,"  + ", key," + data.getKey() + " value: " + data.getValue());
                     try {
                         User profileObject = data.getValue(User.class);
                         //JSONObject jsonObject = new JSONObject(data.getValue(DetailObject.class));
                         profileObject.setName(data.getKey());
                         String fbLink = profileObject.getFbLink();
                         String webLink = profileObject.getWebLink();
                         String imageUrl = profileObject.getImage();
                         Log.d(TAG, "snapshot data 1 2," + fbLink+ ", webLink," + webLink +
                                 " image url," + imageUrl);
                         entityList.add(profileObject);
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

             }
         });
    }

    public LiveData<List<User>> getDataFromFirebase(){
          return entityData;
    }
}

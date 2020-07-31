package com.bustracking.utils;

import com.google.firebase.firestore.DocumentReference;

public interface OnGetDataListener {
    //this is for callbacks
    void onSuccess(DocumentReference documentReference);
    void onStart();
    void onFailure();


}

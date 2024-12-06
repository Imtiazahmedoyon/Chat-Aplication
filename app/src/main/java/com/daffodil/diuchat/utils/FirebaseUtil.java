package com.daffodil.diuchat.utils;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class FirebaseUtil {

    private static final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public static void saveUserDetails(String userId, String username, String email, FirebaseCallback callback) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);

        firestore.collection("Users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public interface FirebaseCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}

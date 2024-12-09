package com.daffodil.diuchat.utils;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUtil {

    private static final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();  // This initializes FirebaseStorage

    // Save user details to Firestore
    public static void saveUserDetails(String userId, String username, String email, FirebaseCallback callback) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);

        firestore.collection("Users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // Check if the user is logged in
    public static boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    // Sign out the current user
    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    // Get current logged in user ID
    public static String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }

    // Get the reference for user's profile picture in Firebase Storage
    public static StorageReference getUserProfilePicReference(String userId) {
        return storage.getReference().child("profile_pics").child(userId + ".jpg");
    }

    // Firebase callback interface for success or failure
    public interface FirebaseCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}

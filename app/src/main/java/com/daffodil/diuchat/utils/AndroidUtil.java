package com.daffodil.diuchat.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;

import com.daffodil.diuchat.R;
import com.daffodil.diuchat.model.User;

public class AndroidUtil {

    // Show a simple toast message
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Set profile picture (for simplicity, using a placeholder image)
    public static void setProfilePic(Context context, ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_person); // Placeholder image
    }

    // Pass basic User data to Intent
    public static void passUserToIntent(Intent intent, User user) {
        intent.putExtra("userId", user.getUserId());
        intent.putExtra("username", user.getUsername());
        intent.putExtra("email", user.getEmail());
    }
}
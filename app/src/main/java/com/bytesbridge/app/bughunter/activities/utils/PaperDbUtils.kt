package com.bytesbridge.app.bughunter.activities.utils

import android.content.Context
import com.bytesbridge.app.bughunter.activities.ui.data.models.UserModel
import com.google.firebase.firestore.auth.User
import io.paperdb.Paper

class PaperDbUtils {

    companion object {

        var currentUser: UserModel? = null

        fun user(context: Context, userData: UserModel) {
            Paper.init(context)
            Paper.book().write("user", userData)
        }

        fun user(context: Context): UserModel? {

            Paper.init(context)
            currentUser = Paper.book().read("user", null)

            return currentUser
        }

        fun clearData(context: Context) {
            Paper.init(context)
            Paper.book().delete("user")
        }

    }
}
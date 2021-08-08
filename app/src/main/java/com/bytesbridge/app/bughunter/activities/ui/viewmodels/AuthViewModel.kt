package com.bytesbridge.app.bughunter.activities.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.LoginModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.SignUpModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.UserModel
import com.bytesbridge.app.bughunter.activities.ui.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

//@HiltViewModel
class AuthViewModel
//    (var repository: Repository) :
    () :
    ViewModel() {
    private var repository: Repository = Repository()

    //    ----------------- Register ---------------
    fun registerUser(
        signUpModel: SignUpModel,
        userRegisteredResponse: (userCreatedStatus: Boolean, message: String) -> Unit
    ) {
        this.repository.registerUser(signUpModel) { successRegisterStatus, message ->
            userRegisteredResponse(successRegisterStatus, message)
        }
    }
//    ----------------- Register ---------------


    //    ----------------- Login ---------------
    fun login(
        loginModel: LoginModel,
        loginResponse: (status: Boolean, message: String, userModel: UserModel?) -> Unit
    ) {
        this.repository.loginUser(loginModel) { successLoginStatus, message, user ->
            loginResponse(successLoginStatus, message, user)
        }
    }
//    ----------------- Login --------------
    fun getUserFromFirebase(uId:String,userData:(user:UserModel?)->Unit){
        this.repository.getUserDataToFireStore(uId){
            userData(it)
        }

    }
}
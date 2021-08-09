package com.bytesbridge.app.bughunter.activities.ui.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bytesbridge.app.bughunter.activities.networking.RetrofitInstance
import com.bytesbridge.app.bughunter.activities.ui.data.models.*
import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.*
import com.bytesbridge.app.bughunter.activities.utils.Constants
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.CONTAINS_STACKOVERFLOW_LINK
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.FIREBASE_ANSWER_ID
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.FIREBASE_HUNTER_COINS
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.FIREBASE_HUNTER_COINS_REWARDED
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.FIREBASE_NUMBER_OF_ANSWERS
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.FIREBASE_NUMBER_OF_QUESTIONS_ASKED
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.FIREBASE_NUMBER_OF_SUCCESSFUL_HUNTS
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.FIREBASE_QUESTIONS_VIEW
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.FIREBASE_QUESTION_ID
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.FIREBASE_QUESTION_TITLE_FOR_SEARCH
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.FIREBASE_USER_ID
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.FIREBASE_USER_IMAGE
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.PATH_FIREBASE_ANSWERS
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.PATH_FIREBASE_QUESTIONS
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.PATH_FIREBASE_USERS
import com.bytesbridge.app.bughunter.activities.utils.PaperDbUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Repository
//@Inject constructor(
//    iBackendApiSearch: IBackendApiSearch,
//    iBackendApiStackOverflow: IBackendApiStackOverflow
//)
{
    private var firebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()

    //    private var storageRef: StorageReference = FirebaseStorage.getReference()
    private var fireStorage: FirebaseStorage = FirebaseStorage.getInstance()

    private var dbFireStore = Firebase.firestore
    private var userLiveData: MutableLiveData<FirebaseUser?> = MutableLiveData()
    private var loggedOutLiveData: MutableLiveData<Boolean?> = MutableLiveData()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

//    val remote = Remote(iBackendApiStackOverflow, iBackendApiSearch)

    inner class Remote
//        (private var iBackendApiStackOverflow: IBackendApiStackOverflow,
//        private var iBackendApiSearch: IBackendApiSearch)
    {
        //---------------- Search ----------------//
        fun searchQuery(query: String, questionTitle: (questionTitle: String?) -> Unit) {

            RetrofitInstance.searchApi.searchQuestion("$query stack overflow")
                .enqueue(object : Callback<SearchResponse> {
                    override fun onResponse(
                        call: Call<SearchResponse>,
                        response: Response<SearchResponse>
                    ) {
                        if (response.isSuccessful) {
                            var title: String? = null
                            val searchResponse = response.body()
                            searchResponse?.let { searchResponseResult ->
                                for (result in searchResponseResult.results) {
                                    if (result.link.contains(CONTAINS_STACKOVERFLOW_LINK)) {
                                        questionTitle(result.title)
                                        title = result.title
                                        return
                                    }
                                }

                                if (title == null) {
                                    questionTitle("null")
                                }

                            } ?: run {
                                questionTitle(null)
                                Log.e("TAG", "onResponse: no search response found <:> ")
                            }
                        } else {
                            questionTitle(null)
                            Log.e("TAG", "onResponse: ${response.errorBody()}")
                        }
                    }

                    override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                        questionTitle(null)
                    }
                })
        }
        //---------------- Search ----------------//

        //---------------- Questions----------------//
        fun searchQuestions(
            title: String,
            response: (stackOverflowQuestions: StackOverflowQuestionResponse?) -> Unit
        ) {
            RetrofitInstance.stackOverflowApi.searchQuestionWithTitle(title)
                .enqueue(object : Callback<StackOverflowQuestionResponse> {
                    override fun onResponse(
                        call: Call<StackOverflowQuestionResponse>,
                        questionResponse: Response<StackOverflowQuestionResponse>
                    ) {
                        if (questionResponse.isSuccessful && questionResponse.body() != null) {
                            val response_: StackOverflowQuestionResponse = questionResponse.body()!!
                            if (questionResponse.isSuccessful && questionResponse.code() == 200) {
                                getQuestionFromFirestoreWithTitle(title) { listOfQuestion ->
                                    listOfQuestion?.let {
                                        response_.items = it
                                        response(questionResponse.body())
                                    } ?: run {
                                        response(questionResponse.body())
                                    }
                                }
                            } else {
                                response(null)
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<StackOverflowQuestionResponse>,
                        t: Throwable
                    ) {
                        response(null)
                    }
                })
        }
        //---------------- Questions ----------------//


        //---------------- Answers ----------------//
        fun answersOfQuestion(
            questionId: Int,
            answersResponse: (stackOverflowAnswersResponse: StackOverflowAnswersResponse?) -> Unit
        ) {
            RetrofitInstance.stackOverflowApi.answersOfQuestion(questionId)
                .enqueue(object : Callback<StackOverflowAnswersResponse> {
                    override fun onResponse(
                        call: Call<StackOverflowAnswersResponse>,
                        response: Response<StackOverflowAnswersResponse>
                    ) {
                        if (response.isSuccessful && response.code() == 200) {
                            answersResponse(response.body())
                        } else {
                            answersResponse(null)
                        }
                    }

                    override fun onFailure(call: Call<StackOverflowAnswersResponse>, t: Throwable) {
                        answersResponse(null)
                    }
                })
        }

        //---------------- Answers ----------------//
    }

    //---------------- Auth ----------------//
    fun uploadImageToFirebase(uId: String, file: Uri, success: (success: Boolean) -> Unit) {
        val storageRef: StorageReference = fireStorage.reference.child("$uId.jpg")
        var task: UploadTask = storageRef.putFile(file)


        val urlTask = task.continueWithTask { _task ->
            if (!_task.isSuccessful) {
                _task.exception?.let {
                    success(false)

                    throw it
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task_ ->
            if (task_.isSuccessful) {
                val downloadUri = task_.result
                addUserImage(downloadUri, uId)
                success(true)
            } else {
                success(false)
                // ...
            }
        }
    }

    private fun addUserImage(downloadUri: Uri, uId: String) {
        Log.e("TAG", "addUserImage: " + downloadUri.toString())
        dbFireStore.collection(PATH_FIREBASE_USERS).document(uId)
            .update(FIREBASE_USER_IMAGE, downloadUri.toString())
    }

    fun registerUser(
        signUpModel: SignUpModel,
        userCreated: (userCreatedStatus: Boolean, message: String) -> Unit
    ) {
        firebaseAuth?.let { firebaseAuth ->
            firebaseAuth.createUserWithEmailAndPassword(signUpModel.email, signUpModel.password)
                .addOnCompleteListener { task ->
                    task.let {
                        if (task.isSuccessful) {
                            var userModel = UserModel(
                                task.result.user?.uid!!,
                                signUpModel.userName,
                                signUpModel.name,
                                signUpModel.email,
                                0,
                                0,
                                100,
                                "",
                                0,
                                System.currentTimeMillis().toString(),
                                System.currentTimeMillis().toString()
                            )

                            coroutineScope.launch {
                                addUserDataToFireStore(userModel) { status, message ->
                                    launch(Dispatchers.Main) {
                                        if (status) {
                                            userCreated(
                                                true,
                                                message
                                            )
                                        } else {
                                            userCreated(
                                                false,
                                                message
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            userCreated(false, "SignUp Failed cause: ${task.exception?.message}");
                        }
                    }
                }
        } ?: run {
            userCreated(false, "Authentication Failed")
        }

    }

    fun loginUser(
        loginModel: LoginModel,
        loginStatus: (status: Boolean, message: String, userModel: UserModel?) -> Unit
    ) {
        firebaseAuth?.let { firebaseAuth ->
            firebaseAuth.signInWithEmailAndPassword(loginModel.email, loginModel.password)
                .addOnCompleteListener { task ->
                    task.let {
                        if (task.isSuccessful) {
                            val user = task.result.user
//                            coroutineScope.launch {
                            user?.let { firebaseUser ->
                                getUserDataToFireStore(firebaseUser.uid) { userModel ->
//                                        launch(Dispatchers.Main) {
                                    userModel?.let {
                                        loginStatus(
                                            true,
                                            "Login Successfully",
                                            userModel
                                        )
                                    } ?: run {
                                        loginStatus(
                                            false,
                                            "User not Found",
                                            null
                                        )
                                    }
//                                        }
//                                    }
                                }
                            }
                                ?: run {
//                                        launch(Dispatchers.Main) {
                                    loginStatus(
                                        false,
                                        "User not Found",
                                        null
                                    )
//                                        }
//                                    }
                                }

                        } else {
                            loginStatus(
                                false,
                                "Login Failed cause: ${task.exception?.message}",
                                null
                            )
                        }
                    }

                }
        } ?: run {
            loginStatus(false, "Authentication Failed", null)
        }
    }

    private fun addUserDataToFireStore(
        userModel: UserModel,
        dataAddedStatus: (status: Boolean, message: String) -> Unit
    ) {

        dbFireStore.collection(PATH_FIREBASE_USERS).document(userModel.userId).set(userModel)
            .addOnSuccessListener {
                dataAddedStatus(
                    true,
                    "Login Successful"
                )
            }.addOnFailureListener { exception ->
                exception.message?.let { message ->
                    dataAddedStatus(
                        false,
                        message
                    )
                } ?: run {
                    dataAddedStatus(
                        false,
                        "SignUp Failed cause: ${exception.message}"
                    )
                }

            }
    }

    fun getUserDataToFireStore(uId: String, userModel: (user: UserModel?) -> Unit) {
        dbFireStore.collection(PATH_FIREBASE_USERS).document(uId).get()
            .addOnSuccessListener { document ->
                Log.e("TAG", "getUserDataToFireStore: ${document.toObject<UserModel>()}")
                userModel(document.toObject<UserModel>())
            }.addOnFailureListener {
                userModel(null)
            }

    }

    @JvmName("getUserLiveData1")
    fun getUserLiveData(): MutableLiveData<FirebaseUser?> {
        return userLiveData
    }

    fun getLoggedOutLiveData(): MutableLiveData<Boolean?> {
        return loggedOutLiveData
    }

    fun submitQuestion(_question: QuestionModel, message: (msg: String) -> Unit) {
        var question = _question
        val firebaseUser = firebaseAuth?.currentUser
        firebaseUser
            ?.let {
                Remote().searchQuery(question.question_title) { title ->
                    title
                        ?.let {
                            question.question_title_for_search = title
                        }
                        ?: run {
                            question.question_title_for_search = question.question_title
                        }
                    coroutineScope.launch {
                        increaseNumberOfQuestionAsked(question.user_id)
                        increaseNumberOfAnswerToQuestion(question.question_id)
                        addQuestionToFireBase(question)
                        {
                            message(it)
                        }
                    }
                }
            }
            ?: run {
                message("User not found")
            }
    }

    fun submitAnswer(_answer: AnswerModel, message: (msg: String) -> Unit) {
        var answer = _answer
        val firebaseUser = firebaseAuth?.currentUser
        firebaseUser
            ?.let {
                dbFireStore.collection(PATH_FIREBASE_ANSWERS).document().set(answer)
                    .addOnSuccessListener {
                        increaseNumberOfAnswerByUser(firebaseUser.uid)
                        message("Answer Added Successfully")
                    }
                    .addOnFailureListener {
                        message("Fail to add message ${it.message}")
                    }
            }
            ?: run {
                message("User not found")
            }
    }


    private fun increaseNumberOfAnswerByUser(uid: String) {
        dbFireStore.collection(PATH_FIREBASE_USERS).document(uid)
            .update(FIREBASE_NUMBER_OF_ANSWERS, FieldValue.increment(1))
    }

    private fun increaseNumberOfAnswerToQuestion(questionId: String) {
        var question: QuestionModel = QuestionModel()
        dbFireStore.collection(PATH_FIREBASE_QUESTIONS)
            .whereEqualTo(FIREBASE_QUESTION_ID, questionId)
            .get().addOnSuccessListener { documents ->
                for (doc in documents) {
                    question = doc.toObject(QuestionModel::class.java)
                    if (question.question_id == questionId) {
                        increaseNumberOfAnswerToQuestion(doc.id)
                    }
                }
            }
            .addOnFailureListener {

            }
    }

    private fun increaseNumberOfQuestionAsked(uid: String) {
        dbFireStore.collection(PATH_FIREBASE_USERS).document(uid)
            .update(FIREBASE_NUMBER_OF_QUESTIONS_ASKED, FieldValue.increment(1))
    }

    private fun increaseNumberOfSuccessfulHunts(uid: String) {
        dbFireStore.collection(PATH_FIREBASE_USERS).document(uid)
            .update(FIREBASE_NUMBER_OF_SUCCESSFUL_HUNTS, FieldValue.increment(1))
    }

    private fun addQuestionToFireBase(question: QuestionModel, message: (msg: String) -> Unit) {
        dbFireStore.collection(PATH_FIREBASE_QUESTIONS).document().set(question)
            .addOnSuccessListener {
                message("Question Added Successfully")
            }.addOnFailureListener {
                message("Fail to add message ${it.message}")
            }

    }

    //save user data
    fun saveUserDataLocally(context: Context, saveCurrentUserData: UserModel) =
        PaperDbUtils.user(context, saveCurrentUserData)

    //save user data
//get user data
    fun getUserDataLocally(context: Context, userData: (saveCurrentUserData: UserModel?) -> Unit) {
        userData(PaperDbUtils.user(context))
    }


    private fun getQuestionFromFirestoreWithTitle(
        title: String,
        response: (stackOverflowQuestions: ArrayList<StackOverflowQuestions>?) -> Unit
    ) {
        dbFireStore.collection(PATH_FIREBASE_QUESTIONS)
            .whereEqualTo(FIREBASE_QUESTION_TITLE_FOR_SEARCH, title).get()
            .addOnSuccessListener {
                val stackOverflowQuestions: ArrayList<StackOverflowQuestions> = ArrayList()
                for (doc in it) {
                    val question = doc.toObject(QuestionModel::class.java)
                    val owner = Owner(
                        question.user_id.toInt(),
                        0,
                        question.user_id.toInt(),
                        "",
                        0,
                        question.question_user_photo,
                        question.question_user_name,
                        ""
                    )
                    stackOverflowQuestions.add(
                        StackOverflowQuestions(
                            emptyList(),
                            owner,
                            true,
                            question.views.toInt(),
                            0,
                            question.number_of_answers.toInt(),
                            0,
                            0,
                            question.question_detail,
                            question.created_at.toInt(),
                            question.updated_at.toInt(),
                            question.question_id.toInt(),
                            "",
                            "",
                            question.question_title,
                            1,
                            question.hunter_coins_offer
                        )
                    )
                }
                response(stackOverflowQuestions)
            }
    }

    //---------------- Auth ----------------//
    fun getQuestions(
        _userId: String? = null,
        type: Int?,
        questionsListCallback: (ArrayList<QuestionModel>?) -> Unit
    ) {

        val userId = _userId ?: firebaseAuth?.currentUser?.uid
//        userId?.let {
        val questionList: ArrayList<QuestionModel> = ArrayList()
        if (type == Constants.MY_QUESTIONS) {
            dbFireStore.collection(PATH_FIREBASE_QUESTIONS)
                .whereEqualTo(FIREBASE_USER_ID, userId).get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        questionList.add(doc.toObject(QuestionModel::class.java))
                    }
                    questionsListCallback(questionList)
                }.addOnFailureListener {
                    questionsListCallback(null)
                }
        } else if (type == Constants.ALL_QUESTIONS) {
            dbFireStore.collection(PATH_FIREBASE_QUESTIONS).get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        questionList.add(doc.toObject(QuestionModel::class.java))
                    }
                    questionsListCallback(questionList)
                }.addOnFailureListener {
                    questionsListCallback(null)
                }
        }
//        } ?: run { Log.e("TAG", "getQuestions: Nouser id found") }
    }

    fun getAnswer(questionId: String, answer: (ans: ArrayList<AnswerModel>?) -> Unit) {
        val answersList: ArrayList<AnswerModel> = ArrayList()
        dbFireStore.collection(PATH_FIREBASE_ANSWERS).whereEqualTo(FIREBASE_QUESTION_ID, questionId)
            .get().addOnSuccessListener { documents ->
                for (doc in documents) {
                    answersList.add(doc.toObject(AnswerModel::class.java))
                }
                viewIncrease(questionId)
                answer(answersList)
            }
            .addOnFailureListener {
                answer(null)
            }
    }

    fun rewardHunterCoins(answer: AnswerModel, function: () -> Unit) {
        sendAddCoinsToUser(answer.answer_user_id, answer.hunter_coins_rewarded)
        sendAddCoinsToUser(answer.question_user_id, (answer.hunter_coins_rewarded) * -1)
        increaseNumberOfSuccessfulHunts(answer.answer_user_id)
        dbFireStore.collection(PATH_FIREBASE_ANSWERS)
            .whereEqualTo(FIREBASE_ANSWER_ID, answer.answer_id).get().continueWith {
                if (it.isSuccessful) {
                    for (doc in it.result) {
                        dbFireStore.collection(PATH_FIREBASE_ANSWERS).document(doc.id)
                            .update(FIREBASE_HUNTER_COINS_REWARDED, answer.hunter_coins_rewarded)
                    }
                    function()
                }
            }
    }

    private fun sendAddCoinsToUser(userId: String, coins: Long) {
        dbFireStore.collection(PATH_FIREBASE_USERS).document(userId)
            .update(FIREBASE_HUNTER_COINS, FieldValue.increment(coins))

    }


    private fun viewIncrease(questionId: String) {
        dbFireStore.collection(PATH_FIREBASE_QUESTIONS)
            .whereEqualTo(FIREBASE_QUESTION_ID, questionId).get().continueWith { documents ->
                if (documents.isSuccessful) {
                    for (doc in documents.result) {
                        dbFireStore.collection(PATH_FIREBASE_QUESTIONS).document(doc.id)
                            .update(FIREBASE_QUESTIONS_VIEW, FieldValue.increment(1))
                    }
                }
            }
    }

}



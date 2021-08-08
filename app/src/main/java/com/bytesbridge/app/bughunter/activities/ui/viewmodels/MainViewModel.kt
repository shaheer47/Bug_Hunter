package com.bytesbridge.app.bughunter.activities.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.AnswerModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.QuestionModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.UserModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.StackOverflowAnswersResponse
import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.StackOverflowQuestionResponse
import com.bytesbridge.app.bughunter.activities.ui.repository.Repository
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.STACK_OVERFLOW
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

//@HiltViewModel
class MainViewModel
//@Inject constructor(
//    var repository: Repository )
    : ViewModel() {
    private var repository: Repository = Repository()

    fun searchQuery(
        _query: String,
        response: (stackOverflowQuestionsResponse: StackOverflowQuestionResponse?) -> Unit
    ) {
        var query = _query
        if (!query.contains(STACK_OVERFLOW)) {
            query += " $STACK_OVERFLOW"
        }
        repository.Remote().searchQuery(query) { title ->
            title?.let { _questionTitle ->
                var questionTitle = _questionTitle
                questionTitle = questionTitle.toLowerCase(Locale.ROOT).trim()
                questionTitle = questionTitle.replace(STACK_OVERFLOW, "")
                questionTitle = questionTitle.replace(" stack", "")
                Log.e("TAG", "searchQuery:$questionTitle ")
                searchTitleFromStackOverflow(questionTitle) { questionSearchResponse ->
                    questionSearchResponse?.let(response) ?: run { response(null) }
                }
            } ?: run {
                response(null)
            }
        }
    }


    private fun searchTitleFromStackOverflow(
        title: String,
        response: (stackOverflowQuestionResponse: StackOverflowQuestionResponse?) -> Unit
    ) {
        repository.Remote().searchQuestions(title) { stackOverflowQuestionsResult ->
            response(stackOverflowQuestionsResult)
        }
    }

     fun getAnswersFromStackOverflow(
        title: Int,
        response: (stackOverflowAnswersResponse: StackOverflowAnswersResponse?) -> Unit
    ) {
        repository.Remote().answersOfQuestion(title) { stackOverflowAnswersResponse ->
            response(stackOverflowAnswersResponse)
        }
    }

    fun submitQuestion(question: QuestionModel, message: (msg: String) -> Unit) {
        repository.submitQuestion(question) {
            message(it)
        }
    }
    fun submitAnswer(answer: AnswerModel, message: (msg: String) -> Unit) {
        repository.submitAnswer(answer) {
            message(it)
        }
    }

    //    ----------------- Save user Data Locally ---------------
    fun saveUserDataLocally(
        context: Context,
        userData: UserModel
    ) {
        this.repository.saveUserDataLocally(context, userData)
    }

    //    ----------------- SaveUser Data locally ---------------
    //    ----------------- get user Data Locally ---------------
    fun getUserDataLocally(
        context: Context, userData: (saveCurrentUserData: UserModel?) -> Unit
    ) {
        this.repository.getUserDataLocally(context) {
            userData(it)
        }
    }
//    ----------------- get Data locally ---------------

    fun getQuestions(type: Int?, questionsList: (ArrayList<QuestionModel>?) -> Unit) {
        repository.getQuestions(type = type) {
            questionsList(it)
        }
    }

    fun getAnswers(questionId: String, answer: (ans: ArrayList<AnswerModel>?) -> Unit) {
        repository.getAnswer(questionId) { ans ->
            answer(ans)
        }
    }

    fun rewardAnswer(answer: AnswerModel, function: () -> Unit) {
        repository.rewardHunterCoins(answer){
            function()
        }
    }
    fun upLoadImage(uid:String, file: Uri, success:(success:Boolean)->Unit){
        repository.uploadImageToFirebase(uid,file,success)
    }
   

}
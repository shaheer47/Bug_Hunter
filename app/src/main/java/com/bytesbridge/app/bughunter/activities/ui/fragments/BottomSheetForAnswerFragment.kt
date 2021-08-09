package com.bytesbridge.app.bughunter.activities.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.ui.data.models.AnswerModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.QuestionModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.UserModel
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.MainViewModel
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.Question
import com.bytesbridge.app.bughunter.activities.utils.LoadingUtils
import com.bytesbridge.app.bughunter.activities.utils.SnackbarUtil.Companion.showSnackBar
import com.bytesbridge.app.bughunter.databinding.FragmentBottomSheetForAnswerBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*


class BottomSheetForAnswerFragment : BottomSheetDialogFragment() {
    private val mainViewModel: MainViewModel by viewModels()
    lateinit var binding: FragmentBottomSheetForAnswerBinding

    private var answerDescription: String = ""
    private var question: QuestionModel? = null

    private var user: UserModel? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupHalfHeight(bottomSheetDialog)
        }
        return dialog
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view?.parent as View).backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)

    }

    private fun setupHalfHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: FrameLayout? =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
        val behavior = BottomSheetBehavior.from(
            bottomSheet!!
        )
        val layoutParams: ViewGroup.LayoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        layoutParams.height = windowHeight
        bottomSheet.layoutParams = layoutParams
//        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isFitToContents = false
        behavior.halfExpandedRatio = 0.6f
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetForAnswerBinding.inflate(inflater, container, false)
        question = arguments?.getSerializable(Question) as QuestionModel
        if (question == null) {
            Toast.makeText(context, "Question not found", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        setUpClickListeners()
        getUserData()
        return binding.root
    }

    private fun getUserData() {
        mainViewModel.getUserDataLocally(requireContext()) { userData ->
            userData?.let {
                user = userData
            }
        }
    }


    private fun setUpClickListeners() {
        binding.apply {
            imgCancel.setOnClickListener {
                dismiss()
            }

            cardSend.setOnClickListener {
                checkValidation() {
                    prepareAnswer()
                }
            }
        }
    }

    private fun prepareAnswer() {
        val answerModel =
            AnswerModel(
                UUID.randomUUID().toString(),
                question?.question_id!!,
                answerDescription,
                0,
                user?.user_image!!,
                System.currentTimeMillis().toString(),
                System.currentTimeMillis().toString(),
                user?.userId.toString(),
                question?.user_id!!,
                user?.userName!!
            )
        answerQuestion(answerModel)
    }

    private fun checkValidation(goodToGo: (check: Boolean) -> Unit) {
        user?.let {
            binding.apply {
                answerDescription = etQuestionDescription.text.toString().trim()

                if (answerDescription.length > 1) {
                    goodToGo(true)
                } else {
                    showSnackBar(root, "Answer Can't be empty")

                }

            }
        } ?: run {
            dismiss()
            closeKeyBoard()
            Toast.makeText(
                requireContext(),
                "Please Login First to post answer",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun answerQuestion(question: AnswerModel) {
        LoadingUtils.loadingStart(binding.cardSend, binding.progress)

        mainViewModel.submitAnswer(question) { message ->
            LoadingUtils.loadingEnd("Answer",binding.cardSend, binding.progress)
            dismiss()
            closeKeyBoard()
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            showSnackBar(binding.root, message = message)

        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }

    companion object {
        fun newInstance(question: QuestionModel) =
            BottomSheetForAnswerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(Question, question)
                }
            }
    }


    private fun closeKeyBoard() {
        val imm: InputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = requireActivity().currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
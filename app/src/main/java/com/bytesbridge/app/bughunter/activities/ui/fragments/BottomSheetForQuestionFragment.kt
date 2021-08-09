package com.bytesbridge.app.bughunter.activities.ui.fragments

import android.app.Activity
import android.app.Dialog
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
import com.bytesbridge.app.bughunter.activities.ui.data.models.QuestionModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.UserModel
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.MainViewModel
import com.bytesbridge.app.bughunter.activities.utils.AlertDialogUtils.Companion.showDialog
import com.bytesbridge.app.bughunter.activities.utils.SnackbarUtil.Companion.showSnackBar
import com.bytesbridge.app.bughunter.databinding.FragmentBottomSheetForQuestionBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*


class BottomSheetForQuestionFragment : BottomSheetDialogFragment() {
    private val mainViewModel: MainViewModel by viewModels()
    lateinit var binding: FragmentBottomSheetForQuestionBinding

    private var questionTitle: String = ""
    private var questionDescription: String = ""

    private var user: UserModel? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dialog = super.onCreateDialog(savedInstanceState)
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
        val layoutParams: ViewGroup.LayoutParams = bottomSheet.getLayoutParams()
        val windowHeight = getWindowHeight()
        layoutParams.height = windowHeight
        bottomSheet.layoutParams = layoutParams
        behavior.isFitToContents = false
        behavior.halfExpandedRatio = 0.6f
//        behavior.state = BottomSheetBehavior.STATE_EXPANDED
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
        binding = FragmentBottomSheetForQuestionBinding.inflate(inflater, container, false)
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
                    showDialog(
                        requireContext(),
                        context?.resources?.getString(R.string.question_submission_message)!!
                    ) { addCoins ->
                        if (!addCoins) {
                            prepareQuestion()
                        }
                    }
                }
            }
        }
    }

    private fun prepareQuestion() {
        val coins = binding.etHunterCoins.text.toString().toLong()
        val questionModel: QuestionModel =
            QuestionModel(
                UUID.randomUUID().toString(),
                "0",
                questionTitle,
                questionDescription,
                if (coins == 0L) 0 else coins,
                System.currentTimeMillis().toString(),
                System.currentTimeMillis().toString(),
                user?.userId!!,
                0,
                "",
                user?.userName!!,
                user?.user_photo!!

            )
        sendQuestion(questionModel)
    }

    private fun checkValidation(goodToGo: (check: Boolean) -> Unit) {
        user?.let {
            binding.apply {
                questionTitle = etQuestionTitle.text.toString().trim()
                questionDescription = etQuestionDescription.text.toString().trim()
                if (questionTitle.length > 1) {
                    if (questionDescription.length > 1) {
                        goodToGo(true)
                    } else {
                        etQuestionDescription.error = "Please Add more details"
                    }
                } else {
                    etQuestionTitle.error = "Please Add Title"
                }
            }
        } ?: run {
            dismiss()
            closeKeyBoard()
            Toast.makeText(
                requireContext(),
                "Please Login First to post question",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun sendQuestion(question: QuestionModel) {
        mainViewModel.submitQuestion(question) { message ->
            dismiss()
            closeKeyBoard()
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            showSnackBar(requireView(), message = message)

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
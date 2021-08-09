package com.bytesbridge.app.bughunter.activities.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import carbon.dialog.ProgressDialog
import com.bumptech.glide.Glide
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.adapters.AnswersAdapter
import com.bytesbridge.app.bughunter.activities.ui.data.models.AnswerModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.QuestionModel
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.MainViewModel
import com.bytesbridge.app.bughunter.activities.utils.AlertDialogUtils.Companion.showDialog
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.Question
import com.bytesbridge.app.bughunter.activities.utils.SnackbarUtil.Companion.showSnackBar
import com.bytesbridge.app.bughunter.databinding.FragmentAnswersListBinding
import com.google.android.material.appbar.AppBarLayout
import com.snov.timeagolibrary.PrettyTimeAgo

class AnswersListFragment : Fragment(), AppBarLayout.OnOffsetChangedListener {

    private lateinit var question: QuestionModel
    private lateinit var answersAdapter: AnswersAdapter
    private lateinit var binding: FragmentAnswersListBinding
    private var answers: ArrayList<AnswerModel> = ArrayList()
    private val mainViewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            question = bundle.getSerializable(Question) as QuestionModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnswersListBinding.inflate(inflater, container, false)
        setupQuestion()
        initClickListener()
        setUpRecycleView()
        getAnswers()
        setUpSwipeToRefreshListener()

        return binding.root
    }

    private fun initClickListener() {
        binding.fabAnswer.setOnClickListener {
            val bottomSheetForAnswerFragment = BottomSheetForAnswerFragment.newInstance(question)
            val fm = requireActivity().supportFragmentManager

            bottomSheetForAnswerFragment.show(fm, "answer_bottom_sheet")
            Handler(Looper.getMainLooper()).postDelayed({
                bottomSheetForAnswerFragment.dialog?.setOnDismissListener {
                    getAnswers()
                }
            }, 500)
        }
    }

    private fun setUpRecycleView() {
        binding.rvAnswers.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            answersAdapter = AnswersAdapter() { answer ->
                if (question.hunter_coins_offer > 0) {
                    mainViewModel.getUserDataLocally(context) { user ->
                        if (user?.hunterCoins!! >= question.hunter_coins_offer) {
                            showDialog(
                                requireContext(),
                                context.resources.getString(
                                    R.string.send_coins_message,
                                    question.hunter_coins_offer.toString()
                                )
                            ) { yes ->
                                if (yes) {
                                    answer.hunter_coins_rewarded = question.hunter_coins_offer
                                    mainViewModel.rewardAnswer(answer) {
                                        getAnswers()
                                    }
                                }
                            }
                        } else {
                            showDialog(
                                requireContext(),
                                context.resources.getString(R.string.not_enough_coins)
                            ) { yes ->
                                if (yes) {
                                    showSnackBar(
                                        binding.root,
                                        "Admin is being notify you need more coins"
                                    )
                                }
                            }
                        }
                    }
                } else {
                    showSnackBar(binding.root, "This question reward is set with 0 hunter coins")
                }
            }
            adapter = answersAdapter
        }
    }

    private fun getAnswers() {
        val progress = ProgressDialog(requireContext())
        progress.setTitle("Loading Answers")
        progress.setText("Wait while answers are loading...")
        progress.setCancelable(false) // disable dismiss by tapping outside of the dialog
        progress.show()
        mainViewModel.getAnswers(question.question_id) { ansList ->
            progress.dismiss()
            ansList?.let {
                if (it.size > 0) {
                    binding.tvNoAnswers.visibility = View.GONE
                    answersAdapter.submitList(it.toList())
                    answersAdapter.notifyDataSetChanged()
                    binding.rvAnswers.visibility = View.VISIBLE

                } else {
                    binding.tvNoAnswers.visibility = View.VISIBLE
                    binding.rvAnswers.visibility = View.GONE

                }
            }
        }
    }

    private fun setupQuestion() {
        binding.apply {
            tvName.text = question.question_user_name
            tvQuestion.text = question.question_title
            tvDate.text = PrettyTimeAgo.getTimeAgo(question.created_at.toLong())
            tvAnswerCount.text = question.number_of_answers
            tvViewsCount.text = question.views.toString()
            tvDescription.text = question.question_detail
            tvHunterPoints.text = question.hunter_coins_offer.toString()
            Glide.with(root.context).load(question.question_user_photo).into(image)
        }
    }


    private fun setUpSwipeToRefreshListener() {
        binding.swipeToRefresh.setOnRefreshListener {
            getAnswers()
            binding.swipeToRefresh.isRefreshing = false
        }
    }


    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        binding.swipeToRefresh.isEnabled = verticalOffset == 0
    }
}
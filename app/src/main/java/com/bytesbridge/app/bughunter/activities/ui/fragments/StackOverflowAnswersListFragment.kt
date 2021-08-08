package com.bytesbridge.app.bughunter.activities.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bytesbridge.app.bughunter.activities.adapters.StackOverflowAnswersAdapter
import com.bytesbridge.app.bughunter.activities.ui.data.models.QuestionModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.StackOverflowAnswers
import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.StackOverflowQuestions
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.MainViewModel
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.Question
import com.bytesbridge.app.bughunter.activities.utils.HtmlAligner
import com.bytesbridge.app.bughunter.activities.utils.ShowLessTextUtils.Companion.setTextViewForShowLessText
import com.bytesbridge.app.bughunter.databinding.FragmentAnswersListBinding
import com.google.android.material.appbar.AppBarLayout
import com.snov.timeagolibrary.PrettyTimeAgo
import org.sufficientlysecure.htmltextview.HtmlFormatter
import org.sufficientlysecure.htmltextview.HtmlFormatterBuilder
import org.sufficientlysecure.htmltextview.LocalLinkMovementMethod

class StackOverflowAnswersListFragment : Fragment(), AppBarLayout.OnOffsetChangedListener {

    private lateinit var question: StackOverflowQuestions
    private lateinit var answersAdapter: StackOverflowAnswersAdapter
    private lateinit var binding: FragmentAnswersListBinding
    private val mainViewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            question = bundle.getSerializable(Question) as StackOverflowQuestions
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnswersListBinding.inflate(inflater, container, false)
        setupQuestion()
        setUpRecycleView()
        getAnswers()
        return binding.root
    }

    private fun setUpRecycleView() {
        binding.rvAnswers.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            answersAdapter = StackOverflowAnswersAdapter()
            adapter = answersAdapter
        }
    }

    private fun getAnswers() {
        mainViewModel.getAnswersFromStackOverflow(question.question_id) { ansList ->
            ansList?.let {
                if (it.items.isNotEmpty()) {
                    binding.tvNoAnswers.visibility = View.GONE
                    answersAdapter.submitList(it.items)
                } else {
                    binding.tvNoAnswers.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupQuestion() {
        binding.apply {
            Glide.with(requireContext()).load(question.owner.profile_image)
            tvName.text = question.owner.display_name
            tvQuestion.text = question.title
            tvDate.text = PrettyTimeAgo.getTimeAgo(question.creation_date.toLong())
            tvAnswerCount.text = question.answer_count.toString()
            val spannable =
                HtmlFormatter.formatHtml(
                    HtmlFormatterBuilder().setHtml(
                        HtmlAligner.alignHTML(
                            question.description
                        )
                    )
                )
            tvDescription.text = spannable
            binding.tvDescription.movementMethod = null
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
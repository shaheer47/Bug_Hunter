package com.bytesbridge.app.bughunter.activities.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import carbon.dialog.ProgressDialog
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.adapters.QuestionsAdapter
import com.bytesbridge.app.bughunter.activities.ui.data.models.QuestionModel
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.MainViewModel
import com.bytesbridge.app.bughunter.activities.utils.Constants
import com.bytesbridge.app.bughunter.databinding.FragmentMyQuestionsBinding
import com.google.android.material.appbar.AppBarLayout
import java.util.*


class QuestionsFragment : Fragment(), AppBarLayout.OnOffsetChangedListener {
    private lateinit var binding: FragmentMyQuestionsBinding
    private val mainViewModel: MainViewModel by viewModels()
    private var questionsList: ArrayList<QuestionModel>? = ArrayList()
    private lateinit var questionAdapter: QuestionsAdapter

    //type 0 is for my question 1 is for other questions
    private var type: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        type = arguments?.getInt("type")
        binding = FragmentMyQuestionsBinding.inflate(inflater, container, false)

        setUpSwipeToRefreshListener()
        getData()

        return binding.root
    }

    private fun getData() {

        val progress = ProgressDialog(requireContext())
        progress.setTitle("Loading Questions")
        progress.setText("Wait while questions are loading...")
        progress.setCancelable(false) // disable dismiss by tapping outside of the dialog
        progress.show()
        mainViewModel.getQuestions(type) { questions ->
            if(questions.isNullOrEmpty()){
                binding.tvNoQuestions.visibility=View.GONE
            }
            else{
                binding.tvNoQuestions.visibility=View.VISIBLE
            }
            progress.dismiss()
            questionsList = questions
            questions?.let {
                binding.rvQuestions.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                questionAdapter = QuestionsAdapter(it) { questionModel ->
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToAnswersListFragment(questionModel)
                    findNavController().navigate(action)

                }
                binding.rvQuestions.adapter = questionAdapter
            }
        }
    }

    companion object {
        fun getInstance(type: Int): QuestionsFragment {
            val fragment = QuestionsFragment()
            val args = Bundle()
            args.putInt("type", type)
            fragment.arguments = args
            return fragment
        }
    }

    private fun setUpSwipeToRefreshListener() {
        binding.swipeToRefresh.setOnRefreshListener {
            loadQuestions()
            binding.swipeToRefresh.isRefreshing = false
        }
    }

    private fun loadQuestions() {
        getData()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        binding.swipeToRefresh.isEnabled = verticalOffset == 0;
    }


}
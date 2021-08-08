package com.bytesbridge.app.bughunter.activities.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.bytesbridge.app.bughunter.activities.adapters.QuestionPagerAdapter
import com.bytesbridge.app.bughunter.activities.ui.data.models.UserModel
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.MainViewModel
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.ALL_QUESTIONS
import com.bytesbridge.app.bughunter.activities.utils.Constants.Companion.MY_QUESTIONS
import com.bytesbridge.app.bughunter.activities.utils.PaperDbUtils.Companion.user
import com.bytesbridge.app.bughunter.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private val mainViewModel: MainViewModel by viewModels()
    private var user: UserModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        getUser()
        setupTitleBar()
        setUpViewPager()
        setupFabClickListener()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpViewPager() {
        val fragments: ArrayList<Fragment> = ArrayList()

        fragments.add(QuestionsFragment.getInstance(ALL_QUESTIONS))
        if (user != null) {
            fragments.add(QuestionsFragment.getInstance(MY_QUESTIONS))
        }

        binding.viewpagerQuestions.adapter =
            QuestionPagerAdapter(requireActivity(), fragments)

        TabLayoutMediator(
            binding.tlQuestions,
            binding.viewpagerQuestions
        ) { tab, position ->
            when (position) {
                0 -> tab.text = "Questions for hunt"
                1 -> tab.text = "Your Questions"
            }
        }.attach()
    }

    private fun getUser() {
        user = user(requireContext())

    }

    private fun setupTitleBar() {
        binding.collapsingToolbar.title = if (user != null) {
            "Welcome ${user?.name}"
        } else {
            "Home"
        }
    }

    private fun setupFabClickListener() {
        binding.fabAsk.setOnClickListener {
            val bottomSheetForQuestionFragment =
                BottomSheetForQuestionFragment()
            val fm: FragmentManager = requireActivity().supportFragmentManager
            bottomSheetForQuestionFragment.show(fm, "question_bottom_sheet")


        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }


}
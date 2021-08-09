package com.bytesbridge.app.bughunter.activities.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytesbridge.app.bughunter.activities.adapters.StackOverflowQuestionsAdapter
import com.bytesbridge.app.bughunter.activities.ui.data.models.QuestionModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.StackOverflowQuestionResponse
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.MainViewModel
import com.bytesbridge.app.bughunter.activities.utils.LoadingUtils.Companion.loadingEnd
import com.bytesbridge.app.bughunter.activities.utils.LoadingUtils.Companion.loadingStart
import com.bytesbridge.app.bughunter.activities.utils.PaperDbUtils
import com.bytesbridge.app.bughunter.activities.utils.SnackbarUtil
import com.bytesbridge.app.bughunter.databinding.FragmentSearchBinding
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptions
import java.util.*

class SearchFragment : Fragment() {

    private lateinit var launcher: ActivityResultLauncher<CropImageContractOptions>
    lateinit var binding: FragmentSearchBinding
    private val mainViewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPhotoResultLauncher()

    }

    private fun initPhotoResultLauncher() {
        launcher = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // use the returned uri
                val uriContent = result.uriContent
                val image: InputImage
                try {
                    loadingStart(binding.tvSearchWithPhoto, binding.progressImg)
                    image = InputImage.fromFilePath(requireContext(), uriContent)
                    val recognizer =
                        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    recognizer.process(image).addOnSuccessListener { text ->
                        mainViewModel.searchQuery(
                            text.text.toLowerCase(Locale.ROOT).trim()
                        ) { questionResponse ->
                            questionResponse?.let {
                                loadingEnd(
                                    "Search",
                                    binding.tvSearchWithPhoto,
                                    binding.progressImg
                                )

                                updateRecycleView(it)
                            } ?: run {
                                loadingEnd(
                                    "Search",
                                    binding.tvSearchWithPhoto,
                                    binding.progressImg
                                )

                                Toast.makeText(
                                    requireContext(),
                                    "No results found",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }.addOnFailureListener { exception ->
                        loadingEnd("Search", binding.tvSearchWithPhoto, binding.progressImg)

                        Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()

                    Log.e("TAG", "onCreate: ${e.message}")
                }

            } else {
                // an error occurred
                Toast.makeText(requireContext(), "Some Error occur", Toast.LENGTH_SHORT).show()
                val exception = result.error
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchBinding.inflate(inflater, container, false)
        setupClickListeners()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupClickListeners() {
        binding.tvNoQuestions.visibility=View.VISIBLE

        binding.tvSearchWithText.setOnClickListener {
            loadingStart(binding.tvSearchWithText, binding.progressSearch)
            mainViewModel.searchQuery(
                binding.editText.text.toString().toLowerCase(Locale.ROOT).trim()
            ) { questionResponse ->
                loadingEnd(
                    "Search with photo",
                    binding.tvSearchWithText,
                    binding.progressSearch
                )
                questionResponse?.let {
                    updateRecycleView(it)
                } ?: run {
                    Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.tvSearchWithPhoto.setOnClickListener {
            launchImageSearch()
        }
    }

    private fun launchImageSearch() {
        launcher.launch(options {
            setGuidelines(CropImageView.Guidelines.ON)
        })

    }

    private fun updateRecycleView(questionResponse: StackOverflowQuestionResponse) {
        questionResponse.items
        binding.rvQuestions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvQuestions.adapter =
            StackOverflowQuestionsAdapter(questionResponse.items) { stackOverflowQuestion ->
                var action: NavDirections? = null
                if (stackOverflowQuestion.type == 0) {
                    action =
                        SearchFragmentDirections.actionSearchFragmentToStackOverflowAnswersListFragment(
                            stackOverflowQuestion
                        )
                } else {
                    var question: QuestionModel
                    stackOverflowQuestion.apply {
                        question = QuestionModel(
                            question_id.toString(),
                            answer_count.toString(),
                            title,
                            description,
                            coins_offer,
                            creation_date.toString(),
                            last_activity_date.toString(),
                            owner.user_id.toString(),
                            view_count.toLong(),
                            title,
                            owner.display_name,
                            owner.profile_image
                        )

                    }
                    action = SearchFragmentDirections.actionSearchFragmentToAnswersListFragment(
                        question
                    )

                }
                findNavController().navigate(action)


            }
    }


}
package com.bytesbridge.app.bughunter.activities.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.adapters.StackOverflowQuestionsAdapter
import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.StackOverflowQuestionResponse
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.MainViewModel
import com.bytesbridge.app.bughunter.activities.utils.Constants
import com.bytesbridge.app.bughunter.databinding.FragmentSearchBinding
import com.github.drjacky.imagepicker.ImagePicker
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptions
import java.util.*

class SearchFragment : Fragment() {

    private lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var binding: FragmentSearchBinding
    private val mainViewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPhotoResultLauncher()

    }

    private fun initPhotoResultLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
                if (data.resultCode == Activity.RESULT_OK) {
                    val uri = data.data?.data!!
                    val image: InputImage
                    try {
                        image = InputImage.fromFilePath(requireContext(), uri)
                        val recognizer =
                            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                        recognizer.process(image).addOnSuccessListener { text ->
                            mainViewModel.searchQuery(
                                text.text.toLowerCase(Locale.ROOT).trim()
                            ) { questionResponse ->
                                questionResponse?.let {
                                    updateRecycleView(it)
                                } ?: run {
                                    Toast.makeText(
                                        requireContext(),
                                        "No results found",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("TAG", "onCreate: ${e.message}")
                    }
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
        binding.tvSearchWithText.setOnClickListener {
            mainViewModel.searchQuery(
                binding.editText.text.toString().toLowerCase(Locale.ROOT).trim()
            ) { questionResponse ->
                questionResponse?.let {
                    updateRecycleView(it)
                } ?: run {
                    Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvSearchWithPhoto.setOnClickListener {
            launchImageSearch()
        }
    }

    private fun launchImageSearch() {
        ImagePicker.Companion.with(requireActivity())
            .createIntentFromDialog { launcher.launch(it) }
    }

    private fun updateRecycleView(questionResponse: StackOverflowQuestionResponse) {
        questionResponse.items
        binding.rvQuestions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvQuestions.adapter =
            StackOverflowQuestionsAdapter(questionResponse.items) { stachOverflowQuestion ->

                val action =
                    SearchFragmentDirections.actionSearchFragmentToStackOverflowAnswersListFragment(
                        stachOverflowQuestion
                    )
                findNavController().navigate(action)


            }
    }


}
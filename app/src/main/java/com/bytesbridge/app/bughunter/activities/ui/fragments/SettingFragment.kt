package com.bytesbridge.app.bughunter.activities.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bytesbridge.app.bughunter.activities.ui.activities.LoginActivity
import com.bytesbridge.app.bughunter.activities.ui.data.models.UserModel
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.MainViewModel
import com.bytesbridge.app.bughunter.activities.utils.PaperDbUtils.Companion.user
import com.bytesbridge.app.bughunter.activities.utils.SnackbarUtil.Companion.showSnackBar
import com.bytesbridge.app.bughunter.databinding.FragmentSettingBinding
import com.github.drjacky.imagepicker.ImagePicker
import java.io.File


class SettingFragment : Fragment() {
    lateinit var binding: FragmentSettingBinding
    var user: UserModel? = null
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(layoutInflater)
        getUser()
        setUpView()
        registeActivityForResult()
        initClickListeners()
        return binding.root
    }

    private fun registeActivityForResult() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
                if (data.resultCode == Activity.RESULT_OK) {
                    val uri = data.data?.data!!
                    mainViewModel.upLoadImage(user?.userId.toString(),uri) {
                        if (it) {
                            showSnackBar(binding.root, "Upload Successful")
                        } else {
                            showSnackBar(binding.root, "Upload Failed")
                        }

                    }

                }

            }

    }

    private fun initClickListeners() {
        binding.img.setOnClickListener {

            laucherPhoto()
        }
    }

    private fun laucherPhoto() {
        ImagePicker.Companion.with(requireActivity()).cropOval()
            .createIntentFromDialog { launcher.launch(it) }
    }

    private fun setUpView() {
        user?.let { user ->
            binding.tvName.text = user.name
            binding.tvUserId.text = user.userId
            binding.tvUserName.text = user.userName
            binding.tvCoins.text = user.hunterCoins.toString()
            binding.tvHunts.text = user.helpfulHunts.toString()
            binding.tvQuestionAsk.text = user.numOfQuestionAsked.toString()

            binding.btn.setOnClickListener {
                Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            binding.llUserData.visibility = View.GONE
            binding.btn.text = "Login"
            binding.btn.setOnClickListener {
                activity?.startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
        }
    }

    private fun getUser() {
        user = user(requireContext())
    }
}
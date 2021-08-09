package com.bytesbridge.app.bughunter.activities.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.ui.activities.LoginActivity
import com.bytesbridge.app.bughunter.activities.ui.data.models.UserModel
import com.bytesbridge.app.bughunter.activities.ui.viewmodels.MainViewModel
import com.bytesbridge.app.bughunter.activities.utils.PaperDbUtils.Companion.clearData
import com.bytesbridge.app.bughunter.activities.utils.PaperDbUtils.Companion.user
import com.bytesbridge.app.bughunter.activities.utils.SnackbarUtil.Companion.showSnackBar
import com.bytesbridge.app.bughunter.databinding.FragmentSettingBinding
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options


class SettingFragment : Fragment() {
    lateinit var binding: FragmentSettingBinding
    var user: UserModel? = null
    private lateinit var launcher: ActivityResultLauncher<CropImageContractOptions>
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
        launcher = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // use the returned uri
                val uriContent = result.uriContent
                uriContent?.let {
                    binding.progress.visibility = View.VISIBLE
                    mainViewModel.upLoadImage(
                        user?.userId.toString(),
                        uriContent
                    ) { success, uri_image ->
                        binding.progress.visibility = View.GONE

                        if (success) {
                            showSnackBar(binding.root, "Upload Successful")
                            user?.let { user_ ->
                                user_.user_image = uri_image.toString()
                                user(requireContext(), user_)
                                loadImage(user?.user_image!!)

                            }
                        } else {
                            showSnackBar(binding.root, "Upload Failed")
                        }
                    }
                }
            } else {
                // an error occurred
                val exception = result.error
            }
        }
//        launcher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
//                if (data.resultCode == Activity.RESULT_OK) {
//                    val uri = data.data?.data!!
//                    mainViewModel.upLoadImage(user?.userId.toString(), uri) { success, uri_image ->
//                        if (success) {
//                            showSnackBar(binding.root, "Upload Successful")
//                            user?.let { user_->
//                                user_.user_image = uri_image.toString()
//                                user(requireContext(), user_)
//                            }
//                        } else {
//                            showSnackBar(binding.root, "Upload Failed")
//                        }
//                    }
//                }
//            }

    }

    private fun initClickListeners() {
        binding.img.setOnClickListener {
            launcherPhoto()
        }
        binding.btn.setOnClickListener {
            activity?.finish()
            clearData(requireContext())
        }
    }

    private fun launcherPhoto() {
        launcher.launch(options {
            setGuidelines(CropImageView.Guidelines.ON)
        })
    }

    private fun setUpView() {
        user?.let { user ->
            binding.tvName.text = user.name
            binding.tvUserId.text = user.userId
            binding.tvUserName.text = user.userName
            binding.tvCoins.text = user.hunterCoins.toString()
            binding.tvHunts.text = user.helpfulHunts.toString()
            binding.tvQuestionAsk.text = user.numberOfQuestions.toString()
            if (user.user_image.isNotEmpty()) {
                loadImage(user.user_image)
            }
            Glide.with(requireContext()).load(user.user_image).into(binding.img)
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

    private fun loadImage(userImage: String) {
        binding.progress.visibility = View.VISIBLE
        Glide.with(requireContext())
            .load(userImage).placeholder(R.drawable.ic_baseline_insert_photo_24)
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    p0: GlideException?,
                    p1: Any?,
                    p2: com.bumptech.glide.request.target.Target<Drawable>?,
                    p3: Boolean
                ): Boolean {
                    binding.progress.visibility = View.GONE

                    return false
                }

                override fun onResourceReady(
                    p0: Drawable?,
                    p1: Any?,
                    p2: com.bumptech.glide.request.target.Target<Drawable>?,
                    p3: DataSource?,
                    p4: Boolean
                ): Boolean {
                    binding.progress.visibility = View.GONE

                    return false
                }
            })
            .into(binding.img)

    }

    private fun getUser() {
        user = user(requireContext())

    }
}
package com.bytesbridge.app.bughunter.activities.adapters

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.ui.data.models.AnswerModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.UserModel
import com.bytesbridge.app.bughunter.activities.utils.PaperDbUtils.Companion.user
import com.bytesbridge.app.bughunter.activities.utils.differUtils
import com.bytesbridge.app.bughunter.databinding.AnswerItemBinding
import com.snov.timeagolibrary.PrettyTimeAgo


class AnswersAdapter(var onClick: (item: AnswerModel) -> Unit) :
    RecyclerView.Adapter<AnswersAdapter.ItemViewHolder>() {
    private var user: UserModel? = null
    private val differ = AsyncListDiffer(this, differUtils)


    fun submitList(list: List<AnswerModel?>?) = differ.submitList(list)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val binding: AnswerItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.answer_item,
            parent,
            false
        )
        user = user(binding.root.context)

        return ItemViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val answer = differ.currentList[position] as AnswerModel
        holder.binding.apply {
            Glide.with(root.context).load(answer.user_image).placeholder(R.drawable.ic_baseline_person_24).into(img)
            tvName.text = answer.answer_user_name
            answerDescription.text = answer.answer_detail
            tvTime.text = PrettyTimeAgo.getTimeAgo(answer.created_at.toLong())

            if ((answer.hunter_coins_rewarded <= 0 && answer.question_user_id != user?.userId) || answer.answer_user_id == user?.userId) {
                llRewarded.visibility = View.GONE
            } else {
                llRewarded.visibility = View.VISIBLE
                if (answer.hunter_coins_rewarded <= 0 && answer.question_user_id == user?.userId) {
                    tvRewardedTitle.setTextColor(
                        ResourcesCompat.getColorStateList(
                            root.context.resources,
                            R.color.MediumGrey,
                            null
                        )
                    )
                    ivRewarded.setColorFilter(
                        ContextCompat.getColor(root.context, R.color.MediumGrey),
                        PorterDuff.Mode.SRC_IN
                    )
                }
            }

            root.setOnClickListener {
                if ((answer.hunter_coins_rewarded <= 0 && answer.question_user_id == user?.userId)&& answer.answer_user_id==user?.userId ) {
                    onClick(answer)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ItemViewHolder(val binding: AnswerItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
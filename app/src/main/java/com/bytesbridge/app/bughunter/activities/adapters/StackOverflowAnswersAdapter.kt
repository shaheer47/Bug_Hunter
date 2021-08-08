package com.bytesbridge.app.bughunter.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.ui.data.models.AnswerModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.UserModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.StackOverflowAnswers
import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.StackOverflowAnswersResponse
import com.bytesbridge.app.bughunter.activities.utils.HtmlAligner
import com.bytesbridge.app.bughunter.activities.utils.HtmlAligner.Companion.alignHTML
import com.bytesbridge.app.bughunter.activities.utils.PaperDbUtils.Companion.user
import com.bytesbridge.app.bughunter.activities.utils.ShowLessTextUtils
import com.bytesbridge.app.bughunter.activities.utils.differUtils
import com.bytesbridge.app.bughunter.databinding.AnswerItemBinding
import com.snov.timeagolibrary.PrettyTimeAgo
import org.sufficientlysecure.htmltextview.HtmlFormatter
import org.sufficientlysecure.htmltextview.HtmlFormatterBuilder

class StackOverflowAnswersAdapter() :
    RecyclerView.Adapter<StackOverflowAnswersAdapter.ItemViewHolder>() {
    private var user: UserModel? = null
    private val differ = AsyncListDiffer(this, differUtils)

    fun submitList(list: List<StackOverflowAnswers>) {
        differ.submitList(list)
    }

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
        val items = differ.currentList[position] as StackOverflowAnswers
        holder.binding.apply {
            tvName.text = items.owner.display_name
            val spannable =
                HtmlFormatter.formatHtml(HtmlFormatterBuilder().setHtml(alignHTML(items.body)))
            answerDescription.text = spannable
            tvTime.text = PrettyTimeAgo.getTimeAgo(items.creation_date.toLong())
            llRewarded.visibility = View.GONE
            Glide.with(root.context).load(items.owner.profile_image)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class ItemViewHolder(val binding: AnswerItemBinding) : RecyclerView.ViewHolder(binding.root)
}
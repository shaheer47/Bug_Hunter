package com.bytesbridge.app.bughunter.activities.adapters

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.StackOverflowQuestions
import com.bytesbridge.app.bughunter.databinding.QuestionItemBinding

class StackOverflowQuestionsAdapter(questions: List<StackOverflowQuestions>, var onClick: (item: StackOverflowQuestions) -> Unit) :
    RecyclerView.Adapter<StackOverflowQuestionsAdapter.ItemViewHolder>() {
    var questionsList = questions

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val binding: QuestionItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.question_item,
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val items = questionsList[position]
        holder.binding.apply {
            Glide.with(root.context).load(items.owner.profile_image).into(image)
            tvName.text = items.owner.display_name
            tvDate.text = items.creation_date.toString()

            tvQuestion.text = items.title

            tvDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(items.description, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(items.description)
            }
            tvAnswerCount.text = items.answer_count.toString()
            tvViewsCount.text = items.view_count.toString()

            root.setOnClickListener {
                onClick(items)
            }
        }


    }


    override fun getItemCount(): Int {
        return questionsList.size
    }

    class ItemViewHolder(val binding: QuestionItemBinding) : RecyclerView.ViewHolder(binding.root)

}
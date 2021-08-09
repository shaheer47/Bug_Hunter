package com.bytesbridge.app.bughunter.activities.adapters
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytesbridge.app.bughunter.R
import com.bytesbridge.app.bughunter.activities.ui.data.models.QuestionModel
import com.bytesbridge.app.bughunter.activities.ui.data.models.responces.StackOverflowQuestions
import com.bytesbridge.app.bughunter.databinding.QuestionItemBinding
import com.snov.timeagolibrary.PrettyTimeAgo

class QuestionsAdapter(questions: List<QuestionModel>, var onClick: (item: QuestionModel) -> Unit) :
    RecyclerView.Adapter<QuestionsAdapter.ItemViewHolder>() {
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
            Glide.with(root.context).load(items.question_user_photo).into(image)
            tvName.text = items.question_user_name
            tvDate.text = PrettyTimeAgo.getTimeAgo(items.created_at.toLong())

            tvQuestion.text = items.question_title
            tvDescription.text = items.question_detail

            tvAnswerCount.text = items.number_of_answers.toString()
            tvViewsCount.text = items.views.toString()

            llHunterCoinsNumber.visibility=View.VISIBLE
            tvHunterPoints.text=items.hunter_coins_offer.toString()

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
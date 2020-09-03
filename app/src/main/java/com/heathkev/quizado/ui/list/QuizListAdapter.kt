package com.heathkev.quizado.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.databinding.SingleListItemBinding
import com.heathkev.quizado.ui.list.ListFragment.Companion.Level.BEGINNER
import kotlinx.android.synthetic.main.single_list_item.view.*
import java.util.*

class QuizListAdapter(val onClickListener: OnClickListener) :
    ListAdapter<QuizListModel, QuizListAdapter.QuizViewHolder>(DiffCallback) {

    class QuizViewHolder(private var binding: SingleListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(quizListModel: QuizListModel) {
            binding.quizListModel = quizListModel
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = SingleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quizListModel = getItem(position)
        holder.itemView.quiz_card.setOnClickListener {
            onClickListener.onClick(quizListModel)
        }
        holder.bind(quizListModel)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<QuizListModel>() {
        override fun areItemsTheSame(oldItem: QuizListModel, newItem: QuizListModel): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: QuizListModel, newItem: QuizListModel): Boolean {
            return oldItem.quiz_id == newItem.quiz_id
        }
    }

    class OnClickListener(val clickListener: (quizListModel: QuizListModel) -> Unit) {
        fun onClick(quizListModel: QuizListModel) = clickListener(quizListModel)
    }
}


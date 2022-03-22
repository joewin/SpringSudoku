package com.mmspring.sudoku.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mmspring.sudoku.databinding.GameItemsLayoutBinding
import com.mmspring.sudoku.databinding.HistoryItemsLayoutBinding
import com.mmspring.sudoku.model.Game
import com.mmspring.sudoku.model.GameHistory
import com.mmspring.sudoku.util.Utility

class HistoryAdapter(private val clickListener: HistoryAdapter.ItemClickListener): ListAdapter<GameHistory,HistoryAdapter.GameHistoryViewHolder>(GameHistoryDiffCallBack()) {

    class GameHistoryDiffCallBack: DiffUtil.ItemCallback<GameHistory>(){
        override fun areItemsTheSame(oldItem: GameHistory, newItem: GameHistory): Boolean {
            return oldItem.id == newItem.id
        }
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: GameHistory, newItem: GameHistory): Boolean {
            return oldItem == newItem
        }
    }
    class GameHistoryViewHolder private constructor(private val binding: HistoryItemsLayoutBinding):
        RecyclerView.ViewHolder(binding.root){
        //binging data and view
        fun bind(clickListener: HistoryAdapter.ItemClickListener, item: GameHistory){

            binding.tvName.text = Utility.getGameId(item.game_id,item.level)
            binding.tvRank.text =item.rank.toString()
            binding.tvDate.text = item.played_date
            binding.tvTime.text = Utility.getFormattedStopWatch(item.best_time * 1000)
            //click listener
            itemView.setOnClickListener {
                clickListener.onClick(item)
            }
        }


        companion object {
            fun from(parent: ViewGroup):GameHistoryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HistoryItemsLayoutBinding.inflate(layoutInflater, parent, false)
                return GameHistoryViewHolder(binding)
            }
        }
    }
    class ItemClickListener(val clickListener: (game: GameHistory) -> Unit) {

        fun onClick(game:GameHistory) = clickListener(game)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHistoryViewHolder {
        return GameHistoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GameHistoryViewHolder, position: Int) {
       val item = getItem(position)
        item.rank = position + 1
        holder.bind(clickListener,item)
    }
}
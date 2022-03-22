package com.mmspring.sudoku.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mmspring.sudoku.R
import com.mmspring.sudoku.databinding.GameItemsLayoutBinding

import com.mmspring.sudoku.model.Game
import com.mmspring.sudoku.util.Utility

class GameListAdapter(private val clickListener:ItemClickListener): ListAdapter<Game,GameListAdapter.GameViewHolder>(GameDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener,item)
    }
    class GameViewHolder private constructor(private val binding: GameItemsLayoutBinding):
        RecyclerView.ViewHolder(binding.root){
        //binging data and view
        fun bind(clickListener: ItemClickListener,item: Game){
            binding.tvItemGameId.text= Utility.getGameId(item.id,item.level)
            binding.tvItemGameLevel.text = Utility.getLevel(item.level)
            binding.tvItemGameTime.text = Utility.getFormattedStopWatch(item.totalTimes * 1000)
            if(Utility.alreadyWOn(item.gameQuiz,item.solution)){
                binding.imgStats.setImageResource(android.R.drawable.btn_star_big_on)
            }
            else{
                binding.imgStats.setImageResource(android.R.drawable.btn_star_big_off)
            }
            binding.executePendingBindings()
            //click listener
            itemView.setOnClickListener {
                clickListener.onClick(item)
            }
        }


        companion object {
            fun from(parent: ViewGroup):GameViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GameItemsLayoutBinding.inflate(layoutInflater, parent, false)
                return GameViewHolder(binding)
            }
        }
    }
    class GameDiffCallBack: DiffUtil.ItemCallback<Game>(){

        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem == newItem
        }
    }
    // onItemClickListener
    class ItemClickListener(val clickListener: (game: Game) -> Unit) {

        fun onClick(game:Game) = clickListener(game)

    }




}
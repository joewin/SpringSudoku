package com.mmspring.sudoku.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mmspring.sudoku.R
import com.mmspring.sudoku.databinding.GameListFragmentBinding
import com.mmspring.sudoku.model.Game
import com.mmspring.sudoku.ui.adapter.GameListAdapter
import com.mmspring.sudoku.util.Utility
import com.mmspring.sudoku.viewmodel.GameListViewModel
import com.mmspring.sudoku.viewmodel.GameListViewModelFactory
import com.mmspring.sudoku.viewmodel.QueryFilter

class GameListFragment : Fragment() {
    private lateinit var binding: GameListFragmentBinding
    private lateinit var adapter: GameListAdapter
    private val viewModel: GameListViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, GameListViewModelFactory(activity.application)).get(GameListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!(activity as AppCompatActivity?)!!.supportActionBar!!.isShowing){
            (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GameListFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        adapter = GameListAdapter(GameListAdapter.ItemClickListener{
            viewModel.onItemClick(it)
        })
        binding.recyclerGameList.adapter = adapter
        viewModel.games.observe(viewLifecycleOwner){
            it?.let {
                adapter.submitList(it)
                binding.progressBar.visibility = View.GONE
            }
        }
        viewModel.navigateToGamePlay.observe(viewLifecycleOwner){
            it?.let {
                if(Utility.alreadyWOn(it.gameQuiz,it.solution)) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Already Played")
                        .setMessage("This game is already won.Do you want to play again?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                            gotoGame(it)
                        }
                        .setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                        }
                        .show()

                }
                else{
                    gotoGame(it)
                }
            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }
    private fun gotoGame(game: Game){
        findNavController().navigate(
            GameListFragmentDirections.actionGameListFragmentToGameFragment(game)
        )
        viewModel.doneNavigating()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.game_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.apply {
            when (item.itemId) {
                R.id.show_very_easy -> onFilterChange(QueryFilter.VERYEASY)
                R.id.show_easy -> onFilterChange(QueryFilter.EASY)
                R.id.show_medium -> onFilterChange(QueryFilter.MEDIUM)
                R.id.show_hard -> onFilterChange(QueryFilter.HARD)
                else -> onFilterChange(QueryFilter.VERYEASY)
            }
            binding.progressBar.visibility = View.VISIBLE
        }
        return true
    }
}
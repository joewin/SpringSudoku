package com.mmspring.sudoku.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mmspring.sudoku.R
import com.mmspring.sudoku.databinding.GameListFragmentBinding
import com.mmspring.sudoku.databinding.HistoryListFragmentBinding
import com.mmspring.sudoku.ui.adapter.GameListAdapter
import com.mmspring.sudoku.ui.adapter.HistoryAdapter
import com.mmspring.sudoku.viewmodel.*

class HistoryListFragment : Fragment() {
    private lateinit var binding: HistoryListFragmentBinding
    private lateinit var adapter: HistoryAdapter
    private val viewModel: HistoryListViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, HistoryListViewModelFactory(activity.application)).get(HistoryListViewModel::class.java)
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
        binding = HistoryListFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        adapter = HistoryAdapter(HistoryAdapter.ItemClickListener{
            //viewModel.onItemClick(it)
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
                findNavController().navigate(
                    GameListFragmentDirections.actionGameListFragmentToGameFragment(it))
                    viewModel.doneNavigating()
            }
        }

        return binding.root
    }


}
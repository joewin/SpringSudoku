package com.mmspring.sudoku.ui.fragment

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.mmspring.sudoku.R
import com.mmspring.sudoku.databinding.MainFragmentBinding
import com.mmspring.sudoku.viewmodel.MainViewModel
import com.mmspring.sudoku.viewmodel.MainViewModelFactory


class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, MainViewModelFactory(activity.application)).get(
            MainViewModel::class.java)
    }
    private lateinit var binding:MainFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if((activity as AppCompatActivity?)!!.supportActionBar!!.isShowing){
            (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        }

    }
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.btnPlay.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToGameListFragment())
        }
        binding.btnStats.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToHistoryListFragment())
        }
        binding.btnAbout.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToAboutUsFragment())
        }
        binding.btnWeekly.setOnClickListener {
            viewModel.getWeeklyGame()
        }
        viewModel.gameLiveData.observe(viewLifecycleOwner){
            it?.let {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToWeeklyGameFragment(it))
                viewModel.doneNavigating()
            }
        }
        viewModel.hintLiveData.observe(viewLifecycleOwner){
            it?.let {
                binding.btnHints.text = getString(R.string.hints) +": $it"
            }
        }
        viewModel.bombLiveData.observe(viewLifecycleOwner){
            it?.let {
                binding.btnBombs.text = getString(R.string.bombs) +": $it"
            }
        }
        viewModel.arrowLiveData.observe(viewLifecycleOwner){
            it?.let {
                binding.btnArrows.text = getString(R.string.arrows) +": $it"
            }
        }
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        viewModel.initiaingData()
    }


}
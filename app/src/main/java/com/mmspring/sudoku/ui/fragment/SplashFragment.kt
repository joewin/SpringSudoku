package com.mmspring.sudoku.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apps4mm.quiz4myanmar.data.AppDatabase
import com.mmspring.sudoku.R
import com.mmspring.sudoku.databinding.SplashFragmentBinding
import com.mmspring.sudoku.repository.GameRepository
import com.mmspring.sudoku.viewmodel.SplashGameViewModel
import com.mmspring.sudoku.viewmodel.SplashGameViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : Fragment() {

    private lateinit var binding:SplashFragmentBinding
    private val viewModel: SplashGameViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, SplashGameViewModelFactory(activity.application)).get(SplashGameViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = SplashFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        viewModel.loading.observe(viewLifecycleOwner) {
            if(it==false){
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToMainFragment())
                viewModel.doneNavigation()
            }
        }
        return binding.root
    }


}
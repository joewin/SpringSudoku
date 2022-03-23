package com.mmspring.sudoku.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mmspring.sudoku.R
import com.mmspring.sudoku.databinding.WeeklyGameFragmentBinding
import com.mmspring.sudoku.model.Cell
import com.mmspring.sudoku.model.Game
import com.mmspring.sudoku.ui.custom.BoardView
import com.mmspring.sudoku.util.Constant
import com.mmspring.sudoku.util.Utility
import com.mmspring.sudoku.viewmodel.GameViewModel
import com.mmspring.sudoku.viewmodel.GameViewModelFactory


class WeeklyGameFragment : Fragment() , BoardView.OnTouchListener {

    private lateinit var binding:WeeklyGameFragmentBinding
    private lateinit var viewModel : GameViewModel
    private lateinit var buttons: List<TextView>
    private lateinit var sharedPref: SharedPreferences
    private lateinit var game: Game
    private val args: GameFragmentArgs by navArgs()
    private lateinit var editor: SharedPreferences.Editor
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences(getString(R.string.prefer), Context.MODE_PRIVATE)
        editor = sharedPref.edit()
        if((activity as AppCompatActivity?)!!.supportActionBar!!.isShowing){
            (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        }
        viewModel = ViewModelProvider(this, GameViewModelFactory(requireActivity().application,game))[GameViewModel::class.java]
        viewModel.selectedCellLiveData.observe(viewLifecycleOwner){ updateSelectedCellUI(it) }
        viewModel.cellsLiveData.observe(viewLifecycleOwner){ updateCells(it) }
        viewModel.isTakingNotesLiveData.observe(viewLifecycleOwner){ updateNoteTakingUI(it) }
        viewModel.highlightedKeysLiveData.observe(viewLifecycleOwner){ updateHighlightedKeys(it)}
        binding.sudokuBoardView.registerListener(this)
        viewModel.hints.observe(viewLifecycleOwner){
            binding.tvHnint.text = "Hints :$it"
        }
        viewModel.bombs.observe(viewLifecycleOwner){
            binding.tvBomb.text = "Bomb:$it"
        }
        viewModel.arrows.observe(viewLifecycleOwner){
            binding.tvArrow.text = "Arrow:$it"
        }
        timeInSeconds = game.totalTimes
        initStopWatch()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = WeeklyGameFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        game = args.argGame
        buttons = listOf(binding.oneButton,binding.twoButton,binding.threeButton,
            binding.fourButton,binding.fiveButton,binding.sixButton,
            binding.sevenButton,binding.eightButton,binding.nineButton)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {

                viewModel.handleInput(index + 1)
                if(checkWin()){
                    Toast.makeText(requireContext(),"Congrats!! You win this week quiz", Toast.LENGTH_LONG).show()
                    goTohistory()
                }
                //Log.d("Input", (index + 1).toString())
            }
        }
        binding.noteButton.setOnClickListener { try{
            viewModel.changeNoteTakingState()
        }
        catch (e:Exception){
            Log.d("My error", e.toString())
        }
        }
        binding.deleteButton.setOnClickListener {
            try{viewModel.delete()
            }
            catch(e:Exception){
                Log.d("My error", e.toString())
            }
        }
        binding.hintsButton.setOnClickListener {
            hintFunction()
        }
        binding.saveButton.setOnClickListener {
            saveFunction()
        }
        binding.bombButton.setOnClickListener {
            bombsFunction()
        }
        binding.arrowButton.setOnClickListener {
            arrowsFunction(it)
        }
        return binding.root
    }

    //saving the game
    private fun saveFunction(){
        viewModel.saveWeeklyGame(timeInSeconds)
        viewModel.savingDone.observe(viewLifecycleOwner){
            if(it) {
                Toast.makeText(requireContext(), "You saved current game", Toast.LENGTH_LONG).show()
                viewModel.setSavingValue()
            }
        }

    }
    private fun arrowsFunction(view: View){
        if (viewModel.arrows.value!! <= 0){

            Toast.makeText(
               requireContext(),
                "No more arrow left",
                Toast.LENGTH_LONG
            ).show()
        }
        else{
            try{
                doArrow(view)

                if (checkWin()) {
                    Toast.makeText(requireContext(), "Congrats!! You win this week quiz", Toast.LENGTH_LONG)
                        .show()
                    goTohistory()
                }
            }
            catch (e:Exception){

            }
        }

    }
    private fun doArrow(it: View) {
        var arrowRow = true
        val popupMenu: android.widget.PopupMenu = android.widget.PopupMenu(requireContext(),it)
        popupMenu.inflate(R.menu.arrow_menu)
        popupMenu.setOnMenuItemClickListener(android.widget.PopupMenu.OnMenuItemClickListener {
            when (it.itemId){
                R.id.arrow_row -> arrowRow = true
                R.id.arrow_col -> arrowRow = false
            }
            viewModel.handleArrow(arrowRow)
            true

        })
        popupMenu.show()

    }
    @SuppressLint("SetTextI18n")
    private fun bombsFunction(){
        if (viewModel.bombs.value!! <= 0) {
            Toast.makeText(
                requireContext(),
                "No more bomb left",
                Toast.LENGTH_LONG
            ).show()
        }
        else{
            try{
                viewModel.handleBomb()
                if (checkWin()) {

                    Toast.makeText(requireContext(), "Congrats!! Winner winner", Toast.LENGTH_LONG)
                        .show()
                    goTohistory()
                }
            }
            catch (e:Exception){

            }
        }
    }
    // doing hint
    @SuppressLint("SetTextI18n")
    private fun hintFunction(){
        try {
            if (viewModel.hints.value!! > 0) {
                viewModel.handleHint()
                if (checkWin()) {
                    Toast.makeText(requireContext(), "Congrats!! Winner winner", Toast.LENGTH_LONG)
                        .show()
                    goTohistory()
                    //back to home
                }
            }
            else {
                Toast.makeText(requireContext(), "No hint left", Toast.LENGTH_LONG)
                    .show()
            }
        }
        catch(e:Exception) { }
    }
    private fun goTohistory(){
        findNavController().navigateUp()
    }
    private fun checkWin():Boolean{
        val cells = viewModel.cellsLiveData.value
        if (cells != null) {
            for(cell in cells){
                if(cell.value == 0 || !cell.isInvalid){
                    return false
                }

            }
        }
        stopTimer()
        viewModel.insertHistory(timeInSeconds)
        return true
    }

    override fun onCellTouched(row: Int, col: Int) {
        try {
            viewModel.updateSelectedCell(row, col)
        }
        catch (e:Exception){
            Log.d("My error", e.toString())
        }
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        binding.sudokuBoardView.updateCells(cells)
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        binding.sudokuBoardView.updateSelectedCellUi(cell.first, cell.second)
    }

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        val color = if (it) ContextCompat.getColor(requireContext(), R.color.teal_200) else Color.LTGRAY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.noteButton.background.colorFilter = BlendModeColorFilter(color, BlendMode.MULTIPLY)
            binding.hintsButton.background.colorFilter = BlendModeColorFilter(color, BlendMode.MULTIPLY)
            binding.deleteButton.background.colorFilter = BlendModeColorFilter(color, BlendMode.MULTIPLY)
            binding.saveButton.background.colorFilter = BlendModeColorFilter(color, BlendMode.MULTIPLY)
        }
        else{
            binding.apply {
                noteButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                hintsButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                deleteButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                saveButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            }
        }
    }

    private fun updateHighlightedKeys(set: Set<Int>?) = set?.let {
        buttons.forEachIndexed { index, button ->
            val color = if (set.contains(index + 1)) ContextCompat.getColor(requireContext(), R.color.teal_200)else Color.TRANSPARENT
            button.setBackgroundColor(color)
        }
    }
    //timmer
    private val mInterval = Constant.TIMER_INTERVAL // 1 second in this case
    private var mHandler: Handler? = null
    private var timeInSeconds = 0L
    private fun initStopWatch() {
        binding.tvTimer.text= getString(R.string.init_stop_watch_value)
        startTimer()
    }
    private fun startTimer() {
        mHandler = Handler(Looper.getMainLooper())
        mStatusChecker.run()
    }

    private fun stopTimer() {
        mHandler?.removeCallbacks(mStatusChecker)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }
    private var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                timeInSeconds += 1
                updateStopWatchView(timeInSeconds)
            } finally {
                mHandler!!.postDelayed(this, mInterval.toLong())
            }
        }
    }
    private fun updateStopWatchView(timeInSeconds: Long) {
        val formattedTime = Utility.getFormattedStopWatch((timeInSeconds * 1000))
        binding.tvTimer.text = formattedTime
    }
}
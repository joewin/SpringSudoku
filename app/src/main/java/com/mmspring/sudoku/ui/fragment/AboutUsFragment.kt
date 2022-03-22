package com.mmspring.sudoku.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.mmspring.sudoku.R
import com.mmspring.sudoku.databinding.FragmentAboutUsBinding


class AboutUsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAboutUsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if((activity as AppCompatActivity?)!!.supportActionBar!!.isShowing){
            (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        }
    }
}
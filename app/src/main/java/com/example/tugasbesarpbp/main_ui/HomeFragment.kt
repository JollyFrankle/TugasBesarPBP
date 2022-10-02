package com.example.tugasbesarpbp.main_ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.tugasbesarpbp.HomeActivity
import com.example.tugasbesarpbp.R
import com.example.tugasbesarpbp.databinding.FragmentHomeBinding
import com.example.tugasbesarpbp.databinding.HomeFragViewModel
import com.google.android.material.textfield.TextInputLayout

class HomeFragment : Fragment() {
    // view binding
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!

    private lateinit var viewModel: HomeFragViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // data binding
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        viewModel = ViewModelProvider(this).get(HomeFragViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Inflate the layout for this fragment
        return binding.root
//        return inflater.inflate(com.example.tugasbesarpbp.R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set actionbar title
        (activity as HomeActivity).setActionBarTitle("JogjaKost")

//        val viewModel = ViewModelProvider(this).get(HomeFragViewModel::class.java)
//        DataBindingUtil.setContentView<FragmentHomeBinding>(activity as HomeActivity, com.example.tugasbesarpbp.R.layout.fragment_home)
//            .apply {
//                this.lifecycleOwner = this@HomeFragment
//                this.viewModel = viewModel
//         }

        // button search on click
        val btnSearch = view.findViewById<Button>(R.id.btnHomeSearch)
        btnSearch.setOnClickListener {
            // go to list item fragment with arguments
            val fragment = R.id.listItemFragment
            val bundle = Bundle()
            val keyword = view.findViewById<TextInputLayout>(R.id.tilHomeSearch).editText?.text.toString()
            bundle.putString("search", keyword)
            (activity as HomeActivity).navHostFragment.navController.navigate(fragment, bundle)
        }
    }
}
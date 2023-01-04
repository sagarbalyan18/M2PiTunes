package com.example.m2p.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import androidx.databinding.DataBindingUtil
import com.example.m2p.BuildConfig
import com.example.m2p.R
import com.example.m2p.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "sagar"

    private lateinit var binding: ActivityMainBinding
//    private val viewModel : MovieListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

/*
        val movieListAdapter = MovieListAdapter(this,LIST_VIEW)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = movieListAdapter
        }

        binding.tvGrid.setOnClickListener {
            binding.recyclerView.layoutManager = GridLayoutManager(this@MainActivity,3)
            binding.tvGrid.setBackgroundColor(resources.getColor(R.color.selected_tab_color))
            binding.tvList.setBackgroundColor(resources.getColor(R.color.dark_grey))
        }
        binding.tvList.setOnClickListener {
            binding.tvList.setBackgroundColor(resources.getColor(R.color.selected_tab_color))
            binding.tvGrid.setBackgroundColor(resources.getColor(R.color.dark_grey))
            binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        }

        viewModel.response.observe(this, {listMovies ->
            Log.d(TAG, "onCreate: ${Gson().toJson(listMovies)}")
            binding.shimmerFrameLayout.visibility = View.GONE
            movieListAdapter.movies = listMovies
        })
*/


    }
}
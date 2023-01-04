package com.example.m2p.ui.screens.song_list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.m2p.R
import com.example.m2p.data.models.SongResult
import com.example.m2p.databinding.FragmentMovieListBinding
import com.example.m2p.utils.Constants.GRID_VIEW
import com.example.m2p.utils.Constants.LIST_VIEW
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongListFragment : Fragment() {

    private val TAG = "sagar"
    private lateinit var binding: FragmentMovieListBinding
    private val viewModel: SongViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_list, container, false)
        val songListAdapter =
            SongListAdapter(requireContext(), { songResult: SongResult -> itemClicked(songResult) })
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            viewModel.viewType.observe(requireActivity(), { type ->
                if (type == LIST_VIEW) {
                    selectListView()
                } else {
                    selectGridView()
                }
            })
            adapter = songListAdapter
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG, "beforeTextChanged")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG, "onTextChanged")
                viewModel.searchTerm(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "afterTextChanged")
                viewModel.searchTerm(s.toString())
            }

        })

        binding.tvGrid.setOnClickListener {
            selectGridView()
            viewModel.setViewType(GRID_VIEW)
        }
        binding.tvList.setOnClickListener {
            selectListView()
            viewModel.setViewType(LIST_VIEW)
        }

        viewModel.response.observe(viewLifecycleOwner, { listMovies ->
            Log.d(TAG, "onCreate: ${Gson().toJson(listMovies)}")
            binding.shimmerFrameLayout.visibility = View.GONE
            songListAdapter.movies = listMovies
        })

        return binding.root
    }

    fun selectGridView() {
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.tvGrid.setBackgroundColor(resources.getColor(R.color.selected_tab_color))
        binding.tvList.setBackgroundColor(resources.getColor(R.color.dark_grey))
    }

    fun selectListView() {
        binding.tvList.setBackgroundColor(resources.getColor(R.color.selected_tab_color))
        binding.tvGrid.setBackgroundColor(resources.getColor(R.color.dark_grey))
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    fun itemClicked(song: SongResult) {
        viewModel.setSelectedSong(song)
        findNavController().navigate(R.id.action_movieListFragment_to_songDetailFragment)
    }


}
package com.example.m2p.ui.screens.song_list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.m2p.R
import com.example.m2p.data.models.SongResult
import com.example.m2p.databinding.ItemMovieBinding
import com.example.m2p.utils.Constants.LIST_VIEW

class SongListAdapter(
    private val context: Context,
    private val clickListener: (SongResult) -> Unit
) : RecyclerView.Adapter<SongListAdapter.ViewHolder>() {

    private val TAG = "sagar"

    class ViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            binding: ItemMovieBinding,
            song: SongResult,
            context: Context,
            clickListener: (SongResult) -> Unit
        ) {
            Glide.with(context).load(song.artworkUrl100).placeholder(R.drawable.place).into(binding.imageView)
            var kind = song.kind ?: "unknown"
            var artist = song.artistName ?: "unknown"
            var songName = song.trackName ?: "unknown"
            if(kind.equals("feature-movie")) kind = "movie"
            if(artist.length > 15) artist = artist.substring(0,15) + "..."
            if(songName.length > 20) songName = songName.substring(0,20) + "..."
            binding.tvSongName.text = songName
            binding.tvArtistName.text = "$kind . $artist"
            binding.root.setOnClickListener {
                clickListener(song)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding: ItemMovieBinding
        if (viewType == LIST_VIEW)
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_movie,
                parent,
                false
            )
        else
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_movie_grid,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        Log.d(TAG, "onBindViewHolder: ${movies[position]}")
        holder.bind(holder.binding, movies[position], context, clickListener)
    }

    override fun getItemCount() = movies.size

    private val diffCallback = object : DiffUtil.ItemCallback<SongResult>() {
        override fun areItemsTheSame(oldItem: SongResult, newItem: SongResult): Boolean {
            return oldItem.artistId == newItem.artistId
        }

        override fun areContentsTheSame(oldItem: SongResult, newItem: SongResult): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var movies: List<SongResult>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

}
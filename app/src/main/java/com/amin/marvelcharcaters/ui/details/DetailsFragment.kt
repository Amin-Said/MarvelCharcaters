package com.amin.marvelcharcaters.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amin.marvelcharcaters.R
import com.amin.marvelcharcaters.adapter.DetailsContentRecyclerAdapter
import com.amin.marvelcharcaters.databinding.FragmentDetailsBinding
import com.amin.marvelcharcaters.model.*
import com.amin.marvelcharcaters.utils.extensions.getImage
import com.amin.marvelcharcaters.utils.extensions.readFile
import com.bumptech.glide.Glide
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private var result: CharacterResult? = null
    lateinit var mAdapter: DetailsContentRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            result = DetailsFragmentArgs.fromBundle(
                it
            ).character
            binding.characterTitle.text = result?.name
            var image = result?.getImage(result!!.thumbnail.path, result!!.thumbnail.extension)

            Glide.with(binding.root.context)
                .load(image)
                .placeholder(R.drawable.image_placeholder)
                .into(binding.characterImage)
            if (!result?.description.isNullOrEmpty()) {
                binding.characterDescription.text = result?.description
            }
        }


        binding.backContainer.setOnClickListener { findNavController().popBackStack() }

        initRecyclerView()

        val list = listOf(
            NestedItem("Comic", getOfflineDataComic()?.data),
            NestedItem("Stories", getOfflineDataStories()?.data),
            NestedItem("Series", getOfflineDataSeries()?.data),
            NestedItem("Events", getOfflineDataEvents()?.data),

            )

        mAdapter.submitList(list)

    }

    fun getOfflineDataComic(): ComicResponse? {
        val charactersJsonResponseToString = requireActivity().assets.readFile("comic.json")

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter: JsonAdapter<ComicResponse> =
            moshi.adapter<ComicResponse>(ComicResponse::class.java)

        val response: ComicResponse? = jsonAdapter.fromJson(charactersJsonResponseToString)
        System.out.println("DEBUGA : moshi : $response")
        return response
    }

    fun getOfflineDataSeries(): SeriesResponse? {
        val charactersJsonResponseToString = requireActivity().assets.readFile("series.json")

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter: JsonAdapter<SeriesResponse> =
            moshi.adapter<SeriesResponse>(SeriesResponse::class.java)

        val response: SeriesResponse? = jsonAdapter.fromJson(charactersJsonResponseToString)
        System.out.println("DEBUGB : moshi : $response")
        return response
    }

    fun getOfflineDataStories(): StoriesResponse? {
        val charactersJsonResponseToString = requireActivity().assets.readFile("stories.json")

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter: JsonAdapter<StoriesResponse> =
            moshi.adapter<StoriesResponse>(StoriesResponse::class.java)

        val response: StoriesResponse? = jsonAdapter.fromJson(charactersJsonResponseToString)
        System.out.println("DEBUGC : moshi : $response")
        return response
    }

    fun getOfflineDataEvents(): EventsResponse? {
        val charactersJsonResponseToString = requireActivity().assets.readFile("events.json")

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter: JsonAdapter<EventsResponse> =
            moshi.adapter<EventsResponse>(EventsResponse::class.java)

        val response: EventsResponse? = jsonAdapter.fromJson(charactersJsonResponseToString)
        System.out.println("DEBUGD : moshi : $response")
        return response
    }

    private fun initRecyclerView() {
        println("DEBUG in init")
        binding.detailsRV.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            mAdapter = DetailsContentRecyclerAdapter()
            adapter = mAdapter
        }
    }

}
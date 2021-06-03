package com.amin.marvelcharcaters.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amin.marvelcharcaters.R
import com.amin.marvelcharcaters.adapter.DetailsContentRecyclerAdapter
import com.amin.marvelcharcaters.databinding.FragmentDetailsBinding
import com.amin.marvelcharcaters.model.*
import com.amin.marvelcharcaters.utils.Config
import com.amin.marvelcharcaters.utils.data.ApiResult
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

    var mainList = mutableListOf<NestedItem>()
    var comics = mutableListOf<PosterItem>()
    var stories = mutableListOf<PosterItem>()
    var series = mutableListOf<PosterItem>()
    var events = mutableListOf<PosterItem>()

    var comicsSize = 0
    var comicsCount = 0
    var  storiesSize = 0
    var storiesCount = 0
    var seriesSize = 0
    var  seriesCount = 0
    var  eventsSize = 0
    var eventsCount = 0


    @VisibleForTesting
    val viewModel: DetailsViewModel by viewModels()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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


            requestResources(result)
        }

        binding.backContainer.setOnClickListener { findNavController().popBackStack() }

        initRecyclerView()

    }

    private fun initRecyclerView() {
        println("DEBUG in init")
        binding.detailsRV.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            mAdapter = DetailsContentRecyclerAdapter()
            adapter = mAdapter
        }
    }

    private fun observeResourceData(type :String,title:String ,url: String) {
        viewModel.fetchResourceData(
            url,
            Config.PUBLIC_KEY_VALUE,
            Config.HASH_Value,
            Config.TIMESTAMP_Value.toString()
        )

        viewModel.result.observe(viewLifecycleOwner) {
            when (it) {
                ApiResult.Loading -> {

                }
                is ApiResult.Error -> {


                }
                is ApiResult.Success -> {

                    when(type){
                        Config.EVENTS_TYPE ->{
                            eventsCount++
                            events.add(PosterItem(title,it.data.data.results[0].thumbnail?.path+"."+it.data.data.results[0].thumbnail?.extension))
                            if (events.isNotEmpty() && eventsSize==eventsCount){
                                mainList.add(NestedItem(Config.EVENTS_TYPE,events))
                                mAdapter.submitList(mainList)
                            }
                        }
                        Config.STORIES_TYPE ->{
                            stories.add(PosterItem(title,it.data.data.results[0].thumbnail?.path+"."+it.data.data.results[0].thumbnail?.extension))
                            storiesCount++
                            stories.add(PosterItem(title,it.data.data.results[0].thumbnail?.path+"."+it.data.data.results[0].thumbnail?.extension))
                            if (stories.isNotEmpty() && storiesSize==storiesCount){
                                mainList.add(NestedItem(Config.STORIES_TYPE,stories))
                                mAdapter.submitList(mainList)
                            }

                        }
                        Config.SERIES_TYPE ->{
                            series.add(PosterItem(title,it.data.data.results[0].thumbnail?.path+"."+it.data.data.results[0].thumbnail?.extension))
                            seriesCount++
                            series.add(PosterItem(title,it.data.data.results[0].thumbnail?.path+"."+it.data.data.results[0].thumbnail?.extension))
                            if (series.isNotEmpty() && seriesSize==seriesCount){
                                mainList.add(NestedItem(Config.SERIES_TYPE,series))
                                mAdapter.submitList(mainList)
                            }

                        }

                    }
                }
            }

        }

        viewModel.isNetworkError.observe(viewLifecycleOwner) {
            if (it) {

            }
        }
    }

    private fun observeComicResourceData(title:String ,url: String) {
        viewModel.fetchComicResourceData(
            url,
            Config.PUBLIC_KEY_VALUE,
            Config.HASH_Value,
            Config.TIMESTAMP_Value.toString()
        )

        viewModel.resultComics.observe(viewLifecycleOwner) {
            when (it) {
                ApiResult.Loading -> {

                }
                is ApiResult.Error -> {


                }
                is ApiResult.Success -> {
                    comicsCount++
                    comics.add(PosterItem(title,it.data.data.results[0].thumbnail.path+"."+it.data.data.results[0].thumbnail.extension))
                    if (comics.isNotEmpty() && comicsSize==comicsCount){
                        mainList.add(NestedItem(Config.COMIC_TYPE,comics))
                        mAdapter.submitList(mainList)
                    }
                }
            }

        }

        viewModel.isNetworkError.observe(viewLifecycleOwner) {
            if (it) {

            }
        }
    }

    private fun requestResources(result:CharacterResult?){
        for (item in result?.comics?.items!!){
            comicsSize = result?.comics?.items!!.size
            observeComicResourceData(item.name,item.resourceURI)
        }

        for (item in result?.stories?.items!!){
            storiesSize = result?.stories?.items!!.size
            observeResourceData(Config.STORIES_TYPE,item.name,item.resourceURI)
        }

        for (item in result?.events?.items!!){
            eventsSize = result?.events?.items!!.size
            observeResourceData(Config.EVENTS_TYPE,item.name,item.resourceURI)

        }

        for (item in result?.series?.items!!){
            seriesSize = result?.series?.items!!.size
            observeResourceData(Config.SERIES_TYPE,item.name,item.resourceURI)

        }
    }

    // for offline data read
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



}
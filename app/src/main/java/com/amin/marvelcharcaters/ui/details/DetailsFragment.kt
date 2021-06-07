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
import com.amin.marvelcharcaters.model.details.NestedItem
import com.amin.marvelcharcaters.model.details.PosterItem
import com.amin.marvelcharcaters.model.details.ResourceResponse
import com.amin.marvelcharcaters.utils.Config
import com.amin.marvelcharcaters.utils.Helper
import com.amin.marvelcharcaters.utils.data.ApiResult.*
import com.amin.marvelcharcaters.utils.extensions.getImage
import com.bumptech.glide.Glide
import com.google.android.material.shape.CornerFamily
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private var result: CharacterResult? = null
    private lateinit var mAdapter: DetailsContentRecyclerAdapter

    private var mainList = mutableListOf<NestedItem>()
    private var comics = mutableListOf<PosterItem>()
    private var stories = mutableListOf<PosterItem>()
    private var series = mutableListOf<PosterItem>()
    private var events = mutableListOf<PosterItem>()

    private var comicsSize = 0
    private var comicsCount = 0
    private var storiesSize = 0
    private var storiesCount = 0
    private var seriesSize = 0
    private var seriesCount = 0
    private var eventsSize = 0
    private var eventsCount = 0

    private var totalRqeustsCount = 0

    private val hash =
        Helper.getHash()

    @VisibleForTesting
    val viewModel: DetailsViewModel by viewModels()

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

        // to make radis in bottom of header image
        setImageHeaderRadius()

        arguments?.let {
            result = DetailsFragmentArgs.fromBundle(
                it
            ).character
            binding.characterTitle.text = result?.name
            val image = result?.getImage(result!!.thumbnail.path, result!!.thumbnail.extension)

            Glide.with(binding.root.context)
                .load(image)
                .placeholder(R.drawable.image_placeholder)
                .into(binding.characterImage)

            Glide.with(binding.root.context)
                .load(image)
                .placeholder(R.drawable.image_placeholder)
                .into(binding.backgroundImage)

            if (!result?.description.isNullOrEmpty()) {
                binding.characterDescription.text = result?.description
            }


            if (Helper.isOnline(requireActivity())) {
                requestResources(result)
            } else {
                handleErrorNetworkState()
            }
        }

        binding.backContainer.setOnClickListener { findNavController().popBackStack() }

        initRecyclerView()

    }

    // for views setup
    private fun setImageHeaderRadius(){
        val radius = resources.getDimension(R.dimen.appBar_radius)
        binding.characterImage.shapeAppearanceModel = binding.characterImage.shapeAppearanceModel
            .toBuilder()
            .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
            .build()

    }

    private fun initRecyclerView() {
        binding.detailsRV.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            mAdapter = DetailsContentRecyclerAdapter()
            adapter = mAdapter
        }
    }

    private fun startLoading() {
        binding.avi.smoothToShow()
    }

    private fun endLoading() {
        binding.avi.hide()
    }

    private fun handleErrorNetworkState() {
        binding.avi.smoothToHide()
        binding.errorMsg.visibility = View.VISIBLE
        binding.errorMsg.text = requireActivity().getString(R.string.error_message_Network)
    }

    private fun handleRequestError() {
        binding.avi.smoothToHide()
        binding.errorMsg.visibility = View.VISIBLE
        binding.errorMsg.text = requireActivity().getString(R.string.error_message_Request)
    }

    // for getting data
    private fun requestResources(result: CharacterResult?) {
        comicsSize = result?.comics?.items?.size!!
        storiesSize = result.stories.items.size
        eventsSize = result.events.items.size
        seriesSize = result.series.items.size

        totalRqeustsCount = comicsSize+storiesSize+eventsSize+seriesSize

        for (item in result.comics.items) {
            observeResourceData(requireActivity().getString(R.string.comics_title), item.name, item.resourceURI)
        }

        for (item in result.stories.items) {
            observeResourceData(requireActivity().getString(R.string.stories_title), item.name, item.resourceURI)
        }

        for (item in result.events.items) {
            observeResourceData(requireActivity().getString(R.string.events_title), item.name, item.resourceURI)

        }

        for (item in result.series.items) {
            observeResourceData(requireActivity().getString(R.string.series_title), item.name, item.resourceURI)

        }
    }

    private fun observeResourceData(type: String, title: String, url: String) {
        viewModel.fetchResourceData(
            url,
            Config.PUBLIC_KEY_VALUE,
            hash,
            Config.TIMESTAMP_Value
        )

        viewModel.result.observeForever{

        }
        viewModel.result.observe(viewLifecycleOwner) {
            when (it) {
                Loading -> {
                    startLoading()
                }
                is Error -> {
                    totalRqeustsCount--
                    if (totalRqeustsCount==0){
                        endLoading()
                        handleRequestError()
                    }
                }
                is Success -> {
                    totalRqeustsCount--
                    when (type) {
                        Config.COMIC_TYPE -> {
                            comicsCount++
                            comics.add(getPosterItem(title,it.data))
                            if (comics.isNotEmpty() && comicsSize == comicsCount) {
                                addResourceItemToTheList(Config.COMIC_TYPE, comics)
                            }
                        }

                        Config.EVENTS_TYPE -> {
                            eventsCount++
                            events.add(getPosterItem(title,it.data))
                            if (events.isNotEmpty() && eventsSize == eventsCount) {
                                addResourceItemToTheList(Config.EVENTS_TYPE, events)
                            }
                        }

                        Config.STORIES_TYPE -> {
                            stories.add(getPosterItem(title,it.data))
                            storiesCount++
                            if (stories.isNotEmpty() && storiesSize == storiesCount) {
                                addResourceItemToTheList(Config.STORIES_TYPE, stories)
                            }
                        }

                        Config.SERIES_TYPE -> {
                            series.add(getPosterItem(title,it.data))
                            seriesCount++
                            if (series.isNotEmpty() && seriesSize == seriesCount) {
                                addResourceItemToTheList(Config.SERIES_TYPE,series)
                            }
                        }

                    }
                    if (totalRqeustsCount==0){
                        endLoading()
                        binding.detailsRV.visibility = View.VISIBLE
                    }
                }
            }

        }

        viewModel.isNetworkError.observe(viewLifecycleOwner) {
            if (it) {
                handleErrorNetworkState()
            }
        }


    }
    private fun addResourceItemToTheList(title:String, itemsList:List<PosterItem>){
        mainList.add(NestedItem(title, itemsList))
        mAdapter.submitList(mainList)
    }

    private fun getPosterItem(title:String, response:ResourceResponse):PosterItem{
        return  PosterItem(
            title,
            response.data.results[0].thumbnail?.path + "." + response.data.results[0].thumbnail?.extension
        )
    }

}


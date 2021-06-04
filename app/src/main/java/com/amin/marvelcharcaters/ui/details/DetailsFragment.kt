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
import com.amin.marvelcharcaters.utils.Config
import com.amin.marvelcharcaters.utils.Helper
import com.amin.marvelcharcaters.utils.data.ApiResult
import com.amin.marvelcharcaters.utils.extensions.getImage
import com.bumptech.glide.Glide
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
    private var  storiesSize = 0
    private var storiesCount = 0
    private var seriesSize = 0
    private var  seriesCount = 0
    private var  eventsSize = 0
    private var eventsCount = 0

    private val hash =
        Helper.md5(Config.TIMESTAMP_Value + Config.PRIVATE_KEY_VALUE + Config.PUBLIC_KEY_VALUE)



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
            if (!result?.description.isNullOrEmpty()) {
                binding.characterDescription.text = result?.description
            }


            if (Helper.isOnline(requireActivity())){
                requestResources(result)
            }else{
                handleErrorNetworkState()
            }
        }

        binding.backContainer.setOnClickListener { findNavController().popBackStack() }

        initRecyclerView()

    }

    // for views setup
    private fun initRecyclerView() {
        binding.detailsRV.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            mAdapter = DetailsContentRecyclerAdapter()
            adapter = mAdapter
        }
    }

    private fun startLoading(){
        binding.avi.smoothToShow()
    }

    private fun endLoading(){
        binding.avi.hide()
    }

    private fun handleErrorNetworkState(){
        binding.avi.smoothToHide()
        binding.errorMsg.visibility = View.VISIBLE
        binding.errorMsg.text = requireActivity().getString(R.string.error_message_Network)
    }

    private fun handleRequestError(){
        binding.avi.smoothToHide()
        binding.errorMsg.visibility = View.VISIBLE
        binding.errorMsg.text = requireActivity().getString(R.string.error_message_Request)
    }

    // for getting data
    private fun requestResources(result:CharacterResult?){
        for (item in result?.comics?.items!!){
            comicsSize = result.comics.items.size
            observeResourceData(Config.COMIC_TYPE,item.name,item.resourceURI)
        }

        for (item in result.stories.items){
            storiesSize = result.stories.items.size
            observeResourceData(Config.STORIES_TYPE,item.name,item.resourceURI)
        }

        for (item in result.events.items){
            eventsSize = result.events.items.size
            observeResourceData(Config.EVENTS_TYPE,item.name,item.resourceURI)

        }

        for (item in result.series.items){
            seriesSize = result.series.items.size
            observeResourceData(Config.SERIES_TYPE,item.name,item.resourceURI)

        }
    }

    private fun observeResourceData(type :String,title:String ,url: String) {
        viewModel.fetchResourceData(
            url,
            Config.PUBLIC_KEY_VALUE,
            hash,
            Config.TIMESTAMP_Value
        )

        viewModel.result.observe(viewLifecycleOwner) {
            when (it) {
                ApiResult.Loading -> {
                    startLoading()
                }
                is ApiResult.Error -> {
                    handleRequestError()

                }
                is ApiResult.Success -> {
                    endLoading()
                    when(type){
                        Config.COMIC_TYPE ->{
                            comicsCount++
                            comics.add(PosterItem(title,it.data.data.results[0].thumbnail?.path+"."+it.data.data.results[0].thumbnail?.extension))
                            if (comics.isNotEmpty() && comicsSize==comicsCount){
                                mainList.add(NestedItem(Config.COMIC_TYPE,comics))
                                mAdapter.submitList(mainList)
                            }
                        }
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
                handleErrorNetworkState()

            }
        }
    }

}
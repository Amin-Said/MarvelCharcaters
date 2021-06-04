package com.amin.marvelcharcaters.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amin.marvelcharcaters.R
import com.amin.marvelcharcaters.adapter.CharactersRecyclerAdapter
import com.amin.marvelcharcaters.databinding.FragmentHomeListBinding
import com.amin.marvelcharcaters.model.*
import com.amin.marvelcharcaters.utils.Config
import com.amin.marvelcharcaters.utils.Helper.isOnline
import com.amin.marvelcharcaters.utils.data.ApiResult
import com.amin.taskdemo.SearchRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeListFragment : Fragment(),
    CharactersRecyclerAdapter.Interaction, SearchRecyclerAdapter.Interaction {

    private var _binding: FragmentHomeListBinding? = null
    private val binding get() = _binding!!
    lateinit var searchEditText: EditText

    private lateinit var mAdapter: CharactersRecyclerAdapter
    private lateinit var mSearchAdapter: SearchRecyclerAdapter

    private var isSearchOpened = false

    var isLoading = false
    val isLastPage = false
    var isScrolling = false
    var queryPageSize = 20
    var loadingPage = 0


    @VisibleForTesting
    val viewModel: HomeListViewModel by viewModels()


    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= queryPageSize
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                observeAllCharactersLiveData(loadingPage)
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingPage = 0

        setupViewForAppBarCustomization()

        initRecyclerView()

        // for search open and close handle
        binding.searchView.setOnSearchClickListener { changeToolbarOnStartSearch() }

        binding.cancelBtn.setOnClickListener { changeToolbarOnCancelSearch() }

        binding.searchView.setOnCloseListener {
            handleOnSearchClose()
            false
        }

        handleSearchQuery()

        if (isOnline(requireActivity())){
            observeAllCharactersLiveData(loadingPage)
        }else{
            handleErrorNetworkState()
        }


    }

    private fun initRecyclerView() {
        binding.charactersRV.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            mAdapter =
                CharactersRecyclerAdapter(this@HomeListFragment)
            adapter = mAdapter
            addOnScrollListener(this@HomeListFragment.scrollListener)
        }
    }

    private fun initSearchRecyclerView() {
        binding.searchRV.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            mSearchAdapter = SearchRecyclerAdapter(this@HomeListFragment)
            adapter = mSearchAdapter
        }
    }


    // for other views setup
    private fun startLoading(){
        binding.avi.smoothToShow()
    }

    private fun endLoading(){
        binding.avi.hide()
    }

    private fun changeToolbarOnStartSearch() {

        isSearchOpened = true
        searchEditText.isCursorVisible = false
        binding.cancelBtn.visibility = View.VISIBLE
        binding.searchView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        binding.appLogo.visibility = View.GONE
    }

    private fun changeToolbarOnCancelSearch() {
        isSearchOpened = false
        binding.cancelBtn.visibility = View.GONE
        binding.searchView.setQuery("", false)
        binding.searchView.isIconified = true
    }

    private fun setupViewForAppBarCustomization() {
        // to customize the SearchView EditText
        searchEditText = binding.searchView.findViewById(R.id.search_src_text) as EditText

        // to hide the default close icon in SearchView EditText
        val closeBtn: ImageView =
            binding.searchView.findViewById(R.id.search_close_btn) as ImageView
        closeBtn.isEnabled = false
        closeBtn.setImageDrawable(null)
    }

    private fun handleOnSearchClose() {
        binding.searchView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        binding.appLogo.visibility = View.VISIBLE
    }

    private fun handleSearchQuery() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchEditText.isCursorVisible = newText.isNotEmpty()

                if (newText.trim().isNotEmpty()) {

                    binding.blurBehindLayout.viewBehind = binding.toBlur
                    binding.blurBehindLayout.visibility = View.VISIBLE

                    observeSearchResultLiveData(newText)

                } else {
                    binding.blurBehindLayout.visibility = View.GONE
                    binding.blurBehindLayout.viewBehind = null

                }

                return false
            }
        })
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

    // for getting data and make actions on it
    private fun observeAllCharactersLiveData(page: Int) {
        viewModel.fetchAllCharacters(
            Config.PUBLIC_KEY_VALUE,
            Config.HASH_Value,
            Config.TIMESTAMP_Value,
            page.toString()
        )

        viewModel.result.observe(viewLifecycleOwner) {
            when (it) {
                ApiResult.Loading -> {
                    startLoading()
                }
                is ApiResult.Error -> {
                    handleRequestError()
                    Timber.d("DEBUERROR error")
                }
                is ApiResult.Success -> {
                    endLoading()
                    queryPageSize = it.data.data.limit
                    when {
                        it.data.data.offset < it.data.data.total -> {

                            mAdapter.addToCurrentList(it.data.data.results)
                            loadingPage++
                        }
                        else -> {
                            isScrolling = false
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

    override fun onCharacterItemSelected(position: Int, item: CharacterResult) {
        goToCharacterDetails(item)
    }


    private fun observeSearchResultLiveData(query: String) {
        viewModel.fetchCharactersDataForSearch(
            Config.PUBLIC_KEY_VALUE,
            Config.HASH_Value,
            Config.TIMESTAMP_Value,
            query
        )

        viewModel.searchResult.observe(viewLifecycleOwner) {
            when (it) {
                ApiResult.Loading -> {

                }
                is ApiResult.Error -> {
                    handleRequestError()
                }
                is ApiResult.Success -> {
                    initSearchRecyclerView()
                    mSearchAdapter.submitList(it.data.data.results)
                    mSearchAdapter.filter.filter(query)


                }
            }

        }

        viewModel.isNetworkError.observe(viewLifecycleOwner) {
            if (it) {
                handleErrorNetworkState()
            }
        }
    }

    override fun onSearchItemSelected(position: Int, item: CharacterResult) {
        goToCharacterDetails(item)
    }

    private fun goToCharacterDetails(item: CharacterResult){
        if (isSearchOpened) changeToolbarOnCancelSearch()
        val action =
            HomeListFragmentDirections.actionGoToDetails(
                item
            )
        findNavController().navigate(action)
    }


}
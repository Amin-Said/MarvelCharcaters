package com.amin.marvelcharcaters.ui.home

import android.animation.LayoutTransition
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amin.marvelcharcaters.R
import com.amin.marvelcharcaters.adapter.CharactersRecyclerAdapter
import com.amin.marvelcharcaters.adapter.SearchRecyclerAdapter
import com.amin.marvelcharcaters.databinding.FragmentHomeListBinding
import com.amin.marvelcharcaters.model.CharacterResult
import com.amin.marvelcharcaters.utils.Config
import com.amin.marvelcharcaters.utils.Helper
import com.amin.marvelcharcaters.utils.Helper.isOnline
import com.amin.marvelcharcaters.utils.data.ApiResult
import com.amin.marvelcharcaters.utils.extensions.toast
import com.amin.marvelcharcaters.utils.extensions.toastFromResource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext


@AndroidEntryPoint
class HomeListFragment : Fragment(),
    CharactersRecyclerAdapter.Interaction, SearchRecyclerAdapter.Interaction, CoroutineScope {

    private var _binding: FragmentHomeListBinding? = null
    private val binding get() = _binding!!
    lateinit var searchEditText: EditText

    private lateinit var mAdapter: CharactersRecyclerAdapter
    private var mSearchAdapter: SearchRecyclerAdapter? = null

    private var isSearchOpened = false
    private var isLoading = false
    private val isLastPage = false
    private var isScrolling = false
    private var isCharacterDataLoaded = false
    private var isSearchDataLoaded = false
    private var isOnSearch = false
    private var isOnNetworkError = false
    private var isToastOpened = false

    private var queryPageSize = 20
    private var loadingPage = 0
    private var lastHitPage = 0

    private val hash =
        Helper.getHash()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    val query = MutableStateFlow("")
    private var lastQuery = ""

    @VisibleForTesting
    val viewModel: HomeListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeListBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenToNetwork()

        // setup pagination page
        loadingPage = 0

        setupViewForAppBarCustomization()

        initCharactersRecyclerView()

        // for search open and close handle
        binding.searchView.setOnSearchClickListener { changeToolbarOnStartSearch() }
        binding.cancelBtn.setOnClickListener { changeToolbarOnCancelSearch() }
        binding.searchView.setOnCloseListener {
            handleOnSearchClose()
            false
        }

        // used to query handle
        job = Job()
        handleSearchQuery()

        requestAllCharactersData(loadingPage)

    }

    override fun onResume() {
        super.onResume()
        if (isSearchOpened) {
            changeToolbarOnCancelSearch()
        }
        if (isOnNetworkError && !isCharacterDataLoaded) {
            requestAllCharactersData(loadingPage)
        }
    }

    override fun onDestroyView() {
        job.cancel()
        super.onDestroyView()
        _binding = null
    }

    // listeners
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
                if (loadingPage != 0 && loadingPage != lastHitPage) {
                    requestAllCharactersData(loadingPage)
                }
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

    @RequiresApi(Build.VERSION_CODES.N)
    fun listenToNetwork() {
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (isOnNetworkError && !isCharacterDataLoaded) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        val endTime = System.nanoTime()
                        requestAllCharactersData(loadingPage)
                    }, 0)
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                requireActivity().toastFromResource(R.string.error_message_network_lost)
            }
        })
    }

    override fun onCharacterItemSelected(position: Int, item: CharacterResult) {
        goToCharacterDetails(item)
    }

    override fun onSearchItemSelected(position: Int, item: CharacterResult) {
        goToCharacterDetails(item)
    }

    // action on items clicks
    private fun goToCharacterDetails(item: CharacterResult) {
        if (isSearchOpened) changeToolbarOnCancelSearch()
        val action =
            HomeListFragmentDirections.actionGoToDetails(
                item
            )
        findNavController().navigate(action)
        isCharacterDataLoaded = false
    }

    // for setup RecyclerViews
    private fun initCharactersRecyclerView() {
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

    private fun cleanSearchRecyclerView(){
        binding.searchRV.apply {
            layoutManager = null
            mSearchAdapter = null
            adapter = null
        }
    }

    // for other views setup
    private fun startLoading() {
        binding.avi.smoothToShow()
        binding.errorMsg.visibility = View.GONE
    }

    private fun endLoading() {
        binding.avi.hide()
    }

    private fun makeBlurBackGroundOnSearch() {
        try {
            if (isOnline(requireActivity())) {
                binding.blurView.visibility = View.VISIBLE
                binding.searchRV.visibility = View.VISIBLE

            }
        } catch (e: Exception) {
            e.stackTrace
        }

    }

    private fun removeBlurBackGroundOnCancelSearch() {
        try {
            binding.blurView.visibility = View.GONE
            binding.searchRV.visibility = View.GONE

            cleanSearchRecyclerView()

        } catch (e: Exception) {
            e.stackTrace
        }
    }
    
    private fun changeToolbarOnStartSearch() {

        isSearchOpened = true
        isOnSearch = true
        searchEditText.isCursorVisible = false
        binding.cancelBtn.visibility = View.VISIBLE
        binding.searchView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        binding.appLogo.visibility = View.GONE
    }

    private fun changeToolbarOnCancelSearch() {
        isSearchOpened = false
        isOnSearch = false
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
        isSearchOpened = false
        isSearchDataLoaded = false

        cleanSearchRecyclerView()

    }

    private fun handleSearchQuery() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // this make the default cursor which appears on focus on search EditText appears
                // only after writing words and not if there is no thing written
                searchEditText.isCursorVisible = newText.isNotEmpty()

                if (newText.trim().isNotEmpty()) {
                    query.value = newText
                    makeBlurBackGroundOnSearch()
                    launch {
                        handleQueryChanges()
                    }

                } else {
                    removeBlurBackGroundOnCancelSearch()
                }

                return false
            }
        })
    }

    suspend fun handleQueryChanges() {
        query.debounce(500)
            .filter { query ->
                return@filter query.isNotEmpty()
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
            .collect { saveQuery ->
                if (lastQuery != saveQuery) {
                    lastQuery = saveQuery
                    requestSearchData(saveQuery)
                    resetLastQueryAfterTime()
                }
            }
    }

    private fun resetLastQueryAfterTime() {
        Handler(Looper.getMainLooper()).postDelayed({
            lastQuery = ""
        }, 500)
    }

    private fun makeDelayAfterToastToShowNext() {
        Handler(Looper.getMainLooper()).postDelayed({
            isToastOpened = false
        }, 2000)
    }

    private fun handleErrorNetworkState() {
        isOnNetworkError = true
        binding.avi.smoothToHide()
        val errorMsg = requireActivity().getString(R.string.error_message_Network)
        if (isCharacterDataLoaded || isSearchDataLoaded || isOnSearch) {
            if (!isToastOpened) {
                requireActivity().toast(errorMsg)
                makeDelayAfterToastToShowNext()
            }
            isToastOpened = true
        } else {
            binding.errorMsg.visibility = View.VISIBLE
            binding.errorMsg.text = errorMsg
        }

    }

    private fun handleRequestError() {
        binding.avi.smoothToHide()
        val errorMsg = requireActivity().getString(R.string.error_message_Request)
        if (isCharacterDataLoaded || isSearchDataLoaded || isOnSearch) {
            if (!isToastOpened) {
                requireActivity().toast(errorMsg)
                makeDelayAfterToastToShowNext()
            }
            isToastOpened = true
        } else {
            binding.errorMsg.visibility = View.VISIBLE
            binding.errorMsg.text = errorMsg
        }
    }


    fun requestAllCharactersData(page: Int) {
        if (isOnline(requireActivity())) {
            observeAllCharactersLiveData(page)
        } else {
            handleErrorNetworkState()
        }

    }

    private fun requestSearchData(query: String) {
        if (isOnline(requireActivity())) {
            initSearchRecyclerView()
            observeSearchResultLiveData(query)
        } else {
            handleErrorNetworkState()
        }

    }

    // for getting data from api
    private fun observeAllCharactersLiveData(page: Int) {
        viewModel.fetchAllCharacters(
            Config.PUBLIC_KEY_VALUE,
            hash,
            Config.TIMESTAMP_Value,
            page.toString()
        )

        lastHitPage = page

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
                    queryPageSize = it.data.data.limit
                    isCharacterDataLoaded = true
                    isOnNetworkError = false

                    when {
                        it.data.data.offset < it.data.data.total -> {

                            if (loadingPage == it.data.data.offset) {
                                mAdapter.addToCurrentList(it.data.data.results)
                                loadingPage++
                            }

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


    private fun observeSearchResultLiveData(query: String) {
        viewModel.fetchCharactersDataForSearch(
            Config.PUBLIC_KEY_VALUE,
            hash,
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
                    isSearchDataLoaded
                    mSearchAdapter?.submitList(it.data.data.results)
                    mSearchAdapter?.filter?.filter(query)


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
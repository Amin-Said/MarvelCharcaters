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
import com.amin.marvelcharcaters.utils.Helper.md5
import com.amin.marvelcharcaters.utils.data.ApiResult
import com.amin.marvelcharcaters.utils.extensions.readFile
import com.amin.taskdemo.SearchRecyclerAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeListFragment : Fragment() ,
    CharactersRecyclerAdapter.Interaction,SearchRecyclerAdapter.Interaction{

    private var _binding: FragmentHomeListBinding? = null
    private val binding get() = _binding!!
    lateinit var searchEditText: EditText

    lateinit var mAdapter: CharactersRecyclerAdapter
    lateinit var mSearchAdapter: SearchRecyclerAdapter

    var isSearchOpened = false

    var isLoading = false
    val isLastPage = false
    var isScrolling = false
    var QUERY_PAGE_SIZE = 20
    var loadingPage = 0


    @VisibleForTesting
    val viewModel: HomeListViewModel by viewModels()


    // for testing locally
    fun getOfflineData(): CharacterResponse? {
        val charactersJsonResponseToString = requireActivity().assets.readFile("ch0.json")

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter: JsonAdapter<CharacterResponse> =
            moshi.adapter<CharacterResponse>(CharacterResponse::class.java)

        val response: CharacterResponse? = jsonAdapter.fromJson(charactersJsonResponseToString)
        System.out.println("DEBUG : moshi : $response")
        return response
    }





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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
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

        setupViewForAppBarCustomization()

        initRecyclerView()

        // for search open and close handle
        binding.searchView.setOnSearchClickListener {  changeToolbarOnStartSearch()  }

        binding.cancelBtn.setOnClickListener{changeToolbarOnCancelSearch()}

        binding.searchView.setOnCloseListener{handleOnSearchClose()
            false}

        handleSearchQuery()

        observeAllCharactersLiveData(loadingPage)


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
        binding.searchView.setIconified(true)
    }

    private fun setupViewForAppBarCustomization(){
        // to customize the SearchView EditText
        searchEditText = binding.searchView.findViewById(R.id.search_src_text) as EditText

        // to hide the default close icon in SearchView EditText
        val closeBtn: ImageView =
            binding.searchView.findViewById(R.id.search_close_btn) as ImageView
        closeBtn.setEnabled(false)
        closeBtn.setImageDrawable(null)
    }

    private fun handleOnSearchClose(){
        binding.searchView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        binding.appLogo.visibility = View.VISIBLE
    }

    private fun handleSearchQuery(){
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
//                    getOfflineData()?.data?.results?.let { mSearchAdapter.submitList(it) }

                    observeAllCharactersLiveData(newText)


                } else {
                    binding.blurBehindLayout.visibility = View.GONE
                    binding.blurBehindLayout.viewBehind = null

                }

                return false
            }
        })
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
        println("DEBUG in init")
        binding.searchRV.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            mSearchAdapter = SearchRecyclerAdapter(this@HomeListFragment)
            adapter = mSearchAdapter
        }
    }

    override fun onSearchItemSelected(position: Int, item: CharacterResult) {
        // not used for now
    }

    override fun onCharacterItemSelected(position: Int, item: CharacterResult) {
        if (isSearchOpened) changeToolbarOnCancelSearch()
        val action =
            HomeListFragmentDirections.actionGoToDetails(
                item
            )
        findNavController().navigate(action)
    }

    private fun observeAllCharactersLiveData(page:Int) {
        val currentTimestamp = System.currentTimeMillis()/1000
        val input = currentTimestamp.toString()+ Config.PRIVATE_KEY_VALUE+ Config.PRIVATE_KEY_VALUE
        val hash = md5(input)
//        viewModel.fetchAllCharacters(Config.PUBLIC_KEY_VALUE,hash,currentTimestamp.toString() ,page.toString())
        viewModel.fetchAllCharacters(Config.PUBLIC_KEY_VALUE,Config.HASH_Value,Config.TIMESTAMP_Value.toString() ,page.toString())

        viewModel.result.observe(viewLifecycleOwner) {
            when(it){
                ApiResult.Loading->{

                }
                is ApiResult.Error->{


                }
                is ApiResult.Success->{
                    QUERY_PAGE_SIZE = it.data.data.limit
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
            if(it){

            }
        }
    }


    private fun observeAllCharactersLiveData(query:String) {
        viewModel.fetchCharactersDataForSearch(Config.PUBLIC_KEY_VALUE,Config.HASH_Value,Config.TIMESTAMP_Value.toString() ,query)

        viewModel.searchResult.observe(viewLifecycleOwner) {
            when(it){
                ApiResult.Loading->{

                }
                is ApiResult.Error->{

                }
                is ApiResult.Success->{
                    initSearchRecyclerView()
                    mSearchAdapter.submitList(it.data.data.results)
                    mSearchAdapter.filter.filter(query)


                }
            }

        }

        viewModel.isNetworkError.observe(viewLifecycleOwner) {
            if(it){

            }
        }
    }




}
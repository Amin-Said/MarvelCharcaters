package com.amin.marvelcharcaters.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amin.marvelcharcaters.R
import com.amin.marvelcharcaters.adapter.CharactersRecyclerAdapter
import com.amin.marvelcharcaters.databinding.FragmentHomeListBinding
import com.amin.marvelcharcaters.model.*
import com.amin.marvelcharcaters.utils.readFile
import com.amin.taskdemo.SearchRecyclerAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class HomeListFragment : Fragment() ,
    CharactersRecyclerAdapter.Interaction,SearchRecyclerAdapter.Interaction{

    private var _binding: FragmentHomeListBinding? = null
    private val binding get() = _binding!!
    lateinit var searchEditText: EditText

    lateinit var mAdapter: CharactersRecyclerAdapter
    lateinit var mSearchAdapter: SearchRecyclerAdapter

    var isSearchOpened = false


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




    var isLoading = false
    val isLastPage = false
    var isScrolling = true
    var QUERY_PAGE_SIZE = 20
    var page = 0

    val scrollListener = object : RecyclerView.OnScrollListener() {
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

            if (isAtLastItem) {
                getOfflineData()?.data?.results?.let { mAdapter.addToCurrentList(it) }
                isScrolling = false
            }
            else {

//                binding.recyclerView.setPadding(0, 0, 0, 0)
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
        getOfflineData()?.data?.results?.let { mAdapter.addToCurrentList(it) }

        // for search open and close handle
        binding.searchView.setOnSearchClickListener {  changeToolbarOnStartSearch()  }

        binding.cancelBtn.setOnClickListener{changeToolbarOnCancelSearch()}

        binding.searchView.setOnCloseListener{handleOnSearchClose()
            false}

        handleSearchQuery()


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
                    initSearchRecyclerView()
                    getOfflineData()?.data?.results?.let { mSearchAdapter.submitList(it) }
                    mSearchAdapter.filter.filter(newText)


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
    }

    override fun onCharacterItemSelected(position: Int, item: CharacterResult) {
        if (isSearchOpened) changeToolbarOnCancelSearch()
        val action = HomeListFragmentDirections.actionGoToDetails(item)
        findNavController().navigate(action)
    }

}
package com.vinaye.githubuserapplication.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vinaye.githubuserapplication.R
import com.vinaye.githubuserapplication.adapter.UserListAdapter
import com.vinaye.githubuserapplication.data.model.User
import com.vinaye.githubuserapplication.databinding.FragmentExploreBinding
import com.vinaye.githubuserapplication.util.ResourceEvent
import com.vinaye.githubuserapplication.view.activities.UserDetailActivity
import com.vinaye.githubuserapplication.viewmodel.ExploreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment(), UserListAdapter.ItemClickCallback {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExploreViewModel by viewModels()
    private var userListAdapter: UserListAdapter? = null
    private var lastQuery: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // initialize   user adapter
        userListAdapter = UserListAdapter()
        //item click  listener   user adapter
        userListAdapter?.setOnItemListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set  ui
        setUI()

        // collect diff  states
        collectState()
    }

    private fun setUI() {
        binding.apply {

            setupInitialUI()

            viewModel.lastQuery.observe(requireActivity()) { query ->
                lastQuery = query
            }

            viewModel.searchResults.observe(requireActivity()) { users ->
                if (users.isEmpty())
                    setupListEmptyUI()
                else {
                    userListAdapter?.submitList(users)
                    setupSuccessUI()
                }
            }
        }
    }

    // set  up empty list
    private fun setupListEmptyUI() {
        binding.apply {
            tvExploreNotFoundFeedback.visibility = View.VISIBLE
            tvExploreUserListLabel.visibility = View.GONE
            recyclerviewExplore.visibility = View.GONE
            llIllustrationContainer.visibility = View.GONE
        }
    }

    // set  up  failure  ui
    private fun setupFailureUI() {
        binding.apply {
            tvExploreUserListLabel.visibility = View.GONE
            recyclerviewExplore.visibility = View.GONE
            llIllustrationContainer.visibility = View.GONE
            tvExploreNotFoundFeedback.visibility = View.GONE
            tvExploreErrorMessage.visibility = View.VISIBLE
            tvExploreErrorMessageDescription.visibility = View.VISIBLE
        }
    }

    // set  up  success  ui
    private fun setupSuccessUI() {
        binding.apply {
            tvExploreUserListLabel.visibility = View.VISIBLE
            recyclerviewExplore.visibility = View.VISIBLE
            llIllustrationContainer.visibility = View.GONE
            tvExploreNotFoundFeedback.visibility = View.GONE
            tvExploreErrorMessage.visibility = View.GONE
            tvExploreErrorMessageDescription.visibility = View.GONE
        }
    }

    // set  up  initial  ui
    private fun setupInitialUI() {
        binding.apply {

            svExploreSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        searchUsers(it)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
            // search container click
            mcardExploreSearchIconContainer.setOnClickListener {
                svExploreSearch.query?.toString()?.let { query -> searchUsers(query) }
            }

            recyclerviewExplore.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = userListAdapter
            }
            // swipe to refresh color schemes
            swipeRefreshExplore.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), R.color.purple_700),
                ContextCompat.getColor(requireContext(), R.color.teal_200),
                ContextCompat.getColor(requireContext(), R.color.teal_700)
            )

            recyclerviewExplore.visibility = View.GONE
            llIllustrationContainer.visibility = View.VISIBLE
            tvExploreErrorMessage.visibility = View.GONE
            tvExploreErrorMessageDescription.visibility = View.GONE

            swipeRefreshExplore.setOnRefreshListener {
                searchUsers(lastQuery)
            }
        }
    }

    // search users
    private fun searchUsers(query: String) {
        showSwipeRefreshLayout(false)
        setSearchable(true)

        if (query.isEmpty()) {
            return
        }

        viewModel.searchUsers(query)
    }

    // set searchable config
    private fun setSearchable(state: Boolean) {
        binding.apply {
            svExploreSearch.isEnabled = state
            svExploreSearch.isSubmitButtonEnabled = state
            svExploreSearch.isFocusableInTouchMode = state

            if (state)
                svExploreSearch.requestFocus()
            else
                svExploreSearch.clearFocus()
        }
    }

    //show  swipeRefreshLayout
    private fun showSwipeRefreshLayout(state: Boolean) {
        binding.swipeRefreshExplore.isRefreshing = state
    }

    private fun collectState() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                when (state) {
                    is ResourceEvent.Success<*> -> {
                        showSwipeRefreshLayout(false)
                        setupSuccessUI()
                        setSearchable(true)
                    }
                    is ResourceEvent.Failure -> {
                        showSwipeRefreshLayout(false)
                        setupFailureUI()
                        setSearchable(true)
                    }
                    is ResourceEvent.Loading -> {
                        showSwipeRefreshLayout(true)
                        setSearchable(false)
                        binding.tvExploreNotFoundFeedback.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
    }

    //  details click event
    override fun onDetailIconClick(user: User) {
        Intent(requireActivity(), UserDetailActivity::class.java).run {
            putExtra(UserDetailActivity.USER_EXTRAS, user)
            requireActivity().startActivity(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null // avoiding memory leaks
        userListAdapter = null
    }
}
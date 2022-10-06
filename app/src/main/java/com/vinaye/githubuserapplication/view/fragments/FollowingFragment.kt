package com.vinaye.githubuserapplication.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vinaye.githubuserapplication.R
import com.vinaye.githubuserapplication.adapter.UserListAdapter
import com.vinaye.githubuserapplication.data.model.User
import com.vinaye.githubuserapplication.util.ResourceEvent
import com.vinaye.githubuserapplication.view.activities.UserDetailActivity
import com.vinaye.githubuserapplication.viewmodel.FollowingViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FollowingFragment : Fragment(R.layout.fragment_follows),
    UserListAdapter.ItemClickCallback {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressbar: ProgressBar
    private lateinit var tvFeedbackMessage: TextView
    private var usersAdapter: UserListAdapter? = null
    private val viewModel: FollowingViewModel by viewModels()

    companion object {
        private const val ARG_USER = "USER"

        // handle arguments
        fun newInstance(user: User): FollowingFragment {
            val args = Bundle().apply {
                putParcelable(ARG_USER, user)
            }
            return FollowingFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //initialize  adapters
        usersAdapter = UserListAdapter()
        usersAdapter?.setOnItemListener(this)

        // handle arguments
        arguments?.getParcelable<User>(ARG_USER)?.let { user ->
            viewModel.getFollowingOfUser(user.username)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set up ui
        setupUI(view)
        // collect  state
        collectFollowingStateFlow()
    }

    override fun onDestroy() {
        super.onDestroy()
        usersAdapter = null
    }

    private fun setupUI(view: View) {
        progressbar = view.findViewById(R.id.progressbar_follows)
        recyclerView = view.findViewById(R.id.recyclerview_following)
        tvFeedbackMessage = view.findViewById(R.id.tv_follows_feedback_message)
        setProgressbarStatus(true)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = usersAdapter
        }

        viewModel.followings.observe(requireActivity()) { users ->
            usersAdapter?.submitList(users)
        }
    }

    private fun collectFollowingStateFlow() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                when (state) {
                    is ResourceEvent.Success<*> -> {
                        setProgressbarStatus(false)
                        setFeedbackMessageStatus(false)
                    }
                    is ResourceEvent.Failure -> {
                        setProgressbarStatus(false)
                        tvFeedbackMessage.text = resources.getString(R.string.error_message)
                        setFeedbackMessageStatus(true)
                    }
                    is ResourceEvent.Loading -> {
                        setProgressbarStatus(true)
                        setFeedbackMessageStatus(false)
                    }
                    else -> Unit
                }
            }
        }
    }

    // set progress bar
    private fun setProgressbarStatus(state: Boolean) {
        progressbar.visibility = when (state) {
            true -> View.VISIBLE
            else -> View.GONE
        }
    }

    // set   feedback
    private fun setFeedbackMessageStatus(state: Boolean) {
        tvFeedbackMessage.visibility = when (state) {
            true -> View.VISIBLE
            else -> View.GONE
        }
    }

    // set   details click
    override fun onDetailIconClick(user: User) {
        Intent(requireActivity(), UserDetailActivity::class.java).run {
            putExtra(UserDetailActivity.USER_EXTRAS, user)

            requireActivity().startActivity(this)
        }
    }
}
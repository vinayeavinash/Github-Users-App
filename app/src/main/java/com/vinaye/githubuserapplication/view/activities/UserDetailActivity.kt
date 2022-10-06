package com.vinaye.githubuserapplication.view.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.vinaye.githubuserapplication.R
import com.vinaye.githubuserapplication.adapter.FollowingFollowersPagerAdapter
import com.vinaye.githubuserapplication.data.model.User
import com.vinaye.githubuserapplication.databinding.ActivityUserDetailBinding
import com.vinaye.githubuserapplication.util.DataConverter
import com.vinaye.githubuserapplication.util.ResourceEvent
import com.vinaye.githubuserapplication.viewmodel.UserDetailViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailBinding
    private lateinit var mAdapter: FollowingFollowersPagerAdapter
    private val viewModel: UserDetailViewModel by viewModels()

    companion object {
        const val USER_EXTRAS = "USER_EXTRAS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userExtra = intent.getParcelableExtra<User>(USER_EXTRAS)
        if (userExtra != null) {
            viewModel.getUser(userExtra.username)
            //set actionBar
            setupActionBar()
            //set ui
            setupUI()
        } else {
            finish()
            // handle error
            showFeedback()
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun setupUI() {
        binding.apply {

            viewModel.user.observe(this@UserDetailActivity) { user ->

                Glide.with(this@UserDetailActivity)
                    .load(user.avatarUrl)
                    .apply(RequestOptions.placeholderOf(R.drawable.bg_placeholder_images))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(sivUserDetailAvatar)

                tvUserDetailFullname.text = user.fullName
                tvUserDetailUsername.text = user.username
                tvUserDetailBio.text = user.bio

                // also set pager adapter
                mAdapter = FollowingFollowersPagerAdapter(this@UserDetailActivity, user)
                viewPagerUserDetail.adapter = mAdapter

                TabLayoutMediator(tabLayoutUserDetail, viewPagerUserDetail) { tab, position ->
                    tab.text =
                        resources.getString(FollowingFollowersPagerAdapter.TAB_TITLES[position])
                }.attach()


                // swipe refresh
                swipeRefreshUserDetail.setOnRefreshListener {
                    viewModel.getUser(user.username)
                }
            }
            //  manage data
            lifecycleScope.launchWhenStarted {
                viewModel.state.collect { event ->
                    when (event) {
                        is ResourceEvent.Success<*> -> {
                            showSwipeRefreshLayout(false)
                            setupSuccesUI()
                        }
                        is ResourceEvent.Failure -> {
                            showSwipeRefreshLayout(false)
                            setupFailureUI()
                        }
                        is ResourceEvent.Loading -> showSwipeRefreshLayout(true)
                        else -> showSwipeRefreshLayout(false)
                    }
                }
            }
        }
    }

    // handle  success response
    private fun setupSuccesUI() {
        binding.apply {
            viewPagerUserDetail.visibility = View.VISIBLE
            tabLayoutUserDetail.visibility = View.VISIBLE
        }
    }

    // handle   failiure  response
    private fun setupFailureUI() {
        binding.apply {
            sivUserDetailAvatar.setImageResource(R.drawable.bg_placeholder_images)
            tvUserDetailFullname.text = resources.getString(R.string.error_message_label)
            tvUserDetailUsername.text = DataConverter.STRING_NULL
            viewPagerUserDetail.visibility = View.GONE
            tabLayoutUserDetail.visibility = View.GONE
        }
    }

    // setup actionbar
    private fun setupActionBar() {
        supportActionBar?.apply {
            title = getString(R.string.action_bar_title_user_details)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_left)
        }
    }

    // check  swipe  widget status
    private fun showSwipeRefreshLayout(state: Boolean) {
        binding.swipeRefreshUserDetail.isRefreshing = state
    }

    // show feedback
    private fun showFeedback() {
        Toast.makeText(
            applicationContext,
            resources.getString(R.string.redirect_message),
            Toast.LENGTH_LONG
        ).show()
    }

    // handle navigation
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
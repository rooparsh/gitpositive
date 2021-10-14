package org.acmvit.gitpositive.ui.follower

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.acmvit.gitpositive.R
import org.acmvit.gitpositive.databinding.BottomFollowersBinding
import org.acmvit.gitpositive.util.getColorStr

@AndroidEntryPoint
class FollowerDialog : BottomSheetDialogFragment() {

    private val viewModel: FollowerViewModel by viewModels()

    private var _binding: BottomFollowersBinding? = null
    private val binding: BottomFollowersBinding
        get() {
            return _binding!!
        }

    private var _view: View? = null

    companion object {
        private const val KEY_USERNAME = "userName"

        fun newInstance(userName: String) = FollowerDialog().apply {
            arguments = Bundle().apply {
                putString(KEY_USERNAME, userName)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_view == null) {
            _binding = BottomFollowersBinding.inflate(inflater, container, false)
            _view = binding.view
        }
        return _view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            bottomTop.text = Html.fromHtml(
                getColorStr("Your ", "#6CFF54") + getColorStr(
                    "Followers", this@FollowerDialog.context?.getColor(
                        R.color.text_color
                    ).toString()
                )
            )
        }
        observeFollowingList()
        viewModel.getUserFollowers(userName = this.arguments?.getString(KEY_USERNAME, "").orEmpty())
    }

    private fun observeFollowingList() {
        viewModel.viewState.observe(viewLifecycleOwner) {
            it?.let { viewState ->
                when (viewState) {
                    is FollowerViewModel.ViewState.Error -> showToast(viewState.message)
                    FollowerViewModel.ViewState.Loading -> {}
                    is FollowerViewModel.ViewState.Success -> {
                        with(binding.recyclerView) {
                            layoutManager = LinearLayoutManager(this@FollowerDialog.context)
                            adapter = FollowersAdapter(viewState.followingList)
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }

    override fun dismiss() {
        super.dismiss()
        _view = null
    }
}
package org.acmvit.gitpositive.ui.following

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
import org.acmvit.gitpositive.databinding.BottomFollowingBinding
import org.acmvit.gitpositive.util.getColorStr

@AndroidEntryPoint
class FollowingDialog : BottomSheetDialogFragment() {

    private val viewModel: FollowingViewModel by viewModels()

    private var _binding: BottomFollowingBinding? = null
    private val binding: BottomFollowingBinding
        get() {
            return _binding!!
        }

    private var _view: View? = null

    companion object {
        private const val KEY_USERNAME = "userName"

        fun newInstance(userName: String) = FollowingDialog().apply {
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
            _binding = BottomFollowingBinding.inflate(inflater, container, false)
            _view = binding.view
        }
        return _view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            bottomTop.text = Html.fromHtml(
                getColorStr("Your ", "#6CFF54") + getColorStr(
                    "Following", this@FollowingDialog.context?.getColor(
                        R.color.text_color
                    ).toString()
                )
            )
        }
        observeFollowingList()
        viewModel.getUserFollowing(userName = this.arguments?.getString(KEY_USERNAME, "").orEmpty())
    }

    private fun observeFollowingList() {
        viewModel.viewState.observe(viewLifecycleOwner) {
            it?.let { viewState ->
                when (viewState) {
                    is FollowingViewModel.ViewState.Error -> showToast(viewState.message)
                    FollowingViewModel.ViewState.Loading -> {}
                    is FollowingViewModel.ViewState.Success -> {
                        with(binding.recyclerView) {
                            layoutManager = LinearLayoutManager(this@FollowingDialog.context)
                            adapter = FollowingAdapter(viewState.followingList)
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
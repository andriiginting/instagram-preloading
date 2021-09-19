package com.andriiginting.vids.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriiginting.vids.R
import com.andriiginting.vids.databinding.DialogAddPostUrlBinding
import com.andriiginting.vids.dialog.fields.DialogListener
import com.andriiginting.vids.dialog.fields.UrlFieldAdapter
import com.andriiginting.vids.feeds.FeedVideo
import com.andriiginting.vids.hideKeyboard
import com.andriiginting.vids.visible

class AddPostDialog : DialogListener, DialogFragment() {
    private var bindingInst: DialogAddPostUrlBinding? = null
    private val binding get() = bindingInst!!

    private val fieldAdapter by lazy { UrlFieldAdapter(this) }

    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(UrlCollectionViewModel::class.java)
    }

    companion object {
        val TAG = AddPostDialog::class.java.simpleName
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bindingInst = DialogAddPostUrlBinding.inflate(LayoutInflater.from(context))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivCloseDialog.setOnClickListener {
            dismissAllowingStateLoss()
            fieldAdapter.clear()
        }
        setupView()
        observeButton()

        setupFieldRecyclerview()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null && dialog?.window != null) {
            with(dialog?.window) {
                this?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingInst = null
    }

    private fun setupView() {
        binding.btnPostAllUrl.setOnClickListener {
            viewModel.postVideos(fieldAdapter.getAllUrl())
            fieldAdapter.clear()
            dismissAllowingStateLoss()
            it.hideKeyboard()
        }

        binding.btnAddMore.setOnClickListener {
            fieldAdapter.addMore(Unit)
        }
    }

    private fun observeButton() {
        viewModel.submitButtonState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is SubmitButtonState.Disabled -> {
                    binding.btnPostAllUrl.apply {
                        isEnabled = false
                        text = getString(R.string.fab_add_post_title)
                        alpha = 0.2F
                    }

                }
                is SubmitButtonState.Enabled -> {
                    binding.btnPostAllUrl.apply {
                        isEnabled = true
                        text = getString(R.string.feed_post_all_url_btn_title)
                        alpha = 1F
                    }
                    binding.btnAddMore.visible()
                }
            }
        })
    }

    private fun setupFieldRecyclerview() {
        binding.rvUrlVideo.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = fieldAdapter
        }
        fieldAdapter.addMore(Unit)
    }

    override fun onIsHiddenAddMoreButton(isHidden: Boolean) {
        binding.btnAddMore.isInvisible = isHidden
    }

    override fun observeField(data: FeedVideo) {
        viewModel.populateUrlCollection(data)
    }
}
package com.example.lostark.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.lostark.R
import com.example.lostark.data.LoadingState
import com.example.lostark.data.db.entity.EventEntity
import com.example.lostark.databinding.FragmentEventBinding
import com.example.lostark.presentation.detailEventRecyclerView.DetailEventAdapter

class EventFragment : Fragment(R.layout.fragment_event) {
    companion object {
        private const val STATE_LOADING = 0
        private const val STATE_DATA = 1
        private const val STATE_ERROR = 2

        fun newInstance(): EventFragment {
            return EventFragment()
        }
    }

    private var fragmentListener: FragmentListener? = null
    private val binding by viewBinding(FragmentEventBinding::bind)
    private val viewModel: EventViewModel by viewModels()
    private val detailEventAdapter = DetailEventAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentListener) {
            fragmentListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.subscribeToEvents(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadingStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoadingState.Loading -> setStateLoading()
                is LoadingState.Data -> {
                    if (state.data.isNotEmpty()) {
                        setStateData(state.data)
                    } else
                        setStateEmpty()
                }
                is LoadingState.Error -> setStateError(state.error)
            }
        }
        binding.eventRecyclerView.adapter = detailEventAdapter
        detailEventAdapter.onItemClick = {
            viewModel.showAlert(requireContext(), layoutInflater, it)
        }
    }

    override fun onDetach() {
        fragmentListener = null
        super.onDetach()
    }

    private fun setStateLoading() {
        binding.viewFlipper.displayedChild = STATE_LOADING
        binding.buttonUpdate.visibility = View.GONE
    }

    private fun setStateData(eventList: List<EventEntity>) {
        binding.viewFlipper.displayedChild = STATE_DATA
        binding.buttonUpdate.visibility = View.GONE
        detailEventAdapter.setItems(eventList)
    }

    private fun setStateError(e: Exception) {
        binding.textViewError.text = e.message
        binding.viewFlipper.displayedChild = STATE_ERROR
        binding.buttonUpdate.setOnClickListener {

        }
    }

    private fun setStateEmpty() {
        binding.buttonUpdate.visibility = View.GONE
        binding.textViewError.text = getString(R.string.event_fragment_empty_state)
        binding.viewFlipper.displayedChild = STATE_ERROR

    }

}
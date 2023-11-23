package com.example.lostark.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.lostark.R
import com.example.lostark.data.model.calendar.Event
import com.example.lostark.databinding.BottomSheetLayoutBinding
import com.example.lostark.presentation.datePickerRecyclerView.Divider
import com.example.lostark.presentation.timeRecyclerView.TimeAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(BottomSheetLayoutBinding::bind)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.bottom_sheet_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val event: Event = arguments?.getParcelable(CalendarFragment.EXTRA_EVENT) ?: Event()
        binding.titleTextView.text = event.name
        val adapter = TimeAdapter()
        binding.timeRecyclerView.adapter = adapter
        ContextCompat.getDrawable(requireContext(), R.drawable.divider_vertical)?.also {
            binding.timeRecyclerView.addItemDecoration(Divider(it))
        }
        adapter.setItems(event.times)
        adapter.onItemClick = { time ->
            parentFragmentManager.setFragmentResult(CalendarFragment.REQUEST_KEY, Bundle()
                .apply { putString(CalendarFragment.EXTRA_TIME, time) })
        }

    }
}
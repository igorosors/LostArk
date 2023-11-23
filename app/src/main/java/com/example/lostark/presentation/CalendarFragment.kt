package com.example.lostark.presentation

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.animation.*
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.lostark.R
import com.example.lostark.data.LoadingState
import com.example.lostark.data.db.entity.DayEntity
import com.example.lostark.data.db.entity.EventEntity
import com.example.lostark.data.model.calendar.Day
import com.example.lostark.databinding.FragmentCalendarBinding
import com.example.lostark.presentation.categoryRecyclerView.CategoryAdapter
import com.example.lostark.presentation.datePickerRecyclerView.DayAdapter
import com.example.lostark.presentation.datePickerRecyclerView.Divider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment(R.layout.fragment_calendar) {
    companion object {
        private const val STATE_LOADING = 0
        private const val STATE_DATA = 1
        private const val STATE_ERROR = 2
        const val EXTRA_EVENT = "extra_event"
        const val EXTRA_TIME = "extra_time"
        const val REQUEST_KEY = "request_key"

        fun newInstance(): CalendarFragment {
            return CalendarFragment()
        }
    }

    private var fragmentListener: FragmentListener? = null
    private val binding by viewBinding(FragmentCalendarBinding::bind)
    private val viewModel: CalendarViewModel by viewModels()
    private val toolbar by lazy { binding.toolbar }
    private val categoryAdapter = CategoryAdapter()
    private val dayAdapter = DayAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentListener) {
            fragmentListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.subscribeToCalendar(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.categoryRecyclerView.adapter = null
        binding.datePickerRecyclerView.adapter = null
        viewModel.loadingStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoadingState.Loading -> setStateLoading()
                is LoadingState.Data ->
                    if (state.data.dayList != null) {
                        setStateData(state.data)
                    } else
                        setStateEmpty()
                is LoadingState.Error -> setStateError(state.error)
            }
        }
        toolbar.setOnMenuItemClickListener {
            viewModel.clear(requireContext())
            true
        }
        ContextCompat.getDrawable(requireContext(), R.drawable.divider_vertical)?.also {
            binding.datePickerRecyclerView.addItemDecoration(Divider(it))
        }
        binding.datePickerRecyclerView.adapter = dayAdapter
        binding.categoryRecyclerView.adapter = categoryAdapter
        categoryAdapter.onItemClick = {layout, imageView ->
            if ((layout.layoutParams.height == 1) ) {
                //layout.visibility = View.VISIBLE
                viewModel.expand(layout)
                imageView.startAnimation(
                    AnimationUtils.loadAnimation(requireContext(), R.anim.open_animation).apply { fillAfter = true }
                )
            } else {
                //layout.visibility = View.GONE
                viewModel.collapse(layout)
                imageView.startAnimation(
                    AnimationUtils.loadAnimation(requireContext(), R.anim.close_animation).apply { fillAfter = true }
                )
            }
        }
        viewModel.createChannel()
    }

    override fun onDetach() {
        fragmentListener = null
        super.onDetach()
    }

    private fun setStateLoading() {
        binding.viewFlipper.displayedChild = STATE_LOADING
        binding.buttonUpdate.visibility = View.GONE
    }


    private fun setStateData(dayEntity: DayEntity) {
        val monthOfYearList = listOf(
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"
        )
        binding.viewFlipper.displayedChild = STATE_DATA
        binding.buttonUpdate.visibility = View.GONE
        val typeToken = object : TypeToken<MutableList<Day>>() {}.type
        val dayList: MutableList<Day> = GsonBuilder().create().fromJson(dayEntity.dayList, typeToken)
        //viewModel.deletePastDays(dayList)


        dayAdapter.setItems(dayList)


        //Открытие первого дня при старте приложения
        var currentDayPosition = 0
        var currentDay = dayList[0].apply { isPicked = true }
        binding.dayOfWeekTextView.text =
            StringBuilder().append(currentDay.name).append(" ").append(currentDay.number).append(" ")
                .append(monthOfYearList[currentDay.monthNumber - 1]).toString()
        categoryAdapter.setItems(dayList[0].categoryList)

        dayAdapter.onItemClick = { day, number ->
            currentDay.isPicked = false
            dayAdapter.notifyItemChanged(currentDayPosition)
            currentDay = dayList[number]
            currentDay.isPicked = true
            currentDayPosition = number
            dayAdapter.notifyItemChanged(number)
            categoryAdapter.setItems(day.categoryList)
            val stringBuilder = StringBuilder()
            stringBuilder.append(day.name).append(" ").append(day.number).append(" ").append(monthOfYearList[day.monthNumber - 1])
            binding.dayOfWeekTextView.text = stringBuilder.toString()
        }
        categoryAdapter.onNestedItemClick = { _, event ->
            val bottomFragment = BottomFragment()
            bottomFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog)
            bottomFragment.arguments = Bundle().apply {
                putParcelable(EXTRA_EVENT, event)
            }
            childFragmentManager.setFragmentResultListener(REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
                val time = bundle.getString(EXTRA_TIME)
                val timeList = time?.split("-")?.get(0)?.split(":")
                val hh = timeList?.get(0)
                val mm = timeList?.get(1)
                val dateString = "${currentDay.monthNumber} ${currentDay.number} $hh:$mm:00 ${currentDay.year}"
                val sdf = SimpleDateFormat("MM dd HH:mm:ss yyyy", Locale.getDefault())
                val inflateView = layoutInflater.inflate(R.layout.number_picker, null)
                val numbers = arrayOfNulls<String>(60)
                for (i in numbers.indices) {
                    numbers[i] = i.toString()
                }
                val numberPicker = inflateView.findViewById<NumberPicker>(R.id.numberPicker).apply {
                    minValue = 0
                    maxValue = numbers.size - 1
                    displayedValues = numbers
                    wrapSelectorWheel = false
                }
                sdf.parse(dateString)?.time.also {
                    if (it != null) {
                        val alertDialog =
                            AlertDialog.Builder(object : ContextThemeWrapper(context, R.style.AlertDialogTheme) {})
                                .apply {
                                    val customTitle = layoutInflater.inflate(R.layout.custom_title, null)
                                    (customTitle as TextView).text = StringBuilder()
                                        .append(event.name).append("\n").append(time)
                                    setView(inflateView)
                                    setCustomTitle(customTitle)
                                    setMessage(getString(R.string.notification_alert_message))
                                    setNegativeButton(getString(R.string.notification_alert_negative_button)) { _, _ -> }
                                    setPositiveButton(getString(R.string.notification_alert_positive_button)) { _, _ ->
                                        val id = System.currentTimeMillis().toInt()
                                        val timeInMs = it - (numberPicker.value * 60000).toLong()
                                        viewModel.startTimer(timeInMs, "$time ${event.name}", id, view, EventEntity(
                                            id = id,
                                            name = event.name,
                                            time = time,
                                            gearScore = event.gearScore,
                                            day = currentDay.number,
                                            month = currentDay.monthNumber,
                                        ))
                                    }
                                    create()
                                }
                        alertDialog.show()
                    }
                    else {
                        view?.apply {
                            val snackbar = Snackbar.make(this, context.getString(R.string.snackbar_error), Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        }
                    }
                }
                bottomFragment.dismiss()
            }
            bottomFragment.show(childFragmentManager, "tag")
        }
    }

    private fun setStateError(e: Exception) {
        binding.viewFlipper.displayedChild = STATE_ERROR
        if ((e is ConnectException) || (e is UnknownHostException) || (e is SocketTimeoutException)) {
            binding.textViewError.text = getString(R.string.internet_exception_text)
        } else {
            binding.textViewError.text = getString(R.string.any_exception_text)
        }
        binding.buttonUpdate.visibility = View.VISIBLE
        binding.buttonUpdate.setOnClickListener {
            viewModel.getWeb(requireContext())
        }
    }

    private fun setStateEmpty() {
        binding.viewFlipper.displayedChild = STATE_DATA
        binding.buttonUpdate.visibility = View.GONE
    }

}
package com.example.lostark.presentation

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.*
import com.example.lostark.R
import com.example.lostark.data.LoadingState
import com.example.lostark.data.db.DatabaseClient
import com.example.lostark.data.db.entity.EventEntity
import com.example.lostark.data.model.receiver.AlarmReceiver
import kotlinx.coroutines.launch

class EventViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _loadingStateLiveData = MutableLiveData<LoadingState<List<EventEntity>>>()
    val loadingStateLiveData: LiveData<LoadingState<List<EventEntity>>> = _loadingStateLiveData
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun subscribeToEvents(context: Context) {
        viewModelScope.launch {
            try {
                _loadingStateLiveData.postValue(LoadingState.Loading())
                DatabaseClient.getInstance(context).getEventFlow().collect {
                    _loadingStateLiveData.postValue(LoadingState.Data(it))
                }
            } catch (e: Exception) {
                _loadingStateLiveData.postValue(LoadingState.Error(e))
            }
        }
    }

    private fun deleteEvent(context: Context, eventEntity: EventEntity) {
        viewModelScope.launch {
            DatabaseClient.getInstance(context).deleteEvent(eventEntity)
        }
    }

    private fun createPendingIntent(requestCode: Int): PendingIntent {
        return PendingIntent.getBroadcast(
            getApplication(),
            requestCode,
            Intent(app, AlarmReceiver::class.java).putExtra(CalendarFragment.REQUEST_KEY, requestCode),
            if (Build.VERSION.SDK_INT >= 23) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }

    fun showAlert(context: Context, layoutInflater: LayoutInflater, eventEntity: EventEntity) {
        val alertDialog =
            AlertDialog.Builder(object : ContextThemeWrapper(context, R.style.AlertDialogTheme) {})
                .apply {
                    val customTitle = layoutInflater.inflate(R.layout.custom_title, null)
                    (customTitle as TextView).text = context.getString(R.string.alert_event_title)
                    setCustomTitle(customTitle)
                    setNegativeButton(context.getString(R.string.alert_event_negative_button)) { _, _ -> }
                    setPositiveButton(context.getString(R.string.alert_event_positive_button)) { _, _ ->
                        alarmManager.cancel(createPendingIntent(eventEntity.id))
                        deleteEvent(context, eventEntity)
                    }
                    create()
                }
        alertDialog.show()
    }

}
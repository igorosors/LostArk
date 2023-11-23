package com.example.lostark.presentation

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import androidx.lifecycle.*
import com.example.lostark.R
import com.example.lostark.data.LoadingState
import com.example.lostark.data.db.DatabaseClient
import com.example.lostark.data.db.entity.DayEntity
import com.example.lostark.data.db.entity.EventEntity
import com.example.lostark.data.gson.deserialize.calendarList.*
import com.example.lostark.data.gson.deserialize.categoryList.CategoryListDeserializer
import com.example.lostark.data.gson.deserialize.eventList.EventListDeserializer
import com.example.lostark.data.model.calendar.*
import com.example.lostark.data.model.receiver.AlarmReceiver
import com.example.lostark.data.remote.TimeApi
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*


class CalendarViewModel(private val app: Application) : AndroidViewModel(app) {

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val _loadingStateLiveData = MutableLiveData<LoadingState<DayEntity>>()
    val loadingStateLiveData: LiveData<LoadingState<DayEntity>> = _loadingStateLiveData

    fun startTimer(time: Long, message: String?, id: Int, view: View?, eventEntity: EventEntity) {
        viewModelScope.launch {
            try {
                val currentTime = TimeApi.apiService.getTime()
                val dateString = with(currentTime) { "$month $day $hour:$minute:$seconds $year" }
                val sdf = SimpleDateFormat("MM dd HH:mm:ss yyyy", Locale.getDefault())
                val currentTimeInMs = sdf.parse(dateString)?.time
                val triggerTime = System.currentTimeMillis() + time - currentTimeInMs!!
                if (triggerTime > System.currentTimeMillis()) {
                    val alarmClockInfo =
                        AlarmManager.AlarmClockInfo(triggerTime, createPendingIntent(message, id, eventEntity))
                    alarmManager.setAlarmClock(alarmClockInfo, createPendingIntent(message, id, eventEntity))
                    saveEvent(app, eventEntity)
                } else {
                    view?.apply {
                        val snackbar = Snackbar.make(this, context.getString(R.string.snackbar_event_gone), Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    }
                }

            } catch (e: Exception) {
                val triggerTime = time + TimeZone.getDefault().rawOffset - 10800000
                if (triggerTime > System.currentTimeMillis()) {
                    val alarmClockInfo =
                        AlarmManager.AlarmClockInfo(triggerTime, createPendingIntent(message, id, eventEntity))
                    alarmManager.setAlarmClock(alarmClockInfo, createPendingIntent(message, id, eventEntity))
                    saveEvent(app, eventEntity)
                } else {
                    view?.apply {
                        val snackbar = Snackbar.make(this, context.getString(R.string.snackbar_event_gone), Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    }
                }
            }
        }
    }

    fun subscribeToCalendar(context: Context) {
        viewModelScope.launch {
            try {
                _loadingStateLiveData.postValue(LoadingState.Loading())
                DatabaseClient.getInstance(context).getCalendarFlow().collect { dayEntity ->
                    if (dayEntity?.dayList != null) {
                        _loadingStateLiveData.postValue(LoadingState.Data(dayEntity))
                    } else {
                        getWeb(context)
                    }
                }
            } catch (e: Exception) {
                _loadingStateLiveData.postValue(LoadingState.Error(e))
            }
        }
    }

    fun getWeb(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val time = TimeApi.apiService.getTime()
                    _loadingStateLiveData.postValue(LoadingState.Loading())
                    val document = Jsoup.connect("https://lostarkcodex.com/ru/eventcalendar").get()
                    // get web data
                    val webData = document.select("div.col").first().html()
                    // get json from web data
                    val stringBuilder = StringBuilder()
                        .append('{')
                        .append(webData.substringAfter('{'))
                    val stringList = stringBuilder.split('\n')

                    val jsonCalendarList = stringList[0].substring(0, stringList[0].length - 2)
                    val jsonEventList = stringList[1].substring(24, stringList[1].length - 2)
                    val jsonCategoryList = stringList[2].substring(22, stringList[2].length - 2)

                    val calendarList = fromJsonCalendarList(jsonCalendarList)
                    val eventList = fromJsonEventList(jsonEventList)
                    val categoryList = fromJsonCategoryList(jsonCategoryList)
                    // 3 json to 1

                    val dateList = MutableList(12) { MutableList(31) { Day() } }
                    var categoryListIterator = 0

                    calendarList.categoryList.forEach { category ->
                        category.code?.toIntOrNull()?.also {
                            categoryList.categoryList.getOrNull(it)?.name.also { name ->
                                if (name != null)
                                    category.code = name.substring(1, name.length - 1)
                            }
                        }
                        category.monthList.forEach { month ->
                            month.dayList.forEach { day ->
                                dateList[month.number - 1][day.number - 1].addCategory(Category(name = category.code))
                                day.gearScoreList.forEach { gearScore ->
                                    gearScore.eventList.forEach { event ->
                                        eventList.eventList.forEach { eventNamed ->
                                            if (event.code.equals(eventNamed.code)) {
                                                event.name = eventNamed.name?.replace("&#39;", "`")
                                                event.gearScore = gearScore.value
                                                dateList[month.number - 1][day.number - 1].categoryList.apply {
                                                    this[this.size - 1].addEvent(event)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        categoryListIterator += 1
                    }
                    val dayList = mutableListOf<Day>()
                    val currentMonth = time.month!! - 1
                    for (j in time.day!! - 1..30) {
                        if (dateList[currentMonth][j].categoryList.isNotEmpty()) {
                            dateList[currentMonth][j].monthNumber = currentMonth + 1
                            dateList[currentMonth][j].number = j + 1
                            dateList[currentMonth][j].year = time.year!!
                            dayList.add(dateList[currentMonth][j])
                        }
                    }
                    for (i in currentMonth + 1..11) {
                        for (j in 0..30) {
                            if (dateList[i][j].categoryList.isNotEmpty()) {
                                dateList[i][j].monthNumber = i + 1
                                dateList[i][j].number = j + 1
                                dateList[i][j].year = time.year!!
                                dayList.add(dateList[i][j])
                            }
                        }
                    }
                    if (currentMonth == 12)
                        for (j in 0..30) {
                            if (dateList[0][j].categoryList.isNotEmpty()) {
                                dateList[0][j].monthNumber = 1
                                dateList[0][j].number = j + 1
                                dateList[0][j].year = time.year!! + 1
                                dayList.add(dateList[0][j])
                            }
                        }
                    // Именование дней
                    val dayOfWeekList =
                        listOf("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
                    val calendar = Calendar.getInstance()
                    calendar.set(time.year!!, time.month - 1, time.day)
                    var dayOfWeekIterator = calendar.get(Calendar.DAY_OF_WEEK) - 1
                    dayList.forEach { day ->
                        day.name = dayOfWeekList[dayOfWeekIterator]
                        dayOfWeekIterator += 1
                        if (dayOfWeekIterator >= 7) dayOfWeekIterator = 0

                    }
                    // final json
                    val gson = GsonBuilder().create()
                    saveCalendar(context, DayEntity(dayList = gson.toJson(dayList)))
                } catch (e: Exception) {
                    _loadingStateLiveData.postValue(LoadingState.Error(e))
                }
            }
        }
    }

    fun clear(context: Context) {
        viewModelScope.launch {
            if ((loadingStateLiveData.value as? LoadingState.Data)?.data != null) {
                DatabaseClient.getInstance(context).clear()
            }
        }
    }

    private fun saveCalendar(context: Context, calendarEntity: DayEntity) {
        viewModelScope.launch {
            DatabaseClient.getInstance(context).saveCalendar(calendarEntity)
        }
    }

    private fun saveEvent(context: Context, eventEntity: EventEntity) {
        viewModelScope.launch {
            DatabaseClient.getInstance(context).saveEvent(listOf(eventEntity))
        }
    }

    private fun fromJsonCategoryList(jsonCategory: String): EventCalendar {
        val gson = GsonBuilder()
            .registerTypeAdapter(EventCalendar::class.java, CategoryListDeserializer())
            .create()
        return gson.fromJson(jsonCategory, EventCalendar::class.java)
    }

    private fun fromJsonEventList(jsonEvent: String): GearScore {
        val gson = GsonBuilder()
            .registerTypeAdapter(GearScore::class.java, EventListDeserializer())
            .create()
        return gson.fromJson(jsonEvent, GearScore::class.java)
    }

    private fun fromJsonCalendarList(jsonCalendar: String): EventCalendar {
        val gson = GsonBuilder()
            .registerTypeAdapter(EventCalendar::class.java, CalendarListDeserializer())
            .registerTypeAdapter(Category::class.java, CalendarDeserializer())
            .registerTypeAdapter(Month::class.java, MonthDeserializer())
            .registerTypeAdapter(Day::class.java, DayDeserializer())
            .registerTypeAdapter(GearScore::class.java, GearScoreDeserializer())
            .create()
        return gson.fromJson(jsonCalendar, EventCalendar::class.java)
    }

    fun expand(view: View) {
        view.measure(0,0)
        view.visibility = View.VISIBLE
        val targetHeight = view.measuredHeight
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                val height = (targetHeight * interpolatedTime).toInt()
                view.layoutParams.height = if (interpolatedTime == 1f) LinearLayout.LayoutParams.WRAP_CONTENT
                else if (height > 0) height
                else 1
                view.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        animation.duration = (targetHeight / view.context.resources.displayMetrics.density).toLong()
        view.startAnimation(animation)

    }

    fun collapse(view: View) {
        val initialHeight = view.measuredHeight
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                val height = (initialHeight - (initialHeight * interpolatedTime)).toInt()
                if (interpolatedTime == 1f) {
                    view.visibility = View.GONE
                    view.layoutParams.height = 1
                }
                else {
                    if (height > 0) view.layoutParams.height = height
                }
                view.requestLayout()
            }
        }
        animation.duration = (initialHeight / view.context.resources.displayMetrics.density).toLong()
        view.startAnimation(animation)
    }

    private fun createPendingIntent(message: String?, requestCode: Int, eventEntity: EventEntity): PendingIntent {
        return PendingIntent.getBroadcast(
            getApplication(),
            requestCode,
            Intent(app, AlarmReceiver::class.java).putExtra(AlarmReceiver.EXTRA_MESSAGE, message)
                .putExtra(AlarmReceiver.EXTRA_ID, requestCode)
                .putExtra(AlarmReceiver.EXTRA_EVENT, eventEntity),
            if (Build.VERSION.SDK_INT >= 23) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                app.getString(R.string.notification_channel_id),
                app.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply { setShowBadge(false) }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            val notificationManager = app.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}
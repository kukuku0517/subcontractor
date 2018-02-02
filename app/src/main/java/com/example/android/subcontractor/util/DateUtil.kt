package com.example.android.subcontractor.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by samsung on 2018-01-19.
 */
class DateUtil {
    companion object {
        fun toString(time: Long): String? {
            val cal = Calendar.getInstance()
            cal.time = Date(time)

            val calNow = Calendar.getInstance()
            calNow.time = Date(System.currentTimeMillis())

            val timeGap = System.currentTimeMillis() - time

            val yearGap = calNow.get(Calendar.YEAR) - cal.get(Calendar.YEAR)
            val monthGap = calNow.get(Calendar.MONTH) - cal.get(Calendar.MONTH)
            val secGap = timeGap / 1000
            val minGap = secGap / 60
            val hourGap = minGap / 60
            val dayGap = hourGap / 24


            if (yearGap > 1 || monthGap > 6) {
                val dateFormat = SimpleDateFormat("yy년 MM월 dd일")
                return dateFormat.format(Date(time))
            }

            if (monthGap % 12 > 1) {
                return "${monthGap}개월 전"
            }
            if (dayGap > 1) {
                return "${dayGap}일 전"
            }
            if (hourGap > 1) {
                return "${hourGap}시간 전"
            }
            if (minGap > 1) {
                return "${minGap}분 전"
            }

            return "${secGap}초 전"

//
//              if (calNow.get(Calendar.HOUR_OF_DAY) - cal.get(Calendar.HOUR_OF_DAY))
//
//
//                    return dateFormat.format(Date(time))
        }
    }
}
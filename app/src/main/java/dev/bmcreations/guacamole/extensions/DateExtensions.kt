package dev.bmcreations.guacamole.extensions

import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.round


class DateExtensions {
    companion object {
        @JvmStatic
        fun formatDate(date: Date?, formatter: String): String? = date?.format(formatter)
    }
}

val now: Date = Date()

val Date.daysFromToday: Long
    get() {
        val msDiff = now.time - this.time
        return TimeUnit.MILLISECONDS.toDays(msDiff)
    }

fun Date.format(format: String): String = SimpleDateFormat(format, Locale.US).format(this)

fun Int.toDateFromMidnight(base: Date): Date = base.startOfDay().plusMinutes(this)

fun Date.isSameDayAs(other: Date?): Boolean {
    if (other == null) {
        return false
    }

    return daysBetween(other) == 0
}

fun Date.startOfWeek(): Date {
    val start = startOfDay()

    return Calendar.getInstance().apply {
        this.time = start
        this.setMin(Calendar.DAY_OF_WEEK)
    }.time
}

fun Date.startOfDay(): Date {
    return Calendar.getInstance().apply {
        this.time = this@startOfDay
        this.setMin(Calendar.HOUR_OF_DAY)
        this.setMin(Calendar.MINUTE)
        this.setMin(Calendar.SECOND)
        this.setMin(Calendar.MILLISECOND)
    }.time
}

fun Date.endOfWeek(): Date {
    val end = endOfDay()
    return Calendar.getInstance().apply {
        this.time = end
        this.setMax(Calendar.DAY_OF_WEEK)
    }.time
}

fun Date.endOfDay(): Date {
    return Calendar.getInstance().apply {
        this.time = this@endOfDay
        this.setMax(Calendar.HOUR_OF_DAY)
        this.setMax(Calendar.MINUTE)
        this.setMax(Calendar.SECOND)
        this.setMax(Calendar.MILLISECOND)
    }.time
}

private fun Calendar.setMax(field: Int) {
    set(field, getMaximum(field))
}

private fun Calendar.setMin(field: Int) {
    set(field, getMinimum(field))
}

val Date.hourOfDay: Int
    get() = Calendar.getInstance().apply { this.time = this@hourOfDay }.get(Calendar.HOUR_OF_DAY)

val Date.minuteOfHour: Int
    get() = Calendar.getInstance().apply { this.time = this@minuteOfHour }.get(Calendar.MINUTE)

val Date.minutesOfDay: Int
    get() {
        val hours = hourOfDay
        val minutes = minuteOfHour

        return hours * 60 + minutes
    }

val Date.monthOfYear: Int
    get() {
        val cal = Calendar.getInstance().apply { this.time = this@monthOfYear }
        val calValue = cal.get(Calendar.MONTH)
        return calValue + 1
    }

val Date.whatYear: Int
    get() = Calendar.getInstance().apply { this.time = this@whatYear }.get(Calendar.YEAR)

val Date.dayOfMonth: Int
    get() = Calendar.getInstance().apply { this.time = this@dayOfMonth }.get(Calendar.DAY_OF_MONTH)

val Date.dayOfYear: Int
    get() = Calendar.getInstance().apply { this.time = this@dayOfYear }.get(Calendar.DAY_OF_YEAR)

val Date.dayOfWeek: Int
    get() = Calendar.getInstance().apply {
        this.firstDayOfWeek = Calendar.SUNDAY
        this.time = this@dayOfWeek
    }.get(Calendar.DAY_OF_WEEK) - 1 // sunday is 1 index

val Date.weekday: Weekday
    get() = Weekday.fromPosition(dayOfWeek )

fun Date.isSunday(): Boolean = Weekday.fromPosition(dayOfWeek) == Weekday.Sunday
fun Date.isMonday(): Boolean = Weekday.fromPosition(dayOfWeek) == Weekday.Monday
fun Date.isTuesday(): Boolean = Weekday.fromPosition(dayOfWeek) == Weekday.Tuesday
fun Date.isWednesday(): Boolean = Weekday.fromPosition(dayOfWeek) == Weekday.Wednesday
fun Date.isThursday(): Boolean = Weekday.fromPosition(dayOfWeek) == Weekday.Thursday
fun Date.isFriday(): Boolean = Weekday.fromPosition(dayOfWeek) == Weekday.Friday
fun Date.isSaturday(): Boolean = Weekday.fromPosition(dayOfWeek) == Weekday.Saturday

fun Date.minusHours(count: Int): Date {
    return Calendar.getInstance().apply {
        this.time = this@minusHours
        this.add(Calendar.HOUR, -count)
    }.time
}

fun Date.plusHours(count: Int): Date {
    return Calendar.getInstance().apply {
        this.time = this@plusHours
        this.add(Calendar.HOUR, count)
    }.time
}

fun Date.plusDays(count: Int): Date {
    return Calendar.getInstance().apply {
        this.time = this@plusDays
        this.add(Calendar.DAY_OF_YEAR, count)
    }.time
}

fun Date.minusDays(count: Int): Date {
    return Calendar.getInstance().apply {
        this.time = this@minusDays
        this.add(Calendar.DAY_OF_YEAR, -count)
    }.time
}

fun Date.minusMinutes(count: Int): Date {
    return Calendar.getInstance().apply {
        this.time = this@minusMinutes
        this.add(Calendar.MINUTE, -count)
    }.time
}

fun Date.plusMinutes(count: Int): Date {
    return Calendar.getInstance().apply {
        this.time = this@plusMinutes
        this.add(Calendar.MINUTE, count)
    }.time
}

fun Date.minusSeconds(count: Int): Date {
    return Calendar.getInstance().apply {
        this.time = this@minusSeconds
        this.add(Calendar.SECOND, -count)
    }.time
}

fun Date.plusSeconds(count: Int): Date {
    return Calendar.getInstance().apply {
        this.time = this@plusSeconds
        this.add(Calendar.SECOND, count)
    }.time
}

fun Date.monthsBetween(other: Date): Int {
    val from = Calendar.getInstance().apply {
        time = this@monthsBetween
    }

    val to = Calendar.getInstance().apply {
        time = other
    }

    val fromMonth = (12 * from.time.whatYear) + from.time.monthOfYear
    val toMonth = (12 * to.time.whatYear) + to.time.monthOfYear

    return abs(toMonth - fromMonth)
}

fun Date.daysBetween(other: Date): Int {
    var from = Calendar.getInstance().apply {
        time = this@daysBetween
    }

    var to = Calendar.getInstance().apply {
        time = other
    }

    if (from.get(Calendar.YEAR) == to.get(Calendar.YEAR)) {
        return abs(from.get(Calendar.DAY_OF_YEAR) - to.get(Calendar.DAY_OF_YEAR));
    } else {
        if (to.get(Calendar.YEAR) > from.get(Calendar.YEAR)) {
            //swap them
            val temp = from
            from = to
            to = temp
        }
        var extraDays = 0

        val fromOriginalYearDays = from.get(Calendar.DAY_OF_YEAR);

        while (from.get(Calendar.YEAR) > to.get(Calendar.YEAR)) {
            from.add(Calendar.YEAR, -1)
            // getActualMaximum() important for leap years
            extraDays += from.getActualMaximum(Calendar.DAY_OF_YEAR)
        }

        return extraDays - to.get(Calendar.DAY_OF_YEAR) + fromOriginalYearDays
    }
}

sealed class Weekday(val weekPosition: Int, val abbreviation: String) {
    object Sunday : Weekday(0, "S")
    object Monday : Weekday(1, "M")
    object Tuesday : Weekday(2, "T")
    object Wednesday : Weekday(3, "W")
    object Thursday : Weekday(4, "T")
    object Friday : Weekday(5, "F")
    object Saturday : Weekday(6, "S")
    object Unknown : Weekday(-1, "?")

    companion object {
        fun fromPosition(pos: Int): Weekday {
            return Weekday::class.nestedClasses
                .map { klass -> klass.objectInstance }
                .filterIsInstance<Weekday>()
                .find { it.weekPosition == pos } ?: Unknown
        }
    }
}

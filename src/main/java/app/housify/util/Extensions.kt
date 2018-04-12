package app.housify.util

import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt


fun ResultSet.getColumns(): List<String> {
    val columns = ArrayList<String>(metaData.columnCount)
    for (i in 1..metaData.columnCount) {
        columns.add(metaData.getColumnName(i).toLowerCase())
    }
    return columns
}

fun ResultSet.asArrayMap(): List<Map<String, String?>> {
    val list = arrayListOf<Map<String, String?>>()
    while (true) { list.add(asMap() ?: break) }
    return list
}

fun ResultSet.asMap(): Map<String, String?>? {
    return if (next()) {
        val map = hashMapOf<String, String?>()
        getColumns().forEach { map[it] = getString(it) }
        map.formatValues()
    } else null
}

fun String.extractFormData(): HashMap<String, String> {
    return split("&").map { it.split("=") }.map { it[0] to it[1] }.toMap(HashMap())
}


fun HashMap<String, String?>.formatValues(): Map<String, String?> {
    formatUnixTimestamp()
    formatTime()
    return this
}

fun HashMap<String, String?>.formatUnixTimestamp() {
    listOf("date", "sale_date", "listing_date").forEach {
        val dateString: String? = this[it]
        if (dateString != null) {
            this[it] = dateString.formatUnixTimestamp()
        }
    }
}
fun String.formatUnixTimestamp(): String = SimpleDateFormat("MM/dd/yyyy").format(Date(this.toLong()))

fun HashMap<String, String?>.formatTime() {
    listOf("avg_time").forEach {
        val timeString: String? = this[it]
        if (timeString != null) {
            this[it] = timeString.formatTime()
        }
    }
}
fun String.formatTime(): String = (this.toDouble() / 86400000).roundToInt().toString()
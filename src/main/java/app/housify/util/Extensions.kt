package app.housify.util

import java.sql.ResultSet
import java.util.ArrayList

fun ResultSet.getColumns(): List<String> {
    val columns = ArrayList<String>(metaData.columnCount)
    for (i in 1..metaData.columnCount) {
        columns.add(metaData.getColumnName(i).toLowerCase())
    }
    return columns
}

fun ResultSet.asArrayMap(): List<Map<String, String>> {
    val list = arrayListOf<Map<String, String>>()
    while (true) { list.add(asMap() ?: break) }
    return list
}

fun ResultSet.asMap(): Map<String, String>? {
    return if (next()) {
        val map = hashMapOf<String, String>()
        getColumns().forEach { map[it] = getString(it) }
        map
    } else null
}
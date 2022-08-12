package com.yandex.android.beacon

import android.content.Context
import android.net.Uri
import com.yandex.android.beacon.BeaconItem.Persistent
import org.json.JSONObject

internal class TestSendBeaconDb(context: Context) : SendBeaconDb(context, DB_NAME) {

    val items = mutableListOf<Persistent>()

    override fun allItems(): List<Persistent> {
        return ArrayList(items)
    }

    override fun add(url: Uri, headers: Map<String, String>, addTimestamp: Long, payload: JSONObject?): Persistent {
        val item = Persistent(url, headers, payload, addTimestamp, 0)
        items.add(item)
        return item
    }

    override fun remove(item: Persistent?): Boolean {
        return items.remove(item)
    }

    companion object {
        const val DB_NAME = "testBeacon.db"
    }
}

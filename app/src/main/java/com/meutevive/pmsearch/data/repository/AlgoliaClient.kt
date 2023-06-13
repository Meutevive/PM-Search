package com.meutevive.pmsearch.data.repository

import com.algolia.search.saas.Client
import com.algolia.search.saas.Index
import com.meutevive.pmsearch.models.PM
import com.algolia.search.saas.Query


class AlgoliaClient {
    private val client = Client("95BI1O1FXC", "0cb0d160a7e1781b04bce500f71d81bc")
    val index: Index = client.getIndex("LesPM")

    fun searchPM(query: String, callback: (List<PM>?, Exception?) -> Unit) {
        val searchQuery = Query(query)
        index.searchAsync(searchQuery) { jsonObject, exception ->
            if (exception != null) {
                callback(null, exception)
            } else {
                val hits = jsonObject?.getJSONArray("hits")
                val pmList = mutableListOf<PM>()
                if (hits != null) {
                    for (i in 0 until hits.length()) {
                        val item = hits.getJSONObject(i)
                        val pm = PM(
                            id = item.getString("id"),
                            pmNumber = item.getString("pmNumber"),
                            address = item.getString("address"),
                            comment = item.getString("comment"),
                            date = item.getLong("date"),
                            photoUrl = item.getString("photoUrl")
                        )
                        pmList.add(pm)
                    }
                }
                callback(pmList, null)
            }
        }
    }
}

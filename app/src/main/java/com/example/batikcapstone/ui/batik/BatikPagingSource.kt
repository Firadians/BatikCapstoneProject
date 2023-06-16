package com.example.batikcapstone.ui.batik

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.batikcapstone.data.model.Batik
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class BatikPagingSource(
    private val firestore: FirebaseFirestore,
) : PagingSource<QuerySnapshot, Batik>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Batik> {
        return try {
            val currentPage = params.key ?: firestore.collection("batikjenis")
                .orderBy("name") // Adjust the sorting field as per your requirement
                .limit(params.loadSize.toLong())
                .get()
                .await()

            val batikList = currentPage.toObjects(Batik::class.java)

            val lastVisible = currentPage.documents[currentPage.size() - 1]

            val nextQuery = firestore.collection("batikjenis")
                .orderBy("name")
                .startAfter(lastVisible)
                .limit(params.loadSize.toLong())

            val nextPage = nextQuery.get().await()

            LoadResult.Page(
                data = batikList,
                prevKey = null,
                nextKey = nextPage,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Batik>): QuerySnapshot? {
        return null
    }
}
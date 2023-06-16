package com.example.batikcapstone.ui.batik

import androidx.lifecycle.ViewModel
import androidx.paging.*
import com.example.batikcapstone.data.model.Batik
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class BatikViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    fun getBatikList(): Flow<PagingData<Batik>> {
        val pagingConfig = PagingConfig(pageSize = 20)

        val pagingSourceFactory = { BatikPagingSource(firestore) }

        return Pager(
            config = pagingConfig,
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}
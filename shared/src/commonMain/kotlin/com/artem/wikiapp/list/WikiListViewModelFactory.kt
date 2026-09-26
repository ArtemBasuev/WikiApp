package com.artem.wikiapp.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.reflect.KClass

class WikiListViewModelFactory(
    private val onNavigateToDetail: (Long) -> Unit,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return WikiListViewModel(onNavigateToDetail = onNavigateToDetail) as T
    }
}
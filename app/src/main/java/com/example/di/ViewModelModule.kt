package com.example.di

import com.example.presentation.history.HistoryViewModel
import com.example.presentation.home.HomeViewModel
import com.example.presentation.settings.RouterManagerViewModel
import com.example.presentation.settings.SettingsViewModel
import com.example.presentation.test.TestViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { HistoryViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get()) }
    viewModel { RouterManagerViewModel(get()) }
    viewModel { TestViewModel(get(), get()) }
}

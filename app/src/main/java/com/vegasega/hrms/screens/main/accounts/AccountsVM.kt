package com.vegasega.hrms.screens.main.accounts

import androidx.lifecycle.ViewModel
import com.vegasega.hrms.networking.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountsVM @Inject constructor(private val repository: Repository): ViewModel() {
}
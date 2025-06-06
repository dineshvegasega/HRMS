package com.vegasega.hrms.screens.main.recruitments

import androidx.lifecycle.ViewModel
import com.vegasega.hrms.networking.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class RecruitmentsVM @Inject constructor(private val repository: Repository): ViewModel() {
}
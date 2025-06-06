package com.vegasega.hrms.abc

import androidx.lifecycle.ViewModel
import com.vegasega.hrms.networking.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ABCVM @Inject constructor(private val repository: Repository) : ViewModel() {

}
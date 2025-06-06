package com.vegasega.hrms.screens.interfaces

interface PermissionCallback {
    fun onPermissionGranted()
    fun onPermissionDenied()
}
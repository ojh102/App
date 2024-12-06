package com.ojh.core.data

interface PermissionRepository {
    fun permission(): String
    fun isMusicReadPermissionGranted(): Boolean
}
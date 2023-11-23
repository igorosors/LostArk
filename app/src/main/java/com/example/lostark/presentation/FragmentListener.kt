package com.example.lostark.presentation

import androidx.fragment.app.Fragment

interface FragmentListener {
    fun back()
    fun switchToFragment(fragment: Fragment): Boolean
}
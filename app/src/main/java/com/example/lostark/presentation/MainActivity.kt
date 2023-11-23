package com.example.lostark.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.lostark.R
import com.example.lostark.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), FragmentListener {

    private val binding by viewBinding(ActivityMainBinding::bind)
    private val bottomNavigation by lazy { binding.bottomNavigation }

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            //
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(
                    this,
                    getString(R.string.toast_need_permission),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(binding.fragmentContainer.id, CalendarFragment.newInstance())
                .commit()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            enableNotification()
        }

        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.itemOne -> switchToFragment(CalendarFragment.newInstance())
                R.id.itemTwo -> switchToFragment(EventFragment.newInstance())
                else -> false
            }
        }

    }

    private fun enableNotification() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) -> {

            }
            else -> {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }



    override fun onBackPressed() {
        supportFragmentManager.popBackStack()
    }

    override fun back() {
        supportFragmentManager.popBackStack()
    }

    override fun switchToFragment(fragment: Fragment): Boolean {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .addToBackStack(null)
            .commit()
        return true
    }

}
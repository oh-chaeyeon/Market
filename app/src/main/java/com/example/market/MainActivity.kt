package com.example.market

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.market.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            // After 3 seconds, replace with ListFragment
            replaceWithListFragment()
        }, DURATION)

        // Set up onBackPressedCallback
        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun replaceWithListFragment() {
        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, homeFragment)
            .commit()

        binding.fragmentContainer.visibility = View.VISIBLE
    }

    companion object {
        private const val DURATION: Long = 3000
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // Check if any fragment in the back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                // Pop the fragment from the back stack
                supportFragmentManager.popBackStack()
            } else {
                // If no fragment in the back stack, handle back press as needed
                isEnabled = false
                onBackPressed()
            }
        }
    }

    fun replaceWithMainActivity2() {
        val mainActivity2Intent = Intent(this, MainActivity2::class.java)
        startActivity(mainActivity2Intent)
        finish()
    }
}
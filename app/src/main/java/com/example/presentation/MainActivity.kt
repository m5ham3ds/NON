package com.example.presentation

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.R
import com.example.data.local.preferences.AppPreferences
import com.example.data.local.preferences.ThemePreferences
import com.example.util.LocaleHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.example.widget.CustomToolbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import timber.log.Timber
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: CustomToolbar
    
    private val appPreferences: AppPreferences by inject()
    private val themePreferences: ThemePreferences by inject()
    private val sessionRepository: com.example.domain.repository.ISessionRepository by inject()

    private val requestPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Timber.d("POST_NOTIFICATIONS permission granted")
        } else {
            Timber.w("POST_NOTIFICATIONS permission denied")
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getPersistedLocale(newBase)
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // App Theme loading before super.onCreate()
        val themeMode = try {
            val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
            sharedPreferences.getString("theme", "system") ?: "system"
        } catch (_: Exception) {
            "system"
        }
        val nightMode = when (themeMode) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Clean up any stale active sessions on launch because the app starting fresh
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                sessionRepository.cleanUpOrphanedSessions()
            } catch (e: Exception) {
                Timber.e(e, "Failed to clean up stale active sessions on app launch")
            }
        }

        // Request notification permission if on Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home_fragment,
                R.id.nav_history_fragment,
                R.id.nav_settings_fragment
            ),
            drawerLayout
        )

        // Setup ActionBar with Navigation Controller
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // Drawer layout setup
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setupWithNavController(navController)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
            if (handled) {
                drawerLayout.closeDrawer(GravityCompat.START)
                return@setNavigationItemSelectedListener true
            }
            when (menuItem.itemId) {
                R.id.action_about -> {
                    val isArabic = com.example.util.LocaleHelper.getPersistedLocale(this) == "ar"
                    val versionText = if (isArabic) "\n\nالإصدار: 1.0.0-Stable\nحقوق النشر © 2026 جميع الحقوق محفوظة" 
                                      else "\n\nVersion: 1.0.0-Stable\nCopyright © 2026 All Rights Reserved"
                    com.example.util.DialogHelper.showCustomDialog(
                        context = this,
                        title = getString(R.string.about_title),
                        message = getString(R.string.about_message) + versionText,
                        dialogType = com.example.util.DialogHelper.DialogType.INFO,
                        iconRes = R.drawable.ic_launcher,
                        positiveButtonText = getString(R.string.btn_close)
                    )
                }
                R.id.action_exit -> {
                    val isArabic = com.example.util.LocaleHelper.getPersistedLocale(this) == "ar"
                    val titleText = if (isArabic) "تأكيد الخروج" else "Confirm Exit"
                    val msgText = if (isArabic) "هل أنت متأكد من رغبتك في الخروج من التطبيق؟" else "Are you sure you want to exit the application?"
                    val posText = if (isArabic) "خروج" else "Exit"
                    val negText = if (isArabic) "إلغاء" else "Cancel"
                    com.example.util.DialogHelper.showCustomDialog(
                        context = this,
                        title = titleText,
                        message = msgText,
                        dialogType = com.example.util.DialogHelper.DialogType.WARNING,
                        iconRes = R.drawable.ic_launcher,
                        positiveButtonText = posText,
                        positiveAction = {
                            finishAffinity()
                        },
                        negativeButtonText = negText
                    )
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Bottom Nav setup
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

package com.example.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.R
import com.example.util.LocaleHelper
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by viewModel()

    private val preferenceChangeListener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        when (key) {
            "theme" -> {
                val themeMode = sharedPreferences.getString("theme", "system") ?: "system"
                val nightMode = when (themeMode) {
                    "dark" -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                    "light" -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
                    else -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(nightMode)
                activity?.recreate()
            }
            "app_language" -> {
                val lang = sharedPreferences.getString("app_language", "system") ?: "system"
                LocaleHelper.setLocale(requireContext(), lang)
                activity?.recreate()
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        // 1. Manage Routers navigation click
        findPreference<Preference>("manage_routers")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settings_to_routerManager)
            true
        }

        // 2. Clear History click
        findPreference<Preference>("clear_history")?.setOnPreferenceClickListener {
            com.example.util.DialogHelper.showCustomDialog(
                context = requireContext(),
                title = getString(R.string.pref_clear_history),
                message = getString(R.string.msg_confirm_clear),
                dialogType = com.example.util.DialogHelper.DialogType.WARNING,
                positiveButtonText = "نعم، احذف الكل",
                positiveAction = {
                    viewModel.confirmClearHistory()
                },
                negativeButtonText = "إلغاء"
            )
            true
        }

        // 3. Export DB click
        findPreference<Preference>("export_db")?.setOnPreferenceClickListener {
            com.example.util.ToastHelper.showInfoToast(requireContext(), "الرجاء الذهاب إلى السجل وتحديد جلسة معينة لتصدير نتائجها بشكل مخصص وسريع")
            true
        }

        // 4. GitHub link click
        findPreference<Preference>("github")?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/msalah564s/wifi-master-pro"))
            startActivity(intent)
            true
        }

        // 5. Set version name dynamically
        findPreference<Preference>("version")?.summary = "1.0.0-Stable"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is SettingsViewModel.UiEvent.ShowMessage -> {
                            com.example.util.ToastHelper.showSuccessToast(requireContext(), getString(event.resId))
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}

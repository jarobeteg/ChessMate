package com.example.chessmate.ui.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.chessmate.R

class SettingsActivity : AbsThemeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateTheme()
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            setThemePreference()
            setThemeColorPreference()
            setChessboardThemePreference()
            setPieceThemePreference()
        }

        private fun setThemePreference(){
            val clickListener = Preference.OnPreferenceClickListener { true }
            val changeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val listPreference = preference as ListPreference
                val entries = listPreference.entries
                listPreference.summary = entries[listPreference.findIndexOfValue(newValue.toString())]

                requireActivity().recreate()
                true
            }

            val themePreference = findPreference<ListPreference>(getString(R.string.pref_theme_key))
            themePreference?.onPreferenceChangeListener = changeListener
            themePreference?.onPreferenceClickListener = clickListener
            themePreference?.summary = themePreference?.entry
        }

        private fun setThemeColorPreference(){
            val clickListener = Preference.OnPreferenceClickListener { true }
            val changeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val listPreference = preference as ListPreference
                val entries = listPreference.entries
                listPreference.summary = entries[listPreference.findIndexOfValue(newValue.toString())]

                requireActivity().recreate()
                true
            }

            val colorPreference = findPreference<ListPreference>(getString(R.string.pref_color_key))
            colorPreference?.onPreferenceChangeListener = changeListener
            colorPreference?.onPreferenceClickListener = clickListener
            colorPreference?.summary = colorPreference?.entry
        }

        private fun setChessboardThemePreference() {
            val clickListener = Preference.OnPreferenceClickListener { true }
            val changeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val listPreference = preference as ListPreference
                val entries = listPreference.entries
                listPreference.summary = entries[listPreference.findIndexOfValue(newValue.toString())]

                requireActivity().recreate()
                true
            }

            val chessboardPreference = findPreference<ListPreference>(getString(R.string.pref_chessboard_theme_key))
            chessboardPreference?.onPreferenceChangeListener = changeListener
            chessboardPreference?.onPreferenceClickListener = clickListener
            chessboardPreference?.summary = chessboardPreference?.entry
        }

        private fun setPieceThemePreference() {
            val clickListener = Preference.OnPreferenceClickListener { true }
            val changeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val listPreference = preference as ListPreference
                val entries = listPreference.entries
                listPreference.summary = entries[listPreference.findIndexOfValue(newValue.toString())]

                requireActivity().recreate()
                true
            }

            val piecePreference = findPreference<ListPreference>(getString(R.string.pref_piece_theme_key))
            piecePreference?.onPreferenceChangeListener = changeListener
            piecePreference?.onPreferenceClickListener = clickListener
            piecePreference?.summary = piecePreference?.entry
        }
    }
}
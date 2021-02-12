package net.imknown.android.forefrontinfo.ui.settings

import android.os.Bundle
import android.os.Looper
import androidx.annotation.StringRes
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.imknown.android.forefrontinfo.MyApplication
import net.imknown.android.forefrontinfo.R
import net.imknown.android.forefrontinfo.base.EventObserver
import net.imknown.android.forefrontinfo.base.isChinaMainlandTimezone
import net.imknown.android.forefrontinfo.ui.base.IFragmentView

class SettingsFragment : PreferenceFragmentCompat(), IFragmentView {

    companion object {
        suspend fun newInstance() = withContext(Dispatchers.Main) {
            SettingsFragment()
        }
    }

    override val visualContext by lazy { context }

    private val settingsViewModel by activityViewModels<SettingsViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            Looper.myLooper() ?: Looper.prepare()
            setPreferencesFromResource(R.xml.preferences, rootKey)
            Looper.myLooper()?.quit()

            whenCreated {
                initViews()
            }
        }
    }

    private fun initViews() {
        // region [Theme]
        settingsViewModel.themesPrefChangeEvent.observe(viewLifecycleOwner, EventObserver {
            it?.let { themesValue ->
                MyApplication.setMyTheme(themesValue)
            }
        })
        // endregion [Theme]

        // region [Scroll Bar Mode]
        settingsViewModel.scrollBarModeChangedEvent.observe(viewLifecycleOwner, EventObserver {
            it?.let { scrollBarMode ->
                settingsViewModel.setScrollBarMode(scrollBarMode)
            }
        })

        settingsViewModel.changeScrollBarModeEvent.observe(viewLifecycleOwner, EventObserver {
            listView.isVerticalScrollBarEnabled = it
        })

        val scrollBarModePref =
            findPreference<ListPreference>(MyApplication.getMyString(R.string.interface_scroll_bar_key))!!
        settingsViewModel.setScrollBarMode(scrollBarModePref.value)
        // endregion [Scroll Bar Mode]

        settingsViewModel.showMessageEvent.observe(viewLifecycleOwner, EventObserver {
            toast(it)
        })

        val aboutShopPref = findPreference(R.string.about_shop_key)
        setOnOpenInExternalListener(
            aboutShopPref, if (isChinaMainlandTimezone()) {
                R.string.about_shop_china_mainland_uri
            } else {
                R.string.about_shop_uri
            }
        )

        val aboutSourcePref = findPreference(R.string.about_source_key)
        setOnOpenInExternalListener(aboutSourcePref, R.string.about_source_uri)

        val aboutPrivacyPolicyPref = findPreference(R.string.about_privacy_policy_key)
        setOnOpenInExternalListener(aboutPrivacyPolicyPref, R.string.about_privacy_policy_uri)

        val aboutLicensesPref = findPreference(R.string.about_licenses_key)
        setOnOpenInExternalListener(aboutLicensesPref, R.string.about_licenses_uri)

        val aboutTranslatorMoreInfoPref = findPreference(R.string.about_translator_more_info_key)
        setOnOpenInExternalListener(
            aboutTranslatorMoreInfoPref,
            R.string.translator_website
        )

        // region [Version Info]
        val versionPref = findPreference(R.string.about_version_key)
        settingsViewModel.version.observe(viewLifecycleOwner) {
            versionPref.summary = MyApplication.getMyString(
                it.id,
                it.versionName,
                it.versionCode,
                it.assetLldVersion,
                it.distributor,
                it.installer,
                it.firstInstallTime,
                it.lastUpdateTime
            )
        }

        settingsViewModel.setBuiltInDataVersion(
            MyApplication.instance.packageName,
            MyApplication.instance.packageManager
        )
        // endregion [Version Info]

        // region [Version Click]
        versionPref.setOnPreferenceClickListener {
            settingsViewModel.versionClicked()

            true
        }
        // endregion [Version Click]
    }

    private fun findPreference(@StringRes resId: Int) =
        findPreference<Preference>(MyApplication.getMyString(resId))!!

    private fun setOnOpenInExternalListener(pref: Preference, @StringRes uriResId: Int) {
        pref.setOnPreferenceClickListener {
            settingsViewModel.openInExternal(uriResId)

            true
        }
    }
}
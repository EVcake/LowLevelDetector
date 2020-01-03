package net.imknown.android.forefrontinfo.ui.others

import android.content.SharedPreferences
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.imknown.android.forefrontinfo.MyApplication
import net.imknown.android.forefrontinfo.R
import net.imknown.android.forefrontinfo.base.BaseListFragment
import net.imknown.android.forefrontinfo.ui.GetRawPropEventViewModel

class OthersFragment : BaseListFragment() {

    companion object {
        fun newInstance() = OthersFragment()
    }

    private val getRawPropEventViewModel: GetRawPropEventViewModel by activityViewModels()
    private val othersViewModel = OthersViewModel()

    override fun init() {
        othersViewModel.models.observe(viewLifecycleOwner, Observer {
            getRawPropEventViewModel.onFinish()

            showModels(it)
        })
    }

    override suspend fun collectModels() {
        othersViewModel.collectModels()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        super.onSharedPreferenceChanged(sharedPreferences, key)

        launch(Dispatchers.IO) {
            if (key == MyApplication.getMyString(R.string.function_raw_build_prop_key)) {
                collectModelsCaller(500)
            }
        }
    }
}
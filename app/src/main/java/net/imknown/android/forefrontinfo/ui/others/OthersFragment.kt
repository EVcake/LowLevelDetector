package net.imknown.android.forefrontinfo.ui.others

import androidx.fragment.app.activityViewModels
import net.imknown.android.forefrontinfo.MyApplication
import net.imknown.android.forefrontinfo.base.EventObserver
import net.imknown.android.forefrontinfo.ui.base.BaseListFragment

class OthersFragment : BaseListFragment() {

    companion object {
        fun newInstance() = OthersFragment()
    }

    override val listViewModel by activityViewModels<OthersViewModel>()

    @ExperimentalStdlibApi
    override fun init() {
        observeLanguageEvent(MyApplication.settingsLanguageEvent)

        listViewModel.rawProp.observe(viewLifecycleOwner, EventObserver {
            listViewModel.payloadRawProp(myAdapter.myModels, it)
        })

        listViewModel.toggleRawPropEvent.observe(viewLifecycleOwner, EventObserver {
            if (it.isOn) {
                myAdapter.notifyItemRangeInserted(it.positionStart, it.itemCount)
            } else {
                myAdapter.notifyItemRangeRemoved(it.positionStart, it.itemCount)
            }
        })
    }
}
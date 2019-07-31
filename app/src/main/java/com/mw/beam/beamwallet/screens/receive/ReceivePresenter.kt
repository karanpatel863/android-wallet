/*
 * // Copyright 2018 Beam Development
 * //
 * // Licensed under the Apache License, Version 2.0 (the "License");
 * // you may not use this file except in compliance with the License.
 * // You may obtain a copy of the License at
 * //
 * //    http://www.apache.org/licenses/LICENSE-2.0
 * //
 * // Unless required by applicable law or agreed to in writing, software
 * // distributed under the License is distributed on an "AS IS" BASIS,
 * // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * // See the License for the specific language governing permissions and
 * // limitations under the License.
 */

package com.mw.beam.beamwallet.screens.receive

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mw.beam.beamwallet.base_screen.BasePresenter
import com.mw.beam.beamwallet.core.entities.WalletAddress
import com.mw.beam.beamwallet.core.helpers.*
import com.mw.beam.beamwallet.core.utils.subscribeIf
import io.reactivex.disposables.Disposable

/**
 * Created by vain onnellinen on 11/13/18.
 */
class ReceivePresenter(currentView: ReceiveContract.View, currentRepository: ReceiveContract.Repository, private val state: ReceiveState)
    : BasePresenter<ReceiveContract.View, ReceiveContract.Repository>(currentView, currentRepository),
        ReceiveContract.Presenter {
    private lateinit var walletIdSubscription: Disposable
    private val changeAddressLiveData = MutableLiveData<WalletAddress>()


    override fun onViewCreated() {
        super.onViewCreated()
        view?.init()

        val address = view?.getWalletAddressFromArguments()

        initViewAddress(address)

        val amount = view?.getAmountFromArguments()
        if (amount != null && amount > 0) {
            view?.setAmount(amount.convertToBeam())
        }

        changeAddressLiveData.observe(view!!.getLifecycleOwner(), Observer {
            if (it.walletID != state.address?.walletID) {
                state.address = it
                initViewAddress(it)
            }
        })
    }

    override fun onAddressLongPressed() {
        saveAddress()
        view?.copyAddress(state.address?.walletID ?: "")
    }

    private fun initViewAddress(address: WalletAddress?) {
        if (address != null) {
            state.address = address
            state.expirePeriod = if (address.duration == 0L) ExpirePeriod.NEVER else ExpirePeriod.DAY
            state.isNeedGenerateAddress = false
            state.wasAddressSaved = state.generatedAddress?.walletID != address.walletID
        }

        state.address?.let {
            view?.initAddress(state.isAutogeneratedAddress(), it)
            view?.configCategory(repository.getCategory(it.walletID))
        }

    }

    override fun onResume() {
        super.onResume()
        state.address?.let {
            view?.initAddress(state.isAutogeneratedAddress(), it)
            view?.configCategory(repository.getCategory(it.walletID))
        }
        view?.handleExpandAdvanced(state.expandAdvanced)
        view?.handleExpandEditAddress(state.expandEditAddress)
    }

    override fun onBackPressed() {
        requestSaveAddress {
            view?.close()
        }
    }

    private fun requestSaveAddress(nextStep: () -> Unit) {
        if (!state.wasAddressSaved) {
            view?.showSaveAddressDialog(nextStep)
        } else if (isAddressInfoChanged()) {
            view?.showSaveChangesDialog(nextStep)
        } else {
            nextStep()
        }
    }

    override fun onSaveAddressPressed() {
        saveAddress()
    }

    override fun onShareTokenPressed() {
        if (state.address != null) {
            saveAddress()
            view?.shareToken(state.address!!.walletID)
        }
    }

    override fun onChangeAddressPressed() {
        view?.showChangeAddressFragment(state.generatedAddress)
    }

    override fun onAdvancedPressed() {
        state.expandAdvanced = !state.expandAdvanced
        view?.handleExpandAdvanced(state.expandAdvanced)
    }

    override fun onEditAddressPressed() {
        state.expandEditAddress = !state.expandEditAddress
        view?.handleExpandEditAddress(state.expandEditAddress)
    }

    override fun onShowQrPressed() {
        saveAddress()
        state.address?.let { address ->
            view?.showQR(address, view?.getAmount()?.convertToGroth(), state.isAutogeneratedAddress())
        }
    }

    override fun onExpirePeriodChanged(period: ExpirePeriod) {
        state.expirePeriod = period
    }

    override fun onAddNewCategoryPressed() {
        view?.showAddNewCategory()
    }

    private fun isAddressInfoChanged(): Boolean {
        return state.address?.let { address ->
            address.label != view?.getComment() ?: "" ||
                    address.duration != state.expirePeriod.value ||
                    repository.getCategory(address.walletID)?.id != state.category?.id

        } ?: false
    }

    override fun initSubscriptions() {
        super.initSubscriptions()

        walletIdSubscription = repository.generateNewAddress().subscribeIf(state.isNeedGenerateAddress) {
            if (state.address == null) {
                state.address = it
                state.generatedAddress = it
                view?.initAddress(true, it)
                view?.configCategory(repository.getCategory(it.walletID))
            }
        }
    }

    override fun onAddressChanged(walletAddress: WalletAddress) {
        changeAddressLiveData.postValue(walletAddress)
    }

    override fun onSelectedCategory(category: Category?) {
        state.category = category
    }

    private fun saveAddress() {
        state.address?.let { address ->
            address.duration = state.expirePeriod.value

            val comment = view?.getComment()
            address.label = comment ?: ""

            view?.getTxComment()?.let {
                if (it.isNotBlank()) {
                    ReceiveTxCommentHelper.saveCommentToAddress(address.walletID, it)
                }
            }

            if (state.wasAddressSaved) {
                repository.updateAddress(address)
            } else {
                repository.saveAddress(address)
            }

            repository.changeCategoryForAddress(address.walletID, state.category)

            state.wasAddressSaved = true
            state.isNeedGenerateAddress = false
        }
    }

    override fun getSubscriptions(): Array<Disposable>? = arrayOf(walletIdSubscription)

    override fun hasStatus(): Boolean = true
}

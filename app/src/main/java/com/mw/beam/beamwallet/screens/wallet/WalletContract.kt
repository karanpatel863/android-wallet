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

package com.mw.beam.beamwallet.screens.wallet

import android.view.Menu
import android.view.MenuInflater
import com.mw.beam.beamwallet.base_screen.MvpPresenter
import com.mw.beam.beamwallet.base_screen.MvpRepository
import com.mw.beam.beamwallet.base_screen.MvpView
import com.mw.beam.beamwallet.core.entities.OnTxStatusData
import com.mw.beam.beamwallet.core.entities.TxDescription
import com.mw.beam.beamwallet.core.entities.WalletStatus
import com.mw.beam.beamwallet.core.helpers.TrashManager
import io.reactivex.Observable
import io.reactivex.subjects.Subject

/**
 * Created by vain onnellinen on 10/1/18.
 */
interface WalletContract {
    interface View : MvpView {
        fun init()
        fun configWalletStatus(walletStatus: WalletStatus, expandBalanceCard: Boolean, expandInProgressCard: Boolean, isEnablePrivacyMode: Boolean)
        fun configTransactions(transactions: List<TxDescription>, isEnablePrivacyMode: Boolean)
        fun configInProgress(receivingAmount: Long, sendingAmount: Long, expandCard: Boolean, isEnablePrivacyMode: Boolean)
        fun configAvailable(availableAmount: Long, maturingAmount: Long, expandCard: Boolean, isEnablePrivacyMode: Boolean)
        fun showTransactionDetails(txId: String)
        fun showReceiveScreen()
        fun showSendScreen()
        fun handleExpandAvailable(shouldExpandAvailable: Boolean)
        fun handleExpandInProgress(shouldExpandInProgress: Boolean)
        fun addTitleListeners(isEnablePrivacyMode: Boolean)
        fun showActivatePrivacyModeDialog()
        fun configPrivacyStatus(isEnable: Boolean)
        fun createOptionsMenu(menu: Menu?, inflater: MenuInflater?, isEnablePrivacyMode: Boolean)
        fun closeDrawer()
        fun clearAllNotification()
        fun showAllTransactions()
    }

    interface Presenter : MvpPresenter<View> {
        fun onReceivePressed()
        fun onSendPressed()
        fun onTransactionPressed(txDescription: TxDescription)
        fun onExpandAvailablePressed()
        fun onExpandInProgressPressed()
        fun onChangePrivacyModePressed()
        fun onPrivacyModeActivated()
        fun onCancelDialog()
        fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?)
        fun onShowAllPressed()
    }

    interface Repository : MvpRepository {
        fun getWalletStatus(): Subject<WalletStatus>
        fun getTxStatus(): Observable<OnTxStatusData>
        fun isNeedConfirmEnablePrivacyMode(): Boolean
        fun getIntentTransactionId(): String?
        fun getTrashSubject(): Subject<TrashManager.Action>
        fun getAllTransactionInTrash(): List<TxDescription>
        fun saveFinishRestoreFlag()
    }
}

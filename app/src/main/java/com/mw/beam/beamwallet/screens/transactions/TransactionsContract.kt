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

package com.mw.beam.beamwallet.screens.transactions

import com.mw.beam.beamwallet.base_screen.MvpPresenter
import com.mw.beam.beamwallet.base_screen.MvpRepository
import com.mw.beam.beamwallet.base_screen.MvpView
import com.mw.beam.beamwallet.core.entities.OnTxStatusData
import com.mw.beam.beamwallet.core.entities.TxDescription
import com.mw.beam.beamwallet.core.helpers.TrashManager
import io.reactivex.Observable
import io.reactivex.subjects.Subject
import java.io.File

interface TransactionsContract {
    interface View: MvpView {
        fun init()
        fun showTransactionDetails(txId: String)
        fun configTransactions(transactions: List<TxDescription>)
        fun showShareFileChooser(file: File)
        fun showProofVerification()
        fun showSearchTransaction()
    }
    interface Presenter: MvpPresenter<View> {
        fun onTransactionPressed(txDescription: TxDescription)
        fun onSearchPressed()
        fun onExportPressed()
        fun onProofVerificationPressed()
    }
    interface Repository: MvpRepository {
        fun getTxStatus(): Observable<OnTxStatusData>
        fun getTrashSubject(): Subject<TrashManager.Action>
        fun getTransactionsFile(): File
        fun getAllTransactionInTrash(): List<TxDescription>?
    }
}
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

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.mw.beam.beamwallet.R
import com.mw.beam.beamwallet.base_screen.BaseFragment
import com.mw.beam.beamwallet.base_screen.BasePresenter
import com.mw.beam.beamwallet.base_screen.MvpRepository
import com.mw.beam.beamwallet.base_screen.MvpView
import com.mw.beam.beamwallet.core.AppConfig
import com.mw.beam.beamwallet.core.entities.TxDescription
import kotlinx.android.synthetic.main.fragment_transactions.*
import java.io.File

class TransactionsFragment : BaseFragment<TransactionsPresenter>(), TransactionsContract.View {
    private lateinit var pageAdapter: TransactionsPageAdapter

    override fun onControllerGetContentLayoutId(): Int = R.layout.fragment_transactions
    override fun getStatusBarColor(): Int = ContextCompat.getColor(context!!, R.color.addresses_status_bar_color)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun init() {
        pageAdapter = TransactionsPageAdapter(context!!) { presenter?.onTransactionPressed(it) }
        pageAdapter.setPrivacyMode(presenter?.repository?.isPrivacyModeEnabled() == true)

        pager.adapter = pageAdapter
        tabLayout.setupWithViewPager(pager)
    }

    override fun configTransactions(transactions: List<TxDescription>) {
        pageAdapter.setData(transactions)
    }

    override fun showProofVerification() {
        findNavController().navigate(TransactionsFragmentDirections.actionTransactionsFragmentToProofVerificationFragment())
    }

    override fun showSearchTransaction() {
        findNavController().navigate(TransactionsFragmentDirections.actionTransactionsFragmentToSearchTransactionFragment())
    }

    override fun showTransactionDetails(txId: String) {
        findNavController().navigate(TransactionsFragmentDirections.actionTransactionsFragmentToTransactionDetailsFragment(txId))
    }

    override fun showShareFileChooser(file: File) {
        val context = context ?: return

        val uri = FileProvider.getUriForFile(context, AppConfig.AUTHORITY, file)

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
        }

        startActivity(Intent.createChooser(intent, getString(R.string.common_share_title)))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.wallet_transactions_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> presenter?.onSearchPressed()
            R.id.menu_export -> presenter?.onExportPressed()
            R.id.menu_proof -> presenter?.onProofVerificationPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun initPresenter(): BasePresenter<out MvpView, out MvpRepository> {
        return TransactionsPresenter(this, TransactionsRepository())
    }

    override fun getToolbarTitle(): String? = getString(R.string.transactions)
}
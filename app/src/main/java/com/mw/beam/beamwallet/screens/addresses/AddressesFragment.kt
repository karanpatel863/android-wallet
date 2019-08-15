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

package com.mw.beam.beamwallet.screens.addresses

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.mw.beam.beamwallet.R
import com.mw.beam.beamwallet.base_screen.BaseFragment
import com.mw.beam.beamwallet.base_screen.BasePresenter
import com.mw.beam.beamwallet.base_screen.MvpRepository
import com.mw.beam.beamwallet.base_screen.MvpView
import com.mw.beam.beamwallet.core.entities.WalletAddress
import com.mw.beam.beamwallet.core.helpers.Tag
import kotlinx.android.synthetic.main.fragment_addresses.*

/**
 * Created by vain onnellinen on 2/28/19.
 */
class AddressesFragment : BaseFragment<AddressesPresenter>(), AddressesContract.View {
    private lateinit var pagerAdapter: AddressesPagerAdapter

    override fun onControllerGetContentLayoutId() = R.layout.fragment_addresses
    override fun getToolbarTitle(): String? = getString(R.string.addresses)

    override fun init() {
        val context = context ?: return

        setHasOptionsMenu(true)
        setMenuVisibility(false)

        pagerAdapter = AddressesPagerAdapter(context, object : AddressesAdapter.OnItemClickListener {
            override fun onItemClick(item: WalletAddress) {
                presenter?.onAddressPressed(item)
            }
        }, {
            return@AddressesPagerAdapter presenter?.onSearchTagsForAddress(it) ?: listOf()
        })

        pager.adapter = pagerAdapter
        pager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                setMenuVisibility(position==Tab.CONTACTS.value)
            }
        })

        tabLayout.setupWithViewPager(pager)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.addresses_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            presenter?.onAddContactPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun navigateToAddContactScreen() {
        findNavController().navigate(AddressesFragmentDirections.actionAddressesFragmentToAddContactFragment())
    }

    override fun getStatusBarColor(): Int = ContextCompat.getColor(context!!, R.color.addresses_status_bar_color)

    override fun showAddressDetails(address: WalletAddress) {
        findNavController().navigate(AddressesFragmentDirections.actionAddressesFragmentToAddressFragment(address))
    }

    override fun updateAddresses(tab: Tab, addresses: List<WalletAddress>) {
        pagerAdapter.setData(tab, addresses)
    }

    override fun initPresenter(): BasePresenter<out MvpView, out MvpRepository> {
        return AddressesPresenter(this, AddressesRepository(), AddressesState())
    }
}

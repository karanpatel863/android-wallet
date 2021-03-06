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

package com.mw.beam.beamwallet.screens.welcome_screen.welcome_description

import androidx.navigation.fragment.findNavController
import com.mw.beam.beamwallet.R
import com.mw.beam.beamwallet.base_screen.BaseFragment
import com.mw.beam.beamwallet.base_screen.BasePresenter
import com.mw.beam.beamwallet.base_screen.MvpRepository
import com.mw.beam.beamwallet.base_screen.MvpView
import kotlinx.android.synthetic.main.fragment_welcome_description.*

/**
 * Created by vain onnellinen on 10/22/18.
 */
class WelcomeDescriptionFragment : BaseFragment<WelcomeDescriptionPresenter>(), WelcomeDescriptionContract.View {
    override fun onControllerGetContentLayoutId() = R.layout.fragment_welcome_description
    override fun getToolbarTitle(): String? = getString(R.string.create_new_wallet)

    override fun generatePhrase() {
        findNavController().navigate(WelcomeDescriptionFragmentDirections.actionWelcomeDescriptionFragmentToWelcomeSeedFragment())
    }

    override fun addListeners() {
        btnGenerate.setOnClickListener {
            presenter?.onGeneratePhrase()
        }
    }

    override fun clearListeners() {
        btnGenerate.setOnClickListener(null)
    }

    override fun initPresenter(): BasePresenter<out MvpView, out MvpRepository> {
        return WelcomeDescriptionPresenter(this, WelcomeDescriptionRepository())
    }
}


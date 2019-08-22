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

package com.mw.beam.beamwallet.screens.welcome_screen.welcome_restore

import com.mw.beam.beamwallet.base_screen.BasePresenter

/**
 * Created by vain onnellinen on 11/5/18.
 */
class WelcomeRestorePresenter(currentView: WelcomeRestoreContract.View, currentRepository: WelcomeRestoreContract.Repository, private val state: WelcomeRestoreState)
    : BasePresenter<WelcomeRestoreContract.View, WelcomeRestoreContract.Repository>(currentView, currentRepository),
        WelcomeRestoreContract.Presenter {
    override fun onUnderstandPressed() {
        view?.showPasswordsFragment(view?.getSeed() ?: return)
    }


    override fun onViewCreated() {
        super.onViewCreated()
        view?.init()
        state.seeds = repository.getSuggestions()
        view?.configSeed(state.phrasesCount)
        view?.initSuggestions(state.seeds ?: return)
    }

    override fun onRestorePressed() {
        view?.showRestoreNotification()
//        view?.showPasswordsFragment(view?.getSeed() ?: return)
    }

    override fun onSeedChanged(seed: String) {
        view?.handleRestoreButton()
        view?.updateSuggestions(seed)
    }

    override fun onSuggestionClick(text: String) {
        view?.setTextToCurrentView(text)
    }

    override fun onValidateSeed(seed: String?): Boolean {
        return if (seed.isNullOrEmpty()) {
            false
        } else {
            state.seeds?.contains(seed) ?: false
        }
    }

    override fun onSeedFocusChanged(seed: String, hasFocus: Boolean) {
        view?.clearSuggestions()
        if (hasFocus) {
            view?.updateSuggestions(seed)
        }
    }

    override fun onKeyboardStateChange(isVisible: Boolean) {
        if (isVisible) {
            view?.showSuggestions()
        } else {
            view?.hideSuggestions()
        }
    }
}

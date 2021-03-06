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

package com.mw.beam.beamwallet.screens.owner_key_verification

import com.mw.beam.beamwallet.base_screen.BasePresenter
import com.mw.beam.beamwallet.core.helpers.FingerprintManager
import com.mw.beam.beamwallet.core.helpers.PreferencesManager

class OwnerKeyVerificationPresenter(view: OwnerKeyVerificationContract.View?, repository: OwnerKeyVerificationContract.Repository)
    : BasePresenter<OwnerKeyVerificationContract.View, OwnerKeyVerificationContract.Repository>(view, repository), OwnerKeyVerificationContract.Presenter {

    override fun onViewCreated() {
        super.onViewCreated()
        view?.init(isEnableFingerprint())
    }

    override fun onFingerprintSuccess() {
        view?.displayFingerPrint(false)
    }

    override fun onError() {
        view?.error()
    }

    override fun onFailed() {
        view?.showFailed()
    }

    override fun onSuccess() {
        view?.success()
    }

    override fun onNext() {
        val password = view?.getPassword()

        when {
            password.isNullOrBlank() -> view?.showEmptyPasswordError()
            repository.checkPassword(password) -> {
                view?.navigateToOwnerKey()
            }
            else -> view?.showWrongPasswordError()
        }
    }

    fun isEnableFingerprint(): Boolean {
        return PreferencesManager.getBoolean(PreferencesManager.KEY_IS_FINGERPRINT_ENABLED)
                && FingerprintManager.isManagerAvailable()
    }

    override fun onChangePassword() {
        view?.clearPasswordError()
    }

    override fun onStop() {
        view?.clearFingerprintCallback()
        super.onStop()
    }
}
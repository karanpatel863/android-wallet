package com.mw.beam.beamwallet.screens.add_contact

import com.mw.beam.beamwallet.base_screen.BasePresenter
import com.mw.beam.beamwallet.core.helpers.Tag
import com.mw.beam.beamwallet.core.helpers.QrHelper
import io.reactivex.disposables.Disposable

class AddContactPresenter(view: AddContactContract.View?, repository: AddContactContract.Repository, private val state: AddContactState):
        BasePresenter<AddContactContract.View, AddContactContract.Repository>(view, repository), AddContactContract.Presenter {

    private lateinit var addressesSubscription: Disposable

    override fun onCancelPressed() {
        view?.close()
    }

    override fun initSubscriptions() {
        super.initSubscriptions()

        addressesSubscription = repository.getAddresses().subscribe {
            state.updateAddresses(it.addresses)

            state.deleteAddresses(repository.getAllAddressesInTrash())
        }
    }

    override fun onStart() {
        super.onStart()
        val allTags = repository.getAllTags()
        view?.setupTagAction(allTags.isEmpty())
    }

    override fun onSavePressed() {
        val address = view?.getAddress() ?: ""
        val name = view?.getName() ?: ""

        if (QrHelper.isValidAddress(address)) {
            if (state.addresses.containsKey(address)) {
                view?.showTokenError(state.addresses[address])
            }
            else{
                repository.saveContact(address, name.trim(), state.tags)
                view?.close()
            }
        } else {
            view?.showTokenError(null)
        }
    }

    override fun onCreateNewTagPressed() {
        view?.navigateToAddNewCategory()
    }

    override fun checkAddress() {
        val address = view?.getAddress() ?: ""
        val name = view?.getName() ?: ""

        if (QrHelper.isValidAddress(address)) {
            if (state.addresses.containsKey(address)) {
                view?.showTokenError(state.addresses[address])
            }
            else{
                view?.hideTokenError()
            }
        } else {
            view?.showTokenError(null)
        }
    }

    override fun onScannedQR(text: String?) {
        if (text == null) return

        val scannedAddress = QrHelper.getScannedAddress(text)

        if (QrHelper.isValidAddress(scannedAddress)) {
            view?.setAddress(scannedAddress)
        } else {
            view?.vibrate(100)
            view?.showErrorNotBeamAddress()
        }
    }

    override fun onSelectTags(tags: List<Tag>) {
        state.tags = tags
        view?.setTags(tags)
    }

    override fun onTagActionPressed() {
        if (repository.getAllTags().isEmpty()) {
            view?.showCreateTagDialog()
        } else {
            view?.showTagsDialog(state.tags)
        }
    }

    override fun onAddNewCategoryPressed() {
        view?.navigateToAddNewCategory()
    }

    override fun onScanPressed() {
        view?.navigateToScanQr()
    }

    override fun onTokenChanged() {
        view?.hideTokenError()
    }

    override fun getSubscriptions(): Array<Disposable>? = arrayOf(addressesSubscription)
}
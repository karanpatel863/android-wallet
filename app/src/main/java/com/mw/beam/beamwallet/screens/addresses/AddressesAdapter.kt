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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.mw.beam.beamwallet.R
import com.mw.beam.beamwallet.R.color.*
import com.mw.beam.beamwallet.core.App
import com.mw.beam.beamwallet.core.AppConfig
import com.mw.beam.beamwallet.core.entities.WalletAddress
import com.mw.beam.beamwallet.core.helpers.Tag
import com.mw.beam.beamwallet.core.helpers.createSpannableString
import com.mw.beam.beamwallet.core.utils.CalendarUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_address.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by vain onnellinen on 2/28/19.
 */
class AddressesAdapter(private val context: Context,
                       private val clickListener: OnItemClickListener,
                       private val longListener: OnLongClickListener?, private val tagProvider: ((address: String) -> List<Tag>)? = null,
                       private val generatedAddress: WalletAddress? = null) : androidx.recyclerview.widget.RecyclerView.Adapter<AddressesAdapter.ViewHolder>() {
    private val multiplyColor = ContextCompat.getColor(context, wallet_adapter_multiply_color)
    private val notMultiplyColor = ContextCompat.getColor(context, wallet_adapter_not_multiply_color)
    private val noNameLabel = context.getString(R.string.no_name)
    private val autoGeneratedLabel = context.getString(R.string.auto_generated).toLowerCase()
    private val labelTypeface = ResourcesCompat.getFont(context, R.font.roboto_regular)

    private var data: List<WalletAddress> = listOf()

    var selectedAddresses = mutableListOf<String>()
    var mode = AddressesFragment.Mode.NONE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_address, parent, false)).apply {
        this.containerView.setOnClickListener {
            clickListener.onItemClick(data[adapterPosition])

            if (mode == AddressesFragment.Mode.EDIT)
            {
                if (selectedAddresses.contains(data[adapterPosition].walletID))
                {
                    selectedAddresses.remove(data[adapterPosition].walletID)
                }
                else{
                    selectedAddresses.add(data[adapterPosition].walletID)
                }

                notifyItemChanged(adapterPosition)
            }
        }

        if (longListener != null)
        {
            this.containerView.setOnLongClickListener{
                longListener?.onLongClick(data[adapterPosition])
                return@setOnLongClickListener true
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = data[position]
        val findTags = tagProvider?.invoke(address.walletID)

        holder.apply {
            val isGeneratedAddress = address.walletID == generatedAddress?.walletID

            val labelView = itemView.findViewById<TextView>(R.id.label)
            labelView.text = when {
                isGeneratedAddress -> autoGeneratedLabel
                address.label.isNotBlank() -> address.label
                else -> noNameLabel
            }

            labelView.setTypeface(labelTypeface, if (isGeneratedAddress) Typeface.ITALIC else Typeface.NORMAL)

            itemView.findViewById<TextView>(R.id.addressId).text = address.walletID
            itemView.setBackgroundColor(if (position % 2 == 0)  notMultiplyColor else multiplyColor) //logically reversed because count starts from zero
            val dateTextView = itemView.findViewById<TextView>(R.id.date)
            val expireDateVisibility = if (address.isContact) View.GONE else View.VISIBLE
            dateTextView.visibility = expireDateVisibility
            expireStateIcon?.visibility = expireDateVisibility

            if (!address.isContact) {
                val expireStateString: String
                val iconId: Int
                when {
                    address.isExpired -> {
                        val dateString = CalendarUtils.fromTimestamp(address.createTime + address.duration, SimpleDateFormat("d MMM yyyy", AppConfig.LOCALE))

                        expireStateString = "${context.getString(R.string.expired).toLowerCase()} $dateString"
                        iconId = R.drawable.ic_expired
                    }
                    address.duration == 0L -> {
                        expireStateString = context.getString(R.string.never_expires).toLowerCase()
                        iconId = R.drawable.ic_infinity
                    }

                    else -> {
                        val calendar = CalendarUtils.calendarFromTimestamp(address.createTime + address.duration)
                        val currentDate = Calendar.getInstance()
                        val timeDiff = calendar.timeInMillis - currentDate.timeInMillis

                        val hours = TimeUnit.MILLISECONDS.toHours(timeDiff)
                        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff) - hours * 60

                        expireStateString = context.getString(R.string.expires_in, hours.toString(), minutes.toString()).toLowerCase()
                        iconId = R.drawable.ic_exp
                    }
                }

                dateTextView.text = expireStateString
                expireStateIcon?.setImageDrawable(ContextCompat.getDrawable(context, iconId))
            }

            val category = itemView.findViewById<TextView>(R.id.tag)

            category.visibility = if (findTags.isNullOrEmpty()) View.GONE else View.VISIBLE

            category.text = findTags.createSpannableString(context)

            checkBox.setOnClickListener {
                clickListener.onItemClick(address)
            }

            if (mode == AddressesFragment.Mode.NONE)
            {
                checkBox.isChecked = false
                checkBox.visibility = View.GONE
            }
            else{
                checkBox.isChecked = selectedAddresses.contains(address.walletID)
                checkBox.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = data.size

    fun item(index:Int): WalletAddress {
        return data[index]
    }

    fun setData(data: List<WalletAddress>) {
        this.data = data
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(item: WalletAddress)
    }

    interface OnLongClickListener {
        fun onLongClick(item: WalletAddress)
    }

    class ViewHolder(override val containerView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(containerView), LayoutContainer
}

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

package com.mw.beam.beamwallet.core.helpers

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.mw.beam.beamwallet.R
import java.util.*

object TagHelper {
    private val gson = Gson()
    private val tagData: TagData by lazy {
        val json = PreferencesManager.getString(PreferencesManager.KEY_TAG_DATA)

        if (json.isNullOrBlank()) {
            return@lazy TagData(listOf())
        }

        gson.fromJson(json, TagData::class.java)
    }

    private fun saveTagData() {
        PreferencesManager.putString(PreferencesManager.KEY_TAG_DATA, gson.toJson(tagData))
    }

    fun getAllTags() = tagData.getAllTags().values.toList()

    fun getTag(tagId: String) = tagData.getAllTags().values.firstOrNull { it.id == tagId }

    fun saveTag(tag: Tag) {
        tagData.saveTag(tag)
        saveTagData()
    }

    fun getTagsForAddress(address: String): List<Tag> {
        return tagData.getAllTags().values.filter { it.addresses.contains(address) }
    }

    fun changeTagsForAddress(address: String, tagList: List<Tag>?) {
        if (tagList.isNullOrEmpty()) {
            removeAddressFromAllTags(address)
        } else {
            tagData.getAllTags().values.forEach {
                if (tagList.any { category -> category.id == it.id }) {
                    it.addAddress(address)
                } else {
                    it.removeAddress(address)
                }
            }
        }
        saveTagData()
    }

    private fun removeAddressFromAllTags(address: String) {
        tagData.getAllTags().values.forEach { it.removeAddress(address) }
    }

    fun deleteTag(tag: Tag) {
        tagData.removeTag(tag)
        saveTagData()
    }
}

private data class TagData(@SerializedName("data") var data: List<Tag>) {
    fun getAllTags() = HashMap(data.map { it.id to it }.toMap())

    fun saveTag(tag: Tag) {
        data = getAllTags().apply {
            put(tag.id, tag)
        }.values.toList()
    }

    fun removeTag(tag: Tag) {
        data = getAllTags().apply {
            remove(tag.id)
        }.values.toList()
    }
}

data class Tag(
        @SerializedName("id") val id: String,
        @SerializedName("color") var color: TagColor = TagColor.Red,
        @SerializedName("name") var name: String = "",
        @SerializedName("addresses") var addresses: List<String> = listOf()) {

    companion object {
        fun new(): Tag {
            return Tag(UUID.randomUUID().toString(), color = TagColor.values().random())
        }
    }

    fun addAddress(address: String) {
        addresses = addresses.toHashSet().apply {
            add(address)
        }.toList()
    }

    fun removeAddress(address: String) {
        addresses = addresses.toHashSet().apply {
            remove(address)
        }.toList()
    }
}

fun List<Tag>?.createSpannableString(context: Context): Spannable {
    return if (this == null || isEmpty()) SpannableString("") else {
        val spannableBuilder = SpannableStringBuilder()
        forEach {
            val color = ContextCompat.getColor(context, it.color.getAndroidColorId())
            val text = if (indexOf(it) != size - 1) "${it.name}, " else it.name
            spannableBuilder.append(text, ForegroundColorSpan(color), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        spannableBuilder
    }
}

enum class TagColor {
    Red, Orange, Yellow, Green, Blue, Pink;

    fun getAndroidColorId() = when(this) {
        Red -> R.color.category_red
        Orange -> R.color.category_orange
        Yellow -> R.color.category_yellow
        Green -> R.color.category_green
        Blue -> R.color.category_blue
        Pink -> R.color.category_pink
    }
}
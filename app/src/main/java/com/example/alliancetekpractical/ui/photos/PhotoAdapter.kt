package com.example.alliancetekpractical.ui.photos

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.alliancetekpractical.databinding.ItemPhotoBinding
import com.example.alliancetekpractical.ui.users.UserModel
import com.example.alliancetekpractical.utility.checkStringValue
import com.example.alliancetekpractical.utility.generateRoundCornerPlaceholder

class PhotoAdapter(
    var mContext: Context,
    var list: MutableList<ImageModel>) :
    RecyclerView.Adapter<PhotoAdapter.ItemViewHolder>() {


    inner class ItemViewHolder(var mBinding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(data: ImageModel, position: Int) {
            val imageUrl = data.imageUrl

            val options: RequestOptions = RequestOptions()
                .centerCrop()
                .placeholder(generateRoundCornerPlaceholder(mContext = mContext))
                .error(generateRoundCornerPlaceholder(mContext = mContext))
                .priority(Priority.HIGH)
            Glide.with(mContext)
                .load(imageUrl)
                .apply(options)
                .into(mBinding.ivPhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

}
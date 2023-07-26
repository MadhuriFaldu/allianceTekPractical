package com.example.alliancetekpractical.ui.users

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alliancetekpractical.databinding.ItemUsersBinding

class UserAdapter(
    var mContext: Context,
    var list: ArrayList<UserModel>) :
    RecyclerView.Adapter<UserAdapter.ItemViewHolder>() {


    inner class ItemViewHolder(var mBinding: ItemUsersBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(data: UserModel, position: Int) {

            mBinding.tvUserName.text = data.firstName +" "+ data.lastName
            mBinding.tvEmail.text = data.email
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemUsersBinding.inflate(
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
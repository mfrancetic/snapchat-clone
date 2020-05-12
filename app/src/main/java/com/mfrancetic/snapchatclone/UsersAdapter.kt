package com.mfrancetic.snapchatclone

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mfrancetic.snapchatclone.R.layout.received_snaps_list_item

class UsersAdapter(
    private val context: Context,
    private val users: ArrayList<String>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = inflater.inflate(received_snaps_list_item, parent, false)

        val email = itemView.findViewById(R.id.received_snap_text_view) as TextView
        val user = getItem(position) as String
        email.text = user

        return itemView
    }

    override fun getItem(position: Int): Any {
        return users[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return users.size
    }
}
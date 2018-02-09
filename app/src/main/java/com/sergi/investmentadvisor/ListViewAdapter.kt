package com.sergi.investmentadvisor

import android.content.Context
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton


/**
 * Created by smartinez on 08/02/2018.
 */
class ListViewAdapter(context: Context, moneys: ArrayList<Money>) : ArrayAdapter<Money>(context, R.layout.item_main_list_view, moneys) {
    // View lookup cache
    private class ViewHolder {
        internal var description: TextView? = null
        internal var amount: TextView? = null
        internal var deleteBtn: ImageButton? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        // Get the data item for this position
        val money = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag
        if (view == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_main_list_view, parent, false)
            viewHolder.description = view!!.findViewById(R.id.textViewDescription)
            viewHolder.amount = view.findViewById(R.id.textViewAmount)
            viewHolder.deleteBtn = view.findViewById(R.id.deleteBtn)
            // Cache the viewHolder object inside the fresh view
            view.setTag(viewHolder)
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = view.getTag() as ViewHolder
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.description!!.setText(money!!.type + ": " + money.name)
        viewHolder.amount!!.setText(money.amount.toString())
        viewHolder.deleteBtn!!.setOnClickListener({
            (MainActivity).db.deleteMoney(money)
            (MainActivity).instance.updateData()
        })

        // Return the completed view to render on screen
        return view
    }
}
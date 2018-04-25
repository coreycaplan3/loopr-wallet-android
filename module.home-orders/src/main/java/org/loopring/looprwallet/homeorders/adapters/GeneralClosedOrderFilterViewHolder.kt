package org.loopring.looprwallet.homeorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_closed_order_filter.*
import org.loopring.looprwallet.core.utilities.ApplicationUtility.str
import org.loopring.looprwallet.homeorders.R

/**
 * Created by Corey on 4/10/2018
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class: To display the filter options for filled or cancelled order tab.
 */
class GeneralClosedOrderFilterViewHolder(
        itemView: View,
        listener: OnGeneralOrderFilterChangeListener
) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View
        get() = itemView

    fun bind(dateValue: String, listener: OnGeneralOrderFilterChangeListener) {
        setButtonClickListener(orderClosedDate1hButton, listener)
        setButtonClickListener(orderClosedDate1dButton, listener)
        setButtonClickListener(orderClosedDate1wButton, listener)
        setButtonClickListener(orderClosedDate1mButton, listener)
        setButtonClickListener(orderClosedDate1yButton, listener)
        setButtonClickListener(orderClosedDateAllButton, listener)

        // Reset their UI values
        orderClosedDate1hButton.context.setTheme(R.style.App_Button_Borderless)
        orderClosedDate1dButton.context.setTheme(R.style.App_Button_Borderless)
        orderClosedDate1wButton.context.setTheme(R.style.App_Button_Borderless)
        orderClosedDate1mButton.context.setTheme(R.style.App_Button_Borderless)
        orderClosedDate1yButton.context.setTheme(R.style.App_Button_Borderless)
        orderClosedDateAllButton.context.setTheme(R.style.App_Button_Borderless)

        // Set the selected one's value
        when (dateValue) {
            str(R.string._1h) -> orderClosedDate1hButton.context.setTheme(R.style.App_Button)
            str(R.string._1d) -> orderClosedDate1dButton.context.setTheme(R.style.App_Button)
            str(R.string._1w) -> orderClosedDate1wButton.context.setTheme(R.style.App_Button)
            str(R.string._1m) -> orderClosedDate1mButton.context.setTheme(R.style.App_Button)
            str(R.string._1y) -> orderClosedDate1yButton.context.setTheme(R.style.App_Button)
            str(R.string.all) -> orderClosedDateAllButton.context.setTheme(R.style.App_Button)
        }
    }

    // MARK - Private Methods

    private fun setButtonClickListener(button: Button, listener: OnGeneralOrderFilterChangeListener) {
        button.setOnClickListener {
            if(listener.getCurrentDateFilterChange() != button.text.toString()) {
                // The user clicked on a different date filter change
                listener.onDateFilterChange(button.text.toString())
            }
        }
    }

}
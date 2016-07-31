package app.com.work.shimonaj.helpdx.data;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.work.shimonaj.helpdx.R;

/**
 * Created by shimonaj on 5/10/2016.
 */
public class TicketDetailViewHolder extends RecyclerView.ViewHolder {
    public ImageView stageImg;
    public TextView titleView;
    public TextView subTitleView;
    public TextView descView;
    public TextView ticketId;
    public TextView category;

    public View mView;
    public TicketDetailViewHolder(View view) {
        super(view);
        mView=view;
        titleView = (TextView) view.findViewById(R.id.detail_title);
       subTitleView= (TextView) view.findViewById(R.id.detail_subtitle);
        descView = (TextView) view.findViewById(R.id.detail_desc);
        category = (TextView) view.findViewById(R.id.category);
      //  stageName = (TextView) view.findViewById(R.id.stagename);
        ticketId= (TextView) view.findViewById(R.id.ticketid);
     //   stageImg= (ImageView) view.findViewById(R.id.list_item_icon);

    }
}
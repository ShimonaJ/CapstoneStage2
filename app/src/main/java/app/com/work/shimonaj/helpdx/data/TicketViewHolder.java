package app.com.work.shimonaj.helpdx.data;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.work.shimonaj.helpdx.R;

/**
 * Created by shimonaj on 5/5/2016.
 */
public  class TicketViewHolder extends RecyclerView.ViewHolder {
    //public ImageView stageImg;
    public TextView titleView;
    public TextView stageName;
    public TextView ticketId;
    public TextView category;
    public TextView assignedTo;
    public TextView createdOn;
    public TextView positionId;
    public  View mView;
    public TicketViewHolder(View view) {
        super(view);
        mView=view;
        titleView = (TextView) view.findViewById(R.id.title);
        stageName = (TextView) view.findViewById(R.id.stagename);
        ticketId= (TextView) view.findViewById(R.id.ticketid);
        assignedTo=(TextView) view.findViewById(R.id.assignedTo);
        //stageImg= (ImageView) view.findViewById(R.id.stageImg);
        category= (TextView) view.findViewById(R.id.category);
        createdOn = (TextView) view.findViewById(R.id.createdOn);
        positionId = (TextView) view.findViewById(R.id.positionId);

    }
}
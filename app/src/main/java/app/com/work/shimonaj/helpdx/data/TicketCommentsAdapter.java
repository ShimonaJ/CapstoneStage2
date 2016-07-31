package app.com.work.shimonaj.helpdx.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import app.com.work.shimonaj.helpdx.R;
import app.com.work.shimonaj.helpdx.TicketDetailActivity;

import app.com.work.shimonaj.helpdx.remote.Config;
import app.com.work.shimonaj.helpdx.util.Utility;

/**
 * Created by shimonaj on 5/17/2016.
 */
public class TicketCommentsAdapter  extends RecyclerView.Adapter<TicketCommentViewHolder> {
    private Cursor mCursor;
    float offset ;
    Interpolator interpolator;
    Random random;
    float maxWidthOffset;
    float maxHeightOffset;
    public TicketCommentsAdapter(Cursor cursor,Activity activity) {
        mCursor = cursor;
        host=activity;
        offset = activity.getResources().getDimensionPixelSize(R.dimen.offset_y);
        inflater = LayoutInflater.from(host);
        interpolator =
                AnimationUtils.loadInterpolator(host, android.R.interpolator.linear_out_slow_in);
        random = new Random();
      maxWidthOffset = 2f * host.getResources().getDisplayMetrics().widthPixels;
         maxHeightOffset = 2f * host.getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(TicketCommentsLoader.Query._ID);
    }
    private Activity host;
    private  LayoutInflater inflater;
    @Override
    public TicketCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.ticket_comment_item, parent, false);
        final TicketCommentViewHolder vh = new TicketCommentViewHolder(view);


        view.setVisibility(View.VISIBLE);
        view.setAlpha(0.85f);
        float xOffset = random.nextFloat() * maxWidthOffset;
        if (random.nextBoolean()) {
            xOffset *= -1;
        }
        view.setTranslationX(xOffset);
        float yOffset = random.nextFloat() * maxHeightOffset;
        if (random.nextBoolean()) {
            yOffset *= -1;
        }
        view.setTranslationY(yOffset);

        // now animate them back into their natural position
        view.animate()
                .translationY(0f)
                .translationX(0f)
                .alpha(1f)
                .setInterpolator(interpolator)
                .setDuration(500)
                .start();
        return vh;
    }

    @Override
    public void onBindViewHolder(final TicketCommentViewHolder holder,final int position) {

        mCursor.moveToPosition(position);
        holder.commentedByView.setText(mCursor.getString(TicketCommentsLoader.Query.COMMENTEDBY));
        holder.commentTitleView.setText(" replied on "+mCursor.getString(TicketCommentsLoader.Query.COMMENTEDON)+"");
        holder.commentTextView.setText(mCursor.getString(TicketCommentsLoader.Query.COMMENT));
        holder.commentTitleView.setTypeface(Utility.mediumRobotoFont);

        holder.commentedByView.setTypeface(Utility.mediumRobotoFont);

        holder.commentTextView.setTypeface(Utility.regularRobotoFont);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }




}
  class TicketCommentViewHolder extends RecyclerView.ViewHolder {

    public TextView commentTextView;
    public TextView commentTitleView;
      public TextView commentedByView;
    public TicketCommentViewHolder(View view) {
        super(view);

        commentTextView = (TextView) view.findViewById(R.id.commentText);
        commentTitleView = (TextView) view.findViewById(R.id.commentTitle);
        commentedByView = (TextView) view.findViewById(R.id.commentedBy);
    }
}
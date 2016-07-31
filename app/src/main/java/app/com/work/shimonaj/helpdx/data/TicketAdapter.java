package app.com.work.shimonaj.helpdx.data;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

import app.com.work.shimonaj.helpdx.MainActivity;
import app.com.work.shimonaj.helpdx.R;
import app.com.work.shimonaj.helpdx.TicketDetailActivity;

import app.com.work.shimonaj.helpdx.remote.Config;
import app.com.work.shimonaj.helpdx.util.Utility;

/**
 * Created by shimonaj on 5/5/2016.
 */
public class TicketAdapter extends RecyclerView.Adapter<TicketViewHolder> {
    private Cursor mCursor;
    float offset ;
    Interpolator interpolator;

    public TicketAdapter(Cursor cursor,Activity activity) {
        mCursor = cursor;
        host=activity;
        offset = activity.getResources().getDimensionPixelSize(R.dimen.offset_y);
        inflater = LayoutInflater.from(host);
        interpolator =
                AnimationUtils.loadInterpolator(host, android.R.interpolator.linear_out_slow_in);

    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(TicketLoader.Query._ID);
    }
    private Activity host;
    private  LayoutInflater inflater;
    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.ticket_list_item, parent, false);
        final TicketViewHolder vh = new TicketViewHolder(view);
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(offset);
        view.setAlpha(0.85f);
        // then animate back to natural position
        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setInterpolator(interpolator)
                .setDuration(500L)
                .start();
        // increase the offset distance for the next view
        offset *= 1.5f;
        return vh;
    }
    @ColorInt
    public static final int RED         = 0xD32F2F;
    @ColorInt
    public static final int GREEN         = 0x43A047;
    @Override
    public void onBindViewHolder(final TicketViewHolder holder,final int position) {

        mCursor.moveToPosition(position);
        holder.titleView.setText("#"+mCursor.getString(TicketLoader.Query.TICKETID)+" "+mCursor.getString(TicketLoader.Query.TITLE));
        holder.titleView.setTypeface(Utility.regularRobotoFont);

        String stagename =mCursor.getString(TicketLoader.Query.STAGENAME);
        holder.stageName.setText(stagename);
        holder.stageName.setTypeface(Utility.regularRobotoFont);
        if(stagename.equals("Open")){
            holder.stageName.setBackgroundResource( R.color.red);
        }else if(stagename.equals("Closed")){
            holder.stageName.setBackgroundResource( R.color.green);
        }
        holder.ticketId.setText(mCursor.getString(TicketLoader.Query.TICKETID));
        holder.positionId.setText(String.valueOf(position));
        holder.createdOn.setText(mCursor.getString(TicketLoader.Query.CREATEDON));
        holder.createdOn.setTypeface(Utility.regularRobotoFont);
        String assignedTo =mCursor.getString(TicketLoader.Query.ASSIGNEDTO);
       //String concat = assignedTo==""?"":" ,"+assignedTo;
        String cat =mCursor.getString(TicketLoader.Query.CATEGORYNAME);
        holder.category.setText(cat.equals("")?"No Category":cat);
        holder.assignedTo.setText(mCursor.getString(TicketLoader.Query.ASSIGNEDTO));
        String assign =mCursor.getString(TicketLoader.Query.ASSIGNEDTO);
        holder.assignedTo.setText(assign.equals("")?"Unassigned":assign);

        holder.category.setTypeface(Utility.regularRobotoFont);
        holder.assignedTo.setTypeface(Utility.regularRobotoFont);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (Config.mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(Config.TicketId, holder.ticketId.getText().toString());

//                    TicketDetailFragment fragment = new TicketDetailFragment();
//                    fragment.setArguments(arguments);
////                    host.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
////                            host, holder.thumbnailView, holder.thumbnailView.getTransitionName()).toBundle());
//
//
//
//                    host.getFragmentManager()
//                            .beginTransaction()
//                            .addSharedElement(holder.stageImg, "stageTransition")
//                            .replace(R.id.ticket_detail_container, fragment)
//                            .addToBackStack(null)
//                            .commit();
                } else {



                    final  Context context = v.getContext();
                    final Intent intent = new Intent(context, TicketDetailActivity.class);
                    Utility.putKeyValInSharedPref(context,Config.TicketId,holder.ticketId.getText().toString());
                    Utility.putKeyValInSharedPref(context,MainActivity.SELECTED_LIST_POS_KEY,holder.positionId.getText().toString());
                    context.startActivity(intent);





//                    context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
//                            host, holder.stageImg, holder.stageImg.getTransitionName()).toBundle());

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }



}
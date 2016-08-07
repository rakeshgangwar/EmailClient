package com.rakeshgangwar.emailclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rakeshgangwar.emailclient.EmailContentActivity;
import com.rakeshgangwar.emailclient.R;
import com.rakeshgangwar.emailclient.objects.MessageSummary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Rakesh on 8/7/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<MessageSummary> messageSummary;
    private Context context;
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView subject;
        public TextView preview;
        public TextView participants;
        public TextView timestamp;
        public TextView itemId;
        public ImageButton starButton;
        public ViewHolder(View itemView) {
            super(itemView);
            subject=(TextView)itemView.findViewById(R.id.subject);
            preview=(TextView)itemView.findViewById(R.id.preview);
            participants=(TextView)itemView.findViewById(R.id.participants);
            timestamp=(TextView)itemView.findViewById(R.id.timestamp);
            itemId=(TextView)itemView.findViewById(R.id.itemId);
            starButton=(ImageButton)itemView.findViewById(R.id.star_button);
        }

    }

    public void add(int position, MessageSummary item){
        messageSummary.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position){
        messageSummary.remove(position);
        notifyItemRemoved(position);
    }

    public RecyclerViewAdapter(List<MessageSummary> messageSummary, Context context) {
        this.messageSummary = messageSummary;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MessageSummary name=messageSummary.get(position);
        String participants="";
        Date simpleDate=new Date(name.getTs());
        SimpleDateFormat df=new SimpleDateFormat("dd MMM");
        holder.subject.setText(name.getSubject());
        holder.preview.setText(name.getPreview());
        holder.timestamp.setText(df.format(simpleDate));
        holder.itemId.setText(name.getId().toString());
        participants=name.getParticipants().get(0);
        if(name.getParticipants().size()>1){
            for(int i=1;i<name.getParticipants().size();i++){
                participants=participants+", "+name.getParticipants().get(i);
            }
        }
        holder.participants.setText(participants);
        if(name.getIsStarred()){
            holder.starButton.setImageResource(R.mipmap.star_on);
        }
        else {
            holder.starButton.setImageResource(R.mipmap.star_off);
        }


        holder.subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, EmailContentActivity.class);
                intent.putExtra("id", name.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return messageSummary.size();
    }
}

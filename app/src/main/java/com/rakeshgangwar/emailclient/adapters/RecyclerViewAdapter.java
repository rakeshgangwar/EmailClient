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
 * RecyclerViewAdapter - Adapter for binding email data to RecyclerView list items.
 *
 * This adapter implements the ViewHolder pattern for efficient RecyclerView performance.
 * It handles:
 * - Binding MessageSummary data to individual list item views
 * - Formatting timestamps and participant names
 * - Setting star icon based on email's starred status
 * - Handling click events on email subject to navigate to detail view
 *
 * @author Rakesh Gangwar
 * @version 1.0
 * @see MessageSummary
 * @see EmailContentActivity
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    /** List of email summaries to display */
    private List<MessageSummary> messageSummary;

    /** Context for creating intents and accessing resources */
    private Context context;

    /**
     * ViewHolder pattern implementation for caching view references.
     * This improves scrolling performance by avoiding repeated findViewById() calls.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /** TextView displaying the email subject */
        public TextView subject;

        /** TextView displaying the email preview (first line of body) */
        public TextView preview;

        /** TextView displaying participant names */
        public TextView participants;

        /** TextView displaying the email timestamp */
        public TextView timestamp;

        /** Hidden TextView storing the email ID for delete operations */
        public TextView itemId;

        /** ImageButton showing starred/unstarred status */
        public ImageButton starButton;

        /**
         * Constructs a ViewHolder and caches references to all child views.
         *
         * @param itemView The root view of the list item layout
         */
        public ViewHolder(View itemView) {
            super(itemView);
            subject = (TextView) itemView.findViewById(R.id.subject);
            preview = (TextView) itemView.findViewById(R.id.preview);
            participants = (TextView) itemView.findViewById(R.id.participants);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            itemId = (TextView) itemView.findViewById(R.id.itemId);
            starButton = (ImageButton) itemView.findViewById(R.id.star_button);
        }

    }

    /**
     * Adds an email to the list at the specified position.
     * Note: This method is currently not used in the application.
     *
     * @param position The position where the item should be inserted
     * @param item The MessageSummary to add
     */
    public void add(int position, MessageSummary item) {
        messageSummary.add(position, item);
        notifyItemInserted(position);
    }

    /**
     * Removes an email from the list at the specified position.
     * Note: This method is currently not used (swipe delete is handled in MainActivity).
     *
     * @param position The position of the item to remove
     */
    public void remove(int position) {
        messageSummary.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Constructs the adapter with a list of email summaries.
     *
     * @param messageSummary The list of MessageSummary objects to display
     * @param context The context for creating intents and accessing resources
     */
    public RecyclerViewAdapter(List<MessageSummary> messageSummary, Context context) {
        this.messageSummary = messageSummary;
        this.context = context;
    }

    /**
     * Creates a new ViewHolder by inflating the row_item layout.
     * This is called by RecyclerView when it needs a new ViewHolder to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View (not used in this implementation)
     * @return A new ViewHolder that holds a View of the row_item layout
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * Binds data from MessageSummary to the ViewHolder's views.
     * This is called by RecyclerView to display data at the specified position.
     *
     * Operations performed:
     * 1. Formats and sets the timestamp (e.g., "15 Nov")
     * 2. Sets subject, preview, and ID
     * 3. Concatenates participant names with commas
     * 4. Sets appropriate star icon based on starred status
     * 5. Attaches click listener to navigate to detail view
     *
     * @param holder The ViewHolder which should be updated
     * @param position The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the email summary for this position
        final MessageSummary message = messageSummary.get(position);

        // Format timestamp to "dd MMM" format (e.g., "15 Nov")
        Date simpleDate = new Date(message.getTs());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");

        // Set basic email information
        holder.subject.setText(message.getSubject());
        holder.preview.setText(message.getPreview());
        holder.timestamp.setText(dateFormat.format(simpleDate));
        holder.itemId.setText(message.getId().toString());

        // Concatenate participant names with commas
        String participants = "";
        if (message.getParticipants() != null && !message.getParticipants().isEmpty()) {
            participants = message.getParticipants().get(0);
            if (message.getParticipants().size() > 1) {
                for (int i = 1; i < message.getParticipants().size(); i++) {
                    participants = participants + ", " + message.getParticipants().get(i);
                }
            }
        }
        holder.participants.setText(participants);

        // Set star icon based on starred status
        if (message.getIsStarred()) {
            holder.starButton.setImageResource(R.mipmap.star_on);
        } else {
            holder.starButton.setImageResource(R.mipmap.star_off);
        }
        // Note: Star button has no click handler - feature not implemented

        // Set click listener on subject to open email detail view
        holder.subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to launch EmailContentActivity
                Intent intent = new Intent(context, EmailContentActivity.class);
                intent.putExtra("id", message.getId());
                // FLAG_ACTIVITY_NEW_TASK required when starting activity from non-Activity context
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of email summaries in the list
     */
    @Override
    public int getItemCount() {
        return messageSummary.size();
    }
}

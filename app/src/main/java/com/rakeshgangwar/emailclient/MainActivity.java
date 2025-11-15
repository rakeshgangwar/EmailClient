package com.rakeshgangwar.emailclient;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.rakeshgangwar.emailclient.adapters.RecyclerViewAdapter;
import com.rakeshgangwar.emailclient.objects.MessageSummary;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * MainActivity - Primary screen of the EmailClient application.
 *
 * This activity displays a scrollable list of email summaries using RecyclerView.
 * Features include:
 * - Fetching email list from REST API
 * - Displaying emails with subject, preview, sender, and timestamp
 * - Swipe-to-delete gesture with visual feedback (red background)
 * - Navigation to email detail view on subject click
 *
 * The activity uses Retrofit for network communication and implements
 * ItemTouchHelper for swipe gesture handling.
 *
 * @author Rakesh Gangwar
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    /** RecyclerView for displaying the email list */
    private RecyclerView recyclerView;

    /** Adapter that binds email data to RecyclerView items */
    private RecyclerView.Adapter recyclerViewAdapter;

    /** LayoutManager for RecyclerView (LinearLayout with vertical orientation) */
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    /** Application instance holding configuration like base URL */
    private EmailApplication emailApplication;

    /**
     * Initializes the activity, sets up RecyclerView, and fetches email list.
     *
     * This method performs the following:
     * 1. Sets up RecyclerView with LinearLayoutManager
     * 2. Initializes adapter with empty data list
     * 3. Configures Retrofit for API communication
     * 4. Fetches email summaries from server
     * 5. Sets up swipe-to-delete gesture handling
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the list that will hold email summaries
        final List<MessageSummary> messageSummaries = new ArrayList<>();

        // Setup RecyclerView for displaying emails
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);  // Performance optimization when size is fixed

        // Configure vertical linear layout for the list
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        // Initialize and set adapter for binding data to views
        recyclerViewAdapter = new RecyclerViewAdapter(messageSummaries, getApplicationContext());
        recyclerView.setAdapter(recyclerViewAdapter);

        // Add divider lines between list items
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        // TODO: Fix - Should use getApplication() instead of new EmailApplication()
        emailApplication = new EmailApplication();


        // Configure Retrofit for API communication
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(emailApplication.getBaseUrl())  // Set base URL for all requests
                .addConverterFactory(GsonConverterFactory.create())  // Enable JSON conversion
                .build();

        // Create service interface implementation
        final EmailService service = retrofit.create(EmailService.class);

        // Fetch email list from server asynchronously
        Call<List<MessageSummary>> call = service.getDetailsList();
        call.enqueue(new Callback<List<MessageSummary>>() {
            /**
             * Called when the API request completes successfully.
             * Adds all received email summaries to the local list and updates the UI.
             *
             * @param call The original call object
             * @param response The response containing list of MessageSummary objects
             */
            @Override
            public void onResponse(Call<List<MessageSummary>> call, Response<List<MessageSummary>> response) {
                // TODO: Add null check for response.body()
                for (MessageSummary summary : response.body()) {
                    messageSummaries.add(summary);
                }
                // Notify adapter to refresh the RecyclerView
                recyclerViewAdapter.notifyDataSetChanged();
            }

            /**
             * Called when the API request fails due to network error or other issues.
             *
             * @param call The original call object
             * @param t The exception that caused the failure
             */
            @Override
            public void onFailure(Call<List<MessageSummary>> call, Throwable t) {
                // TODO: Show user-friendly error message instead of just logging
                Log.e(TAG, "Failed to fetch email list: " + t.getMessage());
            }
        });
        /*
         * Configure swipe-to-delete gesture handler.
         * Allows swiping left or right on email items to delete them.
         * Provides visual feedback with a red background during swipe.
         */
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0,  // No drag movement
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT  // Enable swipe in both directions
        ) {

            /**
             * Called when an item is dragged (not used in this implementation).
             *
             * @return false because drag-and-drop is not supported
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;  // Drag-and-drop not implemented
            }

            /**
             * Called when an item is fully swiped left or right.
             * Removes the email from local list and sends delete request to server.
             *
             * @param viewHolder The ViewHolder of the swiped item
             * @param swipeDir The direction of the swipe (LEFT or RIGHT)
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Remove email from local list immediately for responsive UI
                messageSummaries.remove(viewHolder.getAdapterPosition());

                // Extract email ID from the ViewHolder and send delete request to server
                int emailId = Integer.parseInt(
                        ((RecyclerViewAdapter.ViewHolder) viewHolder).itemId.getText().toString()
                );
                Call<ResponseBody> responseCall = service.deleteMessage(emailId);
                responseCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        // TODO: Verify deletion was successful
                        Log.d(TAG, "Email deleted successfully");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // TODO: Handle error - consider restoring the deleted item
                        Log.e(TAG, "Failed to delete email: " + t.getMessage());
                    }
                });

                // Update the RecyclerView to reflect the deletion
                recyclerViewAdapter.notifyDataSetChanged();
            }

            /**
             * Draws the red background behind the item as it's being swiped.
             * Provides visual feedback to the user that a delete action is in progress.
             *
             * @param c Canvas to draw on
             * @param recyclerView The RecyclerView to which ItemTouchHelper is attached
             * @param viewHolder The ViewHolder being swiped
             * @param dX The amount of horizontal displacement (negative for left, positive for right)
             * @param dY The amount of vertical displacement
             * @param actionState The type of interaction (SWIPE in this case)
             * @param isCurrentlyActive True if the user is currently controlling the view
             */
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;

                    // Create red paint for delete background
                    Paint p = new Paint();
                    p.setARGB(255, 255, 0, 0);  // Red color (TODO: Use color resource)

                    // Draw red background in the direction of the swipe
                    if (dX > 0) {
                        // Swiping to the right - draw from left edge
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);
                    } else {
                        // Swiping to the left - draw from right edge
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);
                    }

                    // Call super to draw the item view on top of the background
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };

        // Attach the swipe handler to the RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Initial notification (unnecessary since list is empty at this point)
        // TODO: Remove this redundant call
        recyclerViewAdapter.notifyDataSetChanged();
    }
}


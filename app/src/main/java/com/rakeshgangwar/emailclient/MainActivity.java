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

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private EmailApplication emailApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final List<MessageSummary> messageSummaries=new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter=new RecyclerViewAdapter(messageSummaries, getApplicationContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        emailApplication=new EmailApplication();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(emailApplication.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final EmailService service=retrofit.create(EmailService.class);

        Call<List<MessageSummary>> call=service.getDetailsList();
        call.enqueue(new Callback<List<MessageSummary>>() {
            @Override
            public void onResponse(Call<List<MessageSummary>> call, Response<List<MessageSummary>> response) {
                for(MessageSummary summary : response.body()){
                    messageSummaries.add(summary);
                }
                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<MessageSummary>> call, Throwable t) {
                Log.d(null, "Fail");
            }
        });
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                messageSummaries.remove(viewHolder.getAdapterPosition());
                Call<ResponseBody> responseCall=service.deleteMessage(Integer.parseInt(((RecyclerViewAdapter.ViewHolder) viewHolder).itemId.getText().toString()));
                responseCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d(null, "Hello");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(null, "Error Occured");
                    }
                });
                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    Paint p = new Paint();
                    p.setARGB(255, 255, 0, 0);
                    if (dX > 0) {
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);
                    } else {
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerViewAdapter.notifyDataSetChanged();
    }
}


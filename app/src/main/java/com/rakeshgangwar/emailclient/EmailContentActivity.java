package com.rakeshgangwar.emailclient;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.rakeshgangwar.emailclient.objects.CompleteMessage;
import com.rakeshgangwar.emailclient.objects.Participant;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmailContentActivity extends AppCompatActivity {

    private EmailApplication emailApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_data);
        emailApplication=new EmailApplication();


        Intent intent=getIntent();
        int id=intent.getExtras().getInt("id");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(emailApplication.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final EmailService service=retrofit.create(EmailService.class);

        Call<CompleteMessage> completeMessageCall=service.getMessage(id);

        completeMessageCall.enqueue(new Callback<CompleteMessage>() {
            @Override
            public void onResponse(Call<CompleteMessage> call, Response<CompleteMessage> response) {
                Log.d(null, "Success");
                populateContent(response.body());
            }

            @Override
            public void onFailure(Call<CompleteMessage> call, Throwable t) {
                Log.d(null, "Fail");
            }
        });
    }

    public void populateContent(CompleteMessage message){
        TextView subject, participants, body, time;
        subject=(TextView)findViewById(R.id.email_subject);
        participants=(TextView)findViewById(R.id.email_participants);
        body=(TextView)findViewById(R.id.email_body);
        time=(TextView)findViewById(R.id.email_time);
        subject.setText(message.getSubject());
        body.setText(message.getBody());
        Date simpleDate=new Date(message.getTs());
        SimpleDateFormat df=new SimpleDateFormat("dd MMM HH:mm");
        time.setText(df.format(simpleDate));
        String participant="";
        participant=message.getParticipants().get(0).getName();
        if(message.getParticipants().size()>1){
            for(int i=1;i<message.getParticipants().size();i++){
                participant=participant+", "+message.getParticipants().get(i).getName();
            }
        }
        participants.setText(participant+" & me");
    }
}

package com.rakeshgangwar.emailclient;

import com.rakeshgangwar.emailclient.objects.CompleteMessage;
import com.rakeshgangwar.emailclient.objects.MessageSummary;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Rakesh on 8/7/2016.
 */
public interface EmailService {
    @GET("api/message")
    Call<List<MessageSummary>> getDetailsList();

    @GET("api/message/{id}")
    Call<CompleteMessage> getMessage(@Path("id") int itemId);

    @DELETE("api/message/{id}")
    Call<ResponseBody> deleteMessage(@Path("id") int itemId);
}
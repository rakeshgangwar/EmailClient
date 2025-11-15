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
 * EmailService - Retrofit service interface defining the email API contract.
 *
 * This interface defines all REST API endpoints for email operations.
 * Retrofit automatically generates the implementation at runtime based on these annotations.
 *
 * Base URL is configured in EmailApplication.java (default: http://192.168.0.160:8088/)
 *
 * API Endpoints:
 * - GET /api/message - Retrieve all email summaries
 * - GET /api/message/{id} - Retrieve specific email by ID
 * - DELETE /api/message/{id} - Delete email by ID
 *
 * @author Rakesh Gangwar
 * @version 1.0
 * @see MessageSummary
 * @see CompleteMessage
 */
public interface EmailService {

    /**
     * Fetches the list of all email summaries from the server.
     *
     * This endpoint returns lightweight email data suitable for list view display.
     * The response includes subject, preview, participants (as strings), timestamp,
     * and metadata like isRead and isStarred flags.
     *
     * @return Call object that can be executed to get List of MessageSummary objects
     */
    @GET("api/message")
    Call<List<MessageSummary>> getDetailsList();

    /**
     * Fetches complete email details for a specific message by ID.
     *
     * This endpoint returns the full email data including the complete body text
     * and participants as Participant objects (with name and email fields).
     *
     * @param itemId The unique identifier of the email to retrieve
     * @return Call object that can be executed to get a CompleteMessage object
     */
    @GET("api/message/{id}")
    Call<CompleteMessage> getMessage(@Path("id") int itemId);

    /**
     * Deletes an email from the server by ID.
     *
     * This permanently removes the email from the server. The operation cannot be undone.
     * The client should handle potential errors and consider implementing an undo feature.
     *
     * @param itemId The unique identifier of the email to delete
     * @return Call object that can be executed to get the server response
     */
    @DELETE("api/message/{id}")
    Call<ResponseBody> deleteMessage(@Path("id") int itemId);
}
package com.rakeshgangwar.emailclient.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * MessageSummary - Data model representing a lightweight email summary for list view.
 *
 * This class is used to display email information in the main list (RecyclerView).
 * It contains essential fields for list display without the full email body,
 * making it more efficient for network transfer and memory usage.
 *
 * Note: Participants are represented as simple strings (e.g., email addresses)
 * rather than Participant objects, which differs from CompleteMessage.
 *
 * JSON mapping: Fields are automatically mapped by Gson from the API response.
 *
 * @author Rakesh Gangwar
 * @version 1.0
 * @see CompleteMessage
 */
public class MessageSummary {

    /** The email subject line */
    private String subject;

    /** List of participant identifiers (email addresses or names as strings) */
    private List<String> participants = new ArrayList<String>();

    /** Preview text (usually the first line of the email body) */
    private String preview;

    /** Flag indicating whether the email has been read */
    private Boolean isRead;

    /** Flag indicating whether the email is starred/favorited */
    private Boolean isStarred;

    /** Unix timestamp in milliseconds when the email was sent/received */
    private long ts;

    /** Unique identifier for the email */
    private Integer id;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getIsStarred() {
        return isStarred;
    }

    public void setIsStarred(Boolean isStarred) {
        this.isStarred = isStarred;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}

package com.rakeshgangwar.emailclient.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * CompleteMessage - Data model representing a full email with all details.
 *
 * This class is used in the email detail view (EmailContentActivity) and contains
 * the complete email information including the full body text.
 *
 * Key differences from MessageSummary:
 * - Includes the full email body (not just preview)
 * - Participants are Participant objects (with name and email fields)
 * - Used for detail view instead of list view
 *
 * Note: There's a type inconsistency - timestamp (ts) is Integer here but long
 * in MessageSummary. Should be standardized to long for Unix timestamps.
 *
 * JSON mapping: Fields are automatically mapped by Gson from the API response.
 *
 * @author Rakesh Gangwar
 * @version 1.0
 * @see MessageSummary
 * @see Participant
 */
public class CompleteMessage {

    /** The email subject line */
    private String subject;

    /** List of email participants with full name and email details */
    private List<Participant> participants = new ArrayList<Participant>();

    /** Preview text (first line of body, may be redundant with body field) */
    private String preview;

    /** Flag indicating whether the email has been read */
    private Boolean isRead;

    /** Flag indicating whether the email is starred/favorited */
    private Boolean isStarred;

    /** Unique identifier for the email */
    private Integer id;

    /** The complete email message body text */
    private String body;

    /** Unix timestamp (Note: Should be long for consistency with MessageSummary) */
    private Integer ts;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public Boolean getStarred() {
        return isStarred;
    }

    public void setStarred(Boolean starred) {
        isStarred = starred;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getTs() {
        return ts;
    }

    public void setTs(Integer ts) {
        this.ts = ts;
    }
}

package com.rakeshgangwar.emailclient.objects;

/**
 * Participant - Data model representing an email participant (sender or recipient).
 *
 * This class contains detailed information about a person involved in an email.
 * It is used within CompleteMessage to provide full participant details including
 * both display name and email address.
 *
 * Usage:
 * - Part of CompleteMessage participant list
 * - Provides structured data for displaying sender/recipient information
 * - Allows for proper formatting of participant names in the UI
 *
 * JSON mapping: Fields are automatically mapped by Gson from the API response.
 *
 * @author Rakesh Gangwar
 * @version 1.0
 * @see CompleteMessage
 */
public class Participant {

    /** The display name of the participant (e.g., "John Doe") */
    private String name;

    /** The email address of the participant (e.g., "john.doe@example.com") */
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

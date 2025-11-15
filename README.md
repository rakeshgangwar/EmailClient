# EmailClient

An Android email client application developed as a technical demonstration for CloudMagic. This app showcases modern Android development practices including RecyclerView, Retrofit networking, and Material Design principles.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Technology Stack](#technology-stack)
- [API Documentation](#api-documentation)
- [Known Issues](#known-issues)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

## Overview

EmailClient is a lightweight Android application that demonstrates email management functionality by connecting to a RESTful backend API. The app provides a clean, Material Design interface for viewing, reading, and deleting emails.

This project was created as a development task to showcase:
- RESTful API integration using Retrofit
- Efficient list rendering with RecyclerView
- Swipe gestures for user interactions
- Material Design UI components

## Features

### Core Functionality

- **Email List View**: Display all emails in a scrollable list with sender, subject, preview, and timestamp
- **Email Detail View**: View complete email content including full message body and participants
- **Swipe to Delete**: Intuitive swipe left/right gesture to delete emails with visual feedback (red background)
- **Star Indicators**: Visual indication of starred/favorite emails
- **Material Design**: Modern UI with CardView components and proper spacing

### Technical Features

- Asynchronous network calls with Retrofit 2
- Efficient list rendering with ViewHolder pattern
- Custom item decorations for list dividers
- Proper date formatting for timestamps
- Smooth animations and transitions

## Screenshots

> Note: Add screenshots of your app here when available

## Prerequisites

Before building and running this application, ensure you have:

- **Android Studio**: Version 2.1.2 or higher
- **Android SDK**: API Level 23 (Android 6.0 Marshmallow)
- **Build Tools**: Version 23.0.3
- **JDK**: Java Development Kit 7 or 8
- **Minimum Android Version**: API 21 (Android 5.0 Lollipop) or higher
- **Backend Server**: A running instance of the email API server

### System Requirements

- **Target SDK**: 23
- **Minimum SDK**: 21
- **Compile SDK**: 23

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/rakeshgangwar/EmailClient.git
cd EmailClient
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Select "Open an existing Android Studio project"
3. Navigate to the cloned repository folder
4. Click "OK" and wait for Gradle sync to complete

### 3. Configure the API Endpoint

Before running the app, you must configure the backend server URL:

1. Open `app/src/main/java/com/rakeshgangwar/emailclient/EmailApplication.java`
2. Update the `BASE_URL` variable with your server address:

```java
private String BASE_URL = "http://YOUR_SERVER_IP:PORT/";
```

**Important**: The default URL `http://192.168.0.160:8088/` is a local development address and will only work on the same network.

### 4. Build the Project

```bash
./gradlew build
```

Or use Android Studio's Build menu: `Build > Make Project`

### 5. Run the Application

- Connect an Android device via USB (with USB debugging enabled) or start an emulator
- Click the "Run" button in Android Studio or use:

```bash
./gradlew installDebug
```

## Configuration

### API Server Configuration

The application requires a backend server with the following REST endpoints:

- `GET /api/message` - Retrieve list of email summaries
- `GET /api/message/{id}` - Retrieve complete email by ID
- `DELETE /api/message/{id}` - Delete email by ID

### Network Permissions

The app requires internet permission, which is already configured in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### ProGuard Configuration

ProGuard is currently disabled. To enable code obfuscation for release builds:

1. Open `app/build.gradle`
2. Set `minifyEnabled true` in the release build type
3. Configure ProGuard rules in `proguard-rules.pro`

## Usage

### Viewing Emails

1. Launch the app to see the main inbox screen
2. Emails are displayed in a scrollable list showing:
   - Sender names
   - Email subject
   - Message preview (first line)
   - Timestamp (formatted as "DD MMM")
   - Star indicator for favorites

### Reading Email Details

1. Tap on any email subject in the list
2. The email detail screen opens showing:
   - Full subject line
   - All participants
   - Complete message body
   - Full timestamp (formatted as "DD MMM HH:mm")

### Deleting Emails

1. Swipe left or right on any email in the list
2. A red background appears as you swipe
3. Complete the swipe gesture to delete
4. The email is removed from both the UI and server

### Code Example: Fetching Emails

```java
// Create Retrofit instance
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(emailApplication.getBaseUrl())
    .addConverterFactory(GsonConverterFactory.create())
    .build();

// Create service
EmailService service = retrofit.create(EmailService.class);

// Fetch emails
Call<List<MessageSummary>> call = service.getDetailsList();
call.enqueue(new Callback<List<MessageSummary>>() {
    @Override
    public void onResponse(Call<List<MessageSummary>> call,
                          Response<List<MessageSummary>> response) {
        // Handle success
        List<MessageSummary> emails = response.body();
        // Update UI
    }

    @Override
    public void onFailure(Call<List<MessageSummary>> call, Throwable t) {
        // Handle error
    }
});
```

## Project Structure

```
EmailClient/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/rakeshgangwar/emailclient/
│   │   │   │   ├── adapters/
│   │   │   │   │   └── RecyclerViewAdapter.java    # List adapter with ViewHolder
│   │   │   │   ├── objects/
│   │   │   │   │   ├── CompleteMessage.java        # Full email data model
│   │   │   │   │   ├── MessageSummary.java         # Email summary model
│   │   │   │   │   └── Participant.java            # Email participant model
│   │   │   │   ├── EmailApplication.java           # Application configuration
│   │   │   │   ├── EmailContentActivity.java       # Email detail screen
│   │   │   │   ├── EmailService.java               # Retrofit API interface
│   │   │   │   ├── MainActivity.java               # Main inbox screen
│   │   │   │   └── SimpleDividerItemDecoration.java # List item divider
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml           # Main screen layout
│   │   │   │   │   ├── activity_email_data.xml     # Detail screen layout
│   │   │   │   │   └── row_item.xml                # Email list item layout
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── dimens.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── styles.xml
│   │   │   │   └── drawable/
│   │   │   │       └── line_divider.xml            # Item separator drawable
│   │   │   └── AndroidManifest.xml
│   │   ├── androidTest/                            # Instrumentation tests
│   │   └── test/                                   # Unit tests
│   └── build.gradle                                # App-level build config
├── gradle/                                         # Gradle wrapper files
├── build.gradle                                    # Project-level build config
├── gradle.properties                               # Gradle properties
├── settings.gradle                                 # Project settings
├── docs/                                           # Documentation
│   ├── ARCHITECTURE.md                             # Architecture documentation
│   └── DEVELOPMENT.md                              # Development guide
└── README.md                                       # This file
```

### Key Components

- **MainActivity.java**: Entry point displaying the email list using RecyclerView
- **EmailContentActivity.java**: Detail view for individual emails
- **RecyclerViewAdapter.java**: Adapter binding email data to list items
- **EmailService.java**: Retrofit interface defining API endpoints
- **Data Models**: POJOs for JSON serialization/deserialization
  - `MessageSummary`: Lightweight model for list view
  - `CompleteMessage`: Full model for detail view
  - `Participant`: Email participant information

## Technology Stack

### Core Technologies

- **Language**: Java
- **Platform**: Android SDK 23 (Marshmallow)
- **Build System**: Gradle 2.1.2
- **Min SDK**: API 21 (Lollipop)
- **Target SDK**: API 23 (Marshmallow)

### Libraries and Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| **Retrofit** | 2.1.0 | HTTP client for API calls |
| **Gson Converter** | 2.1.0 | JSON serialization/deserialization |
| **Gson** | 2.6.2 | JSON parsing library |
| **AppCompat** | 23.4.0 | Backward compatibility support |
| **RecyclerView** | 23.4.0 | Efficient list rendering |
| **CardView** | 23.4.0 | Material Design card components |
| **JUnit** | 4.12 | Unit testing framework |

### Architecture Patterns

- **MVC/MVP Hybrid**: Separation of concerns between data, UI, and logic
- **ViewHolder Pattern**: Efficient view recycling in lists
- **Retrofit Service Interface**: Declarative API definitions
- **Observer Pattern**: Asynchronous callbacks for network operations

## API Documentation

### Expected JSON Structure

#### GET /api/message (List Messages)

**Response**: Array of MessageSummary objects

```json
[
  {
    "id": 1,
    "subject": "Welcome to EmailClient",
    "participants": ["sender@example.com", "recipient@example.com"],
    "preview": "This is the first line of the email...",
    "isRead": false,
    "isStarred": true,
    "ts": 1627891200000
  }
]
```

#### GET /api/message/{id} (Get Single Message)

**Response**: CompleteMessage object

```json
{
  "id": 1,
  "subject": "Welcome to EmailClient",
  "participants": [
    {
      "name": "John Doe",
      "email": "john@example.com"
    }
  ],
  "body": "Full email message body goes here...",
  "isRead": false,
  "isStarred": true,
  "ts": 1627891200000
}
```

#### DELETE /api/message/{id} (Delete Message)

**Response**: Success/error status (ResponseBody)

### API Integration

All API calls are handled through the `EmailService` interface:

```java
public interface EmailService {
    @GET("api/message")
    Call<List<MessageSummary>> getDetailsList();

    @GET("api/message/{id}")
    Call<CompleteMessage> getMessage(@Path("id") int id);

    @DELETE("api/message/{id}")
    Call<ResponseBody> deleteMessage(@Path("id") int id);
}
```

## Known Issues

### Critical Issues

1. **Incorrect Application Class Usage** (MainActivity.java:46)
   - Currently instantiates `EmailApplication` with `new EmailApplication()`
   - Should use `getApplication()` or register in AndroidManifest
   - Impact: Configuration not properly shared

2. **No Error Handling**
   - Network failures only log to console
   - No user-facing error messages or retry mechanisms
   - No loading indicators during API calls

3. **Hardcoded Server URL**
   - Local IP address (`192.168.0.160:8088`) hardcoded in source
   - Won't work outside development network
   - Should use BuildConfig or configuration file

4. **Missing Null Checks**
   - No null safety on `response.body()` calls
   - Potential NullPointerException if API returns null

### Feature Limitations

1. **Incomplete Features**
   - Star button displays state but has no click functionality
   - No "mark as read" feature despite `isRead` field
   - DELETE operations don't handle server errors gracefully

2. **No Data Persistence**
   - No local database (Room/SQLite)
   - All data lost on app restart
   - No offline mode support

3. **Security Concerns**
   - HTTP instead of HTTPS (data transmitted in plaintext)
   - No input validation on API responses
   - Vulnerable to man-in-the-middle attacks

### Design Issues

1. **Tight Coupling**
   - Activities directly handle network calls
   - No repository pattern or data layer abstraction
   - Violates Single Responsibility Principle

2. **Memory Leak Risk**
   - Context stored in adapter could cause memory leaks
   - Should use WeakReference or application context

## Future Enhancements

### High Priority

- [ ] Add proper error handling with user-friendly messages
- [ ] Implement loading indicators for network operations
- [ ] Add pull-to-refresh functionality
- [ ] Implement local data persistence (Room database)
- [ ] Switch to HTTPS for secure communication
- [ ] Add proper null safety checks

### Medium Priority

- [ ] Implement "mark as read" functionality
- [ ] Add star/unstar click handlers
- [ ] Create a repository layer for data management
- [ ] Add pagination/infinite scroll for large email lists
- [ ] Implement search functionality
- [ ] Add email composition feature
- [ ] Support for email attachments

### Low Priority

- [ ] Add unit and instrumentation tests
- [ ] Implement push notifications
- [ ] Support email threading/conversations
- [ ] Add multiple account support
- [ ] Implement swipe actions customization
- [ ] Add dark theme support
- [ ] Improve accessibility features

## Contributing

This is a demonstration project for CloudMagic technical assessment. However, contributions are welcome!

### How to Contribute

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards

- Follow Android coding conventions
- Add JavaDoc comments for public methods and classes
- Ensure code is properly formatted
- Write unit tests for new functionality
- Update documentation as needed

## License

This project was created as a technical assessment for CloudMagic. Please refer to the repository owner for licensing information.

## Contact

**Developer**: Rakesh Gangwar

For questions, issues, or feedback, please open an issue on the GitHub repository.

---

**Note**: This application requires a compatible backend API server to function. Ensure your server implements the required endpoints as documented in the [API Documentation](#api-documentation) section.

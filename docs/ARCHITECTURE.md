# Architecture Documentation

## Table of Contents

- [Overview](#overview)
- [Architecture Pattern](#architecture-pattern)
- [System Architecture](#system-architecture)
- [Component Design](#component-design)
- [Data Flow](#data-flow)
- [Design Patterns](#design-patterns)
- [Technology Stack](#technology-stack)
- [API Integration](#api-integration)
- [Data Models](#data-models)
- [UI Architecture](#ui-architecture)
- [Design Decisions](#design-decisions)
- [Technical Debt](#technical-debt)
- [Scalability Considerations](#scalability-considerations)

## Overview

EmailClient is an Android application built using a simplified MVC/MVP hybrid architecture. The application demonstrates fundamental Android development patterns including RESTful API integration, efficient list rendering, and Material Design principles.

### Core Objectives

1. Fetch and display email messages from a remote server
2. Provide an intuitive UI for viewing email details
3. Enable swipe-based email deletion
4. Demonstrate modern Android development practices

## Architecture Pattern

### MVC/MVP Hybrid Pattern

The application follows a simplified Model-View-Controller (MVC) pattern with some MVP (Model-View-Presenter) characteristics:

```
┌─────────────────────────────────────────────────────────────┐
│                       Application Layer                      │
│                    (EmailApplication.java)                   │
│                  Configuration & Base URL                    │
└─────────────────────────────────────────────────────────────┘
                              │
                    ┌─────────┴──────────┐
                    ▼                    ▼
        ┌──────────────────┐   ┌──────────────────┐
        │   View Layer     │   │   Data Layer     │
        │   (Activities    │   │   (Models +      │
        │   + Layouts)     │   │   Services)      │
        └────────┬─────────┘   └─────────┬────────┘
                 │                       │
                 │   ┌───────────────────┘
                 ▼   ▼
        ┌──────────────────┐
        │   Controller     │
        │   (Activities    │
        │   handle both    │
        │   view & logic)  │
        └──────────────────┘
```

**Components:**

- **Model**: Data classes (`MessageSummary`, `CompleteMessage`, `Participant`)
- **View**: XML layouts (`activity_main.xml`, `row_item.xml`, etc.)
- **Controller**: Activities that manage both UI and business logic
- **Service Layer**: Retrofit interface for API communication

## System Architecture

### High-Level Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                        Presentation Layer                         │
│  ┌────────────────┐              ┌──────────────────────┐        │
│  │  MainActivity  │              │ EmailContentActivity │        │
│  │                │              │                      │        │
│  │ - RecyclerView │              │ - Detail Display     │        │
│  │ - Swipe Handler│              │ - Email Body View    │        │
│  └────────┬───────┘              └──────────┬───────────┘        │
└───────────┼───────────────────────────────────┼──────────────────┘
            │                                   │
            ▼                                   ▼
┌──────────────────────────────────────────────────────────────────┐
│                         Adapter Layer                             │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │            RecyclerViewAdapter (ViewHolder)              │    │
│  │  - Binds data to list items                              │    │
│  │  - Handles click events                                  │    │
│  └──────────────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────────────────┘
            │
            ▼
┌──────────────────────────────────────────────────────────────────┐
│                          Service Layer                            │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │               EmailService (Retrofit)                     │    │
│  │  @GET  /api/message          - List emails               │    │
│  │  @GET  /api/message/{id}     - Get email                 │    │
│  │  @DELETE /api/message/{id}   - Delete email              │    │
│  └────────────────────────┬─────────────────────────────────┘    │
└───────────────────────────┼──────────────────────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────────────┐
│                       Network Layer                               │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │         Retrofit 2.1.0 + Gson Converter                  │    │
│  │  - HTTP client                                            │    │
│  │  - JSON serialization/deserialization                    │    │
│  └────────────────────────┬─────────────────────────────────┘    │
└───────────────────────────┼──────────────────────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────────────┐
│                      Backend REST API                             │
│                    http://SERVER_IP:PORT/                         │
└──────────────────────────────────────────────────────────────────┘
```

## Component Design

### 1. MainActivity (`MainActivity.java`)

**Responsibility**: Primary screen displaying email list

**Key Features**:
- RecyclerView setup and configuration
- Network calls to fetch email summaries
- Swipe-to-delete gesture handling
- Visual feedback during swipe (red background)

**Dependencies**:
- `RecyclerViewAdapter`: List item binding
- `EmailService`: API communication
- `SimpleDividerItemDecoration`: List item separators

**Lifecycle**:
```java
onCreate()
  ├─> Initialize RecyclerView
  ├─> Setup LayoutManager
  ├─> Create Adapter
  ├─> Configure Retrofit
  ├─> Fetch email list (async)
  └─> Setup ItemTouchHelper for swipes
```

### 2. EmailContentActivity (`EmailContentActivity.java`)

**Responsibility**: Display full email details

**Key Features**:
- Receives email ID via Intent
- Fetches complete message data
- Formats participant names
- Displays email body and metadata

**Data Flow**:
```
Intent (with email ID)
  → onCreate()
    → Retrofit call to /api/message/{id}
      → Callback with CompleteMessage
        → populateContent()
          → Update UI TextViews
```

### 3. RecyclerViewAdapter (`RecyclerViewAdapter.java`)

**Responsibility**: Bind email data to RecyclerView items

**Pattern**: ViewHolder Pattern

**Structure**:
```java
RecyclerViewAdapter
  ├─> ViewHolder (inner class)
  │     ├─ TextView participants
  │     ├─ TextView timestamp
  │     ├─ TextView subject
  │     ├─ TextView preview
  │     ├─ ImageButton starButton
  │     └─ TextView itemId (hidden)
  │
  ├─> onCreateViewHolder()
  │     └─ Inflate row_item.xml
  │
  ├─> onBindViewHolder()
  │     ├─ Format timestamp
  │     ├─ Set participant names
  │     ├─ Set star icon state
  │     └─ Attach click listener
  │
  └─> getItemCount()
```

**Optimization**: ViewHolder caches view references to avoid repeated `findViewById()` calls

### 4. EmailService (`EmailService.java`)

**Responsibility**: Retrofit service interface defining API contract

**Endpoints**:

| Method | Endpoint | Return Type | Purpose |
|--------|----------|-------------|---------|
| GET | `/api/message` | `List<MessageSummary>` | Fetch all emails |
| GET | `/api/message/{id}` | `CompleteMessage` | Fetch specific email |
| DELETE | `/api/message/{id}` | `ResponseBody` | Delete email |

**Implementation**:
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

### 5. EmailApplication (`EmailApplication.java`)

**Responsibility**: Application-level configuration

**Current Implementation**:
```java
public class EmailApplication extends Application {
    private String BASE_URL = "http://192.168.0.160:8088/";
}
```

**Issues**:
- Not properly registered in `AndroidManifest.xml`
- Instantiated with `new` operator instead of using singleton
- Should be application-scoped configuration holder

### 6. SimpleDividerItemDecoration (`SimpleDividerItemDecoration.java`)

**Responsibility**: Draw dividers between RecyclerView items

**Implementation**: Extends `RecyclerView.ItemDecoration` and draws a drawable resource between items

## Data Flow

### Email List Retrieval Flow

```
┌─────────────┐
│  User Opens │
│     App     │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────┐
│  MainActivity.onCreate()            │
│  1. Setup RecyclerView              │
│  2. Create empty messageSummaries   │
│  3. Initialize adapter with empty   │
│     list                            │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  Create Retrofit Instance           │
│  - Base URL from EmailApplication   │
│  - Add Gson converter               │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  EmailService.getDetailsList()      │
│  Async network call                 │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  onResponse() Callback              │
│  1. Iterate response.body()         │
│  2. Add each summary to local list  │
│  3. notifyDataSetChanged()          │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  RecyclerView Updates               │
│  Displays email list                │
└─────────────────────────────────────┘
```

### Email Detail View Flow

```
┌─────────────┐
│  User Taps  │
│   Subject   │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────┐
│  RecyclerViewAdapter Click Listener │
│  1. Get email ID from ViewHolder    │
│  2. Create Intent with ID extra     │
│  3. Start EmailContentActivity      │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  EmailContentActivity.onCreate()    │
│  1. Extract ID from intent          │
│  2. Setup Retrofit                  │
│  3. Call getMessage(id)             │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  onResponse() Callback              │
│  1. Receive CompleteMessage         │
│  2. Call populateContent()          │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  populateContent()                  │
│  1. Format participants             │
│  2. Format timestamp                │
│  3. Set subject and body            │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  UI Updated with Email Details      │
└─────────────────────────────────────┘
```

### Email Deletion Flow

```
┌─────────────┐
│ User Swipes │
│    Email    │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────┐
│  ItemTouchHelper.onSwiped()         │
│  1. Get adapter position            │
│  2. Remove from local list          │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  EmailService.deleteMessage(id)     │
│  Async DELETE request               │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  onResponse() / onFailure()         │
│  Log result (no user feedback)      │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  notifyDataSetChanged()             │
│  Update RecyclerView display        │
└─────────────────────────────────────┘
```

## Design Patterns

### 1. ViewHolder Pattern

**Location**: `RecyclerViewAdapter.java:29-46`

**Purpose**: Optimize RecyclerView performance by caching view references

**Implementation**:
```java
public static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView participants;
    public TextView timestamp;
    public TextView subject;
    public TextView preview;
    public ImageButton starButton;
    public TextView itemId;

    public ViewHolder(View itemView) {
        super(itemView);
        participants = (TextView) itemView.findViewById(R.id.participants);
        timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        subject = (TextView) itemView.findViewById(R.id.subject);
        preview = (TextView) itemView.findViewById(R.id.preview);
        starButton = (ImageButton) itemView.findViewById(R.id.star_button);
        itemId = (TextView) itemView.findViewById(R.id.item_id);
    }
}
```

**Benefits**:
- Avoids repeated `findViewById()` calls during scrolling
- Improves list scrolling performance
- Reduces memory allocations

### 2. Adapter Pattern

**Location**: `RecyclerViewAdapter.java`

**Purpose**: Adapt data model (`List<MessageSummary>`) to RecyclerView UI

**Structure**:
- Input: `List<MessageSummary>`
- Output: RecyclerView items with bound data

### 3. Observer Pattern (Callbacks)

**Location**: Throughout API calls in Activities

**Purpose**: Handle asynchronous network responses

**Implementation**:
```java
call.enqueue(new Callback<List<MessageSummary>>() {
    @Override
    public void onResponse(...) {
        // Handle success
    }

    @Override
    public void onFailure(...) {
        // Handle error
    }
});
```

### 4. Service Locator Pattern (Implicit)

**Location**: Retrofit service creation

**Purpose**: Create and provide service instances

**Current Implementation**:
```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(emailApplication.getBaseUrl())
    .addConverterFactory(GsonConverterFactory.create())
    .build();

EmailService service = retrofit.create(EmailService.class);
```

**Improvement Opportunity**: Should be centralized in a singleton or dependency injection framework

## Technology Stack

### Core Framework

- **Android SDK 23**: Marshmallow (API Level 23)
- **Java**: JDK 7/8
- **Gradle**: Build automation tool

### Key Libraries

#### Retrofit 2.1.0
- **Purpose**: Type-safe HTTP client
- **Features**: Declarative API definitions, async callbacks
- **Configuration**: Gson converter for JSON parsing

#### Gson 2.6.2
- **Purpose**: JSON serialization/deserialization
- **Usage**: Automatic conversion of JSON to Java objects

#### Android Support Libraries
- **AppCompat 23.4.0**: Backward compatibility
- **RecyclerView 23.4.0**: Efficient list rendering
- **CardView 23.4.0**: Material Design cards

### Architecture Components (Not Used)

**Notable Absences**:
- No ViewModel (MVVM not used)
- No LiveData (reactive data not implemented)
- No Room (no local database)
- No Repository pattern
- No Dependency Injection (Dagger/Hilt)

## API Integration

### Retrofit Configuration

**Initialization**:
```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("http://192.168.0.160:8088/")
    .addConverterFactory(GsonConverterFactory.create())
    .build();
```

**Converter Factory**: GsonConverterFactory handles automatic JSON conversion

**Error Handling**: Basic logging only (no retry logic or user feedback)

### Network Security

**Issues**:
1. HTTP instead of HTTPS (unencrypted)
2. No certificate pinning
3. No authentication headers
4. No request signing or validation

**Recommendation**: Implement HTTPS with proper certificate validation

## Data Models

### Model Hierarchy

```
┌──────────────────────┐
│   MessageSummary     │  ← List view (lightweight)
│                      │
│ - id: Integer        │
│ - subject: String    │
│ - participants: List │
│ - preview: String    │
│ - isRead: Boolean    │
│ - isStarred: Boolean │
│ - ts: long           │
└──────────────────────┘

┌──────────────────────┐
│  CompleteMessage     │  ← Detail view (full data)
│                      │
│ - id: Integer        │
│ - subject: String    │
│ - participants: List │
│   (Participant objs) │
│ - body: String       │
│ - isRead: Boolean    │
│ - isStarred: Boolean │
│ - ts: Integer        │
└──────────────────────┘

┌──────────────────────┐
│    Participant       │  ← Email participant
│                      │
│ - name: String       │
│ - email: String      │
└──────────────────────┘
```

### Type Inconsistencies

**Issue**: Timestamp field uses different types

- `MessageSummary.ts`: `long`
- `CompleteMessage.ts`: `Integer`

**Recommendation**: Use `long` consistently for Unix timestamps

## UI Architecture

### Layout Hierarchy

```
MainActivity (activity_main.xml)
  └─> ScrollView
      └─> RecyclerView (id: my_recycler_view)
          └─> row_item.xml (repeated for each email)
              ├─> CardView
              │   ├─> LinearLayout
              │       ├─> TextView (participants)
              │       ├─> TextView (timestamp)
              │       ├─> TextView (subject)
              │       ├─> TextView (preview)
              │       ├─> ImageButton (star)
              │       └─> TextView (itemId, hidden)

EmailContentActivity (activity_email_data.xml)
  └─> ScrollView
      └─> LinearLayout
          ├─> CardView (Header)
          │   ├─> TextView (subject)
          │   └─> TextView (participants)
          └─> CardView (Body)
              ├─> TextView (timestamp)
              └─> TextView (body)
```

### Material Design Elements

- **CardView**: Elevated cards with shadows
- **RecyclerView**: Efficient scrolling lists
- **Custom ItemDecoration**: Dividers between items
- **Swipe Gestures**: ItemTouchHelper for interactions

## Design Decisions

### 1. Two Separate Data Models

**Decision**: Use `MessageSummary` for list and `CompleteMessage` for details

**Rationale**:
- Reduces data transfer for list view
- `MessageSummary` has participants as strings (lightweight)
- `CompleteMessage` has full participant objects and body

**Trade-off**: Code duplication vs. efficiency

### 2. Activity-Based Architecture

**Decision**: Use Activities instead of Fragments

**Rationale**:
- Simple app with two screens
- No need for complex navigation
- Easier to understand for demonstration

**Limitation**: Less flexible for future multi-pane layouts

### 3. Direct Network Calls in Activities

**Decision**: Activities directly call Retrofit services

**Rationale**: Simplicity for small app

**Drawback**:
- Tight coupling
- Hard to test
- No separation of concerns

**Recommendation**: Introduce Repository pattern

### 4. No Local Database

**Decision**: No Room/SQLite implementation

**Impact**:
- No offline support
- Data lost on app restart
- Network required for every launch

**Future Enhancement**: Add Room for caching

### 5. Swipe-to-Delete with Visual Feedback

**Decision**: Custom `onChildDraw()` implementation

**Rationale**: Enhance UX with visual feedback

**Implementation**: Red background painted behind swiped item

**Benefit**: Users understand the action before completing it

## Technical Debt

### High Priority

1. **Improper Application Class Usage**
   - `MainActivity` instantiates with `new EmailApplication()`
   - Should register in `AndroidManifest.xml` with `android:name`
   - Should access via `getApplication()`

2. **No Error Handling**
   - Network failures silent (console logs only)
   - No retry mechanisms
   - No user feedback for errors

3. **Missing Null Safety**
   - `response.body()` could be null
   - No null checks before accessing data
   - Potential crashes

4. **Hardcoded Configuration**
   - Server URL hardcoded in source
   - Should use BuildConfig or resources

### Medium Priority

1. **Tight Coupling**
   - Activities handle too many responsibilities
   - Need separation: View, ViewModel, Repository

2. **No Unit Tests**
   - Test files exist but are empty/template
   - Business logic untested

3. **Magic Values**
   - Hardcoded colors: `p.setARGB(255, 255, 0, 0)`
   - Should use color resources

4. **Inefficient List Updates**
   - Uses `notifyDataSetChanged()` instead of specific item notifications
   - Redraws entire list unnecessarily

### Low Priority

1. **Missing JavaDoc**
   - Only creation timestamps in comments
   - No method or class documentation

2. **Unused Imports**
   - Multiple unused imports in MainActivity

3. **Inconsistent Naming**
   - Variable `df` instead of `dateFormat`

## Scalability Considerations

### Current Limitations

1. **No Pagination**: Loads all emails at once
2. **No Caching**: Fetches data on every launch
3. **No Background Sync**: No push notifications or sync
4. **Single Account**: No multi-account support

### Recommendations for Scale

#### 1. Implement Repository Pattern

```
┌────────────┐
│  Activity  │
└─────┬──────┘
      │
      ▼
┌────────────┐
│ ViewModel  │
└─────┬──────┘
      │
      ▼
┌────────────────┐
│  Repository    │
│  ├─ Remote DS  │  ← Retrofit
│  └─ Local DS   │  ← Room
└────────────────┘
```

#### 2. Add Local Database (Room)

- Cache email data
- Enable offline access
- Sync strategy: fetch new, store locally

#### 3. Implement Pagination

- Use `PagedListAdapter` or Paging 3
- Load emails in chunks (e.g., 20 at a time)
- Infinite scroll with loading indicator

#### 4. Add Dependency Injection

- Use Dagger 2 or Hilt
- Centralize service creation
- Improve testability

#### 5. Migrate to MVVM with LiveData

```java
class EmailViewModel extends ViewModel {
    private LiveData<List<MessageSummary>> emails;

    public LiveData<List<MessageSummary>> getEmails() {
        if (emails == null) {
            emails = repository.loadEmails();
        }
        return emails;
    }
}
```

#### 6. Implement Work Manager

- Background sync for new emails
- Respect Android battery optimization
- Reliable background processing

## Conclusion

The EmailClient application demonstrates fundamental Android development concepts with a simple, functional architecture. While suitable for demonstration purposes, production deployment would require significant enhancements in error handling, data persistence, security, and architectural patterns.

**Strengths**:
- Clear component separation
- Modern libraries (Retrofit, RecyclerView)
- Good UX with swipe gestures

**Areas for Improvement**:
- Add proper error handling
- Implement MVVM with Repository pattern
- Add local caching with Room
- Improve security (HTTPS, validation)
- Add comprehensive testing
- Implement dependency injection

This architecture provides a solid foundation for learning and can be incrementally improved to production-ready standards.

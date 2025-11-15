# Development Guide

## Table of Contents

- [Getting Started](#getting-started)
- [Development Environment Setup](#development-environment-setup)
- [Project Configuration](#project-configuration)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Development Workflow](#development-workflow)
- [Testing](#testing)
- [Debugging](#debugging)
- [Common Development Tasks](#common-development-tasks)
- [Troubleshooting](#troubleshooting)
- [Code Style Guidelines](#code-style-guidelines)
- [Git Workflow](#git-workflow)

## Getting Started

### Prerequisites

Before you begin development, ensure you have the following installed:

- **JDK 8**: Java Development Kit
  ```bash
  java -version  # Should show version 1.8.x
  ```

- **Android Studio**: Version 2.1.2 or higher
  - Download from [developer.android.com](https://developer.android.com/studio)

- **Android SDK**: API Level 23 (Marshmallow)
  - Install via Android Studio SDK Manager

- **Git**: Version control
  ```bash
  git --version
  ```

- **Backend Server**: Email API server running locally or on network

### System Requirements

- **OS**: Windows 7+, macOS 10.10+, or Linux (64-bit)
- **RAM**: 8 GB minimum, 16 GB recommended
- **Disk Space**: 4 GB for Android Studio + 2 GB for SDK
- **Screen Resolution**: 1280 x 800 minimum

## Development Environment Setup

### 1. Install JDK 8

**macOS** (using Homebrew):
```bash
brew tap adoptopenjdk/openjdk
brew install --cask adoptopenjdk8
```

**Ubuntu/Debian**:
```bash
sudo apt update
sudo apt install openjdk-8-jdk
```

**Windows**:
- Download from [Oracle](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
- Run installer and set JAVA_HOME environment variable

### 2. Install Android Studio

1. Download Android Studio from [developer.android.com](https://developer.android.com/studio)
2. Run the installer
3. Follow the setup wizard:
   - Choose "Standard" installation type
   - Accept license agreements
   - Let it download SDK components

### 3. Configure Android SDK

1. Open Android Studio
2. Go to **Tools > SDK Manager**
3. Under **SDK Platforms**, ensure the following are installed:
   - Android 6.0 (Marshmallow) API Level 23
   - Android SDK Platform 23

4. Under **SDK Tools**, ensure the following are installed:
   - Android SDK Build-Tools 23.0.3
   - Android SDK Platform-Tools
   - Android SDK Tools
   - Android Emulator
   - Intel x86 Emulator Accelerator (HAXM installer)

### 4. Clone the Repository

```bash
git clone https://github.com/rakeshgangwar/EmailClient.git
cd EmailClient
```

### 5. Open Project in Android Studio

1. Launch Android Studio
2. Select **File > Open**
3. Navigate to the cloned `EmailClient` directory
4. Click **OK**
5. Wait for Gradle sync to complete (may take a few minutes)

### 6. Setup Backend API Server

The app requires a backend server with the following endpoints:

- `GET /api/message` - Returns list of email summaries
- `GET /api/message/{id}` - Returns specific email
- `DELETE /api/message/{id}` - Deletes email

**Option 1: Use provided mock server** (if available)

**Option 2: Create your own mock server**

Example using JSON Server (Node.js):

```bash
npm install -g json-server
```

Create `db.json`:
```json
{
  "message": [
    {
      "id": 1,
      "subject": "Welcome to EmailClient",
      "participants": ["john@example.com", "me"],
      "preview": "This is a test email preview...",
      "isRead": false,
      "isStarred": true,
      "ts": 1627891200000
    }
  ]
}
```

Run server:
```bash
json-server --watch db.json --port 8088
```

## Project Configuration

### Configure API Endpoint

1. Open `app/src/main/java/com/rakeshgangwar/emailclient/EmailApplication.java`

2. Update the `BASE_URL`:

   **For localhost (using emulator)**:
   ```java
   private String BASE_URL = "http://10.0.2.2:8088/";
   ```

   **For physical device on same network**:
   ```java
   private String BASE_URL = "http://YOUR_COMPUTER_IP:8088/";
   ```

   **For remote server**:
   ```java
   private String BASE_URL = "http://your-server.com:8088/";
   ```

### Network Security Configuration (Android 9+)

If targeting API 28+, you may need to allow cleartext (HTTP) traffic:

Create `app/src/main/res/xml/network_security_config.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```

Update `AndroidManifest.xml`:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

## Building the Project

### Using Android Studio

1. **Clean Project**: `Build > Clean Project`
2. **Build Project**: `Build > Make Project` (or `Ctrl+F9` / `Cmd+F9`)
3. **Rebuild Project**: `Build > Rebuild Project`

### Using Gradle (Command Line)

**Clean build**:
```bash
./gradlew clean
```

**Build debug APK**:
```bash
./gradlew assembleDebug
```

**Build release APK** (unsigned):
```bash
./gradlew assembleRelease
```

**Output location**: `app/build/outputs/apk/`

### Build Variants

- **Debug**: Includes debugging symbols, not optimized
- **Release**: Optimized, requires signing for distribution

Switch variants in Android Studio: `Build > Select Build Variant`

## Running the Application

### On Android Emulator

1. **Create AVD** (Android Virtual Device):
   - Go to `Tools > AVD Manager`
   - Click **Create Virtual Device**
   - Choose device (e.g., Pixel 2)
   - Select system image: API 23 (Marshmallow)
   - Finish setup

2. **Run app**:
   - Click **Run** button (green triangle) or press `Shift+F10`
   - Select your AVD
   - App will install and launch

### On Physical Device

1. **Enable Developer Options** on your Android device:
   - Go to `Settings > About Phone`
   - Tap `Build Number` 7 times
   - Developer options will be enabled

2. **Enable USB Debugging**:
   - Go to `Settings > Developer Options`
   - Toggle on `USB Debugging`

3. **Connect device** via USB

4. **Verify connection**:
   ```bash
   adb devices
   ```
   You should see your device listed

5. **Run app**:
   - Click **Run** button in Android Studio
   - Select your device
   - App will install and launch

### Using Gradle

**Install on connected device**:
```bash
./gradlew installDebug
```

**Uninstall**:
```bash
./gradlew uninstallDebug
```

## Development Workflow

### Typical Development Cycle

1. **Make code changes** in Android Studio

2. **Build and run**:
   ```bash
   ./gradlew installDebug
   ```

3. **View logs**:
   ```bash
   adb logcat | grep EmailClient
   ```

4. **Test functionality** on device/emulator

5. **Iterate** based on testing

### Hot Reload / Instant Run

Android Studio 2.x includes Instant Run:

- **Hot Swap**: Method body changes (no restart needed)
- **Warm Swap**: Resource changes (activity restart)
- **Cold Swap**: Structural changes (app restart)

Enable: `File > Settings > Build, Execution, Deployment > Instant Run`

## Testing

### Unit Tests

**Location**: `app/src/test/java/`

**Run tests**:
```bash
./gradlew test
```

**In Android Studio**: Right-click on test class > `Run 'TestClassName'`

### Instrumentation Tests

**Location**: `app/src/androidTest/java/`

**Run tests** (requires connected device/emulator):
```bash
./gradlew connectedAndroidTest
```

### Writing Tests

**Example Unit Test**:
```java
@Test
public void email_subject_isNotNull() {
    MessageSummary message = new MessageSummary();
    message.setSubject("Test Subject");
    assertNotNull(message.getSubject());
}
```

**Example Instrumentation Test**:
```java
@Test
public void testMainActivityLaunch() {
    ActivityScenario.launch(MainActivity.class);
    onView(withId(R.id.my_recycler_view)).check(matches(isDisplayed()));
}
```

## Debugging

### Using Android Studio Debugger

1. Set **breakpoints**: Click in left gutter next to line numbers

2. **Debug mode**: Click debug button (bug icon) or `Shift+F9`

3. **Step through code**:
   - **Step Over**: `F8`
   - **Step Into**: `F7`
   - **Step Out**: `Shift+F8`

4. **Evaluate expressions**: Select code > Right-click > `Evaluate Expression`

### Logcat Debugging

**View logs** in Android Studio: `View > Tool Windows > Logcat`

**Filter by tag**:
```java
Log.d("EmailClient", "Debug message");
```

**Command line**:
```bash
adb logcat -s EmailClient:D
```

**Log levels**:
- `Log.v()` - Verbose
- `Log.d()` - Debug
- `Log.i()` - Info
- `Log.w()` - Warning
- `Log.e()` - Error

### Network Debugging

**Using Charles Proxy / Fiddler**:

1. Install proxy software on your computer
2. Configure Android device to use proxy
3. Monitor HTTP/HTTPS traffic

**Using Android Studio Network Profiler**:

1. Run app in debug mode
2. Open `View > Tool Windows > Profiler`
3. Select **Network** tab
4. View all network requests

### Memory Debugging

**Memory Profiler**:
1. `View > Tool Windows > Profiler`
2. Select **Memory** tab
3. Monitor heap allocations and garbage collection

**Detect leaks**:
- Use LeakCanary library
- Analyze heap dumps

## Common Development Tasks

### Adding a New Dependency

1. Open `app/build.gradle`

2. Add dependency:
   ```gradle
   dependencies {
       compile 'com.squareup.picasso:picasso:2.5.2'
   }
   ```

3. Sync Gradle: Click **Sync Now** banner

### Adding a New Activity

1. Right-click on package > `New > Activity > Empty Activity`

2. Configure activity name and layout

3. Android Studio automatically registers in `AndroidManifest.xml`

### Adding a New Resource

**String resource**:
1. Open `res/values/strings.xml`
2. Add: `<string name="my_string">Hello</string>`
3. Use: `getString(R.string.my_string)`

**Color resource**:
1. Open `res/values/colors.xml`
2. Add: `<color name="my_color">#FF0000</color>`
3. Use: `ContextCompat.getColor(context, R.color.my_color)`

### Updating Gradle Version

1. Update `gradle-wrapper.properties`:
   ```properties
   distributionUrl=https\://services.gradle.org/distributions/gradle-4.1-all.zip
   ```

2. Update build.gradle:
   ```gradle
   classpath 'com.android.tools.build:gradle:3.0.0'
   ```

3. Sync Gradle

## Troubleshooting

### Common Issues and Solutions

#### 1. Gradle Sync Failed

**Symptom**: "Gradle sync failed: Connection refused"

**Solution**:
```bash
# Clear Gradle cache
rm -rf ~/.gradle/caches/

# Re-sync
./gradlew clean build
```

#### 2. App Crashes on Launch

**Symptom**: App immediately crashes after opening

**Solutions**:
- Check Logcat for stack trace
- Verify AndroidManifest.xml is correct
- Ensure all required permissions are declared
- Check for null pointer exceptions

**Common fix** for this app:
```bash
# Check server is running
curl http://localhost:8088/api/message

# Verify BASE_URL is correct in EmailApplication.java
```

#### 3. Network Request Fails

**Symptom**: Emails don't load, blank screen

**Solutions**:

**Check server URL**:
- Emulator: Use `http://10.0.2.2:8088/` for localhost
- Physical device: Use computer's IP address

**Verify INTERNET permission** in `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

**Check Logcat** for network errors:
```bash
adb logcat | grep -E "EmailClient|Retrofit"
```

#### 4. Build Error: "SDK location not found"

**Solution**:

Create `local.properties` in project root:
```properties
sdk.dir=/Users/USERNAME/Library/Android/sdk
```

Or set `ANDROID_HOME` environment variable:
```bash
export ANDROID_HOME=/Users/USERNAME/Library/Android/sdk
```

#### 5. Emulator Won't Start

**Solutions**:
- Ensure virtualization is enabled in BIOS
- Install Intel HAXM:
  ```bash
  # macOS
  brew install --cask intel-haxm
  ```
- Allocate more RAM to AVD (2GB minimum)
- Use system image for your CPU architecture (x86 vs ARM)

#### 6. RecyclerView Not Displaying Items

**Check**:
1. Adapter is set: `recyclerView.setAdapter(adapter)`
2. LayoutManager is set: `recyclerView.setLayoutManager(layoutManager)`
3. Data list is not empty
4. `notifyDataSetChanged()` is called after data update
5. Layout height is not `wrap_content` on RecyclerView

#### 7. Retrofit Returns Null

**Check**:
1. JSON field names match Java class fields exactly
2. Gson converter is added to Retrofit builder
3. Response is successful (`response.isSuccessful()`)
4. Server returns valid JSON

**Enable logging**:
```gradle
dependencies {
    compile 'com.squareup.okhttp3:logging-interceptor:3.4.1'
}
```

```java
HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
logging.setLevel(HttpLoggingInterceptor.Level.BODY);

OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(logging)
    .build();

Retrofit retrofit = new Retrofit.Builder()
    .client(client)
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build();
```

## Code Style Guidelines

### Java Coding Conventions

Follow standard Android/Java conventions:

**Class names**: PascalCase
```java
public class EmailContentActivity { }
```

**Method names**: camelCase
```java
public void fetchEmailList() { }
```

**Constants**: UPPER_SNAKE_CASE
```java
private static final String BASE_URL = "http://...";
```

**Variables**: camelCase
```java
private RecyclerView recyclerView;
```

### Android-Specific Conventions

**Resource naming**:
- Layouts: `activity_main.xml`, `fragment_list.xml`, `item_email.xml`
- IDs: `@+id/recycler_view_emails`, `@+id/text_subject`
- Strings: `<string name="app_name">EmailClient</string>`
- Colors: `<color name="primary_blue">#2196F3</color>`

**Code organization**:
```java
public class MainActivity extends AppCompatActivity {
    // Constants
    private static final String TAG = "MainActivity";

    // Views
    private RecyclerView recyclerView;
    private Adapter adapter;

    // Data
    private List<MessageSummary> emails;

    // Lifecycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) { }

    // Public methods
    public void refreshEmails() { }

    // Private methods
    private void setupRecyclerView() { }

    // Inner classes
    private static class ViewHolder { }
}
```

### Documentation

**JavaDoc for public methods**:
```java
/**
 * Fetches email list from the server and updates the RecyclerView.
 *
 * @param forceRefresh if true, bypasses cache and fetches fresh data
 */
public void fetchEmails(boolean forceRefresh) { }
```

**Inline comments** for complex logic:
```java
// Calculate index considering the header offset
int actualPosition = position - HEADER_COUNT;
```

### Formatting

**Use Android Studio auto-format**:
- **Format file**: `Ctrl+Alt+L` (Windows/Linux) or `Cmd+Option+L` (macOS)
- **Optimize imports**: `Ctrl+Alt+O` or `Cmd+Option+O`

**Indentation**: 4 spaces (no tabs)

**Line length**: 100 characters maximum

## Git Workflow

### Branch Strategy

**Main branches**:
- `master` - Production-ready code
- `develop` - Integration branch for features

**Feature branches**:
```bash
git checkout -b feature/email-search develop
```

### Commit Messages

Follow conventional commits:

```
feat: add email search functionality
fix: resolve crash on swipe delete
docs: update README with setup instructions
refactor: extract network logic to repository
test: add unit tests for MessageSummary
```

**Format**:
```
<type>: <description>

[optional body]

[optional footer]
```

**Types**: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

### Example Workflow

```bash
# Create feature branch
git checkout -b feature/mark-as-read

# Make changes
# ...

# Stage changes
git add .

# Commit
git commit -m "feat: add mark as read functionality"

# Push to remote
git push origin feature/mark-as-read

# Create pull request on GitHub
```

### Code Review Checklist

Before submitting PR:
- [ ] Code compiles without errors
- [ ] All tests pass
- [ ] No new warnings
- [ ] Code is properly formatted
- [ ] JavaDoc added for public APIs
- [ ] No hardcoded strings (use resources)
- [ ] No sensitive data committed (API keys, passwords)

## Additional Resources

### Documentation

- [Android Developer Docs](https://developer.android.com/docs)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Material Design Guidelines](https://material.io/design)

### Tools

- [Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio/) - Icon generator
- [Shape Shifter](https://shapeshifter.design/) - Vector animation tool
- [JSON to POJO](http://www.jsonschema2pojo.org/) - Generate Java classes from JSON

### Learning Resources

- [Google Codelabs](https://codelabs.developers.google.com/?cat=Android)
- [Vogella Android Tutorials](https://www.vogella.com/tutorials/android.html)
- [Android Weekly Newsletter](https://androidweekly.net/)

## Support

For issues or questions:
1. Check existing GitHub issues
2. Review documentation in `/docs`
3. Open a new issue with detailed information

---

**Happy coding!** 🚀

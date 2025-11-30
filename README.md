# Course Management App 📚

Hey! This is my course management app that I built for a coding challenge. It's basically an app where you can add, edit, and manage your courses. I spent 3 days working on this and learned a ton about Android development!

## What Does It Do?

So basically, the app lets you:
- See all your courses in a list (with search and filters!)
- Add new courses with categories
- Edit existing courses
- Delete courses (with a confirmation dialog so you don't accidentally delete stuff)
- View detailed info about each course
- **Works completely offline** - this was a requirement and honestly the trickiest part

## How I Built It

I tried to follow clean architecture as much as I could. Here's how I organized everything:

### The Data Stuff
I used Room Database to store everything locally. This was my first time really diving deep into Room and it was pretty cool once I got the hang of it.

- `CourseDao` - handles all the course CRUD operations
- `CategoryDao` - caches the categories from the API
- `AppDatabase` - the main database

For the API part, I used MockAPI (https://mockapi.io) to create a fake backend. My endpoint is:
```
https://692ae9817615a15ff24e11c1.mockapi.io/api/v1/categories
```

The `CourseRepository` sits between the database and the API, handling all the data sync stuff.

### The Models
Just two simple data classes:
- `Course` - stores course info
- `Category` - stores category names

### The UI
I used Jetpack Compose for the UI (first time using it for a full project!). 

ViewModels:
- `HomeViewModel` - manages the course list, search, and filtering
- `CourseEntryViewModel` - handles adding/editing courses and loading categories

### Dependency Injection
I implemented Hilt for dependency injection. This was new for me and took some time to understand, but it makes the code much cleaner:
- `NetworkModule` - provides Retrofit and API stuff
- `DatabaseModule` - provides the database and DAOs

## How to Run This

1. Clone this repo
2. Open it in Android Studio (I used Ladybug)
3. Let Gradle sync (might take a minute)
4. Hit Run!

## About the MockAPI

I created a project on MockAPI with 46 sample categories. The app fetches these categories when you first open it and caches them locally. So after the first time, it works completely offline!

## Offline Mode

This was a big requirement - the app had to work without internet. Here's how I handled it:

**Courses**: Everything is stored in the local Room database. You can create, edit, and delete courses without any internet connection.

**Categories**: These are fetched from MockAPI the first time, then cached in Room. If you're offline, it just uses the cached data.

## The Score Thing

One of the requirements was to calculate a score for each course. The formula is:
```
Score = Title Length × Number of Lessons
```

Pretty simple, but it auto-calculates whenever you save a course.

## Tech Stack

Here's what I used:
- Kotlin (obviously)
- Jetpack Compose for UI
- Material 3 for design
- Room for local database
- Retrofit for API calls
- OkHttp with logging (super helpful for debugging!)
- Hilt for dependency injection
- Navigation Compose
- Coroutines and Flow for async stuff

## Project Structure

```
app/
├── data/
│   ├── local/          # Room database stuff
│   ├── remote/         # API interface
│   └── repository/     # Repository pattern
├── di/                 # Hilt modules
├── domain/
│   └── model/          # Data models
└── ui/
    ├── screens/        # All the screens
    ├── viewmodel/      # ViewModels
    └── theme/          # Material 3 theme
```

## Building the App

Debug build:
```bash
./gradlew assembleDebug
```

Release build:
```bash
./gradlew assembleRelease
```

## Issues I Ran Into

### Gradle Build Problems
If you get KSP or Gradle errors (I did!), try:
```bash
./gradlew --stop
./gradlew clean
./gradlew assembleDebug
```

Or in Android Studio:
- File → Invalidate Caches → Invalidate and Restart
- File → Sync Project with Gradle Files

## API Details

The categories endpoint returns JSON like this:
```json
[
  {"id": "1", "name": "Development"},
  {"id": "2", "name": "Design"},
  {"id": "3", "name": "Business"}
]
```

## What I'd Add If I Had More Time

- User login/authentication
- Sync courses to cloud
- Track course progress
- Save search history
- Export/import courses

## Final Thoughts

This was a really fun project! I learned a lot about:
- Clean architecture in Android
- Hilt dependency injection
- Working with MockAPI
- Offline-first app design
- Jetpack Compose

Feel free to check out the code and let me know if you have any questions!

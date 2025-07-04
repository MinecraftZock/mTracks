# Room Database Migration

This document describes the migration from mechanoid SQLite to Room database for the mTracks application.

## Overview

The mTracks application has been migrated from using the mechanoid SQLite framework to Android Room for database operations. This migration provides better type safety, compile-time verification of SQL queries, and improved maintainability.

## Architecture

### Core Database (MxCoreDatabase)
The core database handles the main application data including:
- **Track**: Main track information
- **Picture**: Pictures associated with tracks
- **Favorit**: User favorite tracks
- **Country**: Country information for filtering
- **CapturedLatLng**: Location capture data (existing)

### Admin Database (MxAdminDatabase)
The admin database handles administrative data including:
- **TrackstageBrother**: Extended track information
- **TrackstageRid**: Route information
- **PictureStage**: Stage pictures
- **Videos**: Video content

### Repository Pattern
The `TrackRepository` class provides a clean API for database operations:
- Centralized database access
- Coroutine-based async operations
- Flow-based reactive data streams

### Database Manager
The `DatabaseManager` singleton manages database instances:
- Thread-safe database initialization
- Centralized access to repositories
- Database clearing utilities

## Migration Strategy

### 1. Entity Mapping
Mechanoid `Record` classes have been converted to Room `@Entity` classes:
```kotlin
// Old mechanoid approach
TracksRecord record = new TracksRecord();
record.setTrackname("Test Track");

// New Room approach
val track = Track(
    trackname = "Test Track",
    // other properties...
)
```

### 2. Query Migration
SQuery operations have been replaced with Room DAO methods:
```kotlin
// Old mechanoid approach
val cursor = SQuery.newQuery()
    .expr(Tracks.COUNTRY, SQuery.Op.EQ, "DE")
    .select(Tracks.CONTENT_URI, projection)

// New Room approach
val germanTracks = trackDao.getTracksByCountry("DE").first()
```

### 3. Async Operations
All database operations are now suspend functions or return Flow:
```kotlin
// Suspend functions for single operations
suspend fun insertTrack(track: Track): Long

// Flow for reactive data streams
fun getAllTracks(): Flow<List<Track>>
```

## Testing

### Unit Tests
- **TrackDaoTest**: Tests for Track DAO operations
- **PictureDaoTest**: Tests for Picture DAO operations
- **TrackstageBrotherDaoTest**: Tests for admin DAO operations
- **DatabaseErrorScenariosTest**: Error handling tests
- **MigrationTest**: Database migration tests

### Integration Tests
- **TrackRepositoryTest**: Repository layer tests
- **AdditionalEntitiesDaoTest**: Additional entity tests

### UI Tests
- **BaseRoomTest**: Base class for UI tests with Room setup
- Updated existing Espresso tests to use Room

## Usage Examples

### Basic Operations
```kotlin
// Get repository
val repository = DatabaseManager.getTrackRepository(context)

// Insert a track
val track = Track(
    restId = 100,
    trackname = "Test Track",
    longitude = 10.0,
    latitude = 20.0,
    country = "DE"
)
repository.insertTrack(track)

// Query tracks
val germanTracks = repository.getTracksByCountry("DE").first()
val searchResults = repository.searchTracksByName("Berlin").first()
```

### Reactive Updates
```kotlin
// Observe track changes
repository.getAllTracks().collect { tracks ->
    // Update UI with new track list
}

// Observe favorite tracks
repository.getFavoriteTracks().collect { favorites ->
    // Update favorites UI
}
```

## Migration Benefits

1. **Type Safety**: Compile-time verification of queries and entity mapping
2. **Better Testing**: Easier to write unit tests with in-memory databases
3. **Reactive**: Flow-based reactive programming support
4. **Maintainable**: Cleaner, more maintainable code structure
5. **Performance**: Better query optimization and caching
6. **Future-Proof**: Active development and support from Google

## Backward Compatibility

The migration maintains backward compatibility by:
- Preserving existing database schema
- Maintaining the same data structures
- Providing migration paths for existing data
- Keeping the same public API where possible

## Next Steps

1. Complete removal of mechanoid dependencies
2. Update remaining mechanoid usage throughout the application
3. Optimize queries using Room's advanced features
4. Add additional indexes for better performance
5. Implement database encryption if needed
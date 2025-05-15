# Location Reminder App

## Overview

The Location Reminder app allows users to save and be notified of reminders based on specific
locations. The app leverages geofencing capabilities to trigger notifications when users enter or
exit designated areas.

## Architecture

This app follows clean architecture principles with MVVM (Model-View-ViewModel) design pattern,
providing several advantages:

- **Separation of Concerns**: Clear separation between UI, business logic, and data layers
- **Testability**: Each component can be tested independently
- **Maintainability**: Code is organized in a way that makes it easier to understand and modify

### Key Components

#### Data Layer

- **RemindersDatabase**: Room database for local storage of reminders
- **ReminderDAO**: Data Access Object interface for CRUD operations on reminders
- **LocalDataSource**: Implementation of ReminderDataSource using Room
- **ReminderDataSource**: Interface defining the data operations contract

#### Domain Layer

- **ReminderDTO**: Data Transfer Object for database operations
- **ReminderDataItem**: UI model representation of reminders

#### Presentation Layer

- **RemindersActivity**: Main container activity
- **ReminderListFragment**: Shows the list of saved reminders
- **SaveReminderFragment**: Allows users to create and save reminders
- **SelectLocationFragment**: Map interface for selecting reminder locations
- **ViewModels**: Handle business logic and UI state management

### Geofencing

The app uses Android's Geofencing API to:

- Create geofences around selected locations
- Monitor user's entry/exit from defined geofence areas
- Trigger notifications when appropriate

## Testing Strategy

The app implements comprehensive testing:

- **Unit Tests**: Testing individual components in isolation
    - DAO tests for database operations
    - ViewModel tests for business logic
- **Integration Tests**: Testing interactions between components
- **End-to-End Tests**: Testing the app's full functionality

## Design Choices

### Repository Pattern

Implemented to abstract the data sources from the rest of the app, making it easier to switch
between local storage, remote APIs, or add caching mechanisms.

### Dependency Injection

Utilized to provide dependencies across the app, improving testability and maintainability.

### Single Activity Architecture

The app uses a single activity with multiple fragments, following modern Android development
patterns and making navigation more straightforward.

### Result Wrapper

Custom Result class to handle success/error states, providing a clean way to propagate errors
through the layers of the application.

## Future Improvements

- Add remote synchronization for reminders
- Implement reminder sharing functionality
- Add more customization options for notifications
- Enhance location selection with search functionality
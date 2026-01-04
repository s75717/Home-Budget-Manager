# Home Budget Manager

A professional Android application for tracking income and expenses with comprehensive budget management features.

## Features

### 📊 Core Functionality
- **Track Income & Expenses**: Record all financial transactions with detailed information
- **Monthly Financial Summary**: View total income, expenses, and balance for any month
- **Custom Categories**: Create and manage your own transaction categories
- **Smart Filtering**: Filter transactions by month, category, or both
- **Visual Reports**: Clear monthly summaries with color-coded income/expense indicators

### 💰 Transaction Management
- Add income or expense entries
- Select from predefined or custom categories
- Record amount, date, and optional notes
- View transaction history with detailed information
- Delete unwanted transactions

### 🏷️ Category Management
- Pre-populated default categories (Food, Transport, Bills, Salary, etc.)
- Create custom categories with:
  - Custom names
  - Emoji icons
  - Color coding
  - Type (Income/Expense)
- Edit and delete categories
- Separate tabs for expense and income categories

### 📅 Date-based Features
- Navigate through months using previous/next buttons
- Select specific months for filtering
- Date picker for transaction entry
- Monthly summary calculations

## Technical Architecture

### Technology Stack
- **Language**: Java
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 14+)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Persistence Library
- **UI Framework**: Material Design 3

### Project Structure
```
app/
├── model/              # Data models (Transaction, Category)
├── database/           # Room database components (DAOs, Database)
├── repository/         # Data repository layer
├── viewmodel/          # ViewModels for UI logic
├── adapter/            # RecyclerView adapters
└── activities/         # UI screens
```

### Database Schema

#### Categories Table
- id (Primary Key, Auto-generated)
- name (String)
- type (INCOME/EXPENSE)
- color (Hex color code)
- icon (Emoji)

#### Transactions Table
- id (Primary Key, Auto-generated)
- categoryId (Foreign Key to Categories)
- amount (Double)
- type (INCOME/EXPENSE)
- date (Timestamp)
- note (Optional text)

### Key Components

#### Activities
1. **MainActivity**: Dashboard with monthly summary and quick actions
2. **AddTransactionActivity**: Form to add new income/expense entries
3. **TransactionListActivity**: View all transactions with filtering options
4. **CategoryManagementActivity**: Manage custom categories

#### ViewModels
- **BudgetViewModel**: Manages all data operations and LiveData observations

#### Adapters
- **TransactionAdapter**: Displays transaction list
- **CategoryAdapter**: Displays category list

## Setup Instructions

### Prerequisites
- Android Studio (latest version recommended)
- JDK 11 or higher
- Android SDK with API level 36

### Installation Steps

1. **Clone/Open the project in Android Studio**

2. **Sync Gradle Files**
   - Open the project
   - Android Studio should automatically prompt to sync
   - Or manually: File → Sync Project with Gradle Files

3. **Build the Project**
   - Build → Make Project
   - Wait for dependencies to download

4. **Run the Application**
   - Connect an Android device or start an emulator
   - Click Run button or press Shift+F10

## Usage Guide

### First Launch
On first launch, the app automatically creates default categories:
- **Expense**: Food 🍔, Transport 🚗, Bills 💡, Shopping 🛍️, Entertainment 🎬, Health 🏥, Education 📚
- **Income**: Salary 💰, Business 💼, Investment 📈, Gift 🎁

### Adding a Transaction
1. From dashboard, tap "Add Income" or "Add Expense"
2. Enter amount
3. Select category from dropdown
4. Choose date (defaults to today)
5. Add optional notes
6. Tap "Save Transaction"

### Viewing Transactions
1. Tap "View All Transactions" from dashboard
2. Use month filter to view specific month
3. Use category filter to view specific category
4. Long-press any transaction to delete

### Managing Categories
1. Tap "Manage Categories" from dashboard
2. Switch between Expense and Income tabs
3. Tap "+" button to add new category
4. Enter name, icon (emoji), select type and color
5. Tap trash icon to delete a category

### Monthly Navigation
- Use ← → buttons on dashboard to navigate months
- Summary automatically updates for selected month
- Balance shown in green (positive) or red (negative)

## Features in Detail

### Smart Filtering
- **All Time**: View all transactions ever recorded
- **By Month**: Filter to specific month and year
- **By Category**: Show only transactions in selected category
- **Combined**: Filter by both month and category simultaneously

### Color-Coded UI
- Income amounts displayed in green
- Expense amounts displayed in red
- Each category has unique color for easy identification
- Balance color changes based on positive/negative value

### Data Persistence
- All data stored locally using Room database
- Automatic database migrations support
- Foreign key relationships maintain data integrity
- Cascade delete ensures no orphaned records

## Dependencies

```gradle
// Core Android
androidx.appcompat:appcompat:1.7.1
com.google.android.material:material:1.13.0
androidx.activity:activity:1.12.2
androidx.constraintlayout:constraintlayout:2.2.1

// Room Database
androidx.room:room-runtime:2.6.1
androidx.room:room-compiler:2.6.1

// Lifecycle Components
androidx.lifecycle:lifecycle-viewmodel:2.7.0
androidx.lifecycle:lifecycle-livedata:2.7.0
```

## Future Enhancements

Potential features for future versions:
- Data export/import (CSV, Excel)
- Charts and graphs for visual analytics
- Budget goals and alerts
- Recurring transactions
- Multiple currency support
- Cloud sync and backup
- Spending insights and reports
- Split transactions
- Receipt photo attachments

## Troubleshooting

### Common Issues

**Gradle Sync Failed**
- Ensure you have stable internet connection
- Try: File → Invalidate Caches / Restart

**App Crashes on Launch**
- Check minimum SDK version matches device
- Clear app data and reinstall

**Categories Not Loading**
- Database initialization might be running
- Wait a few seconds and reopen app

## License

This project is created for educational purposes.

## Author

Created as a Native Programming project for semester 3.

---

**Version**: 1.0  
**Last Updated**: January 3, 2026

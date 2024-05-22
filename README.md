# parkme
parking app

1. Splash Screen
2. Login Screen
3. Register Screen
4. Map Screen - to select booking spots
5. Booking Screen
6. Payment Screen
7. Home Screen - Dashboard
8. Profile Screen
9. Logout, about, legends - screen

### Database Firestore - Firebase

1. Parking table 
   Source:  https://open-kitchenergis.opendata.arcgis.com/datasets/parking-features/explore?location=43.443719%2C-80.474939%2C14.00

2. User table
   1. Wallet Card
   2. Credit/Debit Card
   3. Booked Spot

### Authentication 
### Crashlytics for crash reporting
### Analytics Firebase


### Future Enhancement

1. History for bookings
2. History wallet balance
3. Reviews
4. Paid Application
5. Refund Model
6. Push Notifications
7. Admin Control


1. gradle(task: "clean assembleRelease") will create a Release Apk

2. gradle( task: "assemble", // assemble is used for building apks
   flavor: "qa", // define your flavours here
   build_type: "Release" ) will create a relase apk

3. gradle( task: "bundle", //for generating AAB
   flavor: "qa", //your app flavour
   build_type: "Debug" ) // will create a debug version of your app bundle

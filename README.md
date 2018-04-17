# Loopr Wallet Android App

### Design
Please refer to the screenshots folder for individual components of the application. Note, some of 
these screenshots are out of date.

### Development

##### Stack
- Use dependency injection for configurable classes that rely on different environments for testing 
or instantiation
- Fragments should extend from *BaseFragment* to show UI in each activity 
- Activities should extend from *BaseActivity*
- ViewModels for handling data whose lifespan is beyond the fragment lifecycle
- Repository classes for saving and loading data to/from *Realm*
- Service classes (not Android *Service*s) in the *library.core* module's networking folder for
handling network calls
- Use dynamic theme values *everywhere*
    - For more information on theme values, dimension variables, etc., check the *library.core*'s 
    *res* folder
- Kotlin's [Coroutines](https://kotlinlang.org/docs/reference/coroutines.html) is used as a 
replacement for standard Android/Java threading
- [Glide](https://github.com/bumptech/glide) is used for image loading

##### Tools


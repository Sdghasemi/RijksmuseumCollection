# Rijksmuseum Museum App
This is a sample app using [RijksData API](https://data.rijksmuseum.nl/object-metadata/api/) to perform queries on the museum [collection](https://www.rijksmuseum.nl/api/nl/collection?q=[query]) endpoint and display thumbnails of **Rembrandt** works.

If there was an error retrieving data, a relevant message would inform the user about the situation.

## Features
The application reaches the endpoint and caches the response. The cache expires after 5 minutes and will get updated every 5 minutes while using the app.
When the cache is ready and valid (not expired) no endpoint call is made and the UI is populated purely with the cache itself.

The images displayed as thumbnails are also cached for better experience.

## Libraries
<ul>
<li>Basic test and AppCompat libraries such as: JUnit, Robolectric, Google Material, ConstraintLayout, Lifecycle (LiveData and ViewModels), Coroutines, etc.</li>
<li>Retrofit and OkHttp for network calls</li>
<li>Room for caching and Database manipulation</li>
<li>Glide for image loading and caching (memory and disk) to maximize scroll smoothness</li>
</ul>

## Access to the API
RijksData API needs an **API Key** to work. You can replace the existing key with your own on [build.gradle.kts](./app/build.gradle.kts#22).

## APK
A [pre-built APK](app-debug.apk) is placed in the root of the project for your convenience.

### Contact developer

If there's ***anything*** you'd like to discuss, feel free to contact me at [Sd.ghasemi1@gmail.com](mailto:Sd.ghasemi1@gmail.com).

Cheers🍻
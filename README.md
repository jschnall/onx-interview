# Weather App

## Requirements

 * Using Kotlin or Java, build an app that displays a Mapbox Map. 
   * See Mapbox instructions in References section of this README

 * When the user taps on the map, use OpenWeatherMap <https://openweathermap.org/> to get the multi-day forecast for the nearest city (reverse geocode).
   * See OpenWeatherMap instructions in References section of this README

 * Display weather data (day, description, icon, wind direction, wind speed) for tapped location to user

 * Use an appropriate local persistence solution to save the weather data on device. Load weather data from the network only once for each location. Once the data has been received, it should be retrieved from the database.
   
 * Build a UI that allows the user to toggle between all locations that have saved weather data.

## References

 #### Mapbox
 * Instructions: <https://www.mapbox.com/android-docs/maps/overview/>
 * Use the latest stable build of Mapbox
 * Public Key: `pk.eyJ1IjoiamJhcnR6IiwiYSI6ImNqcW55YzI0MjBhdjMzeHBwbGRidDMxOTUifQ.1jRgG8StatIBBqe5i8744A`
 * Secret Key: Create a developer account (free) and provision yourself a key.

 #### OpenWeatherMap
 * Example response: <https://api.openweathermap.org/data/2.5/forecast?q=Bozeman&appid=f29c7c77f7f5de2caa22ad9c92e2fe54>
   * The list of forecasts is in the `list` property
 * appId: `f29c7c77f7f5de2caa22ad9c92e2fe54`

## Code submission
 * Create a feature branch off of `master` and push your code changes to it
 * Commit frequently, and write accurate commit messages. Act as if this was a production codebase you were contributing to.
 * When you are finished making changes, open a GitHub PR in this repository.
 * Please submit your code at least 1 hour before your scheduled code review meeting.

# cloud-upload-java
Simple Java web application to handle large file uploads.

## Background

### Ruby
I first started coding Ruby for this challenge, and I've chosen Ruby because it's the language I've been using on my day job for the last year or so, and also because it would allow me to write concise and clean code. So, being Ruby the chosen language, I've decided to write a basic web application using pure Rack.

### Rack Issues
Handling file uploads and any multipart requests with Rack is simple. Rack has a built-in multipart request parser - which is great - but when a file is uploaded with multipart `POST`, there is no way of getting access to the payload stream. In other words, my application wasn't able to give proper upload feedback, because it only received `POST` data __after__ the file had been completely sent to the server.

## Switching to Java
Ruby + Rack was a bad choice to solve this challenge, so I switched to Java.

Initially, I was thinking about using Servlet 3.0 multipart features, but after doing some quick tests, I ended up with the same problem of receiving the entire file, so I decided to do a basic application using ordinary Servlet features.

### Dependencies
I decided to use Commons File-Upload - which is the only external dependency of the application - to avoid parsing multipart request parameters manually.

### Testing

#### Unit tests
All Java code is tested using JUnit and Mockito (for Servlet API Request/Response mocking) and the coding was all test-driven.

The only exception is the JavaScript part of the application. Since I've been involved with back-end applications for almost all my professional background, I haven't done any JavaScript testing (except for a few coding Dojos that I've attended). I could've used Jasmine to test it, but thought it could take too long for me to figure out the new tool before I could deliver the challenge.

#### Integration tests
I didn't write any integration tests for this challenge, mostly because it is a very time consuming task, but I definitely would write some for a production application.

## Notes and possible improvements

### General
* Progress for each upload is stored in a `Map`, and it would be far better to store them in Redis, Memcached or any other Key/Value store.
* To be able to handle concurrent uploads, each upload needs an unique identifier, and currently a timestamp is being used, so using a better unique id generator would be a good idea.

### Heroku
* The application is deployed at Heroku (they've recently officially released Java support), and they're Java platform requires Maven, so I had to use it to build my project.
* There is a class called `com.caiofilipini.upload.server.CloudUpload`, which starts an embedded Jetty server.
* I've used `foreman` to start the server and test the application locally as if it was running on Heroku.

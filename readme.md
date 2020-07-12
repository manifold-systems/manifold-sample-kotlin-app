# Sample Kotlin Application

>Simply write GraphQL queries and use them *directly* and *type-safely* from Koltin, no code gen steps, no data classes,
>no annotations.

Demonstrates the use of [Manifold](https://github.com/manifold-systems/manifold) with Kotlin. In particular this
application uses [manifold-graphql](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-graphql)
to show how resource files can be packaged in a separate Java module for use with Kotlin. Use this application as a
reference to set up Manifold with your own Kotlin applications.

See also [Using Manifold with Kotlin](http://manifold.systems/kotlin.html).

## IntelliJ IDEA and Android Studio

You can use the IDE to edit, build, and execute this project. 

IDE features such as code completion, deterministic usage searching, refactoring, navigation, incremental compilation,
etc. can be used directly with Manifold enabled types. Make GraphQL changes and type-safely use them from Kotlin without
a compilation step in between. Jump from Kotlin directly to GraphQL. Find Kotlin usages directly *from* GraphQL. No code
generation build steps. No data classes. No annotation processors. This is *true* schema-first development -- GraphQL is
the *single source of truth*. 

Run the `com.example.server.MovieServer` class to start the sample GraphQL server.

Run the `com.example.client.MovieClient` class to run a GraphQL client against the server.

## Manifold plugin for IntelliJ IDEA

Install the Manifold plugin directly from IntelliJ:

<kbd>Settings</kbd> ➜ <kbd>Plugins</kbd> ➜ <kbd>Marketplace</kbd> ➜ search: `Manifold` 
 
>Note, the plugin is free for IntelliJ IDEA Community Edition and provides a JetBrains free trial for Ultimate Edition.

## Command Line

Build this project from the command line via:
```
gradlew clean build
```
Run the server:
```
gradlew runServer
```
Run the client:
```
gradlew runClient
```
Stop the server:
```
gradlew --stop
```

  


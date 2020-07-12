package com.example.data

import com.example.schema.movies.*
import com.example.schema.movies.Genre.*
import com.example.schema.movies.Type.*
import java.time.LocalDate
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

/**
 * Simple in-memory sample data for the `movies.sample` schema
 */
class MovieData private constructor() {
    val persons: Map<String, Person>
    val animals: Map<String, Animal>
    val roles: Map<String, Role>
    val movies: Map<String, Movie>
    val reviews: MutableMap<String, Review>

    fun createReview(movieId: String, reviewInput: ReviewInput): Review {
        val movie = movies[movieId]!!
        val review = Review.builder(id(), movie, reviewInput.stars)
            .withComment(reviewInput.comment).build()
        reviews[review.id] = review
        return review
    }

    val actors: Map<String, Actor>
        get() {
            val map = HashMap<String, Actor>(persons)
            map.putAll(animals)
            return map
        }

    companion object {
        val instance = MovieData()

        private var ID = 0

        private fun id(): String {
            return (++ID).toString()
        }

        private fun dateOf(year: Int, month: Int, day: Int): LocalDate {
            return LocalDate.of(year, month, day)
        }
    }

    init {
        val STEVE_MCQUEEN = Person.builder(id(), "Steve McQueen", dateOf(1930, 3, 24))
            .withHeight(1.77)
            .withNationality("American")
            .build()
        val SLIM_PICKENS = Person.builder(id(), "Slim Pickens", dateOf(1919, 6, 29))
            .withHeight(1.91)
            .withNationality("American")
            .build()
        val JAMES_GARNER = Person.builder(id(), "James Garner", dateOf(1928, 4, 7))
            .withHeight(1.87)
            .withNationality("American")
            .build()
        persons = listOf(STEVE_MCQUEEN, SLIM_PICKENS, JAMES_GARNER).map { it.id to it }.toMap()

        val TRIGGER = Animal.builder(id(), "Trigger")
            .withKind("Horse")
            .withNationality("American")
            .build()
        animals = listOf(TRIGGER).map { it.id to it }.toMap()

        val MICHAEL_DELANEY = Role.builder(id(), STEVE_MCQUEEN, "Michael Delaney", Main)
            .build()
        val HILTS = Role.builder(id(), STEVE_MCQUEEN, "Hilts 'The Cooler King'", Main)
            .build()
        val DOC_MCCOY = Role.builder(id(), STEVE_MCQUEEN, "Doc McCoy", Main)
            .build()
        val COWBOY = Role.builder(id(), SLIM_PICKENS, "Cowboy", Supporting)
            .build()
        val HENDLY = Role.builder(id(), JAMES_GARNER, "Hendly 'The Scrounger'", Supporting)
            .build()
        val COMANCHE = Role.builder(id(), TRIGGER, "Comanche", Main)
            .build()
        val ACE = Role.builder(id(), SLIM_PICKENS, "Ace", Flat)
            .build()
        roles = listOf(MICHAEL_DELANEY, HILTS, DOC_MCCOY, COWBOY, HENDLY, COMANCHE, ACE).map { it.id to it }.toMap()

        val LE_MANS = Movie.builder(id(), "Le Mans", listOf(Action), dateOf(1971, 6, 3), listOf(MICHAEL_DELANEY))
            .withStarring(STEVE_MCQUEEN)
            .build()
        val THE_GREAT_ESCAPE =
            Movie.builder(id(), "The Great Escape", listOf(Action, Drama), dateOf(1963, 7, 4), listOf(HILTS, HENDLY))
                .withStarring(STEVE_MCQUEEN)
                .build()
        val THE_GETAWAY =
            Movie.builder(id(), "The Getaway", listOf(Action, Drama, Romance), dateOf(1972, 12, 6), listOf(DOC_MCCOY, COWBOY))
                .withStarring(STEVE_MCQUEEN)
                .build()
        val TONKA = Movie.builder(id(), "Tonka", listOf(Drama, Western), dateOf(1958, 12, 25), listOf(COMANCHE, ACE))
            .withStarring(TRIGGER)
            .build()
        movies = listOf(LE_MANS, THE_GREAT_ESCAPE, THE_GETAWAY, TONKA).map { it.id to it }.toMap()
        reviews = LinkedHashMap()
    }
}

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
        val review: Review = Review.builder(id(), movie, reviewInput.stars)
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

        private var _ID = 0

        private fun id(): String {
            return (++_ID).toString()
        }

        private fun date(year: Int, month: Int, day: Int): LocalDate {
            return LocalDate.of(year, month, day)
        }

        private fun <E> list(vararg e: E): List<E> {
            return listOf(*e)
        }
    }

    init {
        val STEVE_MCQUEEN: Person = Person.builder(id(), "Steve McQueen", date(1930, 3, 24))
            .withHeight(1.77)
            .withNationality("American")
            .build()
        val SLIM_PICKENS: Person = Person.builder(id(), "Slim Pickens", date(1919, 6, 29))
            .withHeight(1.91)
            .withNationality("American")
            .build()
        val JAMES_GARNER: Person = Person.builder(id(), "James Garner", date(1928, 4, 7))
            .withHeight(1.87)
            .withNationality("American")
            .build()
        persons = listOf(STEVE_MCQUEEN, SLIM_PICKENS, JAMES_GARNER).map { it.id to it }.toMap()

        val TRIGGER: Animal = Animal.builder(id(), "Trigger")
            .withKind("Horse")
            .withNationality("American")
            .build()
        animals = listOf(TRIGGER).map { it.id to it }.toMap()

        val MICHAEL_DELANEY: Role = Role.builder(id(), STEVE_MCQUEEN, "Michael Delaney", Main)
            .build()
        val HILTS: Role = Role.builder(id(), STEVE_MCQUEEN, "Hilts 'The Cooler King'", Main)
            .build()
        val DOC_MCCOY: Role = Role.builder(id(), STEVE_MCQUEEN, "Doc McCoy", Main)
            .build()
        val COWBOY: Role = Role.builder(id(), SLIM_PICKENS, "Cowboy", Supporting)
            .build()
        val HENDLY: Role = Role.builder(id(), JAMES_GARNER, "Hendly 'The Scrounger'", Supporting)
            .build()
        val COMANCHE: Role = Role.builder(id(), TRIGGER, "Comanche", Main)
            .build()
        val ACE: Role = Role.builder(id(), SLIM_PICKENS, "Ace", Flat)
            .build()
        roles = listOf(MICHAEL_DELANEY, HILTS, DOC_MCCOY, COWBOY, HENDLY, COMANCHE, ACE).map { it.id to it }.toMap()

        val LE_MANS: Movie = Movie.builder(id(), "Le Mans", list(Action), date(1971, 6, 3), list(MICHAEL_DELANEY))
            .withStarring(STEVE_MCQUEEN)
            .build()
        val THE_GREAT_ESCAPE: Movie =
            Movie.builder(id(), "The Great Escape", list(Action, Drama), date(1963, 7, 4), list(HILTS, HENDLY))
                .withStarring(STEVE_MCQUEEN)
                .build()
        val THE_GETAWAY: Movie =
            Movie.builder(id(), "The Getaway", list(Action, Drama, Romance), date(1972, 12, 6), list(DOC_MCCOY, COWBOY))
                .withStarring(STEVE_MCQUEEN)
                .build()
        val TONKA: Movie = Movie.builder(id(), "Tonka", list(Drama, Western), date(1958, 12, 25), list(COMANCHE, ACE))
            .withStarring(TRIGGER)
            .build()
        movies = listOf(LE_MANS, THE_GREAT_ESCAPE, THE_GETAWAY, TONKA).map { it.id to it }.toMap()
        reviews = LinkedHashMap()
    }
}

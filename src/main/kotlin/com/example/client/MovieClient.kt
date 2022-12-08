package com.example.client

import com.example.schema.movies.*
import com.example.schema.movies.Genre.*
import com.example.schema.queries
import com.example.schema.queries.*


/**
 * This simple client demonstrates type-safe usage of the sample [queries] schema file `queries.graphql`
 * using Manifold.
 *
 * Remember to run the [com.example.server.MovieServer] before running this class :)
 */
object MovieClient {
    private const val ENDPOINT = "http://localhost:4567/graphql"

    @JvmStatic
    fun main(args: Array<String>) {
        queryExample()
        mutationExample()
    }

    private fun queryExample() {
        val query = MovieQuery.builder().withGenre(Action).build()
        val result = query.request(ENDPOINT).post()
        val actionMovies = result.movies
        for (movie in actionMovies) {
            println("""
                Title: ${movie.title}
                Genre: ${movie.genre}
                Year: ${movie.releaseDate.year}
                
                """.trimIndent())
        }
    }

    private fun mutationExample() {
        // Find the movie to review ("Le Mans")
        val movie = MovieQuery.builder().withTitle("Le Mans").build()
            .request(ENDPOINT).post().movies.first()
        // Submit a review for the movie
        val review = ReviewInput.builder(5).withComment("Topnotch racing film.").build()
        val mutation = ReviewMutation.builder(movie.id, review).build()
        val createdReview = mutation.request(ENDPOINT).post().createReview
        println("""
            Review for: ${movie.title}
            Stars: ${createdReview.stars}
            Comment: ${createdReview.comment}
            
            """.trimIndent())
    }
}

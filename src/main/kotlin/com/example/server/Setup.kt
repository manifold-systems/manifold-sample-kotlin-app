package com.example.server

import com.example.data.MovieData
import com.example.schema.movies
import com.example.schema.movies.Movie
import com.example.schema.movies.ReviewInput
import graphql.TypeResolutionEnvironment
import graphql.schema.*
import graphql.schema.idl.*
import manifold.ext.rt.RuntimeMethods
import manifold.graphql.rt.api.GqlScalars
import manifold.json.rt.api.DataBindings
import manifold.json.rt.api.IJsonBindingsBacked
import manifold.rt.api.Bindings
import java.io.InputStream
import java.io.InputStreamReader
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors


/**
 * Initialize GraphQL runtime wiring.  Create simple [DataFetcher]s for the [movies] schema.
 */
object Setup {
    fun init(): graphql.GraphQL {
        val stream: InputStream =
            Setup::class.java.getResource("/com/example/schema/movies.graphql").openStream()
        val typeDefinitionRegistry: TypeDefinitionRegistry = SchemaParser().parse(InputStreamReader(stream))
        val runtimeWiringBuilder: RuntimeWiring.Builder = RuntimeWiring.newRuntimeWiring()
            .type(movies.QueryRoot::class.java.simpleName) { builder: TypeRuntimeWiring.Builder -> builder
                .dataFetcher("movies", makeFieldMatchingDataFetcherList(MovieData.instance.movies.values))
                .dataFetcher("actors", makeMappedFieldMatchingDataFetcherList(MovieData.instance.movies.values,
                    Function { e: Bindings ->
                        (e as Movie).cast.stream().map { c: movies.Role -> c.actor.bindings }
                            .collect(Collectors.toSet())
                    }))
                .dataFetcher("movie", makeFieldMatchingDataFetcherSingle(MovieData.instance.movies.values))
                .dataFetcher("role", makeFieldMatchingDataFetcherSingle(MovieData.instance.roles.values))
                .dataFetcher("person", makeFieldMatchingDataFetcherSingle(MovieData.instance.persons.values))
                .dataFetcher("persons", makeFieldMatchingDataFetcherList(MovieData.instance.persons.values))
                .dataFetcher("animal", makeFieldMatchingDataFetcherSingle(MovieData.instance.animals.values))
                .dataFetcher("review", makeFieldMatchingDataFetcherSingle(MovieData.instance.reviews.values))}
            .type(movies.MutationRoot::class.java.simpleName) { builder: TypeRuntimeWiring.Builder -> builder
                .dataFetcher("createReview", makeCreateReviewFetcher())}
            .type(movies.Actor::class.java.simpleName) { builder: TypeRuntimeWiring.Builder -> builder
                .typeResolver { env: TypeResolutionEnvironment ->
                    (if ((env.getObject() as Map<*, *>).containsKey("height")) env.schema
                        .getType(
                            movies.Person::class.java.simpleName
                        ) else env.schema.getType(
                        movies.Animal::class.java.simpleName
                    )) as GraphQLObjectType }}
            .type("CastMember") { builder: TypeRuntimeWiring.Builder -> builder
                .typeResolver { env: TypeResolutionEnvironment ->
                    (if ((env.getObject() as Map<*, *>).containsKey("height"))
                        env.schema.getType(movies.Person::class.java.simpleName)
                     else
                        env.schema.getType(movies.Animal::class.java.simpleName)) as GraphQLObjectType }}
        GqlScalars.transformFormatTypeResolvers()
            .forEach(Consumer { scalarType: GraphQLScalarType? -> runtimeWiringBuilder.scalar(scalarType) })
        val runtimeWiring: RuntimeWiring = runtimeWiringBuilder.build()
        val schemaGenerator = SchemaGenerator()
        val graphQLSchema: GraphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)
        return graphql.GraphQL.newGraphQL(graphQLSchema).build()
    }

    private fun makeFieldMatchingDataFetcherList(list: Collection<IJsonBindingsBacked>): DataFetcher<List<Bindings>> {
        return DataFetcher { env: DataFetchingEnvironment ->
            list.stream()
                .filter { item: IJsonBindingsBacked -> env.arguments.entries.stream()
                    .allMatch { arg: Map.Entry<String, Any?> -> arg.value == null ||
                            isFieldMatch(item.bindings, arg.key, arg.value) }}
                .map { obj: IJsonBindingsBacked -> obj.bindings }
                .collect(Collectors.toList())
        }
    }

    private fun makeMappedFieldMatchingDataFetcherList(list: Collection<IJsonBindingsBacked>,
            mapper: Function<Bindings, Set<Bindings>>): DataFetcher<List<Bindings>> {
        return DataFetcher { env: DataFetchingEnvironment ->
            list.stream()
                .filter { item: IJsonBindingsBacked ->
                    env.arguments.entries.stream()
                        .allMatch { arg: Map.Entry<String, Any?> ->
                            arg.value == null || isFieldMatch(
                                item.bindings,
                                arg.key,
                                arg.value
                            )
                        }
                }
                .map { obj: IJsonBindingsBacked -> obj.bindings }
                .map { t: Bindings ->
                    mapper.apply(
                        t
                    )
                }
                .flatMap { obj: Set<Bindings?> -> obj.stream() }
                .collect(Collectors.toList<Bindings>())
        }
    }

    private fun makeFieldMatchingDataFetcherSingle(list: Collection<IJsonBindingsBacked>): DataFetcher<Bindings> {
        return DataFetcher { env: DataFetchingEnvironment ->
            list.stream()
                .filter { item: IJsonBindingsBacked ->
                    env.arguments.entries.stream()
                        .allMatch { arg: Map.Entry<String, Any?> ->
                            arg.value == null || isFieldMatch(
                                item.bindings,
                                arg.key,
                                arg.value
                            )
                        }
                }
                .map { obj: IJsonBindingsBacked -> obj.bindings }
                .findFirst().orElse(null)
        }
    }

    private fun makeCreateReviewFetcher(): DataFetcher<Bindings> {
        return DataFetcher { env: DataFetchingEnvironment ->
            val review: movies.Review = MovieData.instance
                .createReview(
                    env.getArgument("movieId"),
                    RuntimeMethods.coerce(DataBindings(env.getArgument("review") as Map<String, Any>), ReviewInput::class.javaObjectType) as ReviewInput)
            println(review.javaClass.toString() + " : " + review.toString())
            review.bindings
        }
    }

    private fun isFieldMatch(bindings: Bindings, arg: String, value: Any?): Boolean {
        var actualValue = bindings[arg]
        if (value == null) {
            return actualValue == null
        }
        if (value.javaClass.isInstance(actualValue)) {
            return actualValue == value
        }
        if (actualValue is List<*>) {
            // loose matching for lists
            return (actualValue as List<*>?)!!.contains(value)
        }
        // Use Scalar coercion
        actualValue = RuntimeMethods.coerce(actualValue, value.javaClass)
        return actualValue == value
    }
}
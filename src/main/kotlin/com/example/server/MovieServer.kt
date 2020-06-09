package com.example.server

import graphql.ExecutionInput
import graphql.GraphQL
import graphql.GraphQLError
import manifold.graphql.rt.api.request.GqlRequestBody
import manifold.json.rt.Json
import manifold.json.rt.api.DataBindings
import manifold.rt.api.Bindings
import spark.Response
import spark.Spark.*
import kotlin.streams.toList


/**
 * A simple GraphQL Server using SparkJava and Manifold.
 *
 *
 * See the `MovieClient` class to see and experiment with Manifold GraphQL type-safe queries.
 */
object MovieServer {
    @JvmStatic
    fun main(args: Array<String>) {
        port(4567)
        val graphQL = Setup.init()

        //
        // Handle POST request, assumes JSON request content
        //
        post("/graphql") { req, res ->
            val request = GqlRequestBody<Bindings> { Json.fromJson(req.body()) as DataBindings }
            val exec = ExecutionInput.newExecutionInput()
                .query(request.query)
                .variables(request.variables as DataBindings)
                .build()
            executeRequest(graphQL, res, exec)
        }

        //
        // Handle Get request, assumes JSON request content
        //
        get("/graphql") { req, res ->
            val exec = ExecutionInput.newExecutionInput()
                .query(req.queryParams("query"))
                .variables(Json.fromJson(req.queryParams("variables")) as DataBindings)
                .build()
            executeRequest(graphQL, res, exec)
        }
    }

    private fun executeRequest(graphQL: GraphQL, res: Response, exec: ExecutionInput): Any {
        val execute = graphQL.execute(exec)
        val result = DataBindings()
        val errors = execute.errors
        if (errors.isNotEmpty()) {
            result["errors"] = errors.stream()
                .map { obj: GraphQLError -> obj.toSpecification() }.toList()
        }
        val data = execute.getData<Any>()
        if (data != null) {
            result["data"] = data
        }
        res.type("application/json")
        return Json.toJson(result)
    }
}

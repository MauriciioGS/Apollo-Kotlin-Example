import com.apollographql.apollo3.ApolloClient

val apolloClient = ApolloClient.Builder()
    .serverUrl("https://rickandmortyapi.com/graphql")
    .build()
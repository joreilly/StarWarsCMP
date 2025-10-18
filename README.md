GraphQL based Compose Multiplatform sample created to try out https://github.com/apollographql/apollo-kotlin-compose.  That library currently just targets Android so project also limited to that for now.

<img width="270" height="600" alt="Screenshot_20251018_201317" src="https://github.com/user-attachments/assets/9537bd64-fb11-4a0b-94a5-66b2578b0f60" />



<p/>

This is pretty much all the code below.
```
fun apolloClient() = ApolloClient.Builder()
    .serverUrl("https://swapi-graphql.netlify.app/graphql")
    .build()


@Query("""
{
  allFilms {
    films {
      ...filmFragment
    }
  }
}
"""
)
class GetAllFilms


@Fragment("""
fragment filmFragment on Film {
  id
  title
  director
}
"""
)
class FilmFragment

@Composable
@Preview
fun App() {
    MaterialTheme {
        Scaffold(
            topBar = { CenterAlignedTopAppBar(title = { Text("StarWarsCMP") }) }
        ) {
            Column(Modifier.padding(it)) {
                ApolloClientProvider(::apolloClient) {
                    val state = useQuery(GetAllFilms())

                    when (val result = state.value) {
                        is ApolloResult.Error<*> -> {
                            Text(result.exception.toString())
                        }

                        is ApolloResult.Success<GetAllFilms.Data> -> {
                            LazyColumn {
                                items(result.data.allFilms.films) {
                                    FilmView(it.filmFragment)
                                }
                            }
                        }

                        null -> { CircularProgressIndicator() }
                    }
                }
            }

        }
    }
}

@Composable
fun FilmView(film: FilmFragment.Data) {
    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            ListItem(
                headlineContent = { Text(film.title, style = MaterialTheme.typography.titleLarge) },
                supportingContent = { Text(film.director, style = MaterialTheme.typography.titleMedium, color = Color.DarkGray) }
            )
        }
    }
}
```

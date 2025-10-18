@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.starwarscmp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import apollo.kotlin.Fragment
import apollo.kotlin.Query
import com.apollographql.apollo.ApolloClient
import com.apollographql.compose.ApolloClientProvider
import com.apollographql.compose.ApolloResult
import com.apollographql.compose.useQuery
import org.jetbrains.compose.ui.tooling.preview.Preview


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
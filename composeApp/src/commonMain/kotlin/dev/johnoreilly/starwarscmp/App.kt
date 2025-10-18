package dev.johnoreilly.starwarscmp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import apollo.kotlin.Fragment
import apollo.kotlin.Query
import com.apollographql.apollo.ApolloClient
import com.apollographql.compose.ApolloClientProvider
import com.apollographql.compose.ApolloResult
import com.apollographql.compose.useQuery
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import starwarscmp.composeapp.generated.resources.Res
import starwarscmp.composeapp.generated.resources.compose_multiplatform


fun apolloClient() = ApolloClient.Builder()
    .serverUrl("https://swapi-graphql.netlify.app/graphql")
    .build()



@Query("""
{
  allPeople {
    people {
      ...personFragment      
    }
  }
}
""")
class GetAllPeople


@Fragment("""
fragment personFragment on Person {
  id
  name
  homeworld {
    name
  }
}    
"""
)
class Person

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ApolloClientProvider(::apolloClient) {
                val state = useQuery(GetAllPeople())

                when (val result = state.value) {
                    is ApolloResult.Error<*> -> {
                        println(result.exception)
                    }

                    is ApolloResult.Success<GetAllPeople.Data> -> {
                        PeopleList(result.data.allPeople?.people?.mapNotNull { it?.personFragment } ?: emptyList())

                    }

                    null -> {}
                }
            }
        }
    }
}


@Composable
fun PeopleList(people: List<Person.Data>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(people) { person ->
            PersonView(person)
        }
    }
}

@Composable
fun PersonView(person: Person.Data) {
    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        person.name ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                supportingContent = {
                    Text(
                        person.homeworld?.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.DarkGray
                    )
                }
            )
        }
    }
}

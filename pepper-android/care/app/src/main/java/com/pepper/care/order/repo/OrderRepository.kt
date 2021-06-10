package com.pepper.care.order.repo

import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.core.services.platform.entities.Allergy
import com.pepper.care.core.services.platform.entities.PlatformMeal
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.collections.ArrayList

interface OrderRepository {
    suspend fun fetchMeals(): StateFlow<List<PlatformMeal>>
    suspend fun addOrder(meal: String)
}

class OrderRepositoryImpl(
    private val appPreferences: AppPreferencesRepository
) : OrderRepository {

    override suspend fun fetchMeals(): StateFlow<List<PlatformMeal>> {
        val id = appPreferences.patientIdState.value

        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.Person.PATIENT)
                .personId(id)
                .task(PlatformMessageBuilder.Task.MEAL_ID)
                .taskId("1")
                .build()
        )

        delay(3000)

        return appPreferences.mealsState
    }


    private fun getMockMeals(): ArrayList<PlatformMeal> {
        return ArrayList<PlatformMeal>(
            listOf(
                PlatformMeal(
                    "0",
                    "Appelpannenkoeken",
                    "Vier heerlijke pannenkoeken, allemaal lekker gevuld met een mooie appel-compote. De pannenkoeken zijn vol van smaak en aangenaam zoet door de vulling van appel en rozijn. Een lekkere zoete maaltijd, ook leuk ter afwisseling van andere maaltijden.",
                    HashSet(listOf(Allergy.GLUTEN, Allergy.LACTOSE, Allergy.EGGS)),
                    960.toString(),
                    "https://www.kantenklaarmaaltijden.nl/312-large_default/appelpannenkoeken.jpg"
                ),
                PlatformMeal(
                    "1",
                    "Macaroni Bolognese en Italiaanse groenten",
                    "De macaroni Bolognese met Italiaanse groenten is een heerlijke Italiaanse pasta. De macaroni Bolognese is bereid met een saus van tomaten en is rijkelijk gevuld met rundergehakt. De Italiaanse groentemix bevat onder andere doperwtjes, worteltjes en stukjes sperzieboon.",
                    HashSet(listOf(Allergy.GLUTEN, Allergy.CELERY, Allergy.EGGS)),
                    420.toString(),
                    "https://www.kantenklaarmaaltijden.nl/39-large_default/macaroni-bolognese-en-italiaanse-groenten.jpg"
                ),
                PlatformMeal(
                    "2",
                    "Rookworst met jus voor een 'broodje warme worst",
                    "Met onze heerlijke rookworst heeft u zรณ een smakelijk 'broodje warme worst' klaar. Onze rookworst is bereid naar eigen recept, is kant en klaar en voorzien van een ruime hoeveelheid jus. Even verwarmen en op een broodje doen -wellicht wat mosterd erbij- en uw smakelijke lunchgerecht, gezonde snack of lekkere tussendoortje is klaar! Dit lunchgerecht bevat 80 gram vlees en 100 gram jus.",
                    HashSet(listOf(Allergy.GLUTEN, Allergy.LACTOSE)),
                    350.toString(),
                    "https://www.kantenklaarmaaltijden.nl/254-large_default/rookworst-met-jus-voor-een-broodje-warme-worst-.jpg"
                )
            )
        )
    }

    override suspend fun addOrder(meal: String) {
//        appPreferences.updatePublishMessage(
//            PlatformMessageBuilder.Builder()
//                .person(PlatformMessageBuilder.PersonType.PATIENT)
//                .message(PlatformMessageBuilder.MessageType.PUSH_MEAL)
//                .data(meal)
//                .build()
//                .format()
//        )
    }
}
package com.example.tch057_03_tp2.activite

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VoyageRepository {
    data class Voyage(
        val id: Int,
        val title: String,
        val imageUrl: String,
        val price: Double,
        val description: String,
        val placesRestantes: Int,
        val duree: String,
        val possibleDates: List<Long>,
        val type: String
    )

    val types = listOf("All", "Adventure", "Relaxation", "Cultural", "Historical")
    private val voyageList = listOf(
        Voyage(
            id = 1,
            title = "Crystal Cove, Maldives",
            imageUrl = "https://www.sunsiyam.com/media/5k3iw5a5/ssiv_general_04.jpg?width=782&height=521&mode=max",
            price = 8400.00,
            description = "Dormez dans des bungalows lumineux sur l'eau et plongez avec des raies manta dorées dans ce paradis bioluminescent.",
            placesRestantes = 13,
            duree = "5 jours",
            possibleDates = listOf(1745193600000L, 1746384000000L, 1746988800000L, 1750012800000L, 1755820800000L), // Dates: "22 Avril", "5 Mai", "12 Mai", "16 Juin", "30 Août"
            type = "Relaxation"
        ),
        Voyage(
            id = 2,
            title = "Santorin, Grèce",
            imageUrl = "https://res.cloudinary.com/enchanting/q_70,f_auto,w_600,h_400,c_fit/ymt-web/2023/01/600x400-Santorini20Greece20Sunset.jpg",
            price = 7600.00,
            description = "Découvrez les bâtiments emblématiques blanchis à la chaux avec des dômes bleus surplombant la mer Égée.",
            placesRestantes = 8,
            duree = "7 jours",
            possibleDates = listOf(1747161600000L, 1747680000000L, 1748457600000L, 1750204800000L, 1755820800000L), // Dates: "15 Mai", "20 Mai", "30 Mai", "11 Juin", "30 Août"
            type = "Cultural"
        ),
        Voyage(
            id = 3,
            title = "Kyoto, Japon",
            imageUrl = "https://boutiquejapan.com/wp-content/uploads/2019/07/yasaka-pagoda-higashiyama-kyoto-japan-1140x761.jpg",
            price = 5800.00,
            description = "Explorez des temples historiques, des jardins tranquilles et les fascinantes fleurs de cerisier de Kyoto.",
            placesRestantes = 5,
            duree = "10 jours",
            possibleDates = listOf(1750012800000L, 1750617600000L, 1751395200000L, 1751913600000L, 1755283200000L), // Dates: "5 Juin", "12 Juin", "20 Juin", "26 Juin", "12 Août"
            type = "Cultural"
        ),
        Voyage(
            id = 4,
            title = "Mont Fuji, Japon",
            imageUrl = "https://www.altiplano-voyage.com/assets/japon/villes/mont-fuji_1.jpg?1508344167",
            price = 6400.00,
            description = "Admirez la majesté du Mont Fuji et explorez les magnifiques paysages environnants.",
            placesRestantes = 10,
            duree = "6 jours",
            possibleDates = listOf(1744243200000L, 1747680000000L, 1750704000000L, 1752105600000L, 1755993600000L), // Dates: "10 Avril", "20 Mai", "15 Juin", "5 Juillet", "25 Août"
            type = "Adventure"
        ),
        Voyage(
            id = 5,
            title = "Colisée de Rome, Italie",
            imageUrl = "https://www.italie-authentique.com/app/uploads/sites/23/2018/11/adobestock_111251812.jpeg",
            price = 5000.00,
            description = "Plongez dans l'histoire de la Rome antique en visitant l'emblématique Colisée.",
            placesRestantes = 15,
            duree = "4 jours",
            possibleDates = listOf(1746384000000L, 1746988800000L, 1751395200000L, 1752278400000L, 1755734400000L), // Dates: "1 Mai", "12 Mai", "24 Juin", "10 Juillet", "20 Août"
            type = "Historical"
        ),
        Voyage(
            id = 6,
            title = "Site Trinity, États-Unis",
            imageUrl = "https://images.squarespace-cdn.com/content/v1/5e0bcb6ae872050321c24c17/1579327649448-KZZG8YISV4AAMQ4RUEI8/Trinity.jpeg",
            price = 3600.00,
            description = "Découvrez l'histoire de la première explosion nucléaire au Site Trinity.",
            placesRestantes = 8,
            duree = "3 jours",
            possibleDates = listOf(1744070400000L, 1752969600000L, 1760870400000L), // Dates: "7 Avril", "20 Juillet", "16 Octobre"
            type = "Historical"
        ),
        Voyage(
            id = 7,
            title = "Nagasaki, Japon",
            imageUrl = "https://gaijinpot.scdn3.secure.raxcdn.com/app/uploads/sites/6/2016/02/Nagasaki.jpg",
            price = 6000.00,
            description = "Visitez Nagasaki, un mélange fascinant d'histoire et de culture japonaise.",
            placesRestantes = 12,
            duree = "5 jours",
            possibleDates = listOf(1747161600000L, 1751395200000L, 1752278400000L, 1755734400000L, 1757116800000L), // Dates: "15 Mai", "25 Juin", "10 Juillet", "20 Août", "5 Septembre"
            type = "Cultural"
        ),
        Voyage(
            id = 8,
            title = "Istanbul, Turquie",
            imageUrl = "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1b/54/be/42/hagia-sophia-museum-by.jpg?w=600&h=400&s=1",
            price = 5400.00,
            description = "Explorez la ville magique d'Istanbul, carrefour entre l'Orient et l'Occident.",
            placesRestantes = 18,
            duree = "6 jours",
            possibleDates = listOf(1744329600000L, 1746384000000L, 1751395200000L, 1752604800000L, 1755129600000L), // Dates: "12 Avril", "1 Mai", "20 Juin", "15 Juillet", "10 Août"
            type = "Cultural"
        ),
        Voyage(
            id = 9,
            title = "Malaga, Espagne",
            imageUrl = "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/10/6e/bd/01/hotel.jpg?w=600&h=400&s=1",
            price = 4400.00,
            description = "Découvrez les plages ensoleillées et les trésors culturels de Malaga.",
            placesRestantes = 20,
            duree = "5 jours",
            possibleDates = listOf(1747075200000L, 1751395200000L, 1752105600000L, 1755734400000L, 1757894400000L), // Dates: "10 Mai", "25 Juin", "5 Juillet", "20 Août", "15 Septembre"
            type = "Relaxation"
        )
    )


    // Function to convert a single millisecond timestamp to a French date string in the format "12 juin"
    fun convertLongToDateString(timestamp: Long): String {
        val date = Date(timestamp)
        val frenchLocale = Locale("fr", "FR")
        val formatter = SimpleDateFormat("d MMMM", frenchLocale)
        return formatter.format(date)
    }

    // Function to convert a list of millisecond timestamps to a list of French date strings
    fun convertLongListToDateStringList(timestamps: List<Long>): List<String> {
        return timestamps.map { convertLongToDateString(it) }
    }

    // Function to convert a date string like "12 juin 2025" into milliseconds (12:00:00 AM by default)
    fun convertDateStringToMilliseconds(dateString: String): Long {
        val frenchLocale = Locale("fr", "FR")
        val formatter = SimpleDateFormat("d MMMM yyyy", frenchLocale)
        val date = formatter.parse(dateString)
        return date?.time ?: throw IllegalArgumentException("Invalid date format: $dateString")
    }
    fun fetchVoyages(): List<Voyage> {
        return voyageList
    }

    fun getVoyageById(id: Int): Voyage? {
        return voyageList.find { it.id == id }
    }
}
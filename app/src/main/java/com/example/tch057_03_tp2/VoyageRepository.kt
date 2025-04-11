package com.example.tch057_03_tp2

class VoyageRepository {
    data class Voyage(
        val id: Int,
        val title: String,
        val imageUrl: String,
        val price: String,
        val description: String,
        val placesRestantes: Int, // Remaining spots
        val duree: String, // Duration (e.g., "5 jours")
        val possibleDates: List<String> // List of dates (e.g., ["22 Avril", "5 Mai"])
    )


        private val voyageList = listOf(
            Voyage(
                id = 1,
                title = "Crystal Cove, Maldives",
                imageUrl = "https://example.com/image1.jpg",
                price = "$4,200",
                description = "Sleep in glowing overwater bungalows & snorkel with golden manta rays in this bioluminescent paradise.",
                placesRestantes = 13,
                duree = "5 jours",
                possibleDates = listOf("22 Avril", "5 Mai", "12 Mai", "16 Juin", "30 Août")
            ),
            Voyage(
                id = 2,
                title = "Santorini, Greece",
                imageUrl = "https://example.com/image2.jpg",
                price = "$3,800",
                description = "Discover the iconic whitewashed buildings with blue domes overlooking the Aegean Sea.",
                placesRestantes = 8,
                duree = "7 jours",
                possibleDates = listOf("15 Mai", "20 Mai", "30 Mai")
            ),
            Voyage(
                id = 3,
                title = "Kyoto, Japan",
                imageUrl = "https://example.com/image3.jpg",
                price = "$2,900",
                description = "Explore historic temples, tranquil gardens, and the mesmerizing cherry blossoms of Kyoto.",
                placesRestantes = 5,
                duree = "10 jours",
                possibleDates = listOf("5 Juin", "12 Juin", "20 Juin")
            )
        )

        fun fetchVoyages(): List<Voyage> {
            return voyageList
        }

        fun getVoyageById(id: Int): Voyage? {
            return voyageList.find { it.id == id }
        }

}


package com.example.tch057_03_tp2

class VoyageRepository {
    data class Voyage(
        val id: Int,
        val title: String,
        val imageUrl: String,
        val price: String,
        val description: String,
        val placesRestantes: Int,
        val duree: String,
        val possibleDates: List<String>
    )

    private val voyageList = listOf(
        Voyage(
            id = 1,
            title = "Crystal Cove, Maldives",
            imageUrl = "https://www.sunsiyam.com/media/5k3iw5a5/ssiv_general_04.jpg?width=782&height=521&mode=max",
            price = "$4,200",
            description = "Dormez dans des bungalows lumineux sur l'eau et plongez avec des raies manta dorées dans ce paradis bioluminescent.",
            placesRestantes = 13,
            duree = "5 jours",
            possibleDates = listOf("22 Avril", "5 Mai", "12 Mai", "16 Juin", "30 Août")
        ),
        Voyage(
            id = 2,
            title = "Santorin, Grèce",
            imageUrl = "https://res.cloudinary.com/enchanting/q_70,f_auto,w_600,h_400,c_fit/ymt-web/2023/01/600x400-Santorini20Greece20Sunset.jpg",
            price = "$3,800",
            description = "Découvrez les bâtiments emblématiques blanchis à la chaux avec des dômes bleus surplombant la mer Égée.",
            placesRestantes = 8,
            duree = "7 jours",
            possibleDates = listOf("15 Mai", "20 Mai", "30 Mai", "11 Juin", "30 Août")
        ),
        Voyage(
            id = 3,
            title = "Kyoto, Japon",
            imageUrl = "https://boutiquejapan.com/wp-content/uploads/2019/07/yasaka-pagoda-higashiyama-kyoto-japan-1140x761.jpg",
            price = "$2,900",
            description = "Explorez des temples historiques, des jardins tranquilles et les fascinantes fleurs de cerisier de Kyoto.",
            placesRestantes = 5,
            duree = "10 jours",
            possibleDates = listOf("5 Juin", "12 Juin", "20 Juin", "26 Juin", "12 Août")
        ),
        Voyage(
            id = 4,
            title = "Mont Fuji, Japon",
            imageUrl = "https://www.altiplano-voyage.com/assets/japon/villes/mont-fuji_1.jpg?1508344167",
            price = "$3,200",
            description = "Admirez la majesté du Mont Fuji et explorez les magnifiques paysages environnants.",
            placesRestantes = 10,
            duree = "6 jours",
            possibleDates = listOf("10 Avril", "20 Mai", "15 Juin", "5 Juillet", "25 Août")
        ),
        Voyage(
            id = 5,
            title = "Colisée de Rome, Italie",
            imageUrl = "https://www.italie-authentique.com/app/uploads/sites/23/2018/11/adobestock_111251812.jpeg",
            price = "$2,500",
            description = "Plongez dans l'histoire de la Rome antique en visitant l'emblématique Colisée.",
            placesRestantes = 15,
            duree = "4 jours",
            possibleDates = listOf("1 Mai", "12 Mai", "24 Juin", "10 Juillet", "20 Août")
        ),
        Voyage(
            id = 6,
            title = "Site Trinity, États-Unis",
            imageUrl = "https://images.squarespace-cdn.com/content/v1/5e0bcb6ae872050321c24c17/1579327649448-KZZG8YISV4AAMQ4RUEI8/Trinity.jpeg",
            price = "$1,800",
            description = "Découvrez l'histoire de la première explosion nucléaire au Site Trinity.",
            placesRestantes = 8,
            duree = "3 jours",
            possibleDates = listOf("7 Avril", "20 Juillet", "16 Octobre")
        ),
        Voyage(
            id = 7,
            title = "Nagasaki, Japon",
            imageUrl = "https://gaijinpot.scdn3.secure.raxcdn.com/app/uploads/sites/6/2016/02/Nagasaki.jpg",
            price = "$3,000",
            description = "Visitez Nagasaki, un mélange fascinant d'histoire et de culture japonaise.",
            placesRestantes = 12,
            duree = "5 jours",
            possibleDates = listOf("15 Mai", "25 Juin", "10 Juillet", "20 Août", "5 Septembre")
        ),
        Voyage(
            id = 8,
            title = "Istanbul, Turquie",
            imageUrl = "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1b/54/be/42/hagia-sophia-museum-by.jpg?w=600&h=400&s=1",
            price = "$2,700",
            description = "Explorez la ville magique d'Istanbul, carrefour entre l'Orient et l'Occident.",
            placesRestantes = 18,
            duree = "6 jours",
            possibleDates = listOf("12 Avril", "1 Mai", "20 Juin", "15 Juillet", "10 Août")
        ),
        Voyage(
            id = 9,
            title = "Malaga, Espagne",
            imageUrl = "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/10/6e/bd/01/hotel.jpg?w=600&h=400&s=1",
            price = "$2,200",
            description = "Découvrez les plages ensoleillées et les trésors culturels de Malaga.",
            placesRestantes = 20,
            duree = "5 jours",
            possibleDates = listOf("10 Mai", "25 Juin", "5 Juillet", "20 Août", "15 Septembre")
        )
    )

    fun fetchVoyages(): List<Voyage> {
        return voyageList
    }

    fun getVoyageById(id: Int): Voyage? {
        return voyageList.find { it.id == id }
    }
}
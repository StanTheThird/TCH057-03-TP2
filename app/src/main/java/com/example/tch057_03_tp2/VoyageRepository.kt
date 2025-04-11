package com.example.tch057_03_tp2

class VoyageRepository {

    // Function to simulate fetching voyages from an API
    fun fetchVoyages(): List<Voyage> {
        return listOf(
            Voyage("Paris Adventure", "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1245620/header.jpg?t=1739922037", "100€", "Explore the beauty of Paris!"),
            Voyage("New York Getaway", "https://via.placeholder.com/300", "200€", "Experience the thrill of NYC!"),
            Voyage("Tokyo Expedition", "https://via.placeholder.com/300", "300€", "Discover the wonders of Tokyo!"),
            Voyage("Sydney Journey", "https://via.placeholder.com/300", "400€", "Delve into Sydney's culture!"),
            Voyage("Cape Town Escape", "https://via.placeholder.com/300", "500€", "Enjoy the charm of Cape Town!"),
            Voyage("Rome Discovery", "https://via.placeholder.com/300", "150€", "Admire the history of Rome!"),
            Voyage("Dubai Luxury", "https://via.placeholder.com/300", "600€", "Indulge in Dubai's luxury!"),
            Voyage("Bangkok Adventure", "https://via.placeholder.com/300", "250€", "Experience the vibrancy of Bangkok!"),
            Voyage("Rio Carnival", "https://via.placeholder.com/300", "350€", "Celebrate life in Rio!"),
            Voyage("Amsterdam Cruise", "https://via.placeholder.com/300", "180€", "Relax on an Amsterdam cruise!")
        )
    }

    // Voyage data class
    data class Voyage(val title: String, val imageUrl: String, val price: String, val description: String)
}
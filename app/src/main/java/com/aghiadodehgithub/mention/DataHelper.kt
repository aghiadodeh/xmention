package com.aghiadodehgithub.mention

internal object DataHelper {
    fun generateUsers(): ArrayList<User> {
        val users = ArrayList<User>()
        val names = arrayListOf(
            "Jane Warren",
            "Annette Cooper",
            "Dwight Jones",
            "Irma Flores",
            "Ronald Robertson",
            "Darlene Steward",
            "Morris Henry",
            "Johnny Watson",
            "Kathryn Cooper",
            "Connie Lane",
            "Ronald Robertson",
            "Annette Cooper"
        )

        val images = arrayListOf(
            "https://images.pexels.com/photos/5870282/pexels-photo-5870282.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            "https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            "https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            "https://images.pexels.com/photos/733872/pexels-photo-733872.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260",
            "https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            "https://images.pexels.com/photos/1065084/pexels-photo-1065084.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            "https://images.pexels.com/photos/874158/pexels-photo-874158.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            "https://images.pexels.com/photos/1043474/pexels-photo-1043474.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            "https://images.pexels.com/photos/1845534/pexels-photo-1845534.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            "https://images.pexels.com/photos/3034903/pexels-photo-3034903.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            "https://images.pexels.com/photos/2379004/pexels-photo-2379004.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            "https://images.pexels.com/photos/1841819/pexels-photo-1841819.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
        )

        var counter = 0
        names.forEach { users.add(User(name = it, email = it.replace(" ", "").plus("@mail.com"), image = images[counter++])) }

        return users
    }
}
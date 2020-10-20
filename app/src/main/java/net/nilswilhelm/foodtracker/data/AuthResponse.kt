package net.nilswilhelm.foodtracker.data

class AuthResponse(
    var token: String,
    var userId: String,
    var expiresIn: Int
)
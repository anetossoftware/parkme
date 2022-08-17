package  com.anetos.parkme.data

object UrlConstants

object ConstantFirebase {
    const val COLLECTION_USERS = "users"
    const val COLLECTION_PARKING_SPOT = "parking_spot"

    enum class ROLES {
        REGULAR, ADMIN, SERVICE_PROVIDER
    }

    enum class AVAILABILITY_STATUS {
        AVAILABLE, OCCUPIED
    }

    enum class USER {
        FREE, PREMIUM
    }
}
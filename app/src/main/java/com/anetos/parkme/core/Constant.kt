package com.anetos.parkme.data

object Constant {

    var PURCHASED = ""
}

object RequestCode {
    const val APP_UPDATE_REQUEST_CODE = 1001
    val REQUEST_LOCATION = 199
    const val LOCATION_PERMISSION_REQUEST_CODE = 129
}

object SharePrefConstant {
    const val keyUserDetails = "user_details"
    const val keyFirebaseToken = "firebase_token"
}

object ConstantDelay {
    const val NAVIGATION_DELAY = 700L
}

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
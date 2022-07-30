package com.anetos.parkme.data

object Constant {

    var PURCHASED = ""
}

object RequestCode {
    const val APP_UPDATE_REQUEST_CODE = 1001
}

object SharePrefConstant {
    const val keyUserDetails = "user_details"
    const val keyFirebaseToken = "firebase_token"
}

object ConstantFirebase {
    const val COLLECTION_USERS = "users"
    const val FIRESTORE_COLLECTION = "db_parkme"
    const val FIRESTORE_MY_NOTE = "my_notes"

    enum class ROLES {
        USER, ADMIN, SERVICE_PROVIDER
    }
}
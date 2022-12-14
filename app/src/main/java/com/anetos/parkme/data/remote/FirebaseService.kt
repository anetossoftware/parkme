package com.anetos.parkme.data.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

/**
 * Api service interface to handle all the data from retrofit
 *
 * created by Jaydeep Bhayani on 16/07/2022
 */
interface FirebaseService {

    val getFirebaseFirestore : FirebaseFirestore

    companion object {
        const val BASE_URL = "https://engineering.league.dev/challenge/api/"
    }
}

class FirebaseRepo : FirebaseService {
    override val getFirebaseFirestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()
}
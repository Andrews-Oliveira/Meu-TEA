package com.example.meutea.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class DataSource {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signUp(nome: String, email: String, senha: String): FirebaseUser? {
        // Usando a instância já criada
        val result = auth.createUserWithEmailAndPassword(email, senha).await()

        // Atualizando o nome de exibição do usuário
        result.user?.let { user ->
            user.updateProfile(
                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build()
            ).await()
        }

        return result.user
    }

    suspend fun signIn(email: String, senha: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, senha).await()
        return result.user
    }
}


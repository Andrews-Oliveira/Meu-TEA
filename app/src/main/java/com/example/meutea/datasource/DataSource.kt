package com.example.meutea.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class DataSource {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    suspend fun signUp(nome: String, email: String, senha: String): FirebaseUser? {
        return try {
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()

            // Criar usuário no Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, senha).await()
            val user = authResult.user

            if (user != null) {
                // Atualizar o nome do usuário
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build()
                user.updateProfile(profileUpdates).await()

                // **Salvar os dados no Firestore**
                val userData = hashMapOf(
                    "uid" to user.uid,
                    "nome" to nome,
                    "email" to email,
                    "carteira_digital" to false // Indica que ainda não preencheu os dados da carteira
                )
                db.collection("usuarios").document(user.uid).set(userData).await()

                user // Retorna o usuário criado
            } else {
                null // Retorna null se algo deu errado
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null // Retorna null se ocorrer erro
        }
    }


    suspend fun signIn(email: String, senha: String): FirebaseUser? {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, senha).await()
            authResult.user
        } catch (e: Exception) {
            e.printStackTrace()
            null // Retorna null se houver erro no login
        }
    }
}

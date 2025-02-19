import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class DataSource {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signUp(nome: String, email: String, senha: String): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, senha).await()
        result.user?.let { user ->
            user.updateProfile(
                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build()
            ).await()
        }
        return result.user
    }

    // Método para login
    suspend fun signIn(email: String, senha: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, senha).await()
        return result.user
    }
}

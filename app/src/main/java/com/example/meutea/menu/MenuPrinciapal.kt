package com.example.meutea.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.meutea.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPrincipalScreen(navController: NavController, usuarioId: String) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Não consultar usuários arbitrários: o perfil da sessão deve corresponder à rota.
    LaunchedEffect(usuarioId) {
        if (FirebaseAuth.getInstance().currentUser?.uid != usuarioId) {
            navController.navigate("loginScreen") {
                popUpTo("welcomeScreen") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController, drawerState)
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar( // 🔹 Agora o título está realmente centralizado!
                    title = {
                        Text(
                            text = "MeuTEA",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(painterResource(R.drawable.ic_menu_24), contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0072CE))
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 🔹 Fundo com imagem borrada
                Image(
                    painter = painterResource(id = R.drawable.imghome_background),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(10.dp), // 🔥 Agora o fundo está realmente borrado!
                    contentScale = ContentScale.Crop
                )

                // 🔹 Overlay escuro para melhorar a visibilidade do texto
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center // 🔹 Agora o "Bem-vindo" fica mais alinhado
                ) {
                    Text(
                        text = "Bem-vindo",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    MenuButton(
                        text = "CARTEIRA DIGITAL",
                        iconId = R.drawable.outline_person_24,
                        color = Color(0xFF2F9ACC),
                        onClick = {
                            errorMessage = null
                            FirebaseFirestore.getInstance().collection("usuarios").document(usuarioId)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.exists() && document.getBoolean("carteira_digital") == true) {
                                        navController.navigate("carteirinhaViewScreen/$usuarioId")
                                    } else {
                                        navController.navigate("carteirinhaScreen/$usuarioId")
                                    }
                                }
                                .addOnFailureListener {
                                    errorMessage = "Não foi possível carregar a carteirinha. Verifique sua conexão."
                                }
                        }
                    )

                    MenuButton(
                        text = "CONVERSA",
                        iconId = R.drawable.baseline_chat_24,
                        color = Color(0xFF48AC51),
                        onClick = { navController.navigate("conversaScreen") } // ✅ Agora leva para a tela de conversas
                    )

                    errorMessage?.let { mensagem ->
                        Text(
                            text = mensagem,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

//                    MenuButton(
//                        text = "ROTINA",
//                        iconId = R.drawable.baseline_today_24,
//                        color = Color(0xFFECB868),
//                        onClick = { navController.navigate("RotinasScreen") }
//                    )
                }
            }
        }
    }
}

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color(0xD6002244))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "MeuTEA",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Botão de Logout
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("loginScreen") {
                    popUpTo("welcomeScreen") { inclusive = true }
                    launchSingleTop = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Informações dos Desenvolvedores
        Text(
            text = "Desenvolvido por:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Andrews Viana\nJoelson dos Santos\nWelliguinton Kaque",
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun MenuButton(
    text: String,
    iconId: Int,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = text,
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

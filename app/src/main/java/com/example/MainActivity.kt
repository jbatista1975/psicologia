package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.Patient
import com.example.ui.FilterMode
import com.example.ui.PatientViewModel
import com.example.ui.theme.MyApplicationTheme

enum class TabItem {
    Patients, Register, Archive
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: PatientViewModel = viewModel()) {
    var selectedTab by remember { mutableStateOf(TabItem.Patients) }
    val searchVal by viewModel.searchQuery.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()
    val patientsList by viewModel.patients.collectAsState()
    val context = LocalContext.current

    // Estado da Modal/Dialog de Confirmação de Deleção
    var patientToDelete by remember { mutableStateOf<Patient?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Estado do Diálogo de Visualização de Prontuário Completo
    var patientToViewDetails by remember { mutableStateOf<Patient?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                Toast.makeText(context, "Menu Cade Psicologia", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu Lateral",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "Cade Psicologia",
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Subtítulo da Página Atual
                            Text(
                                text = when (selectedTab) {
                                    TabItem.Patients -> "Pacientes"
                                    TabItem.Register -> "Novo Cadastro"
                                    TabItem.Archive -> "Arquivo"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            // Avatar da Curadoria Médica de Alexandria
                            AsyncImage(
                                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuC6D0CZeaNIYramk8If_vw8jzs4VHs2wzzeMcWOQ7u349LY4KbLu2rxqmh9EuyVY3BZ1lHIE8vPIOXJwz_3yk_nntaoZO8l1MrnxDAgR19xRIQYIocXqFE5p-rzjJ1JZhVyxQ-SKS9G4ScaeRRIfV5Jf4y0m9SA22i_BRh49-5GBOzAeCFf5fC1TvBxwecvmn1weyqvtGomf8jpg90-p007QschgL2Df_6IJfYerZXxivxsNj09msgQnlCNHXcmRTtv_qWlJjqtFwI",
                                contentDescription = "Curadora Alexandria",
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == TabItem.Patients,
                    onClick = { selectedTab = TabItem.Patients },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Pacientes") },
                    label = { Text("Pacientes", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        unselectedTextColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.testTag("tab_patients")
                )
                NavigationBarItem(
                    selected = selectedTab == TabItem.Register,
                    onClick = { selectedTab = TabItem.Register },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Cadastrar") },
                    label = { Text("Cadastrar", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        unselectedTextColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.testTag("tab_register")
                )
                NavigationBarItem(
                    selected = selectedTab == TabItem.Archive,
                    onClick = { selectedTab = TabItem.Archive },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Arquivo info") },
                    label = { Text("Arquivo", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        unselectedTextColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.testTag("tab_archive")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                },
                label = "ScreenTransitions"
            ) { targetScreen ->
                when (targetScreen) {
                    TabItem.Patients -> {
                        PatientsListScreen(
                            patients = patientsList,
                            searchQuery = searchVal,
                            onSearchChange = { viewModel.searchQuery.value = it },
                            activeFilter = activeFilter,
                            onFilterChange = { viewModel.activeFilter.value = it },
                            onDeleteStart = {
                                patientToDelete = it
                                showDeleteDialog = true
                            },
                            onPatientView = {
                                patientToViewDetails = it
                            },
                            onFABAddNew = {
                                selectedTab = TabItem.Register
                            }
                        )
                    }
                    TabItem.Register -> {
                        RegisterPatientScreen(
                            onSaveSuccess = { name, bdate, obs ->
                                viewModel.addPatient(name, bdate, obs)
                                Toast.makeText(context, "Registro de Custódia salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                selectedTab = TabItem.Patients
                            }
                        )
                    }
                    TabItem.Archive -> {
                        ArchivePhilosophyScreen(totalCount = patientsList.size)
                    }
                }
            }
        }
    }

    // Diálogo de Confirmação para Deletar Paciente
    if (showDeleteDialog && patientToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Confirmar Remoção?",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Você tem certeza de que deseja excluir os dados e prontuários de ${patientToDelete?.fullName}? Esta ação é permanente."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        patientToDelete?.let { viewModel.deletePatient(it) }
                        showDeleteDialog = false
                        patientToDelete = null
                        Toast.makeText(context, "Registro removido do arquivo.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Excluir", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de Prontuário / Detalhes Completos do Paciente
    if (patientToViewDetails != null) {
        val patient = patientToViewDetails!!
        AlertDialog(
            onDismissRequest = { patientToViewDetails = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ficha de Custódia",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    IconButton(onClick = { patientToViewDetails = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar")
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar e Iniciais
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        ).padding(16.dp).fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getInitials(patient.fullName),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        }
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                text = patient.fullName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Código ID: #${patient.id}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    // Data de Nascimento
                    Column {
                        Text(
                            text = "DATA DE NASCIMENTO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = patient.birthDate.ifEmpty { "Não informada" },
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Serif
                        )
                    }

                    // Histórico / Prontuário
                    Column {
                        Text(
                            text = "PRONTUÁRIO / OBSERVAÇÕES",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = patient.notes.ifEmpty { "Estável. Sem anotações ou ocorrências arquivadas." },
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = if (patient.notes.isEmpty()) FontStyle.Italic else FontStyle.Normal
                            )
                        }
                    }

                    // Data de Registro
                    Text(
                        text = "Registrado em: " + java.text.DateFormat.getDateTimeInstance()
                            .format(java.util.Date(patient.registrationTimestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        patientToViewDetails = null
                        patientToDelete = patient
                        showDeleteDialog = true
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Excluir Paciente", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// ==================== TELA 1: LISTAGEM DE PACIENTES ====================
@Composable
fun PatientsListScreen(
    patients: List<Patient>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    activeFilter: FilterMode,
    onFilterChange: (FilterMode) -> Unit,
    onDeleteStart: (Patient) -> Unit,
    onPatientView: (Patient) -> Unit,
    onFABAddNew: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título Principal
            Text(
                text = "Lista de Pacientes",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Caixa de Busca (Search)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth().testTag("search_field"),
                placeholder = { Text("Buscar por nome ou ocorrência...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpar")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            // Filtros de Chips
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    FilterMode.ALL to "Todos",
                    FilterMode.RECENT to "Recentes",
                    FilterMode.AZ to "A-Z",
                    FilterMode.CRITICAL to "Críticos"
                ).forEach { (mode, label) ->
                    val isSelected = activeFilter == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChange(mode) },
                        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.background,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // Listagem
            if (patients.isEmpty()) {
                // Estado Vazio (Empty State)
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Vazio",
                            modifier = Modifier.size(54.dp),
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Nenhum paciente arquivado",
                            fontFamily = FontFamily.Serif,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Pressione o botão '+' para adicionar um novo registro de custódia.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(patients) { patient ->
                        PatientItemRow(
                            patient = patient,
                            onClick = { onPatientView(patient) },
                            onDelete = { onDeleteStart(patient) }
                        )
                    }
                }
            }
        }

        // FAB Customizado com Gradiente e Visual Atraktivo
        ExtendedFloatingActionButton(
            onClick = onFABAddNew,
            icon = { Icon(Icons.Default.Add, contentDescription = "Adicionar") },
            text = { Text("ADICIONAR") },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_patient_fab"),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun PatientItemRow(
    patient: Patient,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    // Verifica se possui alertas "Críticos" nas anotações
    val isCritical = patient.notes.contains("crítico", ignoreCase = true) ||
            patient.notes.contains("urgente", ignoreCase = true) ||
            patient.notes.contains("urgência", ignoreCase = true)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Monograma Circular
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isCritical) MaterialTheme.colorScheme.errorContainer 
                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = getInitials(patient.fullName),
                color = if (isCritical) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // Detalhes do Paciente
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = patient.fullName,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isCritical) {
                    Box(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.error)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "CRÍTICO",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Text(
                text = "Nascimento: " + patient.birthDate.ifEmpty { "Não informada" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Botão de Excluir Direto
        IconButton(
            onClick = onDelete,
            modifier = Modifier.testTag("delete_btn_${patient.id}")
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Deletar Paciente",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            )
        }
    }
}

// Helper para pegar iniciais
fun getInitials(name: String): String {
    val words = name.trim().split("\\s+".toRegex())
    return when {
        words.isEmpty() -> ""
        words.size == 1 -> words[0].take(2).uppercase()
        else -> (words[0].take(1) + words[1].take(1)).uppercase()
    }
}


// ==================== TELA 2: FORMULÁRIO DE CADASTRO ====================
@Composable
fun RegisterPatientScreen(
    onSaveSuccess: (String, String, String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título e sub
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "REGISTRO DE CUSTÓDIA",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Cadastrar Novo Custodiado",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "Insira as informações fundamentais para iniciar o registro clínico médico e psicológico com a curadoria Cade Psicologia.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Formulário Bento-layout
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Nome Completo
                    Column {
                        Text(
                            text = "NOME COMPLETO *",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                nameError = false
                            },
                            modifier = Modifier.fillMaxWidth().testTag("add_name_field"),
                            placeholder = { Text("Ex: Alexandre de Oliveira") },
                            isError = nameError,
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                        if (nameError) {
                            Text(
                                text = "Nome completo é obrigatório para custódia.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Data de Nascimento
                    Column {
                        Text(
                            text = "DATA DE NASCIMENTO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = birthDate,
                            onValueChange = { birthDate = it },
                            modifier = Modifier.fillMaxWidth().testTag("add_birth_field"),
                            placeholder = { Text("Ex: 12/03/1985") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Notas Clínicas / Prontuário
                    Column {
                        Text(
                            text = "OBSERVAÇÕES CLÍNICAS (PRONTUÁRIO)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            modifier = Modifier.fillMaxWidth().height(120.dp).testTag("add_notes_field"),
                            placeholder = { Text("Dica: Adicione o termo 'Crítico' ou 'Urgente' para habilitar filtros críticos prioritários...") },
                            shape = RoundedCornerShape(8.dp),
                            maxLines = 5
                        )
                    }

                    // Botão Salvar
                    Button(
                        onClick = {
                            if (fullName.isBlank()) {
                                nameError = true
                            } else {
                                onSaveSuccess(fullName, birthDate, notes)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp).testTag("save_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Salvar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SALVAR CUSTÓDIA",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // Callout de Privacidade e Citação
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Privacidade
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Privacidade",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Privacidade: Todos os registros são encriptados e seguem estritamente as diretrizes éticas e de privacidade da curadoria Cade Psicologia.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                // Citação
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.05f))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "\"A escuta atenta é a alma de toda cura psicológica.\"",
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Atmosfera Banner Visual com imagem de psicologia
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = R.drawable.psychology_brand,
                    contentDescription = "Marca de Psicologia",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Escurecer um pouco a imagem para ler o texto em cima
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Acolhendo mentes,",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "preservando vidas.",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


// ==================== TELA 3: QUOTE / SOBRE FILOSOFIA ====================
@Composable
fun ArchivePhilosophyScreen(totalCount: Int) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Título e sub
        item {
            Column {
                Text(
                    text = "MEMÓRIAS E DIRETRIZES",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Filosofia Cade Psicologia",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "A plataforma de registros médicos estruturada para médicos, curadores e analistas de cuidado continuado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Estatísticas Bento-layout
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card 1
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "PACIENTES ATIVOS", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = totalCount.toString(),
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                // Card 2
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "INTEGRIDADE", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "100%",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }

        // Descrição Narrativa Editorial
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "A Curadoria da Custódia",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "A clínica contemporânea exige precisão e confiabilidade. Toda informação registrada no aplicativo é sincronizada localmente com um repositório SQLite robusto, garantindo integridade off-line e acesso rápido. Conforme estabelecido pelas boas práticas da psicologia e da medicina, a dignidade dos registros rege o acolhimento do paciente com Cade Psicologia.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }

        // Outro quote reflexivo
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Estrela",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "O Cade Psicologia é projetado por especialistas para que as anotações clínicas contem histórias ricas em detalhes e acolhimento humano.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(start = 14.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}


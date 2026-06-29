# 📁 Config Package

Ce package contient les classes de configuration de l'application Spring Boot, notamment la sécurité et l'initialisation des données.

## 🏗️ Architecture de Configuration

### 🎯 Objectif Principal
La configuration assure :
- **Sécurité** de l'application via Spring Security
- **Initialisation** des données de démonstration
- **Paramétrage** des comportements par défaut
- **Intégration** des composants externes

---

### 📋 Fichiers de Configuration

#### 🔐 SecurityConfig.java
**Configuration Spring Security et contrôle d'accès**

**Responsabilités principales :**
- Définition des règles d'accès par rôle
- Configuration du hashage des mots de passe
- Gestion des CORS pour les appels frontend
- Activation de la console H2 pour le développement

**Configuration détaillée :**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Hashage des mots de passe avec BCrypt
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())  // Désactivé pour l'API REST
            .authorizeHttpRequests(authz -> authz
                // Accès admin uniquement
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // Accès médecin uniquement  
                .requestMatchers("/api/doctor/**").hasRole("DOCTOR")
                // Accès public pour création d'utilisateurs
                .requestMatchers("/api/users/**").permitAll()
                // Console H2 pour développement
                .requestMatchers("/h2-console/**").permitAll()
                // Documentation Swagger
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Autres endpoints nécessitent authentification
                .requestMatchers("/api/**").authenticated()
                // Tout le reste est public
                .anyRequest().permitAll()
            )
            .headers(headers -> headers.frameOptions().disable()); // Pour H2 console

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

**Règles de sécurité implémentées :**

| Endpoint | Accès | Description |
|----------|-------|-------------|
| `/api/admin/**` | ADMIN uniquement | Gestion complète du système |
| `/api/doctor/**` | DOCTOR uniquement | Accès limité aux données du médecin |
| `/api/users/**` | Public | Création d'utilisateurs (nécessaire pour démarrage) |
| `/h2-console/**` | Public | Console de développement H2 |
| `/swagger-ui/**` | Public | Documentation API interactive |
| `/v3/api-docs/**` | Public | Spécification OpenAPI |

**Politique de mots de passe :**
- **Algorithme** : BCrypt (standard de sécurité)
- **Force** : Facteur par défaut (10 rounds)
- **Stockage** : Hash uniquement, jamais en clair

---

#### 🌱 DataInitializer.java
**Initialisation des données de démonstration**

**Objectif :**
- Créer des utilisateurs de base pour tester l'application
- Générer des données réalistes pour démonstration
- Établir des relations complexes entre entités
- Faciliter le démarrage rapide de l'application

**Configuration :**
```java
@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Ne s'exécute que si la base est vide
        if (userRepository.count() == 0) {
            initializeData();
        }
    }

    private void initializeData() {
        // 1. Créer les utilisateurs de base
        User admin = createAdminUser();
        User doctor = createDoctorUser();

        // 2. Créer l'entité Doctor associée
        Doctor doctorEntity = createDoctorEntity(doctor);

        // 3. Créer des patients de démonstration
        List<Patient> patients = createDemoPatients();

        // 4. Créer des prescriptions de démonstration
        createDemoPrescriptions(doctorEntity, patients);

        // 5. Créer le portefeuille virtuel automatiquement
        createVirtualWallet(doctorEntity);
    }
}
```

**Données créées automatiquement :**

### 👤 Utilisateurs
```java
// Administrateur
User admin = new User(
    "Admin System", 
    "admin@medical.com", 
    passwordEncoder.encode("admin123"), 
    UserRole.ADMIN
);

// Médecin
User doctor = new User(
    "Dr. Sarah Johnson",
    "sarah.johnson@medical.com", 
    passwordEncoder.encode("doctor123"), 
    UserRole.DOCTOR
);
```

### 👨‍⚕️ Médecin
```java
Doctor doctorEntity = new Doctor(
    doctor,                           // User associé
    "Dr. Sarah Johnson",              // Nom complet
    "Cardiologie",                   // Spécialité
    "MED-2023-001",                  // Numéro de licence
    "+33 1 23 45 67 89"              // Téléphone
);
```

### 🧑‍⚕️ Patients (5 patients de démonstration)
```java
List<Patient> patients = Arrays.asList(
    new Patient("Jean", "Dupont", LocalDate.of(1980, 5, 15), "M", "+33 6 12 34 56 78", "123 Rue de la Paix, 75001 Paris"),
    new Patient("Marie", "Martin", LocalDate.of(1975, 8, 22), "F", "+33 6 23 45 67 89", "456 Avenue des Champs, 75008 Paris"),
    new Patient("Pierre", "Durand", LocalDate.of(1985, 3, 10), "M", "+33 6 34 56 78 90", "789 Boulevard Saint-Germain, 75006 Paris"),
    new Patient("Sophie", "Bernard", LocalDate.of(1990, 11, 28), "F", "+33 6 45 67 89 01", "321 Rue de Rivoli, 75004 Paris"),
    new Patient("Michel", "Petit", LocalDate.of(1978, 7, 5), "M", "+33 6 56 78 90 12", "654 Place de la Concorde, 75008 Paris")
);
```

### 💊 Prescriptions (exemples)
```java
// Prescription 1
Prescription prescription1 = new Prescription(
    doctorEntity, patient1, admin,
    new BigDecimal("1500.00"),           // Montant total
    "Traitement cardiaque - Médicaments sur 3 mois"
);

// Prescription 2  
Prescription prescription2 = new Prescription(
    doctorEntity, patient2, admin,
    new BigDecimal("800.00"),            // Montant total
    "Suivi diabète - Équipement de surveillance"
);
```

### 💰 Portefeuille Virtuel
```java
// Créé automatiquement avec solde initial de 0
VirtualWallet wallet = new VirtualWallet(doctorEntity);
// Solde sera mis à jour lors de la création des prescriptions (5% de commission)
```

---

## 🔄 Cycle de Vie

### 🚀 Démarrage de l'Application
1. **Spring Boot** démarre
2. **SecurityConfig** configure la sécurité
3. **DataInitializer** vérifie si la base est vide
4. **Si vide** : Création des données de démonstration
5. **Sinon** : Utilisation des données existantes

### 🛡️ Sécurité en Action
```java
// Tentative d'accès admin par un médecin
GET /api/admin/prescriptions
→ 403 Forbidden (rôle DOCTOR ne peut pas accéder)

// Tentative d'accès médecin par un admin  
GET /api/doctor/prescriptions?doctorId=xxx
→ 200 OK (rôle ADMIN peut tout accéder)

// Création d'utilisateur sans authentification
POST /api/users/admin
→ 200 OK (endpoint public pour démarrage)
```

---

## ⚙️ Personnalisation

### 🔧 Modification des Données de Démonstration
```java
// Pour ajouter plus de patients
private void createMorePatients() {
    for (int i = 0; i < 10; i++) {
        Patient patient = new Patient(
            "Patient" + i, "Test" + i,
            LocalDate.of(1980 + i, 1, 1),
            i % 2 == 0 ? "M" : "F",
            "+33 6 00 00 00 " + String.format("%02d", i),
            "Adresse test " + i
        );
        patientRepository.save(patient);
    }
}
```

### 🔒 Adaptation des Règles de Sécurité
```java
// Pour ajouter un nouveau rôle
.requestMatchers("/api/pharmacist/**").hasRole("PHARMACIST")

// Pour rendre certains endpoints privés
.requestMatchers("/api/users/**").hasRole("ADMIN")  // Plus public
```

### 🌐 Configuration CORS
```java
// Pour restreindre les origines en production
configuration.setAllowedOrigins(Arrays.asList("https://app-medical.com"));
```

---

## 🧪 Tests et Débogage

### 🧪 Tests de Configuration
```java
@SpringBootTest
@AutoConfigureTestDatabase
class SecurityConfigTest {

    @Test
    void testPasswordEncoding() {
        String password = "test123";
        String encoded = passwordEncoder.encode(password);
        assertNotEquals(password, encoded);
        assertTrue(passwordEncoder.matches(password, encoded));
    }

    @Test
    void testCorsConfiguration() {
        // Tester les en-têtes CORS
        mockMvc.perform(options("/api/test"))
                .andExpect(status().isOk());
    }
}
```

### 🔍 Débogage de la Sécurité
```java
// Pour logger les tentatives d'accès
@Bean
public AuditorAware<String> auditorAware() {
    return () -> {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(auth != null ? auth.getName() : "anonymous");
    };
}
```

---

## 🚀 Bonnes Pratiques

### 🔒 Sécurité
- **Jamais** de mots de passe en clair
- **Toujours** utiliser HTTPS en production
- **Valider** toutes les entrées utilisateur
- **Logger** les tentatives d'accès non autorisées

### 🌱 Initialisation
- **Conditionnelle** : uniquement si base vide
- **Idempotente** : peut être exécutée plusieurs fois
- **Testable** : données prévisibles pour les tests
- **Documentée** : données connues pour le support

### ⚙️ Configuration
- **Externalisée** : propriétés dans application.properties
- **Versionnée** : configuration différente par environnement
- **Testée** : tests unitaires pour chaque règle
- **Documentée** : README avec exemples

---

## 🔮 Évolutions Possibles

### 🔐 Sécurité Avancée
```java
// JWT tokens
@Bean
public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
}

// OAuth2
@EnableOAuth2Client
public class OAuth2Config { ... }

// Rate limiting
@Bean
public FilterRegistrationBean<RateLimitFilter> rateLimitFilter() {
    return new FilterRegistrationBean<>(new RateLimitFilter());
}
```

### 🌱 Initialisation Avancée
```java
// Chargement depuis fichiers JSON
@Value("classpath:demo-data.json")
private Resource demoDataFile;

// Initialisation conditionnelle par profil
@Profile("demo")
@Component
public class DemoDataInitializer { ... }
```

### ⚙️ Configuration Dynamique
```java
// Configuration depuis base de données
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private List<String> allowedOrigins;
    private boolean enableSwagger;
    // getters/setters
}
```

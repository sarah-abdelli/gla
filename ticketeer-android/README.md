# Ticketeer Android — App Agent de Contrôle

Application mobile pour les agents de contrôle du réseau ferroviaire.

## Fonctionnalités
- Login agent (login/mot de passe) → reçoit un JWT
- Scanner QR Code du billet via la caméra
- Validation du billet en temps réel (< 2 secondes)
- Affichage résultat ACCEPTÉ ✅ ou REFUSÉ ❌

## Prérequis
- Android Studio Hedgehog+
- SDK Android 24+ (Android 7.0)
- Le backend Spring Boot doit tourner sur le PC

## Ouvrir le projet
1. Ouvre Android Studio
2. File → Open → sélectionne le dossier `ticketeer-android`
3. Attends que Gradle télécharge les dépendances (2-3 min)

## Configuration réseau
Dans `RetrofitClient.java` :
- **Émulateur Android** : `http://10.0.2.2:8080/` (déjà configuré)
- **Téléphone réel** : remplace par l'IP de ton PC ex: `http://192.168.1.X:8080/`

## Compte de test (agent)
- Login : `agent1`
- Mot de passe : `agent123`

## Structure
```
app/src/main/java/com/ticketeer/agent/
├── activity/
│   ├── LoginActivity.java    ← écran connexion
│   ├── ScanActivity.java     ← scanner QR Code
│   └── ResultActivity.java   ← affichage résultat
├── model/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── ValidationRequest.java
│   └── ValidationResponse.java
├── network/
│   ├── ApiService.java       ← interface Retrofit
│   └── RetrofitClient.java   ← configuration HTTP
└── utils/
    └── SessionManager.java   ← stockage JWT
```

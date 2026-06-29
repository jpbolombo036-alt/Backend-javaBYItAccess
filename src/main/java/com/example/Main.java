package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l'application Spring Boot
 * 
 * Cette classe sert de point d'entrée pour l'application de gestion des prescriptions médicales.
 * L'annotation @SpringBootApplication active :
 * - Auto-configuration : Configuration automatique des composants Spring
 * - Component scanning : Recherche automatique des beans Spring dans le package
 * - Configuration properties : Chargement des propriétés depuis application.properties
 * 
 * L'application expose des API REST pour gérer les prescriptions, commissions, 
 * paiements et portefeuilles virtuels dans un contexte médical.
 * 
 * @author Medical Prescription System
 * @version 1.0
 */
@SpringBootApplication
public class Main {
    
    /**
     * Point d'entrée principal de l'application
     * 
     * @param args Arguments de ligne de commande (non utilisés dans cette application)
     */
    public static void main(String[] args) {
        // Démarre l'application Spring Boot avec configuration automatique
        SpringApplication.run(Main.class, args);
    }
}
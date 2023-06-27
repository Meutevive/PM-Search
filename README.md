
# PM Search

L'application mobile PM est conçue pour aider les techniciens fibre optique à rechercher et enregistrer des points de mutualisation (PM) dans leur ville. Les fonctionnalités principales de l'application incluent la recherche de PM par ville et par son numéro, l'enregistrement de nouveaux PM avec des informations telles que le numéro, la position et le commentaire, et la possibilité d'ajouter des photos.


## DEV

- [@Meutevive](https://github.com/Meutevive)


## Prérequis

Android Studio (dernière version)

Compte Firebase

JDK 8 ou ultérieur


## Installation

**Cloner le dépôt GitHub:**

Ouvrez un terminal
Naviguez vers l'endroit où vous voulez que le projet soit cloné

Exécutez la commande :
```bash
  git clone https://github.com/Meutevive/PM-Search.git
```

**Importer le projet dans Android Studio:**

Ouvrez Android Studio

*Cliquez sur File > New > Import Project*

Sélectionnez le dossier du projet que vous venez de cloner et cliquez sur OK
Créer un nouveau projet Firebase

**Connectez-vous à votre compte Firebase à l'adresse :**

https://console.firebase.google.com/

Cliquez sur *"Ajouter un projet"*

Donnez un nom à votre projet et suivez les étapes pour le créer

**Ajouter votre application à Firebase:**

Dans votre projet Firebase, cliquez sur *"Ajouter une application"

Sélectionnez *"Android"*

Entrez le nom du package de votre application (vous pouvez le trouver dans le fichier *AndroidManifest.xml* de votre projet)

Suivez les instructions pour enregistrer votre application,
Téléchargez le fichier *google-services.json* et placez-le dans le dossier *app/* de votre projet Android Studio

**Configurer Firebase Cloud Messaging:**

Dans le menu de gauche de la console Firebase, cliquez sur *"Cloud Messaging"*
Suivez les instructions pour configurer FCM pour votre application
Notez la clé du serveur - elle sera nécessaire pour envoyer des notifications depuis le serveur

**Lancer l'application**

Dans Android Studio, cliquez sur* Run > Run 'app'*
Choisissez l'émulateur ou l'appareil sur lequel vous voulez exécuter l'application
L'application devrait maintenant s'exécuter sur l'appareil choisi
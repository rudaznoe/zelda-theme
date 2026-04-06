# Wild Legend Theme - Clean Build

Projet Android propre pour générer un APK via GitHub Actions.

## Ce que contient ce repo
- Une activité simple pour activer le live wallpaper
- Un `WallpaperService` autonome
- Un workflow GitHub Actions qui génère `app-debug.apk`

## Build GitHub
Dès que le projet est poussé sur `main`, GitHub Actions lance le build.
Le fichier APK sera disponible dans **Actions > Build APK > Artifacts > app-debug**.

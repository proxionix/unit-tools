# Migration vers JSON Assets - Commande Matériel

## Vue d'ensemble

La fonctionnalité "Commande Matériel" a été migrée d'une configuration XML statique vers un système JSON dynamique éditable. Cette migration permet de modifier le catalogue de produits sans recompiler l'application.

## Modifications apportées

### 1. Architecture de données

#### Avant (XML statique)
- Maxima des produits dans `integers.xml`
- Noms de produits codés en dur (a1-a24)
- Nécessitait recompilation pour toute modification

#### Après (JSON dynamique)
- Catalogue complet dans `materials_order.json`
- Noms localisés (FR/NL) pour chaque produit
- Éditable sans recompilation

### 2. Fichiers créés

**`app/src/main/assets/materials_order.json`**
```jsonc
{
  "products": [
  {"code": "a1", "name_fr": "Agrafes", "name_nl": "Nietjes", "max": 1, "per": 1000},
  // ...24 produits au total
  ]
}
```

**`app/src/main/java/com/unit/tools/model/OrderItem.kt`**
- Data class modélisant un produit
- Propriétés: `code`, `name_fr`, `name_nl`, `max`, `per`

**`app/src/main/java/com/unit/tools/data/OrderCatalog.kt`**
- Loader JSON avec validation robuste
- Vérifie: 24 items, séquence a1-a24, champs vides, valeurs invalides
- Log des warnings pour faciliter le debug

### 3. Fichiers modifiés

**`build.gradle.kts`**
- Ajout: `implementation("com.google.code.gson:gson:2.11.0")`

**`MaterialOrderViewModel.kt`**
- Ajout: `catalog: StateFlow<List<OrderItem>>`
- Ajout: `loadCatalog(context: Context)`
- Ajout: `displayName(index1Based: Int, language: String): String`
- Modifié: `maxima()` - lit depuis catalogue JSON (sans Context)
- Modifié: `quantitiesMapClamped()` - utilise `catalog[i].max` (sans Context)

**`MaterialOrderScreen.kt`**
- Ajout: Chargement catalogue via `LaunchedEffect`
- Ajout: Détection langue via `AppCompatDelegate.getApplicationLocales()`
- Modifié: Affichage noms produits via `viewModel.displayName()`
- Supprimé: Validation PDF (déplacée dans `PdfOrderFiller`)
- Supprimé: Import `BuildConfig` (inutilisé)

**`PdfOrderFiller.kt`**
- Modifié: Resource management avec `.use {}` pour auto-close
- Amélioration: Gestion sécurisée des ressources (InputStream, PDDocument)

### 4. Fichiers supprimés

- `app/src/main/res/values/integers.xml` (remplacé par JSON)
- 9 fichiers markdown redondants (consolidés dans ce document)

## Structure du catalogue JSON

### Format
```jsonc
{
  "products": [
    {
  "code": "a1",           // Identifiant unique (a1-a24)
  "name_fr": "Agrafes",   // Nom français
  "name_nl": "Nietjes",   // Nom néerlandais
  "max": 1,               // Quantité maximum commandable
  "per": 1000             // Unités par paquet
    }
  ]
}
```

### Validation
Le loader `OrderCatalog.kt` valide automatiquement:
1. **Nombre de produits**: Doit être exactement 24
2. **Séquence des codes**: Attend a1, a2, ..., a24 dans l'ordre
3. **Champs vides**: Log warning si `name_fr` ou `name_nl` vide
4. **Valeurs invalides**: Log warning si `max ≤ 0` ou `per ≤ 0`

## Flux de données

```
materials_order.json
    ↓
OrderCatalog.load(context)
    ↓
MaterialOrderViewModel.catalog
    ↓
MaterialOrderScreen.displayName()
    ↓
UI (Affichage localisé)
```

## Localisation

### Détection de langue
- Utilise `AppCompatDelegate.getApplicationLocales()` (per-app locale)
- Fallback sur `Locale.getDefault()` si non défini
- Supporte FR, NL, EN

### Templates PDF
- **Français**: `FR-Materiel.pdf`
- **Néerlandais**: `NL-Materiel.pdf`
- Sélection automatique selon la langue de l'app

### Affichage produits
```kotlin
viewModel.displayName(index1Based = 1, language = "nl") // "Nietjes"
viewModel.displayName(index1Based = 1, language = "fr") // "Agrafes"
```

## Génération PDF

### Workflow
1. Utilisateur saisit nom/prénom + quantités (24 produits)
2. ViewModel clamp les quantités selon `max` du catalogue
3. `PdfOrderFiller` sélectionne template FR/NL
4. Remplissage AcroForm: `name_tech` + a1-a24
5. Flatten du formulaire
6. Sauvegarde dans cache: `CommandeMateriel_yyyyMMdd_HHmmss.pdf`

### Champs PDF
- **name_tech**: Nom complet du technicien
- **a1-a24**: Quantités commandées (clampées selon maxima)

### Resource Management
```kotlin
context.assets.open(templateName).use { stream ->
    PDDocument.load(stream).use { doc ->
        // Traitement
    }
}
```

## Envoi email

### Configuration
- **Destinataire**: `warehouse_houthalen@unit-t.eu`
- **Sujet**: Défini dans `R.string.order_email_subject`
- **Corps**: Défini dans `R.string.order_email_body`
- **Pièce jointe**: PDF généré

### FileProvider
Fichier de configuration: `app/src/main/res/xml/file_paths.xml`
```xml
<cache-path name="pdf_cache" path="." />
```

## Tests recommandés

### 1. Chargement catalogue
- ✅ Vérifier 24 produits chargés
- ✅ Vérifier codes a1-a24
- ✅ Vérifier noms FR/NL présents
- ✅ Vérifier maxima/per > 0

### 2. Affichage UI
- ✅ Langue FR: noms français affichés
- ✅ Langue NL: noms néerlandais affichés
- ✅ Quantités clampées selon maxima

### 3. Génération PDF
- ✅ Template FR sélectionné (langue FR)
- ✅ Template NL sélectionné (langue NL)
- ✅ Champ `name_tech` rempli
- ✅ Champs a1-a24 remplis
- ✅ Valeurs clampées correctement

### 4. Email
- ✅ Intent email lancé
- ✅ PDF joint
- ✅ Destinataire correct
- ✅ Sujet/corps localisés

## Maintenance

### Modifier le catalogue
1. Éditer `app/src/main/assets/materials_order.json`
2. Respecter le format exact (24 produits, codes a1-a24)
3. **Pas de recompilation nécessaire**
4. Rebuild assets uniquement

### Ajouter une langue
1. Ajouter champ `name_XX` dans JSON
2. Modifier `displayName()` dans ViewModel
3. Ajouter template `XX-Materiel.pdf` dans assets
4. Modifier `isApplicationLanguageDutch()` → fonction générique

## Dépendances

```kotlin
// JSON parsing
implementation("com.google.code.gson:gson:2.11.0")

// PDF manipulation
implementation("com.tom_roush.pdfbox:pdfbox-android:2.0.27.0")

// Per-app locale
implementation("androidx.appcompat:appcompat:1.7.0")
```

## Fichiers clés

```
UnitTools/
├── app/src/main/
│   ├── assets/
│   │   └── materials_order.json          # Catalogue produits (ÉDITABLE)
│   ├── java/com/unit/tools/
│   │   ├── data/
│   │   │   └── OrderCatalog.kt            # Loader JSON
│   │   ├── model/
│   │   │   └── OrderItem.kt               # Modèle produit
│   │   ├── pdf/
│   │   │   └── PdfOrderFiller.kt          # Génération PDF
│   │   ├── share/
│   │   │   └── EmailSender.kt             # Envoi email
│   │   └── ui/
│   │       ├── order/
│   │       │   └── MaterialOrderViewModel.kt
│   │       └── screens/
│   │           └── MaterialOrderScreen.kt
│   └── res/
│       ├── values/
│       │   └── strings.xml                # Textes localisés
│       └── xml/
│           └── file_paths.xml             # FileProvider config
└── build.gradle.kts                       # Dépendances
```

## Résolution de problèmes

### Catalogue ne charge pas
- Vérifier `materials_order.json` dans `app/src/main/assets/`
- Vérifier format JSON valide
- Vérifier 24 produits exactement
- Consulter logs avec tag `OrderCatalog`

### Noms produits incorrects
- Vérifier langue détectée: logs avec tag `MaterialOrderScreen`
- Vérifier champs `name_fr` / `name_nl` dans JSON
- Tester avec langue différente dans Settings → System → Languages

### PDF invalide
- Vérifier templates `FR-Materiel.pdf` / `NL-Materiel.pdf` dans assets
- Vérifier champs AcroForm: `name_tech`, a1-a24
- Consulter logs avec tag `PdfOrderFiller`

### Email ne s'ouvre pas
- Vérifier FileProvider configuré dans `AndroidManifest.xml`
- Vérifier `file_paths.xml` existe
- Vérifier app email installée sur appareil

## Migration Summary

**✅ Objectifs atteints:**
- ✅ Catalogue JSON éditable sans recompilation
- ✅ Noms produits localisés FR/NL
- ✅ Validation robuste du catalogue
- ✅ Build sans erreurs
- ✅ Code obsolète supprimé
- ✅ Resource management sécurisé (.use {})
- ✅ Architecture propre et maintenable

**📦 Fichiers modifiés:** 5  
**🆕 Fichiers créés:** 3  
**🗑️ Fichiers supprimés:** 10  
**🔧 Dépendances ajoutées:** 1 (Gson 2.11.0)

---
*Document généré lors de la migration vers JSON assets - Octobre 2025*

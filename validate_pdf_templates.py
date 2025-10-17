#!/usr/bin/env python3
"""
Script de validation des templates PDF
Vérifie que FR-Materiel.pdf et NL-Materiel.pdf contiennent les champs requis

Prérequis: pip install pypdf2 ou pdfplumber
"""

import sys
from pathlib import Path

try:
    from PyPDF2 import PdfReader
except ImportError:
    print("❌ PyPDF2 non installé. Installez avec: pip install PyPDF2")
    sys.exit(1)


def validate_pdf_fields(pdf_path: Path) -> tuple[set, set]:
    """Valide les champs d'un PDF et retourne (champs_présents, champs_manquants)"""
    
    # Champs requis
    expected_fields = {"name_tech"}
    for i in range(1, 25):
        expected_fields.add(f"a{i}")
    
    try:
        reader = PdfReader(pdf_path)
        
        # Récupérer les champs du formulaire
        if "/AcroForm" not in reader.trailer["/Root"]:
            print(f"⚠️  {pdf_path.name}: Aucun AcroForm trouvé")
            return set(), expected_fields
        
        fields = reader.get_fields()
        if fields is None:
            print(f"⚠️  {pdf_path.name}: Formulaire vide")
            return set(), expected_fields
        
        field_names = set(fields.keys())
        missing_fields = expected_fields - field_names
        
        return field_names, missing_fields
        
    except Exception as e:
        print(f"❌ Erreur lors de la lecture de {pdf_path.name}: {e}")
        return set(), expected_fields


def main():
    """Point d'entrée principal"""
    
    # Chemins des templates
    assets_dir = Path(__file__).parent / "app" / "src" / "main" / "assets"
    fr_pdf = assets_dir / "FR-Materiel.pdf"
    nl_pdf = assets_dir / "NL-Materiel.pdf"
    
    print("🔍 Validation des templates PDF\n")
    print("=" * 60)
    
    all_valid = True
    
    for pdf_path in [fr_pdf, nl_pdf]:
        print(f"\n📄 {pdf_path.name}")
        print("-" * 60)
        
        if not pdf_path.exists():
            print(f"❌ Fichier introuvable: {pdf_path}")
            all_valid = False
            continue
        
        present, missing = validate_pdf_fields(pdf_path)
        
        if not missing:
            print(f"✅ Tous les champs sont présents ({len(present)} champs)")
        else:
            print(f"❌ Champs manquants ({len(missing)}):")
            missing_sorted = sorted(missing, key=lambda x: (x[0], int(x[1:]) if x[1:].isdigit() else 0))
            for field in missing_sorted:
                print(f"   - {field}")
            all_valid = False
        
        if present:
            print(f"\n📋 Champs présents ({len(present)}):")
            present_sorted = sorted(present, key=lambda x: (x[0], int(x[1:]) if x[1:].isdigit() else 0))
            # Afficher en colonnes
            for i in range(0, len(present_sorted), 6):
                fields_chunk = present_sorted[i:i+6]
                print(f"   {', '.join(fields_chunk)}")
    
    print("\n" + "=" * 60)
    
    if all_valid:
        print("✅ Validation réussie : tous les templates sont conformes")
        return 0
    else:
        print("❌ Validation échouée : des champs sont manquants")
        print("\n💡 Consultez PDF_FIELDS_GUIDE.md pour corriger les templates")
        return 1


if __name__ == "__main__":
    sys.exit(main())

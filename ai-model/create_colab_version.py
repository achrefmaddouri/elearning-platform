import json
import shutil

# Copier le notebook original
shutil.copy('E_Learning_AI_Recommendation_System.ipynb', 
            'E_Learning_AI_Recommendation_System_Colab.ipynb')

# Lire le notebook
with open('E_Learning_AI_Recommendation_System_Colab.ipynb', 'r', encoding='utf-8') as f:
    notebook = json.load(f)

print("🔧 Création de la version Google Colab...")

# Modifier la cellule de setup (index 2)
notebook['cells'][2]['source'] = [
    "# Mount Google Drive\n",
    "from google.colab import drive\n",
    "import os\n",
    "\n",
    "drive.mount('/content/drive')\n",
    "\n",
    "# Set up working directory\n",
    "work_dir = '/content/drive/MyDrive/ELearning_AI_Project'\n",
    "os.makedirs(work_dir, exist_ok=True)\n",
    "os.chdir(work_dir)\n",
    "\n",
    "print(\"📁 Google Drive mounted successfully!\")\n",
    "print(f\"📂 Working directory: {work_dir}\")\n",
    "\n",
    "# Create necessary directories\n",
    "os.makedirs('data', exist_ok=True)\n",
    "os.makedirs('models', exist_ok=True)\n",
    "os.makedirs('outputs', exist_ok=True)\n",
    "\n",
    "print(\"✅ Environment setup complete!\")"
]

# Modifier le titre de la section
notebook['cells'][1]['source'] = [
    "## 🔧 Setup Google Colab Environment\n",
    "\n",
    "First, let's mount Google Drive to access our datasets and save our trained models."
]

# Effacer les outputs d'erreur
notebook['cells'][2]['outputs'] = []

# Sauvegarder
with open('E_Learning_AI_Recommendation_System_Colab.ipynb', 'w', encoding='utf-8') as f:
    json.dump(notebook, f, indent=1, ensure_ascii=False)

print("✅ Notebook Colab créé: E_Learning_AI_Recommendation_System_Colab.ipynb")
print("\n📝 Instructions:")
print("1. Aller sur https://colab.research.google.com")
print("2. File → Upload notebook")
print("3. Sélectionner: E_Learning_AI_Recommendation_System_Colab.ipynb")
print("4. Runtime → Run all")
print("\n🎉 Tout fonctionnera parfaitement sur Colab!")

# 🐍 Guide d'Installation - Python 3.11 pour E-Learning AI

## 📋 Étape 1: Télécharger Python 3.11

1. **Aller sur le site officiel:**
   - Ouvrir: https://www.python.org/downloads/
   - Cliquer sur "Download Python 3.11.x" (la dernière version 3.11)
   
2. **Télécharger l'installateur Windows:**
   - Fichier: `python-3.11.x-amd64.exe`

## 🔧 Étape 2: Installer Python 3.11

1. **Lancer l'installateur**
2. **IMPORTANT:** ✅ Cocher "Add Python 3.11 to PATH"
3. Choisir "Customize installation"
4. Cocher toutes les options
5. Dans "Advanced Options":
   - ✅ Install for all users
   - ✅ Add Python to environment variables
   - Chemin d'installation: `C:\Python311\`
6. Cliquer "Install"

## 🌐 Étape 3: Vérifier l'Installation

Ouvrir PowerShell et taper:
```powershell
python --version
```

Vous devriez voir: `Python 3.11.x`

**Si vous voyez Python 3.13:**
```powershell
# Utiliser le chemin complet
C:\Python311\python.exe --version
```

## 📦 Étape 4: Créer un Environnement Virtuel

```powershell
# Aller dans le dossier du projet
cd C:\Users\21692\Desktop\charfaFinal\achref\ai-model

# Créer l'environnement virtuel avec Python 3.11
C:\Python311\python.exe -m venv venv_py311

# Activer l'environnement
.\venv_py311\Scripts\Activate.ps1
```

**Si vous avez une erreur de sécurité PowerShell:**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

## 📚 Étape 5: Installer les Bibliothèques

```powershell
# Mettre à jour pip
python -m pip install --upgrade pip

# Installer toutes les bibliothèques nécessaires
pip install scikit-surprise implicit lightfm tensorflow pandas numpy matplotlib seaborn plotly scipy scikit-learn jupyter
```

## 🚀 Étape 6: Lancer Jupyter Notebook

```powershell
# Toujours dans l'environnement virtuel activé
jupyter notebook
```

Le navigateur s'ouvrira automatiquement. Ouvrir `E_Learning_AI_Recommendation_System.ipynb`

## ✅ Étape 7: Tester le Notebook

1. Dans Jupyter, cliquer sur "Kernel" → "Restart & Run All"
2. Toutes les cellules devraient s'exécuter sans erreur!

## 🔄 Pour les Prochaines Fois

Chaque fois que vous voulez utiliser le notebook:

```powershell
cd C:\Users\21692\Desktop\charfaFinal\achref\ai-model
.\venv_py311\Scripts\Activate.ps1
jupyter notebook
```

## 🆘 Dépannage

### Problème: "python n'est pas reconnu"
**Solution:** Utiliser le chemin complet `C:\Python311\python.exe`

### Problème: Impossible d'activer l'environnement
**Solution:** 
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Problème: pip install échoue
**Solution:** 
```powershell
python -m pip install --upgrade pip
pip install --upgrade setuptools wheel
```

## 📝 Script Automatique

J'ai créé un script `setup_environment.ps1` qui fait tout automatiquement!

```powershell
.\setup_environment.ps1
```

---

**🎉 Une fois terminé, votre notebook fonctionnera parfaitement!**

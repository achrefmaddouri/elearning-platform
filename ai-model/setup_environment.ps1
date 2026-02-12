# Script d'installation automatique pour E-Learning AI
# Ce script configure l'environnement Python 3.11

Write-Host "🚀 Configuration de l'environnement E-Learning AI..." -ForegroundColor Cyan
Write-Host ""

# Vérifier si Python 3.11 est installé
Write-Host "🔍 Vérification de Python 3.11..." -ForegroundColor Yellow

$python311Path = "C:\Python311\python.exe"

if (Test-Path $python311Path) {
    Write-Host "✅ Python 3.11 trouvé!" -ForegroundColor Green
    $pythonCmd = $python311Path
} else {
    Write-Host "⚠️  Python 3.11 non trouvé dans C:\Python311\" -ForegroundColor Red
    Write-Host "Tentative avec 'python' dans PATH..." -ForegroundColor Yellow
    
    $version = python --version 2>&1
    if ($version -match "3\.11") {
        Write-Host "✅ Python 3.11 trouvé dans PATH!" -ForegroundColor Green
        $pythonCmd = "python"
    } else {
        Write-Host "❌ Python 3.11 n'est pas installé!" -ForegroundColor Red
        Write-Host ""
        Write-Host "📥 Veuillez installer Python 3.11:" -ForegroundColor Yellow
        Write-Host "   1. Aller sur https://www.python.org/downloads/" -ForegroundColor White
        Write-Host "   2. Télécharger Python 3.11.x" -ForegroundColor White
        Write-Host "   3. Installer dans C:\Python311\" -ForegroundColor White
        Write-Host "   4. Relancer ce script" -ForegroundColor White
        Write-Host ""
        Read-Host "Appuyez sur Entrée pour quitter"
        exit 1
    }
}

# Afficher la version
$version = & $pythonCmd --version
Write-Host "Version: $version" -ForegroundColor Cyan
Write-Host ""

# Créer l'environnement virtuel
Write-Host "📦 Création de l'environnement virtuel..." -ForegroundColor Yellow

if (Test-Path "venv_py311") {
    Write-Host "⚠️  L'environnement existe déjà. Suppression..." -ForegroundColor Yellow
    Remove-Item -Recurse -Force "venv_py311"
}

& $pythonCmd -m venv venv_py311

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Environnement virtuel créé!" -ForegroundColor Green
} else {
    Write-Host "❌ Erreur lors de la création de l'environnement" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Activer l'environnement
Write-Host "🔧 Activation de l'environnement..." -ForegroundColor Yellow
$activateScript = ".\venv_py311\Scripts\Activate.ps1"

# Vérifier la politique d'exécution
$policy = Get-ExecutionPolicy -Scope CurrentUser
if ($policy -eq "Restricted") {
    Write-Host "⚠️  Modification de la politique d'exécution..." -ForegroundColor Yellow
    Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser -Force
}

# Activer
& $activateScript

Write-Host "✅ Environnement activé!" -ForegroundColor Green
Write-Host ""

# Mettre à jour pip
Write-Host "⬆️  Mise à jour de pip..." -ForegroundColor Yellow
python -m pip install --upgrade pip --quiet

Write-Host "✅ pip mis à jour!" -ForegroundColor Green
Write-Host ""

# Installer les bibliothèques
Write-Host "📚 Installation des bibliothèques..." -ForegroundColor Yellow
Write-Host "   (Cela peut prendre plusieurs minutes)" -ForegroundColor Cyan
Write-Host ""

pip install -r requirements.txt

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ Toutes les bibliothèques sont installées!" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "⚠️  Certaines bibliothèques ont échoué" -ForegroundColor Yellow
    Write-Host "   Mais le notebook devrait quand même fonctionner" -ForegroundColor Cyan
}

Write-Host ""
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "🎉 INSTALLATION TERMINÉE!" -ForegroundColor Green
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
Write-Host "📝 Pour lancer Jupyter Notebook:" -ForegroundColor Yellow
Write-Host "   1. Activer l'environnement: .\venv_py311\Scripts\Activate.ps1" -ForegroundColor White
Write-Host "   2. Lancer Jupyter: jupyter notebook" -ForegroundColor White
Write-Host ""
Write-Host "🚀 Ou utilisez le script de lancement:" -ForegroundColor Yellow
Write-Host "   .\launch_notebook.ps1" -ForegroundColor White
Write-Host ""

Read-Host "Appuyez sur Entrée pour terminer"

# Script de lancement rapide pour Jupyter Notebook
# Active l'environnement et lance Jupyter

Write-Host "🚀 Lancement du notebook E-Learning AI..." -ForegroundColor Cyan
Write-Host ""

# Vérifier si l'environnement existe
if (-not (Test-Path "venv_py311")) {
    Write-Host "❌ L'environnement virtuel n'existe pas!" -ForegroundColor Red
    Write-Host "   Lancez d'abord: .\setup_environment.ps1" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Appuyez sur Entrée pour quitter"
    exit 1
}

# Activer l'environnement
Write-Host "🔧 Activation de l'environnement..." -ForegroundColor Yellow
& .\venv_py311\Scripts\Activate.ps1

# Lancer Jupyter
Write-Host "📓 Lancement de Jupyter Notebook..." -ForegroundColor Yellow
Write-Host ""
Write-Host "Le navigateur va s'ouvrir automatiquement..." -ForegroundColor Cyan
Write-Host "Pour arrêter le serveur: Ctrl+C" -ForegroundColor Yellow
Write-Host ""

jupyter notebook

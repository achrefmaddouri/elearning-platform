import json

# Read the notebook
with open('E_Learning_AI_Recommendation_System.ipynb', 'r', encoding='utf-8') as f:
    notebook = json.load(f)

# Fix the Google Colab cell (cell index 2, which is the first code cell)
# Replace the Google Colab mount code with local directory setup
new_colab_cell = [
    "# Setup working directory (Local Environment)\n",
    "import os\n",
    "\n",
    "# Use current directory as working directory\n",
    "work_dir = os.getcwd()\n",
    "print(f\"📁 Working directory: {work_dir}\")\n",
    "\n",
    "# Create necessary directories\n",
    "os.makedirs('data', exist_ok=True)\n",
    "os.makedirs('models', exist_ok=True)\n",
    "os.makedirs('outputs', exist_ok=True)\n",
    "\n",
    "print(\"✅ Environment setup complete!\")"
]

# Update the cell
notebook['cells'][2]['source'] = new_colab_cell
notebook['cells'][2]['outputs'] = []  # Clear the error output
notebook['cells'][2]['execution_count'] = None  # Reset execution count

# Update the markdown cell before it to reflect local environment
notebook['cells'][1]['source'] = [
    "## 🔧 Setup Environment\n",
    "\n",
    "First, let's set up our working directory and create necessary folders for data, models, and outputs."
]

print("✅ Notebook fixed successfully!")
print("📝 Changes made:")
print("  - Removed Google Colab dependencies")
print("  - Updated to use local file system")
print("  - Cleared error outputs")

# Save the fixed notebook
with open('E_Learning_AI_Recommendation_System.ipynb', 'w', encoding='utf-8') as f:
    json.dump(notebook, f, indent=1, ensure_ascii=False)

print("\n💾 Fixed notebook saved!")
print("🚀 You can now run the notebook without errors!")

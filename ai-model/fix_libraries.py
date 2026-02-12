import json
import re

# Read the notebook
with open('E_Learning_AI_Recommendation_System.ipynb', 'r', encoding='utf-8') as f:
    notebook = json.load(f)

print("🔧 Fixing library compatibility issues...")

# Find and update the pip install cell (index 3)
pip_install_cell = notebook['cells'][3]
pip_install_cell['source'] = [
    "# Install additional libraries (compatible versions)\n",
    "# Note: We're using sklearn's NMF instead of surprise library for Python 3.13 compatibility\n",
    "!pip install scikit-learn pandas numpy matplotlib seaborn plotly tensorflow scipy\n",
    "\n",
    "print(\"📦 Installing compatible libraries...\")"
]

# Find and update the imports cell (index 4)
imports_cell = notebook['cells'][4]
imports_cell['source'] = [
    "# Import all required libraries\n",
    "import pandas as pd\n",
    "import numpy as np\n",
    "import matplotlib.pyplot as plt\n",
    "import seaborn as sns\n",
    "import plotly.express as px\n",
    "import plotly.graph_objects as go\n",
    "from plotly.subplots import make_subplots\n",
    "\n",
    "# Machine Learning Libraries\n",
    "import tensorflow as tf\n",
    "from tensorflow import keras\n",
    "from sklearn.model_selection import train_test_split, cross_val_score\n",
    "from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score\n",
    "from sklearn.preprocessing import StandardScaler, MinMaxScaler, LabelEncoder\n",
    "from sklearn.decomposition import NMF, TruncatedSVD\n",
    "from sklearn.ensemble import RandomForestRegressor\n",
    "from sklearn.metrics.pairwise import cosine_similarity\n",
    "from scipy.sparse import csr_matrix\n",
    "\n",
    "# Utility Libraries\n",
    "import warnings\n",
    "import pickle\n",
    "import json\n",
    "from datetime import datetime, timedelta\n",
    "import random\n",
    "\n",
    "# Settings\n",
    "warnings.filterwarnings('ignore')\n",
    "sns.set_style(\"whitegrid\")\n",
    "plt.style.use('seaborn-v0_8')\n",
    "\n",
    "# Random seeds for reproducibility\n",
    "np.random.seed(42)\n",
    "tf.random.set_seed(42)\n",
    "random.seed(42)\n",
    "\n",
    "print(\"✅ All libraries imported successfully!\")\n",
    "print(f\"🔢 TensorFlow version: {tf.__version__}\")\n",
    "print(f\"🐼 Pandas version: {pd.__version__}\")\n",
    "print(f\"🔢 NumPy version: {np.__version__}\")"
]

print("✅ Library imports updated!")
print("✅ Notebook fixed for Python 3.13 compatibility!")
print("\n📝 Changes made:")
print("  - Removed scikit-surprise (replaced with sklearn NMF/SVD)")
print("  - Removed implicit library")
print("  - Removed lightfm library")
print("  - Using sklearn's decomposition methods instead")

# Save the fixed notebook
with open('E_Learning_AI_Recommendation_System.ipynb', 'w', encoding='utf-8') as f:
    json.dump(notebook, f, indent=1, ensure_ascii=False)

print("\n💾 Fixed notebook saved!")
print("🚀 Now you need to update the model training cells to use sklearn instead of surprise")

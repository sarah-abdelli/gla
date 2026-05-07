import { useEffect, useState } from "react";
import axios from "axios";

function Villes() {
  const [villes, setVilles] = useState([]);
  const [nouvelleVille, setNouvelleVille] = useState("");
  const [error, setError] = useState("");

  const chargerVilles = () => {
    axios
      .get("http://localhost:8080/api/admin/villes")
      .then((res) => setVilles(res.data))
      .catch(() => setError("Impossible de charger les villes"));
  };

  useEffect(() => {
    chargerVilles();
  }, []);

  const ajouterVille = async (e) => {
    e.preventDefault();
    setError("");

    if (!nouvelleVille.trim()) {
      setError("Veuillez saisir un nom de ville");
      return;
    }

    try {
      await axios.post("http://localhost:8080/api/admin/villes", {
        nom: nouvelleVille.trim(),
      });

      setNouvelleVille("");
      chargerVilles();
    } catch {
      setError("Ville déjà existante ou erreur serveur");
    }
  };

  const supprimerVille = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/api/admin/villes/${id}`);
      chargerVilles();
    } catch {
      setError("Impossible de supprimer cette ville");
    }
  };

  return (
    <section className="page-section city-page">
      <div className="city-panel">
        <div className="city-header">
          <div className="city-icon">🏙️</div>

          <div>
            <h2>Gestion des villes</h2>
            <p>
              Ajoutez une nouvelle ville ou consultez la liste existante
            </p>
          </div>
        </div>

        <form onSubmit={ajouterVille} className="city-form">
          <input
            type="text"
            placeholder="Nouvelle ville"
            value={nouvelleVille}
            onChange={(e) => setNouvelleVille(e.target.value)}
          />

          <button type="submit">＋ Ajouter</button>
        </form>

        {error && <p className="error">{error}</p>}

        <div className="city-list">
          {villes.map((ville) => (
            <div className="city-card" key={ville.id}>
              <div className="city-left">
                <span className="city-mini-icon">🏙️</span>
                <strong>{ville.nom}</strong>
              </div>

              <button
                className="delete-btn"
                onClick={() => supprimerVille(ville.id)}
              >
                🗑️
              </button>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

export default Villes;
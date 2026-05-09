import { useEffect, useState } from "react";
import axios from "axios";

function Trains() {
  const [trains, setTrains] = useState([]);
  const [nouveauTrain, setNouveauTrain] = useState("");
  const [error, setError] = useState("");

  const chargerTrains = () => {
    axios
      .get("http://localhost:8080/api/admin/trains")
      .then((res) => setTrains(res.data))
      .catch(() => setError("Impossible de charger les trains"));
  };

  useEffect(() => {
    chargerTrains();
  }, []);

  const ajouterTrain = async (e) => {
    e.preventDefault();
    setError("");

    if (!nouveauTrain.trim()) {
      setError("Veuillez saisir un numéro de train");
      return;
    }

    try {
      await axios.post("http://localhost:8080/api/admin/trains", {
        numero: nouveauTrain.trim(),
      });
      setNouveauTrain("");
      chargerTrains();
    } catch {
      setError("Train déjà existant ou erreur serveur");
    }
  };

  const supprimerTrain = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/api/admin/trains/${id}`);
      chargerTrains();
    } catch {
      setError("Impossible de supprimer ce train");
    }
  };

  return (
    <section className="page-section city-page">
      <div className="city-panel">
        <div className="city-header">
          <div className="city-icon">🚄</div>
          <div>
            <h2>Gestion des trains</h2>
            <p>Ajoutez un nouveau train ou consultez la liste existante</p>
          </div>
        </div>

        <form onSubmit={ajouterTrain} className="city-form">
          <input
            type="text"
            placeholder="Ex : TGV999"
            value={nouveauTrain}
            onChange={(e) => setNouveauTrain(e.target.value)}
          />
          <button type="submit">＋ Ajouter</button>
        </form>

        {error && <p className="error">{error}</p>}

        <div className="city-list">
          {trains.map((train) => (
            <div className="city-card" key={train.id}>
              <div className="city-left">
                <span className="city-mini-icon">🚄</span>
                <strong>{train.numero}</strong>
              </div>
              <button
                className="delete-btn"
                onClick={() => supprimerTrain(train.id)}
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

export default Trains;
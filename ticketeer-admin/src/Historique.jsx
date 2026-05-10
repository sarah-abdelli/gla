import { useEffect, useState } from "react";
import axios from "axios";

function Historique() {
  const [validations, setValidations] = useState([]);
  const [recherche, setRecherche] = useState("");
  const [resultatsRecherche, setResultatsRecherche] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/admin/validations")
      .then((res) => setValidations(res.data))
      .catch(() => setError("Impossible de charger les validations"));
  }, []);

  const rechercherParUUID = async (e) => {
    e.preventDefault();
    if (!recherche.trim()) {
      setResultatsRecherche(null);
      return;
    }
    setLoading(true);
    setError("");
    try {
      const res = await axios.get(
        `http://localhost:8080/api/admin/tracabilite/${recherche.trim()}`
      );
      setResultatsRecherche(res.data);
    } catch {
      setError("Billet introuvable ou erreur serveur");
      setResultatsRecherche(null);
    } finally {
      setLoading(false);
    }
  };

  const reinitialiser = () => {
    setRecherche("");
    setResultatsRecherche(null);
    setError("");
  };

  const data = resultatsRecherche !== null ? resultatsRecherche : validations;

  const formatDate = (dateHeure) => {
    if (!dateHeure) return "—";
    const d = new Date(dateHeure);
    return d.toLocaleString("fr-FR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  return (
    <section className="page-section city-page">
      <div className="city-panel">
        <div className="city-header">
          <div className="city-icon">📋</div>
          <div>
            <h2>Historique des validations</h2>
            <p>Consultez toutes les validations ou recherchez par UUID de billet</p>
          </div>
        </div>

        {/* Recherche par UUID */}
        <form onSubmit={rechercherParUUID} className="city-form">
          <input
            type="text"
            placeholder="UUID du billet..."
            value={recherche}
            onChange={(e) => setRecherche(e.target.value)}
          />
          <button type="submit">🔍 Rechercher</button>
        </form>

        {resultatsRecherche !== null && (
          <button
            onClick={reinitialiser}
            style={{
              background: "transparent",
              border: "1px solid var(--bg-card-border)",
              color: "var(--text-secondary)",
              borderRadius: "10px",
              padding: "8px 16px",
              fontFamily: "Outfit, sans-serif",
              cursor: "pointer",
              marginBottom: "16px",
              fontSize: "0.85rem",
            }}
          >
            ← Voir toutes les validations
          </button>
        )}

        {error && <p className="error">{error}</p>}

        {loading && (
          <p style={{ color: "var(--text-secondary)", textAlign: "center", padding: "20px" }}>
            Chargement...
          </p>
        )}

        <div className="city-list">
          {data.length === 0 && !loading ? (
            <div className="city-card" style={{ justifyContent: "center" }}>
              <span style={{ color: "var(--text-muted)" }}>
                Aucune validation trouvée
              </span>
            </div>
          ) : (
            data.map((v) => (
              <div className="city-card" key={v.id} style={{ flexDirection: "column", alignItems: "stretch", gap: "8px" }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                    <span className="city-mini-icon">🎫</span>
                    <strong style={{ color: "var(--text-primary)" }}>
                      Validation #{v.id}
                    </strong>
                  </div>
                  <span
                    className={`badge ${v.resultat === "ACCEPTEE" ? "badge-valid" : "badge-invalid"}`}
                  >
                    {v.resultat === "ACCEPTEE" ? "✅ Acceptée" : "❌ Refusée"}
                  </span>
                </div>

                <div style={{ display: "flex", flexDirection: "column", gap: "4px", paddingLeft: "52px" }}>
                  <span style={{ color: "var(--text-secondary)", fontSize: "0.83rem" }}>
                    🕐 {formatDate(v.dateHeure)}
                  </span>

                  {v.billet && (
                    <span style={{ color: "var(--text-muted)", fontSize: "0.78rem", fontFamily: "JetBrains Mono, monospace" }}>
                      UUID : {v.billet.uuid}
                    </span>
                  )}

                  {v.agent && (
                    <span style={{ color: "var(--text-secondary)", fontSize: "0.83rem" }}>
                      👮 Agent : {v.agent.nom}
                    </span>
                  )}

                  {v.motifRefus && (
                    <span style={{ color: "var(--error)", fontSize: "0.83rem" }}>
                      ⚠️ Motif : {v.motifRefus}
                    </span>
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </section>
  );
}

export default Historique;
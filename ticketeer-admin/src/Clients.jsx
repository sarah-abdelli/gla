import { useEffect, useState } from "react";
import axios from "axios";

function Clients() {
  const [clients, setClients] = useState([]);
  const [error, setError] = useState("");
  const [editId, setEditId] = useState(null);
  const [editNom, setEditNom] = useState("");
  const [editEmail, setEditEmail] = useState("");

  const chargerClients = () => {
    axios
      .get("http://localhost:8080/api/admin/clients")
      .then((res) => setClients(res.data))
      .catch(() => setError("Impossible de charger les clients"));
  };

  useEffect(() => {
    chargerClients();
  }, []);

  const supprimerClient = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/api/admin/clients/${id}`);
      chargerClients();
    } catch {
      setError("Impossible de supprimer ce client");
    }
  };

  const ouvrirEdit = (client) => {
    setEditId(client.id);
    setEditNom(client.nom);
    setEditEmail(client.email);
    setError("");
  };

  const annulerEdit = () => {
    setEditId(null);
    setEditNom("");
    setEditEmail("");
    setError("");
  };

  const sauvegarderEdit = async (id) => {
    try {
      await axios.put(`http://localhost:8080/api/admin/clients/${id}`, {
        nom: editNom,
        email: editEmail,
      });
      annulerEdit();
      chargerClients();
    } catch {
      setError("Impossible de modifier ce client");
    }
  };

  return (
    <section className="page-section city-page">
      <div className="city-panel">
        <div className="city-header">
          <div className="city-icon">👤</div>
          <div>
            <h2>Gestion des clients</h2>
            <p>Consultez, modifiez ou supprimez les comptes voyageurs</p>
          </div>
        </div>

        {error && <p className="error">{error}</p>}

        <div className="city-list">
          {clients.map((client) =>
            editId === client.id ? (
              // Mode édition
              <div className="city-card" key={client.id} style={{ flexDirection: "column", alignItems: "stretch", gap: "12px" }}>
                <input
                  style={{
                    background: "var(--input-bg)",
                    border: "1px solid var(--input-border)",
                    color: "var(--text-primary)",
                    borderRadius: "10px",
                    padding: "10px 14px",
                    fontFamily: "Outfit, sans-serif",
                    fontSize: "0.95rem",
                    outline: "none",
                  }}
                  value={editNom}
                  onChange={(e) => setEditNom(e.target.value)}
                  placeholder="Nom"
                />
                <input
                  style={{
                    background: "var(--input-bg)",
                    border: "1px solid var(--input-border)",
                    color: "var(--text-primary)",
                    borderRadius: "10px",
                    padding: "10px 14px",
                    fontFamily: "Outfit, sans-serif",
                    fontSize: "0.95rem",
                    outline: "none",
                  }}
                  value={editEmail}
                  onChange={(e) => setEditEmail(e.target.value)}
                  placeholder="Email"
                />
                <div style={{ display: "flex", gap: "10px" }}>
                  <button
                    onClick={() => sauvegarderEdit(client.id)}
                    style={{
                      flex: 1,
                      padding: "10px",
                      borderRadius: "10px",
                      border: "none",
                      background: "var(--accent-gradient)",
                      color: "white",
                      fontFamily: "Outfit, sans-serif",
                      fontWeight: "600",
                      cursor: "pointer",
                    }}
                  >
                    ✅ Sauvegarder
                  </button>
                  <button
                    onClick={annulerEdit}
                    style={{
                      flex: 1,
                      padding: "10px",
                      borderRadius: "10px",
                      border: "1px solid var(--bg-card-border)",
                      background: "transparent",
                      color: "var(--text-secondary)",
                      fontFamily: "Outfit, sans-serif",
                      fontWeight: "600",
                      cursor: "pointer",
                    }}
                  >
                    Annuler
                  </button>
                </div>
              </div>
            ) : (
              // Mode affichage
              <div className="city-card" key={client.id}>
                <div className="city-left" style={{ flexDirection: "column", alignItems: "flex-start", gap: "4px" }}>
                  <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
                    <span className="city-mini-icon">👤</span>
                    <strong>{client.nom}</strong>
                  </div>
                  <span style={{ color: "var(--text-secondary)", fontSize: "0.85rem", marginLeft: "54px" }}>
                    📧 {client.email}
                  </span>
                  {client.billets !== undefined && (
                    <span style={{ color: "var(--text-muted)", fontSize: "0.8rem", marginLeft: "54px" }}>
                      🎫 {client.billets} billet(s) valide(s)
                    </span>
                  )}
                </div>
                <div style={{ display: "flex", gap: "8px" }}>
                  <button
                    onClick={() => ouvrirEdit(client)}
                    style={{
                      width: "46px",
                      height: "46px",
                      borderRadius: "14px",
                      border: "1px solid rgba(79,124,255,0.35)",
                      background: "rgba(79,124,255,0.1)",
                      color: "#4f7cff",
                      cursor: "pointer",
                      fontSize: "18px",
                      transition: "0.2s ease",
                    }}
                  >
                    ✏️
                  </button>
                  <button
                    className="delete-btn"
                    onClick={() => supprimerClient(client.id)}
                  >
                    🗑️
                  </button>
                </div>
              </div>
            )
          )}
        </div>
      </div>
    </section>
  );
}

export default Clients;
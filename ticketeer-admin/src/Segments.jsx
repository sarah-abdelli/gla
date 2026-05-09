import { useEffect, useState } from "react";
import axios from "axios";

function Segments() {
  const [segments, setSegments] = useState([]);
  const [villes, setVilles] = useState([]);
  const [trains, setTrains] = useState([]);
  const [error, setError] = useState("");

  const [form, setForm] = useState({
    villeDepart: "",
    villeArrivee: "",
    numeroTrain: "",
    date: "",
    heureDepart: "",
    heureArrivee: "",
  });

  const charger = async () => {
    try {
      const [seg, vil, tr] = await Promise.all([
        axios.get("http://localhost:8080/api/admin/segments"),
        axios.get("http://localhost:8080/api/admin/villes"),
        axios.get("http://localhost:8080/api/admin/trains"),
      ]);
      setSegments(seg.data);
      setVilles(vil.data);
      setTrains(tr.data);
    } catch {
      setError("Impossible de charger les données");
    }
  };

  useEffect(() => {
    charger();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const ajouterSegment = async (e) => {
    e.preventDefault();
    setError("");

    const { villeDepart, villeArrivee, numeroTrain, date, heureDepart, heureArrivee } = form;

    if (!villeDepart || !villeArrivee || !numeroTrain || !date || !heureDepart || !heureArrivee) {
      setError("Tous les champs sont obligatoires");
      return;
    }

    if (villeDepart === villeArrivee) {
      setError("La ville de départ et d'arrivée doivent être différentes");
      return;
    }

    try {
      await axios.post("http://localhost:8080/api/admin/segments", form);
      setForm({
        villeDepart: "",
        villeArrivee: "",
        numeroTrain: "",
        date: "",
        heureDepart: "",
        heureArrivee: "",
      });
      charger();
    } catch {
      setError("Erreur lors de l'ajout du segment");
    }
  };

  const supprimerSegment = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/api/admin/segments/${id}`);
      charger();
    } catch {
      setError("Impossible de supprimer ce segment");
    }
  };

  const inputStyle = {
    background: "var(--input-bg)",
    border: "1px solid var(--input-border)",
    color: "var(--text-primary)",
    borderRadius: "10px",
    padding: "12px 14px",
    fontFamily: "Outfit, sans-serif",
    fontSize: "0.95rem",
    outline: "none",
    width: "100%",
  };

  const selectStyle = {
    ...inputStyle,
    cursor: "pointer",
  };

  return (
    <section className="page-section city-page">
      <div className="city-panel">
        <div className="city-header">
          <div className="city-icon">🛤️</div>
          <div>
            <h2>Gestion des segments</h2>
            <p>Ajoutez ou supprimez des segments de trajet</p>
          </div>
        </div>

        {/* Formulaire d'ajout */}
        <form onSubmit={ajouterSegment}>
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "1fr 1fr 1fr",
              gap: "12px",
              marginBottom: "12px",
            }}
          >
            {/* Ville départ */}
            <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
              <label style={{ fontSize: "0.75rem", color: "var(--text-secondary)", textTransform: "uppercase", letterSpacing: "0.08em" }}>
                Ville départ
              </label>
              <select name="villeDepart" value={form.villeDepart} onChange={handleChange} style={selectStyle}>
                <option value="">Choisir...</option>
                {villes.map((v) => (
                  <option key={v.id} value={v.nom}>{v.nom}</option>
                ))}
              </select>
            </div>

            {/* Ville arrivée */}
            <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
              <label style={{ fontSize: "0.75rem", color: "var(--text-secondary)", textTransform: "uppercase", letterSpacing: "0.08em" }}>
                Ville arrivée
              </label>
              <select name="villeArrivee" value={form.villeArrivee} onChange={handleChange} style={selectStyle}>
                <option value="">Choisir...</option>
                {villes.map((v) => (
                  <option key={v.id} value={v.nom}>{v.nom}</option>
                ))}
              </select>
            </div>

            {/* Train */}
            <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
              <label style={{ fontSize: "0.75rem", color: "var(--text-secondary)", textTransform: "uppercase", letterSpacing: "0.08em" }}>
                Train
              </label>
              <select name="numeroTrain" value={form.numeroTrain} onChange={handleChange} style={selectStyle}>
                <option value="">Choisir...</option>
                {trains.map((t) => (
                  <option key={t.id} value={t.numero}>{t.numero}</option>
                ))}
              </select>
            </div>

            {/* Date */}
            <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
              <label style={{ fontSize: "0.75rem", color: "var(--text-secondary)", textTransform: "uppercase", letterSpacing: "0.08em" }}>
                Date
              </label>
              <input type="date" name="date" value={form.date} onChange={handleChange} style={inputStyle} />
            </div>

            {/* Heure départ */}
            <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
              <label style={{ fontSize: "0.75rem", color: "var(--text-secondary)", textTransform: "uppercase", letterSpacing: "0.08em" }}>
                Heure départ
              </label>
              <input type="time" name="heureDepart" value={form.heureDepart} onChange={handleChange} style={inputStyle} />
            </div>

            {/* Heure arrivée */}
            <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
              <label style={{ fontSize: "0.75rem", color: "var(--text-secondary)", textTransform: "uppercase", letterSpacing: "0.08em" }}>
                Heure arrivée
              </label>
              <input type="time" name="heureArrivee" value={form.heureArrivee} onChange={handleChange} style={inputStyle} />
            </div>
          </div>

          <button
            type="submit"
            style={{
              width: "100%",
              padding: "14px",
              border: "none",
              borderRadius: "14px",
              background: "var(--accent-gradient)",
              color: "white",
              fontFamily: "Outfit, sans-serif",
              fontSize: "1rem",
              fontWeight: "700",
              cursor: "pointer",
              marginBottom: "20px",
              boxShadow: "0 8px 24px rgba(79, 124, 255, 0.35)",
            }}
          >
            ＋ Ajouter le segment
          </button>
        </form>

        {error && <p className="error">{error}</p>}

        {/* Liste des segments */}
        <div className="city-list">
          {segments.length === 0 ? (
            <div className="city-card" style={{ justifyContent: "center" }}>
              <span style={{ color: "var(--text-muted)" }}>Aucun segment disponible</span>
            </div>
          ) : (
            segments.map((seg) => (
              <div className="city-card" key={seg.id}>
                <div className="city-left" style={{ flexDirection: "column", alignItems: "flex-start", gap: "4px" }}>
                  <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
                    <span className="city-mini-icon">🛤️</span>
                    <strong style={{ color: "var(--text-primary)" }}>
                      {seg.villeDepart?.nom} → {seg.villeArrivee?.nom}
                    </strong>
                  </div>
                  <div style={{ paddingLeft: "54px", display: "flex", flexDirection: "column", gap: "2px" }}>
                    <span style={{ color: "var(--text-secondary)", fontSize: "0.83rem" }}>
                      🚄 {seg.train?.numero}
                    </span>
                    <span style={{ color: "var(--text-muted)", fontSize: "0.8rem" }}>
                      📅 {seg.dateDepart} · {seg.heureDepart} → {seg.heureArrivee}
                    </span>
                  </div>
                </div>
                <button
                  className="delete-btn"
                  onClick={() => supprimerSegment(seg.id)}
                >
                  🗑️
                </button>
              </div>
            ))
          )}
        </div>
      </div>
    </section>
  );
}

export default Segments;

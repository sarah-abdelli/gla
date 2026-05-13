import { useEffect, useState } from "react";
import axios from "axios";

const API = "http://localhost:8080/api/admin";

const inputStyle = {
  background: "var(--input-bg)",
  border: "1px solid var(--input-border)",
  color: "var(--text-primary)",
  borderRadius: "10px",
  padding: "10px 14px",
  fontFamily: "Outfit, sans-serif",
  fontSize: "0.9rem",
  outline: "none",
  width: "100%",
};

const labelStyle = {
  fontSize: "0.72rem",
  color: "var(--text-secondary)",
  textTransform: "uppercase",
  letterSpacing: "0.08em",
  marginBottom: "4px",
  display: "block",
};

const emptySegment = () => ({
  segmentId: "",
  filtreDate: "",
  segmentsParDate: [],
  loadingSegments: false,
});

const toMin = (hhmm) => {
  if (!hhmm) return null;
  const [h, m] = hhmm.split(":").map(Number);
  return h * 60 + m;
};

const getInfo = (s) => {
  if (s.segmentId) {
    const f = s.segmentsParDate.find((x) => x.id === parseInt(s.segmentId));
    return f ? { villeArrivee: f.villeArrivee?.nom, heureArrivee: f.heureArrivee } : null;
  }
  return null;
};

function Itineraires() {
  const [itineraires, setItineraires] = useState([]);
  const [segmentsList, setSegmentsList] = useState([emptySegment()]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const charger = async () => {
    try {
      const res = await axios.get(`${API}/itineraires`);
      setItineraires(res.data);
    } catch { setError("Impossible de charger les itinéraires"); }
  };

  useEffect(() => { charger(); }, []);

  const update = (index, field, value) => {
    setSegmentsList((prev) => {
      const updated = [...prev];
      updated[index] = { ...updated[index], [field]: value };
      return updated;
    });
  };

  const handleFiltreDateSegment = async (index, date) => {
    update(index, "filtreDate", date);
    update(index, "segmentId", "");
    update(index, "segmentsParDate", []);
    if (!date) return;
    update(index, "loadingSegments", true);
    try {
      const res = await axios.get(`${API}/segments?date=${date}`);
      setSegmentsList((prev) => {
        const updated = [...prev];
        updated[index] = { ...updated[index], segmentsParDate: res.data, loadingSegments: false };
        return updated;
      });
    } catch {
      setSegmentsList((prev) => {
        const updated = [...prev];
        updated[index] = { ...updated[index], loadingSegments: false };
        return updated;
      });
    }
  };

  const ajouterSegment = () => {
    setSegmentsList((prev) => [...prev, emptySegment()]);
  };

  const retirerSegment = (index) => {
    if (segmentsList.length === 1) return;
    setSegmentsList((prev) => prev.filter((_, i) => i !== index));
  };

  const segmentsFiltres = (index, segmentsParDate) => {
    if (index === 0) return segmentsParDate;
    const prevInfo = getInfo(segmentsList[index - 1]);
    if (!prevInfo) return segmentsParDate;
    const prevArrMin = toMin(prevInfo.heureArrivee);
    return segmentsParDate.filter((s) => {
      if (s.villeDepart?.nom !== prevInfo.villeArrivee) return false;
      const depMin = toMin(s.heureDepart);
      if (prevArrMin === null || depMin === null) return true;
      return depMin > prevArrMin;
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(""); setSuccess("");
    const payload = {
      segments: segmentsList.map((s) => ({ segmentId: parseInt(s.segmentId) })),
    };
    try {
      await axios.post(`${API}/itineraires`, payload);
      setSuccess("Itinéraire créé !");
      setSegmentsList([emptySegment()]);
      charger();
    } catch (err) {
      setError(err.response?.data?.message || "Erreur lors de la création");
    }
  };

  const supprimerItineraire = async (id) => {
    try {
      await axios.delete(`${API}/itineraires/${id}`);
      charger();
    } catch { setError("Impossible de supprimer"); }
  };

  return (
    <section className="page-section city-page">
      <div className="city-panel">
        <div className="city-header">
          <div className="city-icon">🗺️</div>
          <div>
            <h2>Gestion des itinéraires</h2>
            <p>Créez des itinéraires directs ou avec correspondances</p>
          </div>
        </div>

        <form onSubmit={handleSubmit}>
          {segmentsList.map((seg, index) => {
            const filtres = segmentsFiltres(index, seg.segmentsParDate);

            return (
              <div key={index} style={{ background: "rgba(15,23,42,0.6)", border: "1px solid var(--bg-card-border)", borderRadius: "16px", padding: "20px", marginBottom: "12px" }}>

                {/* Header */}
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "16px" }}>
                  <span style={{ color: "var(--accent-blue)", fontWeight: "600", fontSize: "0.9rem" }}>
                    Segment {index + 1}
                  </span>
                  {segmentsList.length > 1 && (
                    <button type="button" onClick={() => retirerSegment(index)}
                      style={{ width: "30px", height: "30px", borderRadius: "8px", border: "1px solid rgba(239,68,68,0.35)", background: "rgba(239,68,68,0.1)", color: "#ef4444", cursor: "pointer", fontSize: "14px" }}>
                      ✕
                    </button>
                  )}
                </div>

                <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
                  {/* Filtre date */}
                  <div>
                    <label style={labelStyle}>📅 Filtrer par date</label>
                    <div style={{ display: "flex", gap: "8px", alignItems: "center" }}>
                      <input type="date" value={seg.filtreDate}
                        onChange={(e) => handleFiltreDateSegment(index, e.target.value)}
                        style={inputStyle} />
                      {seg.filtreDate && (
                        <button type="button" onClick={() => handleFiltreDateSegment(index, "")}
                          style={{ background: "none", border: "1px solid var(--input-border)", borderRadius: "8px", padding: "8px 12px", cursor: "pointer", color: "var(--text-secondary)", fontSize: "0.8rem", whiteSpace: "nowrap" }}>
                          ✕ Effacer
                        </button>
                      )}
                    </div>
                  </div>

                  {/* Sélection segment */}
                  <div>
                    <label style={labelStyle}>
                      Segment existant{" "}
                      {seg.filtreDate && !seg.loadingSegments && (
                        <span style={{ color: "var(--accent-blue)" }}>({filtres.length} compatibles)</span>
                      )}
                    </label>
                    <select value={seg.segmentId}
                      onChange={(e) => update(index, "segmentId", e.target.value)}
                      style={{ ...inputStyle, cursor: "pointer" }}
                      required
                      disabled={!seg.filtreDate || seg.loadingSegments}>
                      <option value="">
                        {!seg.filtreDate
                          ? "Choisir une date d'abord"
                          : seg.loadingSegments
                          ? "Chargement..."
                          : filtres.length === 0
                          ? "Aucun segment cette date"
                          : "Sélectionner..."}
                      </option>
                      {filtres.map((s) => (
                        <option key={s.id} value={s.id}>
                          {s.villeDepart?.nom} → {s.villeArrivee?.nom} | {s.train?.numero} | {s.heureDepart}–{s.heureArrivee}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
              </div>
            );
          })}

          <button type="button" onClick={ajouterSegment}
            style={{ width: "100%", padding: "12px", border: "1px dashed rgba(79,124,255,0.4)", borderRadius: "14px", background: "rgba(79,124,255,0.05)", color: "var(--accent-blue)", fontFamily: "Outfit, sans-serif", fontSize: "0.95rem", fontWeight: "600", cursor: "pointer", marginBottom: "16px" }}>
            ＋ Ajouter un segment
          </button>

          {error && <p className="error">{error}</p>}
          {success && <p style={{ background: "rgba(34,197,94,0.1)", border: "1px solid rgba(34,197,94,0.25)", color: "var(--success)", padding: "10px 14px", borderRadius: "10px", fontSize: "0.85rem", marginBottom: "12px" }}>{success}</p>}

          <button type="submit"
            style={{ width: "100%", padding: "14px", border: "none", borderRadius: "14px", background: "var(--accent-gradient)", color: "white", fontFamily: "Outfit, sans-serif", fontSize: "1rem", fontWeight: "700", cursor: "pointer", boxShadow: "0 8px 24px rgba(79,124,255,0.35)" }}>
            ✅ Créer l'itinéraire
          </button>
        </form>

        {/* Liste */}
        <div style={{ marginTop: "32px" }}>
          <h3 style={{ color: "var(--text-primary)", marginBottom: "16px", fontSize: "1rem" }}>
            Itinéraires existants ({itineraires.length})
          </h3>
          <div className="city-list">
            {itineraires.length === 0 ? (
              <div className="city-card" style={{ justifyContent: "center" }}>
                <span style={{ color: "var(--text-muted)" }}>Aucun itinéraire</span>
              </div>
            ) : (
              itineraires.map((it) => (
                <div className="city-card" key={it.id}>
                  <div className="city-left" style={{ flexDirection: "column", alignItems: "flex-start", gap: "4px" }}>
                    <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
                      <span className="city-mini-icon">🗺️</span>
                      <strong style={{ color: "var(--text-primary)" }}>{it.villeDepart?.nom} → {it.villeArrivee?.nom}</strong>
                      <span className="badge" style={{ background: it.segments?.length === 1 ? "rgba(79,124,255,0.1)" : "rgba(124,92,252,0.1)", color: it.segments?.length === 1 ? "var(--accent-blue)" : "var(--accent-purple)", border: `1px solid ${it.segments?.length === 1 ? "rgba(79,124,255,0.3)" : "rgba(124,92,252,0.3)"}` }}>
                        {it.segments?.length === 1 ? "Direct" : `${it.segments?.length} segments`}
                      </span>
                    </div>
                    <div style={{ paddingLeft: "54px", display: "flex", flexDirection: "column", gap: "2px" }}>
                      {it.segments?.map((s, i) => (
                        <span key={i} style={{ color: "var(--text-muted)", fontSize: "0.78rem" }}>
                          {i + 1}. {s.villeDepart?.nom} → {s.villeArrivee?.nom} | 🚄 {s.train?.numero} | {s.heureDepart} → {s.heureArrivee}
                        </span>
                      ))}
                    </div>
                  </div>
                  <button className="delete-btn" onClick={() => supprimerItineraire(it.id)}>🗑️</button>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </section>
  );
}

export default Itineraires;
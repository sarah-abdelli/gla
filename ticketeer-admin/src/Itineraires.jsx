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
  mode: "existant",
  segmentId: "",
  villeDepart: "",
  villeArrivee: "",
  numeroTrain: "",
  date: "",
  heureDepart: "",
  heureArrivee: "",
  trainsDisponibles: [],
  segmentsDisponibles: [],
  loadingTrains: false,
  loadingSegments: false,
});

const toMin = (hhmm) => {
  if (!hhmm) return null;
  const [h, m] = hhmm.split(":").map(Number);
  return h * 60 + m;
};

const getInfo = (s) => {
  if (s.mode === "existant" && s.segmentId) {
    const f = s.segmentsDisponibles?.find((x) => x.id === parseInt(s.segmentId));
    return f ? { villeArrivee: f.villeArrivee?.nom, heureArrivee: f.heureArrivee } : null;
  }
  if (s.mode === "nouveau" && s.villeArrivee && s.heureArrivee)
    return { villeArrivee: s.villeArrivee, heureArrivee: s.heureArrivee };
  return null;
};

function Itineraires() {
  const [itineraires, setItineraires] = useState([]);
  const [villes, setVilles] = useState([]);
  const [segmentsList, setSegmentsList] = useState([emptySegment()]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loadingItins, setLoadingItins] = useState(false);

  // Filtre liste
  const [dateFiltre, setDateFiltre] = useState("");

  const chargerVilles = async () => {
    try {
      const res = await axios.get(`${API}/villes`);
      setVilles(res.data);
    } catch { setError("Impossible de charger les villes"); }
  };

  const chargerItinerairesParDate = async (date) => {
    if (!date) { setItineraires([]); return; }
    setLoadingItins(true);
    try {
      const res = await axios.get(`${API}/itineraires?date=${date}`);
      setItineraires(res.data);
    } catch { setError("Impossible de charger les itinéraires"); }
    finally { setLoadingItins(false); }
  };

  useEffect(() => { chargerVilles(); }, []);

  const update = (index, field, value) => {
    setSegmentsList((prev) => {
      const updated = [...prev];
      updated[index] = { ...updated[index], [field]: value };
      return updated;
    });
  };

  // Quand la date change sur un segment  charger les segments et trains dispo
  const handleDateChange = async (index, date) => {
    update(index, "date", date);
    update(index, "numeroTrain", "");
    update(index, "segmentId", "");
    if (!date) {
      update(index, "trainsDisponibles", []);
      update(index, "segmentsDisponibles", []);
      return;
    }

    // Charger trains dispo
    update(index, "loadingTrains", true);
    try {
      const res = await axios.get(`${API}/trains/disponibles?date=${date}`);
      setSegmentsList((prev) => {
        const updated = [...prev];
        updated[index] = { ...updated[index], trainsDisponibles: res.data, loadingTrains: false };
        return updated;
      });
    } catch { update(index, "loadingTrains", false); }

    // Charger segments dispo pour ce jour
    update(index, "loadingSegments", true);
    try {
      const res = await axios.get(`${API}/segments?date=${date}`);
      setSegmentsList((prev) => {
        const updated = [...prev];
        // Si segment > 0, filtrer par ville départ = ville arrivée précédente
        let segs = res.data;
        if (index > 0) {
          const prevInfo = getInfo(updated[index - 1]);
          if (prevInfo?.villeArrivee) {
            const prevArrMin = toMin(prevInfo.heureArrivee);
            segs = segs.filter((s) => {
              if (s.villeDepart?.nom !== prevInfo.villeArrivee) return false;
              const depMin = toMin(s.heureDepart);
              if (prevArrMin === null || depMin === null) return true;
              return depMin > prevArrMin + 29;
            });
          }
        }
        updated[index] = { ...updated[index], segmentsDisponibles: segs, loadingSegments: false };
        return updated;
      });
    } catch { update(index, "loadingSegments", false); }
  };

  const ajouterSegment = () => {
    const prevInfo = getInfo(segmentsList[segmentsList.length - 1]);
    const nouveau = emptySegment();
    if (prevInfo?.villeArrivee) nouveau.villeDepart = prevInfo.villeArrivee;
    setSegmentsList((prev) => [...prev, nouveau]);
  };

  const retirerSegment = (index) => {
    if (segmentsList.length === 1) return;
    setSegmentsList((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(""); setSuccess("");
    const payload = {
      segments: segmentsList.map((s) =>
        s.mode === "existant"
          ? { segmentId: parseInt(s.segmentId) }
          : { villeDepart: s.villeDepart, villeArrivee: s.villeArrivee, numeroTrain: s.numeroTrain, date: s.date, heureDepart: s.heureDepart, heureArrivee: s.heureArrivee }
      ),
    };
    try {
      await axios.post(`${API}/itineraires`, payload);
      setSuccess("Itinéraire créé !");
      setSegmentsList([emptySegment()]);
      if (dateFiltre) chargerItinerairesParDate(dateFiltre);
    } catch (err) {
      setError(err.response?.data?.message || "Erreur lors de la création");
    }
  };

  const supprimerItineraire = async (id) => {
    try {
      await axios.delete(`${API}/itineraires/${id}`);
      if (dateFiltre) chargerItinerairesParDate(dateFiltre);
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

        {/* Formulaire */}
        <form onSubmit={handleSubmit}>
          {segmentsList.map((seg, index) => {
            const isLocked = index > 0 && seg.villeDepart;

            return (
              <div key={index} style={{ background: "rgba(15,23,42,0.6)", border: "1px solid var(--bg-card-border)", borderRadius: "16px", padding: "20px", marginBottom: "12px" }}>

                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "16px" }}>
                  <span style={{ color: "var(--accent-blue)", fontWeight: "600", fontSize: "0.9rem" }}>
                    Segment {index + 1}
                    {isLocked && <span style={{ color: "var(--text-muted)", fontWeight: "400", fontSize: "0.78rem", marginLeft: "8px" }}>· départ depuis {seg.villeDepart}</span>}
                  </span>
                  <div style={{ display: "flex", gap: "6px", alignItems: "center" }}>
                    {["existant", "nouveau"].map((m) => (
                      <button key={m} type="button" onClick={() => update(index, "mode", m)}
                        style={{ padding: "5px 12px", borderRadius: "8px", border: "1px solid", borderColor: seg.mode === m ? "var(--accent-blue)" : "var(--bg-card-border)", background: seg.mode === m ? "rgba(79,124,255,0.15)" : "transparent", color: seg.mode === m ? "var(--accent-blue)" : "var(--text-muted)", fontFamily: "Outfit, sans-serif", fontSize: "0.78rem", cursor: "pointer" }}>
                        {m === "existant" ? "📋 Existant" : "✏️ Nouveau"}
                      </button>
                    ))}
                    {segmentsList.length > 1 && (
                      <button type="button" onClick={() => retirerSegment(index)}
                        style={{ width: "30px", height: "30px", borderRadius: "8px", border: "1px solid rgba(239,68,68,0.35)", background: "rgba(239,68,68,0.1)", color: "#ef4444", cursor: "pointer", fontSize: "14px" }}>
                        ✕
                      </button>
                    )}
                  </div>
                </div>

                {/* Mode existant  */}
                {seg.mode === "existant" && (
                  <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "12px" }}>
                    <div>
                      <label style={labelStyle}>Date du segment</label>
                      <input type="date" value={seg.date} onChange={(e) => handleDateChange(index, e.target.value)} style={inputStyle} required />
                    </div>
                    <div>
                      <label style={labelStyle}>
                        Segment existant{" "}
                        {seg.date && <span style={{ color: "var(--accent-blue)" }}>({seg.loadingSegments ? "..." : `${seg.segmentsDisponibles.length} compatibles`})</span>}
                      </label>
                      <select value={seg.segmentId} onChange={(e) => update(index, "segmentId", e.target.value)}
                        style={{ ...inputStyle, cursor: "pointer" }} disabled={!seg.date || seg.loadingSegments} required>
                        <option value="">{!seg.date ? "Choisir une date d'abord" : seg.loadingSegments ? "Chargement..." : seg.segmentsDisponibles.length === 0 ? "Aucun segment disponible" : "Sélectionner..."}</option>
                        {seg.segmentsDisponibles.map((s) => (
                          <option key={s.id} value={s.id}>
                            {s.villeDepart?.nom} → {s.villeArrivee?.nom} | {s.train?.numero} | {s.heureDepart}–{s.heureArrivee}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>
                )}

                {/* Mode nouveau */}
                {seg.mode === "nouveau" && (
                  <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "12px" }}>
                    <div>
                      <label style={labelStyle}>Ville départ</label>
                      {isLocked ? (
                        <div style={{ ...inputStyle, color: "var(--text-muted)", cursor: "not-allowed", opacity: 0.7 }}>{seg.villeDepart}</div>
                      ) : (
                        <select value={seg.villeDepart} onChange={(e) => update(index, "villeDepart", e.target.value)} style={{ ...inputStyle, cursor: "pointer" }} required>
                          <option value="">Choisir...</option>
                          {villes.map((v) => <option key={v.id} value={v.nom}>{v.nom}</option>)}
                        </select>
                      )}
                    </div>
                    <div>
                      <label style={labelStyle}>Ville arrivée</label>
                      <select value={seg.villeArrivee} onChange={(e) => update(index, "villeArrivee", e.target.value)} style={{ ...inputStyle, cursor: "pointer" }} required>
                        <option value="">Choisir...</option>
                        {villes.filter((v) => v.nom !== seg.villeDepart).map((v) => <option key={v.id} value={v.nom}>{v.nom}</option>)}
                      </select>
                    </div>
                    <div>
                      <label style={labelStyle}>Date</label>
                      <input type="date" value={seg.date} onChange={(e) => handleDateChange(index, e.target.value)} style={inputStyle} required />
                    </div>
                    <div>
                      <label style={labelStyle}>Train {seg.date && <span style={{ color: "var(--accent-blue)" }}>({seg.loadingTrains ? "..." : `${seg.trainsDisponibles.length} dispo`})</span>}</label>
                      <select value={seg.numeroTrain} onChange={(e) => update(index, "numeroTrain", e.target.value)} style={{ ...inputStyle, cursor: "pointer" }} disabled={!seg.date || seg.loadingTrains} required>
                        <option value="">{!seg.date ? "Date d'abord" : seg.loadingTrains ? "Chargement..." : "Choisir..."}</option>
                        {seg.trainsDisponibles.map((t) => <option key={t.id} value={t.numero}>{t.numero}</option>)}
                      </select>
                    </div>
                    <div>
                      <label style={labelStyle}>Heure départ</label>
                      <input type="time" value={seg.heureDepart} onChange={(e) => update(index, "heureDepart", e.target.value)} style={inputStyle} required />
                    </div>
                    <div>
                      <label style={labelStyle}>Heure arrivée</label>
                      <input type="time" value={seg.heureArrivee} onChange={(e) => update(index, "heureArrivee", e.target.value)} style={inputStyle} required />
                    </div>
                  </div>
                )}
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

        {/* Filtre liste */}
        <div style={{ display: "flex", alignItems: "center", gap: "12px", marginTop: "32px", marginBottom: "16px" }}>
          <h3 style={{ color: "var(--text-primary)", fontSize: "1rem", margin: 0 }}>Itinéraires du</h3>
          <input type="date" value={dateFiltre} onChange={(e) => { setDateFiltre(e.target.value); chargerItinerairesParDate(e.target.value); }}
            style={{ ...inputStyle, width: "auto" }} />
          {dateFiltre && !loadingItins && (
            <span style={{ color: "var(--text-muted)", fontSize: "0.85rem" }}>{itineraires.length} itinéraire(s)</span>
          )}
        </div>

        <div className="city-list">
          {!dateFiltre ? (
            <div className="city-card" style={{ justifyContent: "center" }}>
              <span style={{ color: "var(--text-muted)" }}>Choisissez une date pour afficher les itinéraires</span>
            </div>
          ) : loadingItins ? (
            <div className="city-card" style={{ justifyContent: "center" }}>
              <span style={{ color: "var(--text-muted)" }}>Chargement...</span>
            </div>
          ) : itineraires.length === 0 ? (
            <div className="city-card" style={{ justifyContent: "center" }}>
              <span style={{ color: "var(--text-muted)" }}>Aucun itinéraire pour cette date</span>
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
    </section>
  );
}

export default Itineraires;
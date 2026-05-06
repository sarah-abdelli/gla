function Historique() {
  const validations = [
    {
      id: 1,
      billetUuid: "a3f1-2026",
      resultat: "ACCEPTEE",
      train: "TGV-101",
      trajet: "Paris → Lyon",
    },
    {
      id: 2,
      billetUuid: "b7c2-2026",
      resultat: "REFUSEE",
      train: "TER-204",
      trajet: "Marseille → Nice",
      motifRefus: "Billet déjà utilisé",
    },
    {
      id: 3,
      billetUuid: "d9e4-2026",
      resultat: "ACCEPTEE",
      train: "TGV-330",
      trajet: "Lille → Paris",
    },
  ];

  return (
    <section className="page-section">
      <h2>Historique 📋</h2>

      <div className="items-list">
        {validations.map((v) => (
          <div className="item-card" key={v.id}>
            🎫 Billet : {v.billetUuid}
            <span className="muted">🚄 {v.train} · {v.trajet}</span>
            <span className="muted">
              {v.resultat === "ACCEPTEE" ? "✅ Acceptée" : "❌ Refusée"}
            </span>
            {v.motifRefus && (
              <span className="muted">Motif : {v.motifRefus}</span>
            )}
          </div>
        ))}
      </div>
    </section>
  );
}

export default Historique;
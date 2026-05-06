function Trains() {
  const trains = [
    { id: 1, numero: "TGV-101", trajet: "Paris → Lyon" },
    { id: 2, numero: "TER-204", trajet: "Marseille → Nice" },
    { id: 3, numero: "TGV-330", trajet: "Lille → Paris" },
  ];

  return (
  <section className="page-section">
    <h2>Gestion des trains 🚄</h2>

    <div className="items-list">
      {trains.map((train, index) => (
        <div className="item-card" key={index}>
          🚄 {train.numero}
          {train.depart && train.arrivee && (
            <span className="muted">
              {train.depart} → {train.arrivee}
            </span>
          )}
        </div>
      ))}
    </div>
  </section>
);
}

export default Trains;
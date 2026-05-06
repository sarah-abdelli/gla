function Clients() {
  const clients = [
    { id: 1, nom: "Alice Martin", email: "alice@test.com" },
    { id: 2, nom: "Karim Benali", email: "karim@test.com" },
    { id: 3, nom: "Sofia Leroy", email: "sofia@test.com" },
    { id: 4, nom: "Nadia Dupont", email: "nadia@test.com" },
  ];

  return (
    <section className="page-section">
      <h2>Clients 👤</h2>

      <div className="items-list">
        {clients.map((c) => (
          <div className="item-card" key={c.id}>
            👤 {c.nom}
            <span className="muted">📧 {c.email}</span>
          </div>
        ))}
      </div>
    </section>
  );
}

export default Clients;
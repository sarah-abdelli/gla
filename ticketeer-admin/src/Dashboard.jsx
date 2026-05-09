import { useEffect, useState } from "react";
import axios from "axios";

function Dashboard() {
  const [stats, setStats] = useState({
    villes: 0,
    trains: 0,
    clients: 0,
    validations: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [villes, trains, clients, validations] = await Promise.all([
          axios.get("http://localhost:8080/api/admin/villes"),
          axios.get("http://localhost:8080/api/admin/trains"),
          axios.get("http://localhost:8080/api/admin/clients"),
          axios.get("http://localhost:8080/api/admin/validations"),
        ]);
        setStats({
          villes: villes.data.length,
          trains: trains.data.length,
          clients: clients.data.length,
          validations: validations.data.length,
        });
      } catch (err) {
        console.error("Erreur chargement stats", err);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  const cards = [
    { icon: "🏙️", label: "Villes", value: stats.villes, color: "#4f7cff" },
    { icon: "🚄", label: "Trains", value: stats.trains, color: "#7c5cfc" },
    { icon: "👤", label: "Clients", value: stats.clients, color: "#22c55e" },
    { icon: "📋", label: "Validations", value: stats.validations, color: "#f59e0b" },
  ];

  return (
    <div>
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
          gap: "20px",
          marginBottom: "32px",
        }}
      >
        {cards.map((card) => (
          <div
            key={card.label}
            className="card"
            style={{
              display: "flex",
              flexDirection: "column",
              gap: "12px",
              borderTop: `3px solid ${card.color}`,
            }}
          >
            <span style={{ fontSize: "2rem" }}>{card.icon}</span>
            <div>
              <div
                style={{
                  fontSize: "2rem",
                  fontWeight: "700",
                  color: card.color,
                }}
              >
                {loading ? "…" : card.value}
              </div>
              <div style={{ color: "var(--text-secondary)", fontSize: "0.9rem" }}>
                {card.label}
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="card">
        <p style={{ color: "var(--text-secondary)", fontSize: "0.9rem" }}>
          Bienvenue sur le panneau d'administration Ticketeer. Utilisez le menu
          de gauche pour gérer les villes, trains, clients et consulter
          l'historique des validations.
        </p>
      </div>
    </div>
  );
}

export default Dashboard;
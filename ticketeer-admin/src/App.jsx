import { useState } from "react";
import Login from "./Login";
import Dashboard from "./Dashboard";
import Villes from "./Villes";
import Trains from "./Trains";
import Segments from "./Segments";
import Historique from "./Historique";
import Clients from "./Clients";
import "./App.css";

function App() {
  const [agentNom, setAgentNom] = useState(localStorage.getItem("agentNom"));
  const [page, setPage] = useState("dashboard");

  if (!agentNom) {
    return <Login onLogin={setAgentNom} />;
  }

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("agentNom");
    setAgentNom(null);
  };

  const renderPage = () => {
    switch (page) {
      case "villes":
        return <Villes />;
      case "trains":
        return <Trains />;
      case "segments":
        return <Segments />;
      case "historique":
        return <Historique />;
      case "clients":
        return <Clients />;
      default:
        return <Dashboard />;
    }
  };

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-logo">🚆 Ticketeer</div>

        <div className="sidebar-agent">
          Connecté :
          <strong>{agentNom}</strong>
        </div>

        <nav>
          <button
            className={page === "dashboard" ? "active" : ""}
            onClick={() => setPage("dashboard")}
          >
            📊 Dashboard
          </button>

          <button
            className={page === "villes" ? "active" : ""}
            onClick={() => setPage("villes")}
          >
            🏙️ Villes
          </button>

          <button
            className={page === "trains" ? "active" : ""}
            onClick={() => setPage("trains")}
          >
            🚄 Trains
          </button>

          <button
            className={page === "segments" ? "active" : ""}
            onClick={() => setPage("segments")}
          >
            🛤️ Segments
          </button>

          <button
            className={page === "historique" ? "active" : ""}
            onClick={() => setPage("historique")}
          >
            📋 Historique
          </button>

          <button
            className={page === "clients" ? "active" : ""}
            onClick={() => setPage("clients")}
          >
            👤 Clients
          </button>
        </nav>

        <div className="sidebar-logout">
          <button onClick={logout}>Déconnexion</button>
        </div>
      </aside>

      <main className="main-content">
        <h1 className="page-title">Admin Ticketeer 🎟️</h1>
        <p className="page-subtitle">Panneau d'administration</p>

        {renderPage()}
      </main>
    </div>
  );
}

export default App;
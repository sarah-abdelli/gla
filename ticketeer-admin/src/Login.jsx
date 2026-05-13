import { useState } from "react";
import axios from "axios";

function Login({ onLogin }) {
  const [login, setLogin] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      // Endpoint admin séparé — les agents ne peuvent pas se connecter ici
      const res = await axios.post("http://localhost:8080/api/auth/login/admin", {
        login,
        motDePasse,
      });

      localStorage.setItem("agentNom", res.data.nom);
      onLogin(res.data.nom);
      onLogin(res.data.nom);
    } catch (err) {
      if (err.response?.status === 403) {
        setError("Accès réservé aux administrateurs");
      } else {
        setError("Login ou mot de passe incorrect");
      }
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <div className="login-header">
          <div className="login-logo">
            <span>🚆</span>
            Ticketeer
          </div>
          <p className="login-subtitle">Panneau d'administration</p>
          <div className="login-badge">Système opérationnel</div>
        </div>

        <div className="login-card">
          <div className="login-card-title">Authentification</div>

          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-group">
              <label>Identifiant</label>
              <input
                type="text"
                placeholder="admin"
                value={login}
                onChange={(e) => setLogin(e.target.value)}
              />
            </div>

            <div className="form-group">
              <div className="form-group-header">
                <label>Mot de passe</label>
              </div>
              <input
                type="password"
                placeholder="••••••••"
                value={motDePasse}
                onChange={(e) => setMotDePasse(e.target.value)}
              />
            </div>

            <button type="submit" className="login-btn">
              Se connecter
            </button>

            {error && <p className="error">{error}</p>}
          </form>
        </div>
      </div>
    </div>
  );
}

export default Login;

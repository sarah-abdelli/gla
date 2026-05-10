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
      const res = await axios.post("http://localhost:8080/api/auth/login", {
        login,
        motDePasse,
      });

      localStorage.setItem("token", res.data.token);
      localStorage.setItem("agentNom", res.data.agentNom);
      onLogin(res.data.agentNom);
    } catch (err) {
      setError("Login ou mot de passe incorrect");
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

          <p className="login-subtitle">Panneau d’administration</p>
          <div className="login-badge">Système opérationnel</div>
        </div>

        <div className="login-card">
          <div className="login-card-title">Authentification</div>

          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-group">
              <label>Identifiant</label>
              <input
                type="text"
                placeholder="agent1"
                value={login}
                onChange={(e) => setLogin(e.target.value)}
              />
            </div>

            <div className="form-group">
              <div className="form-group-header">
                <label>Mot de passe</label>
                <a href="#" className="forgot-link">
                  Mot de passe oublié ?
                </a>
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

          <div className="login-footer">
            Accès restreint · <a href="#">Assistance</a> ·{" "}
            <a href="#">Documentation</a>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;
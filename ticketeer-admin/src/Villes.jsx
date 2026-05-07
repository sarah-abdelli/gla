import { useEffect, useState } from "react";
import axios from "axios";

function Villes() {
  const [villes, setVilles] = useState([]);

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/admin/villes")
      .then((res) => setVilles(res.data))
      .catch((err) => console.error(err));
  }, []);

  return (
    <section className="page-section">
      <h2>Gestion des villes 🏙️</h2>

      <div className="items-list">
        {villes.map((ville) => (
          <div className="item-card" key={ville.id}>
            🏙️ <strong>{ville.nom}</strong>
          </div>
        ))}
      </div>
    </section>
  );
}

export default Villes;
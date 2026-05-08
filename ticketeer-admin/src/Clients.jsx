import { useEffect, useState } from "react";
import axios from "axios";

function Clients() {
  const [clients, setClients] = useState([]);

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/admin/clients")
      .then((res) => setClients(res.data))
      .catch((err) => console.error(err));
  }, []);

  return (
    <section className="page-section">
      <h2>Clients 👤</h2>

      <div className="items-list">
        {clients.map((client) => (
          <div className="item-card" key={client.id}>
            👤 <strong>{client.nom}</strong>
            <span className="muted">📧 {client.email}</span>
          </div>
        ))}
      </div>
    </section>
  );
}

export default Clients;
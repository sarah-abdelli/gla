import { useEffect, useState } from "react";
import axios from "axios";

function Trains() {
  const [trains, setTrains] = useState([]);

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/admin/trains")
      .then((res) => setTrains(res.data))
      .catch((err) => console.error(err));
  }, []);

  return (
    <section className="page-section">
      <h2>Gestion des trains 🚄</h2>

      <div className="items-list">
        {trains.map((train) => (
          <div className="item-card" key={train.id}>
            🚄 <strong>{train.numero}</strong>
          </div>
        ))}
      </div>
    </section>
  );
}

export default Trains;
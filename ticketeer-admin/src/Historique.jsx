import { useEffect, useState } from "react";
import axios from "axios";

function Historique() {
  const [validations, setValidations] = useState([]);

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/admin/validations")
      .then((res) => setValidations(res.data))
      .catch((err) => console.error(err));
  }, []);

  return (
    <section className="page-section">
      <h2>Historique 📋</h2>

      <div className="items-list">
        {validations.length === 0 ? (
          <div className="item-card">
            Aucune validation pour le moment
          </div>
        ) : (
          validations.map((v) => (
            <div className="item-card" key={v.id}>
              🎫 Validation #{v.id}
              <span className="muted">Résultat : {v.resultat}</span>
              {v.motifRefus && (
                <span className="muted">Motif : {v.motifRefus}</span>
              )}
            </div>
          ))
        )}
      </div>
    </section>
  );
}

export default Historique;
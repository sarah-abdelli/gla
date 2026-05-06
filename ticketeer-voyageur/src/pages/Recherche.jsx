import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { rechercherItineraires } from '../api/api'

const VILLES = [
    'Paris', 'Lyon', 'Marseille', 'Bordeaux',
    'Lille', 'Strasbourg', 'Nantes', 'Toulouse',
    'Nice', 'Montpellier'
]

function Recherche() {
    const [depart, setDepart] = useState('')
    const [arrivee, setArrivee] = useState('')
    const [resultats, setResultats] = useState([])
    const [loading, setLoading] = useState(false)
    const [erreur, setErreur] = useState('')
    const navigate = useNavigate()

    const rechercher = async (e) => {
        e.preventDefault()
        if (!depart || !arrivee) {
            setErreur('Veuillez sélectionner une ville de départ et d\'arrivée.')
            return
        }
        if (depart === arrivee) {
            setErreur('La ville de départ et d\'arrivée doivent être différentes.')
            return
        }
        setErreur('')
        setLoading(true)
        try {
            const res = await rechercherItineraires(depart, arrivee)
            setResultats(res.data)
            if (res.data.length === 0) setErreur('Aucun itinéraire trouvé.')
        } catch (err) {
            setErreur('Erreur lors de la recherche. Vérifiez que le backend tourne.')
        } finally {
            setLoading(false)
        }
    }

    const selectionner = (itineraire) => {
        navigate('/paiement', { state: { itineraire } })
    }

    return (
        <div className="max-w-2xl mx-auto">
            <h2 className="text-2xl font-bold text-blue-700 mb-6">🔍 Rechercher un trajet</h2>

            <form onSubmit={rechercher} className="bg-white rounded-xl shadow p-6 mb-6">
                <div className="grid grid-cols-2 gap-4 mb-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Départ</label>
                        <select
                            value={depart}
                            onChange={(e) => setDepart(e.target.value)}
                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            <option value="">-- Choisir --</option>
                            {VILLES.map(v => <option key={v} value={v}>{v}</option>)}
                        </select>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Arrivée</label>
                        <select
                            value={arrivee}
                            onChange={(e) => setArrivee(e.target.value)}
                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            <option value="">-- Choisir --</option>
                            {VILLES.map(v => <option key={v} value={v}>{v}</option>)}
                        </select>
                    </div>
                </div>

                {erreur && <p className="text-red-500 text-sm mb-3">{erreur}</p>}

                <button
                    type="submit"
                    disabled={loading}
                    className="w-full bg-blue-700 text-white py-2 rounded-lg font-semibold hover:bg-blue-800 transition disabled:opacity-50"
                >
                    {loading ? 'Recherche...' : 'Rechercher'}
                </button>
            </form>

            {resultats.length > 0 && (
                <div className="space-y-4">
                    <h3 className="font-semibold text-gray-700">{resultats.length} itinéraire(s) trouvé(s)</h3>
                    {resultats.map((itin) => (
                        <div key={itin.id} className="bg-white rounded-xl shadow p-5">
                            <div className="flex justify-between items-center mb-3">
                                <div>
                                    <p className="text-lg font-bold text-blue-700">
                                        {itin.segments[0]?.villeDepart?.nom} → {itin.segments[itin.segments.length - 1]?.villeArrivee?.nom}
                                    </p>
                                    <p className="text-sm text-gray-500">
                                        {itin.segments.length === 1 ? '🟢 Direct' : `🔄 ${itin.segments.length} segments`}
                                    </p>
                                </div>
                                <div className="text-right">
                                    <p className="text-sm text-gray-600">
                                        {itin.segments[0]?.heureDepart?.substring(0, 5)} → {itin.segments[itin.segments.length - 1]?.heureArrivee?.substring(0, 5)}
                                    </p>
                                </div>
                            </div>
                            <button
                                onClick={() => selectionner(itin)}
                                className="w-full bg-green-600 text-white py-2 rounded-lg font-semibold hover:bg-green-700 transition"
                            >
                                Sélectionner ce trajet
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}

export default Recherche
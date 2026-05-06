import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { creerBillet } from '../api/api'

function Paiement() {
    const { state } = useLocation()
    const navigate = useNavigate()
    const itineraire = state?.itineraire

    const [nom, setNom] = useState('')
    const [email, setEmail] = useState('')
    const [carte, setCarte] = useState('')
    const [loading, setLoading] = useState(false)
    const [erreur, setErreur] = useState('')

    if (!itineraire) {
        return (
            <div className="text-center py-20">
                <p className="text-red-500">Aucun itinéraire sélectionné.</p>
            </div>
        )
    }

    const payer = async (e) => {
        e.preventDefault()
        if (!nom || !email || carte.length < 16) {
            setErreur('Veuillez remplir tous les champs correctement.')
            return
        }
        setErreur('')
        setLoading(true)
        try {
            // Paiement simulé : on crée le billet avec voyageurId=1 (alice de test)
            const res = await creerBillet(1, itineraire.id)
            const uuid = res.data.uuid
            navigate(`/billet/${uuid}`)
        } catch (err) {
            setErreur('Erreur lors de la création du billet. Vérifiez le backend.')
        } finally {
            setLoading(false)
        }
    }

    const villeDepart = itineraire.segments[0]?.villeDepart?.nom
    const villeArrivee = itineraire.segments[itineraire.segments.length - 1]?.villeArrivee?.nom
    const heureDepart = itineraire.segments[0]?.heureDepart?.substring(0, 5)
    const heureArrivee = itineraire.segments[itineraire.segments.length - 1]?.heureArrivee?.substring(0, 5)

    return (
        <div className="max-w-lg mx-auto">
            <h2 className="text-2xl font-bold text-blue-700 mb-6">💳 Paiement simulé</h2>

            {/* Résumé du trajet */}
            <div className="bg-blue-50 border border-blue-200 rounded-xl p-4 mb-6">
                <p className="font-semibold text-blue-800 text-lg">
                    {villeDepart} → {villeArrivee}
                </p>
                <p className="text-gray-600 text-sm">{heureDepart} → {heureArrivee}</p>
                <p className="text-green-700 font-bold mt-2 text-xl">Prix : 29,90 €</p>
            </div>

            {/* Formulaire de paiement simulé */}
            <form onSubmit={payer} className="bg-white rounded-xl shadow p-6">
                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">Nom complet</label>
                    <input
                        type="text"
                        value={nom}
                        onChange={(e) => setNom(e.target.value)}
                        placeholder="Alice Dupont"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>

                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="alice@test.com"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>

                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Numéro de carte (simulé)
                    </label>
                    <input
                        type="text"
                        value={carte}
                        onChange={(e) => setCarte(e.target.value.replace(/\D/g, '').substring(0, 16))}
                        placeholder="1234 5678 9012 3456"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    <p className="text-xs text-gray-400 mt-1">💡 Paiement simulé — aucune vraie transaction</p>
                </div>

                {erreur && <p className="text-red-500 text-sm mb-3">{erreur}</p>}

                <button
                    type="submit"
                    disabled={loading}
                    className="w-full bg-green-600 text-white py-3 rounded-lg font-bold text-lg hover:bg-green-700 transition disabled:opacity-50"
                >
                    {loading ? 'Traitement...' : '✅ Confirmer et payer 29,90 €'}
                </button>
            </form>
        </div>
    )
}

export default Paiement
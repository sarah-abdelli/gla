import { useState } from 'react'
import { useLocation, useNavigate, Link } from 'react-router-dom'
import { creerBillet } from '../api/api'
import { useAuth } from '../context/AuthContext'

function Paiement() {
    const { state }   = useLocation()
    const navigate    = useNavigate()
    const { user }    = useAuth()
    const itineraire  = state?.itineraire
    const prix        = state?.prix || '29,90 €'

    const [carte, setCarte]     = useState('')
    const [loading, setLoading] = useState(false)
    const [erreur, setErreur]   = useState('')

    if (!itineraire) return (
        <div className="text-center py-20">
            <div className="text-6xl mb-4">😕</div>
            <p className="text-red-500 text-lg font-medium">Aucun itinéraire sélectionné.</p>
        </div>
    )

    if (!user) return (
        <div className="text-center py-20">
            <div className="text-6xl mb-4">🔐</div>
            <p className="text-gray-700 text-lg font-semibold mb-4">Vous devez être connecté pour réserver.</p>
            <Link to="/login" className="bg-blue-700 hover:bg-blue-800 text-white px-8 py-3 rounded-xl font-bold shadow transition-all hover:scale-105 inline-block">
                Se connecter →
            </Link>
        </div>
    )

    const villeDepart  = itineraire.segments[0]?.villeDepart?.nom
    const villeArrivee = itineraire.segments[itineraire.segments.length - 1]?.villeArrivee?.nom
    const heureDepart  = itineraire.segments[0]?.heureDepart?.substring(0, 5)
    const heureArrivee = itineraire.segments[itineraire.segments.length - 1]?.heureArrivee?.substring(0, 5)
    const dateDepart   = itineraire.segments[0]?.dateDepart

    const payer = async (e) => {
        e.preventDefault()
        if (carte.length < 16) {
            setErreur('Veuillez entrer un numéro de carte à 16 chiffres.')
            return
        }
        setErreur('')
        setLoading(true)
        try {
            const res = await creerBillet(user.id, itineraire.id)
            navigate(`/billet/${res.data.uuid}`)
        } catch {
            setErreur('Erreur lors de la création du billet. Vérifiez le backend.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div>
            <div className="mb-8">
                <h2 className="text-3xl font-extrabold text-gray-800 mb-1">💳 Finaliser la réservation</h2>
                <p className="text-gray-500 text-sm">Paiement 100% simulé — aucune vraie transaction</p>
            </div>

            {/* Trip summary card */}
            <div className="bg-gradient-to-r from-blue-900 to-blue-600 rounded-2xl p-6 mb-6 text-white shadow-xl">
                <div className="flex items-center justify-between">
                    <div>
                        <div className="text-blue-200 text-sm font-medium mb-2">🚄 Votre trajet</div>
                        <div className="text-2xl font-black mb-1">{villeDepart} → {villeArrivee}</div>
                        <div className="text-blue-200 text-sm">{heureDepart} départ · {heureArrivee} arrivée</div>
                        {dateDepart && (
                            <div className="text-blue-300 text-xs mt-1">
                                📅 {new Date(dateDepart).toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })}
                            </div>
                        )}
                    </div>
                    <div className="text-right">
                        <div className="text-blue-200 text-xs mb-1">Prix total</div>
                        <div className="text-4xl font-black text-yellow-300">{prix}</div>
                        <div className="text-blue-300 text-xs mt-1">1 voyageur</div>
                    </div>
                </div>
            </div>

            {/* Form */}
            <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-100">

                {/* Voyageur connecté */}
                <div className="mb-6 bg-blue-50 rounded-xl px-5 py-4 flex items-center gap-4 border border-blue-100">
                    <div className="w-10 h-10 bg-blue-700 rounded-full flex items-center justify-center text-white font-black text-lg">
                        {user.nom.charAt(0).toUpperCase()}
                    </div>
                    <div>
                        <div className="font-bold text-gray-800">{user.nom}</div>
                        <div className="text-sm text-gray-500">{user.email}</div>
                    </div>
                    <span className="ml-auto text-xs bg-green-100 text-green-700 font-bold px-2 py-1 rounded-full">✅ Connecté</span>
                </div>

                <h3 className="text-base font-bold text-gray-700 mb-5 flex items-center gap-2">
                    <span className="bg-blue-700 text-white rounded-full w-6 h-6 flex items-center justify-center text-xs font-black">1</span>
                    Informations de paiement
                </h3>

                <form onSubmit={payer} className="space-y-5">
                    <div>
                        <label className="block text-sm font-semibold text-gray-600 mb-2">
                            💳 Numéro de carte <span className="text-blue-500 font-normal text-xs">(simulé)</span>
                        </label>
                        <input type="text" value={carte}
                               onChange={e => setCarte(e.target.value.replace(/\D/g, '').substring(0, 16))}
                               placeholder="1234 5678 9012 3456"
                               className="w-full border-2 border-gray-200 rounded-xl px-4 py-3 focus:outline-none focus:border-blue-500 bg-gray-50 font-mono tracking-widest transition-colors" />
                        <p className="text-xs text-gray-400 mt-1.5">💡 Aucune vraie transaction — entrez n'importe quel numéro à 16 chiffres</p>
                    </div>

                    {erreur && (
                        <div className="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-xl text-sm flex items-center gap-2">
                            ⚠️ {erreur}
                        </div>
                    )}

                    <button type="submit" disabled={loading}
                            className="w-full bg-green-600 hover:bg-green-700 text-white py-4 rounded-xl font-black text-lg shadow-lg transition-all hover:scale-[1.01] disabled:opacity-50 flex items-center justify-center gap-3 mt-2">
                        {loading
                            ? <><span className="animate-spin">⏳</span> Traitement en cours...</>
                            : <>✅ Payer {prix} et obtenir mon billet</>
                        }
                    </button>
                </form>
            </div>
        </div>
    )
}

export default Paiement
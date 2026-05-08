import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { rechercherItineraires } from '../api/api'

const VILLES = ['Paris', 'Lyon', 'Marseille', 'Bordeaux', 'Lille', 'Strasbourg', 'Nantes', 'Toulouse', 'Nice', 'Montpellier']

function getToday() {
    return new Date().toISOString().split('T')[0]
}

function getDuree(itin) {
    const dep = itin.segments[0]?.heureDepart
    const arr = itin.segments[itin.segments.length - 1]?.heureArrivee
    if (!dep || !arr) return ''
    const [dh, dm] = dep.split(':').map(Number)
    const [ah, am] = arr.split(':').map(Number)
    const mins = (ah * 60 + am) - (dh * 60 + dm)
    if (mins <= 0) return ''
    return mins >= 60 ? `${Math.floor(mins / 60)}h${mins % 60 > 0 ? (mins % 60) + 'min' : ''}` : `${mins}min`
}

function getPlacesMin(itin) {
    if (!itin.segments.length) return null
    return Math.min(...itin.segments.map(s => s.placesDisponibles ?? 200))
}

function PlacesBadge({ places }) {
    if (places === null || places === undefined) return null
    if (places === 0) return (
        <span className="text-xs font-bold px-2 py-0.5 rounded-full bg-red-100 text-red-600">
            Complet
        </span>
    )
    if (places <= 50) return (
        <span className="text-xs font-bold px-2 py-0.5 rounded-full bg-orange-100 text-orange-600">
            ⚡ {places} places
        </span>
    )
    return (
        <span className="text-xs font-bold px-2 py-0.5 rounded-full bg-green-100 text-green-700">
            ✓ {places} places
        </span>
    )
}

function Recherche() {
    const [depart, setDepart]     = useState('')
    const [arrivee, setArrivee]   = useState('')
    const [date, setDate]         = useState(getToday())
    const [resultats, setResultats] = useState([])
    const [loading, setLoading]   = useState(false)
    const [erreur, setErreur]     = useState('')
    const [rechercheFaite, setRechercheFaite] = useState(false)
    const navigate = useNavigate()

    const rechercher = async (e) => {
        e.preventDefault()
        if (!depart || !arrivee) { setErreur("Veuillez sélectionner une ville de départ et d'arrivée."); return }
        if (depart === arrivee)  { setErreur("La ville de départ et d'arrivée doivent être différentes."); return }
        setErreur('')
        setLoading(true)
        setResultats([])
        try {
            const res = await rechercherItineraires(depart, arrivee, date)
            setResultats(res.data)
            setRechercheFaite(true)
            if (res.data.length === 0) setErreur('Aucun itinéraire trouvé pour ce trajet à cette date.')
        } catch {
            setErreur('Erreur de connexion au serveur. Vérifiez que le backend tourne.')
        } finally {
            setLoading(false)
        }
    }

    const selectionner = (itin) => navigate('/paiement', { state: { itineraire: itin } })

    const dateAffichee = date
        ? new Date(date + 'T00:00:00').toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })
        : ''

    return (
        <div>
            <div className="mb-8">
                <h2 className="text-3xl font-extrabold text-gray-800 mb-1">Rechercher un trajet</h2>
                <p className="text-gray-500 text-sm">Sélectionnez votre départ, votre arrivée et la date souhaitée</p>
            </div>

            {/* Search form */}
            <div className="bg-white rounded-2xl shadow-lg p-6 mb-8 border border-gray-100">
                <form onSubmit={rechercher}>
                    <div className="grid md:grid-cols-4 gap-4 items-end">
                        {/* Départ */}
                        <div>
                            <label className="block text-sm font-semibold text-gray-600 mb-2 flex items-center gap-1.5">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#1d4ed8" strokeWidth="2.5"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                                Gare de départ
                            </label>
                            <select value={depart} onChange={e => setDepart(e.target.value)}
                                    className="w-full border-2 border-gray-200 rounded-xl px-4 py-3 focus:outline-none focus:border-blue-500 text-gray-800 font-medium bg-gray-50 transition-colors">
                                <option value="">-- Choisir --</option>
                                {VILLES.map(v => <option key={v} value={v}>{v}</option>)}
                            </select>
                        </div>

                        {/* Arrivée */}
                        <div>
                            <label className="block text-sm font-semibold text-gray-600 mb-2 flex items-center gap-1.5">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="#1d4ed8"><path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/></svg>
                                Gare d'arrivée
                            </label>
                            <select value={arrivee} onChange={e => setArrivee(e.target.value)}
                                    className="w-full border-2 border-gray-200 rounded-xl px-4 py-3 focus:outline-none focus:border-blue-500 text-gray-800 font-medium bg-gray-50 transition-colors">
                                <option value="">-- Choisir --</option>
                                {VILLES.map(v => <option key={v} value={v}>{v}</option>)}
                            </select>
                        </div>

                        {/* Date */}
                        <div>
                            <label className="block text-sm font-semibold text-gray-600 mb-2 flex items-center gap-1.5">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#1d4ed8" strokeWidth="2.5"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                                Date de départ
                            </label>
                            <input
                                type="date"
                                value={date}
                                min={getToday()}
                                onChange={e => setDate(e.target.value)}
                                className="w-full border-2 border-gray-200 rounded-xl px-4 py-3 focus:outline-none focus:border-blue-500 text-gray-800 font-medium bg-gray-50 transition-colors"
                            />
                        </div>

                        {/* Bouton */}
                        <button type="submit" disabled={loading}
                                className="bg-blue-700 hover:bg-blue-800 text-white py-3 px-6 rounded-xl font-bold shadow-md transition-all hover:scale-[1.02] disabled:opacity-50 flex items-center justify-center gap-2">
                            {loading
                                ? <><span className="animate-spin">⏳</span> Recherche...</>
                                : <><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.5"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg> Rechercher</>
                            }
                        </button>
                    </div>

                    {erreur && (
                        <div className="mt-4 bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-xl text-sm font-medium flex items-center gap-2">
                            ⚠️ {erreur}
                        </div>
                    )}
                </form>
            </div>

            {/* Results */}
            {resultats.length > 0 && (
                <div className="animate-fade-in-up">
                    <div className="flex items-center justify-between mb-4">
                        <h3 className="text-lg font-bold text-gray-700">
                            {resultats.length} trajet{resultats.length > 1 ? 's' : ''} disponible{resultats.length > 1 ? 's' : ''}
                        </h3>
                        <span className="text-sm text-gray-400">{depart} → {arrivee} · {dateAffichee}</span>
                    </div>

                    <div className="space-y-4">
                        {resultats.map((itin, idx) => {
                            const isDirect = itin.segments.length === 1
                            const villeD  = itin.segments[0]?.villeDepart?.nom
                            const villeA  = itin.segments[itin.segments.length - 1]?.villeArrivee?.nom
                            const hDep   = itin.segments[0]?.heureDepart?.substring(0, 5)
                            const hArr   = itin.segments[itin.segments.length - 1]?.heureArrivee?.substring(0, 5)
                            const duree  = getDuree(itin)
                            const train  = itin.segments[0]?.train?.numero
                            const places = getPlacesMin(itin)
                            const complet = places === 0

                            return (
                                <div key={idx} className={`bg-white rounded-2xl shadow-md hover:shadow-xl transition-all border overflow-hidden ${complet ? 'border-red-100 opacity-70' : 'border-gray-100'}`}>
                                    <div className={`h-1.5 w-full ${complet ? 'bg-red-300' : isDirect ? 'bg-green-500' : 'bg-orange-400'}`} />

                                    <div className="p-6 flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
                                        {/* Route */}
                                        <div className="flex items-center gap-6 flex-1">
                                            <div className="text-center min-w-[90px]">
                                                <div className="text-3xl font-black text-gray-800">{hDep}</div>
                                                <div className="text-sm font-bold text-blue-700 mt-1">{villeD}</div>
                                            </div>

                                            <div className="flex-1 flex flex-col items-center">
                                                {duree && <div className="text-xs text-gray-400 font-semibold mb-1">⏱ {duree}</div>}
                                                <div className="relative w-full flex items-center">
                                                    <div className="h-0.5 bg-gray-200 flex-1 rounded" />
                                                    <span className="mx-3 text-2xl">🚄</span>
                                                    <div className="h-0.5 bg-gray-200 flex-1 rounded" />
                                                </div>
                                                <div className={`mt-2 text-xs font-bold px-3 py-1 rounded-full ${isDirect ? 'bg-green-100 text-green-700' : 'bg-orange-100 text-orange-700'}`}>
                                                    {isDirect ? '✅ Direct' : `🔄 ${itin.segments.length} correspondances`}
                                                </div>
                                                <div className="text-xs text-gray-400 mt-1">{train}</div>
                                            </div>

                                            <div className="text-center min-w-[90px]">
                                                <div className="text-3xl font-black text-gray-800">{hArr}</div>
                                                <div className="text-sm font-bold text-blue-700 mt-1">{villeA}</div>
                                            </div>
                                        </div>

                                        {/* Price + places + CTA */}
                                        <div className="flex flex-col items-center md:items-end gap-3 min-w-[160px]">
                                            <div className="text-right">
                                                <div className="text-3xl font-black text-green-600">29,90 €</div>
                                                <div className="text-xs text-gray-400">par voyageur</div>
                                            </div>
                                            <PlacesBadge places={places} />
                                            <button
                                                onClick={() => selectionner(itin)}
                                                disabled={complet}
                                                className="bg-blue-700 hover:bg-blue-800 disabled:bg-gray-300 disabled:cursor-not-allowed text-white px-7 py-2.5 rounded-xl font-bold shadow-md transition-all hover:scale-105 w-full text-center">
                                                {complet ? 'Complet' : 'Réserver →'}
                                            </button>
                                        </div>
                                    </div>

                                    {/* Correspondance details */}
                                    {!isDirect && (
                                        <div className="bg-orange-50 px-6 py-3 border-t border-orange-100">
                                            <div className="text-xs font-semibold text-orange-600 mb-1">Détail des correspondances</div>
                                            <div className="flex flex-wrap gap-2 text-sm text-orange-700">
                                                {itin.segments.map((seg, si) => (
                                                    <span key={si} className="flex items-center gap-1">
                                                        {si > 0 && <span className="text-orange-400 mx-1">→</span>}
                                                        <span className="bg-white border border-orange-200 rounded-lg px-2 py-0.5 font-medium text-xs">
                                                            {seg.villeDepart?.nom} {seg.heureDepart?.substring(0, 5)} → {seg.villeArrivee?.nom} {seg.heureArrivee?.substring(0, 5)}
                                                            <span className="text-orange-400 ml-1">· {seg.train?.numero}</span>
                                                        </span>
                                                    </span>
                                                ))}
                                            </div>
                                        </div>
                                    )}
                                </div>
                            )
                        })}
                    </div>
                </div>
            )}

            {rechercheFaite && resultats.length === 0 && !erreur && (
                <div className="text-center py-16 text-gray-400">
                    <div className="text-5xl mb-4">🔍</div>
                    <p className="font-medium">Aucun trajet trouvé entre {depart} et {arrivee} le {dateAffichee}.</p>
                </div>
            )}
        </div>
    )
}

export default Recherche
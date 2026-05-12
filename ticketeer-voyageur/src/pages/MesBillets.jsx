import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getMesBillets } from '../api/api'
import { useAuth } from '../context/AuthContext'

function MesBillets() {
    const { user }              = useAuth()
    const [billets, setBillets] = useState([])
    const [loading, setLoading] = useState(true)
    const [erreur, setErreur]   = useState('')

    useEffect(() => {
        getMesBillets(user.id)
            .then(res => {
                const data = Array.isArray(res.data) ? res.data : []

                // Trier par date+heure de départ : les plus récents en premier
                const sorted = [...data].sort((a, b) => {
                    const segsA = a.itineraire?.segments || []
                    const segsB = b.itineraire?.segments || []
                    const dateA = segsA[0]?.dateDepart && segsA[0]?.heureDepart
                        ? new Date(`${segsA[0].dateDepart}T${segsA[0].heureDepart}`)
                        : new Date(0)
                    const dateB = segsB[0]?.dateDepart && segsB[0]?.heureDepart
                        ? new Date(`${segsB[0].dateDepart}T${segsB[0].heureDepart}`)
                        : new Date(0)
                    return dateB - dateA // plus récent en premier
                })

                setBillets(sorted)
            })
            .catch(() => setErreur('Impossible de charger vos billets.'))
            .finally(() => setLoading(false))
    }, [user.id])

    // Vérifie si le DERNIER segment est terminé (heure d'arrivée passée)
    const estExpire = (segs) => {
        if (!segs || segs.length === 0) return false
        const dernierSeg = segs[segs.length - 1]
        const dep   = dernierSeg?.dateDepart
        const heure = dernierSeg?.heureArrivee
        if (!dep || !heure) return false
        return new Date() > new Date(`${dep}T${heure}`)
    }

    const etatEffectif = (billet, segs) => {
        // Si la date+heure d'arrivée du dernier segment est passée → EXPIRÉ
        // peu importe l'état en base (VALIDE ou UTILISÉ)
        if (estExpire(segs)) return 'EXPIRE'
        if (billet.etat === 'INVALIDE') return 'INVALIDE'
        return billet.etat
    }

    const etatStyle = (etat) => {
        if (etat === 'VALIDE')  return 'bg-green-100 text-green-700 border-green-200'
        if (etat === 'UTILISE') return 'bg-gray-100 text-gray-500 border-gray-200'
        if (etat === 'EXPIRE')  return 'bg-gray-100 text-gray-400 border-gray-200'
        return 'bg-red-100 text-red-600 border-red-200'
    }

    const etatIcon = (etat) => {
        if (etat === 'VALIDE')  return '✅'
        if (etat === 'UTILISE') return '☑️'
        if (etat === 'EXPIRE')  return '⏰'
        return '❌'
    }

    const barreStyle = (etat) => {
        if (etat === 'VALIDE')  return 'bg-green-500'
        if (etat === 'UTILISE') return 'bg-gray-300'
        if (etat === 'EXPIRE')  return 'bg-gray-300'
        return 'bg-red-400'
    }

    if (loading) return (
        <div className="flex flex-col items-center justify-center py-32">
            <div className="text-6xl mb-4 animate-bounce">🎫</div>
            <p className="text-gray-500 font-medium">Chargement de vos billets...</p>
        </div>
    )

    return (
        <div>
            <div className="mb-8 flex items-center justify-between">
                <div>
                    <h2 className="text-3xl font-extrabold text-gray-800 mb-1">🎫 Mes billets</h2>
                    <p className="text-gray-500 text-sm">Bonjour <span className="font-semibold text-blue-700">{user.nom}</span> — voici tous vos billets</p>
                </div>
                <Link to="/recherche"
                      className="bg-blue-700 hover:bg-blue-800 text-white px-5 py-2.5 rounded-xl font-bold text-sm shadow transition-all hover:scale-105 flex items-center gap-2">
                    + Nouveau billet
                </Link>
            </div>

            {erreur && (
                <div className="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-xl text-sm mb-6">
                    ⚠️ {erreur}
                </div>
            )}

            {billets.length === 0 && !erreur && (
                <div className="text-center py-20 text-gray-400">
                    <div className="text-6xl mb-4">🎫</div>
                    <p className="font-medium text-lg">Vous n'avez pas encore de billets.</p>
                    <Link to="/recherche" className="mt-4 inline-block text-blue-600 hover:underline font-semibold">
                        Rechercher un trajet →
                    </Link>
                </div>
            )}

            <div className="space-y-4">
                {billets.map(billet => {
                    const segs   = billet.itineraire?.segments || []
                    const villeD = segs[0]?.villeDepart?.nom
                    const villeA = segs[segs.length - 1]?.villeArrivee?.nom
                    const hDep   = segs[0]?.heureDepart?.substring(0, 5)
                    const hArr   = segs[segs.length - 1]?.heureArrivee?.substring(0, 5)
                    const date   = segs[0]?.dateDepart
                    const isDirect = segs.length === 1

                    const etat = etatEffectif(billet, segs)
                    const isGrise = etat === 'UTILISE' || etat === 'EXPIRE' || etat === 'INVALIDE'

                    return (
                        <div key={billet.uuid}
                             className={`bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden hover:shadow-xl transition-all ${isGrise ? 'opacity-60' : ''}`}>
                            <div className={`h-1.5 w-full ${barreStyle(etat)}`} />

                            <div className="p-6 flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
                                <div className="flex items-center gap-6 flex-1">
                                    <div className="text-center min-w-[80px]">
                                        <div className={`text-2xl font-black ${isGrise ? 'text-gray-400' : 'text-gray-800'}`}>{hDep}</div>
                                        <div className={`text-sm font-bold mt-0.5 ${isGrise ? 'text-gray-400' : 'text-blue-700'}`}>{villeD}</div>
                                    </div>

                                    <div className="flex flex-col items-center flex-1">
                                        <div className="relative w-full flex items-center">
                                            <div className="h-0.5 bg-gray-200 flex-1 rounded" />
                                            <span className="mx-2 text-xl">{isGrise ? '🚂' : '🚄'}</span>
                                            <div className="h-0.5 bg-gray-200 flex-1 rounded" />
                                        </div>
                                        <div className={`mt-1.5 text-xs font-bold px-2 py-0.5 rounded-full ${isDirect ? 'bg-green-100 text-green-700' : 'bg-orange-100 text-orange-700'}`}>
                                            {isDirect ? 'Direct' : `${segs.length} correspondances`}
                                        </div>
                                    </div>

                                    <div className="text-center min-w-[80px]">
                                        <div className={`text-2xl font-black ${isGrise ? 'text-gray-400' : 'text-gray-800'}`}>{hArr}</div>
                                        <div className={`text-sm font-bold mt-0.5 ${isGrise ? 'text-gray-400' : 'text-blue-700'}`}>{villeA}</div>
                                    </div>
                                </div>

                                <div className="flex flex-col items-end gap-2 min-w-[180px]">
                                    {date && (
                                        <div className="text-xs text-gray-400">
                                            📅 {new Date(date).toLocaleDateString('fr-FR', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' })}
                                        </div>
                                    )}
                                    <span className={`px-3 py-1 rounded-full text-xs font-bold border ${etatStyle(etat)}`}>
                                        {etatIcon(etat)} {etat}
                                    </span>
                                    <Link to={`/billet/${billet.uuid}`}
                                          className={`px-4 py-2 rounded-xl font-bold text-xs shadow transition-all flex items-center gap-1.5 ${
                                              isGrise
                                                  ? 'bg-gray-200 text-gray-500 hover:bg-gray-300'
                                                  : 'bg-blue-700 hover:bg-blue-800 text-white hover:scale-105'
                                          }`}>
                                        Voir le billet →
                                    </Link>
                                </div>
                            </div>

                            <div className="bg-gray-50 px-6 py-2 border-t border-gray-100">
                                <p className="text-xs font-mono text-gray-400 truncate">{billet.uuid}</p>
                            </div>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}

export default MesBillets

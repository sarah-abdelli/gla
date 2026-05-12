import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { QRCodeSVG } from 'qrcode.react'
import { getBillet } from '../api/api'

function Billet() {
    const { uuid } = useParams()
    const [billet, setBillet]   = useState(null)
    const [loading, setLoading] = useState(true)
    const [erreur, setErreur]   = useState('')

    useEffect(() => {
        getBillet(uuid)
            .then(res => setBillet(res.data))
            .catch(() => setErreur('Billet introuvable.'))
            .finally(() => setLoading(false))
    }, [uuid])

    if (loading) return (
        <div className="flex flex-col items-center justify-center py-32">
            <div className="text-6xl mb-4 animate-bounce">🚄</div>
            <p className="text-gray-500 font-medium">Chargement de votre billet...</p>
        </div>
    )

    if (erreur) return (
        <div className="text-center py-20">
            <div className="text-6xl mb-4">😕</div>
            <p className="text-red-500 text-lg font-semibold">{erreur}</p>
            <Link to="/" className="mt-4 inline-block text-blue-600 hover:underline font-medium">← Retour à l'accueil</Link>
        </div>
    )

    const segs      = billet.itineraire?.segments || []
    const villeD    = segs[0]?.villeDepart?.nom
    const villeA    = segs[segs.length - 1]?.villeArrivee?.nom
    const hDep      = segs[0]?.heureDepart?.substring(0, 5)
    const hArr      = segs[segs.length - 1]?.heureArrivee?.substring(0, 5)
    const date      = segs[0]?.dateDepart
    const isDirect  = segs.length === 1

    // Expiré seulement quand le DERNIER segment est terminé
    // Le QR reste visible pendant toutes les correspondances
    const dernierSeg = segs[segs.length - 1]
    const dateHeureArrivee = dernierSeg?.dateDepart && dernierSeg?.heureArrivee
        ? new Date(`${dernierSeg.dateDepart}T${dernierSeg.heureArrivee}`)
        : null
    const estExpire = dateHeureArrivee ? new Date() > dateHeureArrivee : false

    // QR visible seulement si trajet pas encore terminé (peu importe VALIDE ou UTILISÉ)
    const qrVisible = !estExpire

    // EXPIRÉ si date passée, peu importe l'état en base
    const etatLabel = estExpire ? 'EXPIRÉ' : billet.etat

    const etatStyle = !estExpire && billet.etat === 'VALIDE' ? 'bg-green-100 text-green-700 border-green-200' :
        estExpire                             ? 'bg-gray-100 text-gray-400 border-gray-200'   :
        billet.etat === 'INVALIDE'            ? 'bg-red-100 text-red-600 border-red-200'      :
                                               'bg-gray-100 text-gray-500 border-gray-200'

    return (
        <div className="max-w-md mx-auto">
            <div className="text-center mb-6">
                <h2 className="text-3xl font-extrabold text-gray-800">🎫 Votre Billet</h2>
                <p className="text-gray-500 mt-1 text-sm">Présentez ce QR Code à l'agent de contrôle</p>
            </div>

            {/* Ticket */}
            <div className={`bg-white rounded-3xl shadow-2xl overflow-hidden border border-gray-100 ${!qrVisible ? 'opacity-75' : ''}`}>

                {/* Header bleu ou gris selon état */}
                <div className={`p-7 text-white ${qrVisible ? 'bg-gradient-to-br from-blue-900 to-blue-600' : 'bg-gradient-to-br from-gray-600 to-gray-400'}`}>
                    <div className="flex items-center justify-between mb-5">
                        <div className="flex items-center gap-2 text-white/70 text-sm font-semibold">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2"><rect x="2" y="7" width="20" height="12" rx="2"/><line x1="2" y1="12" x2="22" y2="12"/></svg>
                            Ticketeer
                        </div>
                        <span className={`px-3 py-1 rounded-full text-xs font-bold border ${etatStyle}`}>
                            {etatLabel}
                        </span>
                    </div>

                    {/* Ville départ → arrivée */}
                    <div className="flex items-center justify-between">
                        <div>
                            <div className="text-4xl font-black">{hDep}</div>
                            <div className="text-white/70 text-sm mt-1">{villeD}</div>
                        </div>
                        <div className="flex flex-col items-center px-4">
                            <span className="text-3xl">✈</span>
                            <div className={`mt-1 text-xs font-bold px-2 py-0.5 rounded-full ${isDirect ? 'bg-green-400/20 text-green-200' : 'bg-orange-400/20 text-orange-200'}`}>
                                {isDirect ? 'Direct' : `${segs.length - 1} correspondance${segs.length - 1 > 1 ? 's' : ''}`}
                            </div>
                        </div>
                        <div className="text-right">
                            <div className="text-4xl font-black">{hArr}</div>
                            <div className="text-white/70 text-sm mt-1">{villeA}</div>
                        </div>
                    </div>

                    {date && (
                        <div className="mt-4 pt-4 border-t border-white/20 text-sm">
                            <span className="text-white/70">
                                📅 {new Date(date).toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })}
                            </span>
                        </div>
                    )}
                </div>

                {/* Détail des correspondances */}
                {!isDirect && (
                    <div className="px-6 py-4 bg-orange-50 border-b border-orange-100">
                        <p className="text-xs font-bold text-orange-600 uppercase tracking-widest mb-3">🔄 Détail du trajet</p>
                        <div className="space-y-2">
                            {segs.map((seg, i) => (
                                <div key={i} className="flex items-start gap-3">
                                    <div className="flex flex-col items-center pt-1">
                                        <div className="w-2 h-2 rounded-full bg-blue-500 flex-shrink-0" />
                                        {i < segs.length - 1 && <div className="w-0.5 h-6 bg-gray-300 mt-1" />}
                                    </div>
                                    <div className="flex-1 flex items-center justify-between pb-1">
                                        <div>
                                            <span className="text-xs font-bold text-gray-700">
                                                {seg.heureDepart?.substring(0, 5)} {seg.villeDepart?.nom}
                                            </span>
                                            <span className="text-xs text-gray-400 mx-1">→</span>
                                            <span className="text-xs font-bold text-gray-700">
                                                {seg.heureArrivee?.substring(0, 5)} {seg.villeArrivee?.nom}
                                            </span>
                                        </div>
                                        <span className="text-xs bg-blue-100 text-blue-700 font-bold px-2 py-0.5 rounded-full ml-2 flex-shrink-0">
                                            {seg.train?.numero}
                                        </span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {/* Pour trajet direct : juste le numéro de train */}
                {isDirect && (
                    <div className="px-6 py-2 bg-gray-50 border-b border-gray-100 flex justify-between items-center">
                        <span className="text-xs text-gray-400 font-medium">Train</span>
                        <span className="text-xs font-bold text-blue-700">{segs[0]?.train?.numero}</span>
                    </div>
                )}

                {/* Séparateur ticket */}
                <div className="relative h-6 bg-white flex items-center">
                    <div className="absolute -left-3 w-6 h-6 bg-slate-50 rounded-full border border-gray-200" />
                    <div className="flex-1 mx-4 border-t-2 border-dashed border-gray-200" />
                    <div className="absolute -right-3 w-6 h-6 bg-slate-50 rounded-full border border-gray-200" />
                </div>

                {/* QR + infos */}
                <div className="px-8 pb-8 pt-2 flex flex-col items-center">

                    {qrVisible ? (
                        <div className="bg-white p-4 rounded-2xl shadow-inner border-2 border-blue-100 mb-5">
                            <QRCodeSVG value={billet.uuid} size={190} level="H" />
                        </div>
                    ) : (
                        <div className="bg-gray-100 p-4 rounded-2xl border-2 border-gray-200 mb-5 flex items-center justify-center"
                             style={{ width: 222, height: 222 }}>
                            <div className="text-center text-gray-400">
                                <div className="text-5xl mb-3">
                                    {billet.etat === 'UTILISE' ? '☑️' : billet.etat === 'INVALIDE' ? '❌' : '⏰'}
                                </div>
                                <p className="text-sm font-semibold">
                                    {billet.etat === 'UTILISE' ? 'Billet déjà utilisé'
                                        : billet.etat === 'INVALIDE' ? 'Billet invalide'
                                        : 'Billet expiré'}
                                </p>
                                <p className="text-xs mt-1 text-gray-300">QR Code non disponible</p>
                            </div>
                        </div>
                    )}

                    {billet.voyageur && (
                        <div className="w-full bg-blue-50 rounded-xl px-5 py-3 flex justify-between items-center text-sm mb-3">
                            <span className="text-gray-500 font-medium">Voyageur</span>
                            <span className="font-bold text-gray-800">{billet.voyageur.nom}</span>
                        </div>
                    )}

                    <div className="w-full bg-gray-50 rounded-xl px-5 py-3 text-center">
                        <p className="text-xs text-gray-400 mb-1 font-semibold uppercase tracking-widest">Référence billet</p>
                        <p className="text-xs font-mono text-gray-500 break-all">{billet.uuid}</p>
                    </div>
                </div>
            </div>

            <div className="mt-6 text-center">
                <Link to="/recherche" className="text-blue-600 hover:underline font-semibold text-sm">
                    ← Réserver un autre trajet
                </Link>
            </div>
        </div>
    )
}

export default Billet

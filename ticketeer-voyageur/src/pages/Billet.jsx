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
    const train     = segs[0]?.train?.numero

    const etatStyle = billet.etat === 'VALIDE'  ? 'bg-green-100 text-green-700 border-green-200' :
        billet.etat === 'UTILISE' ? 'bg-gray-100 text-gray-500 border-gray-200' :
            'bg-red-100 text-red-600 border-red-200'

    return (
        <div className="max-w-md mx-auto">
            <div className="text-center mb-6">
                <h2 className="text-3xl font-extrabold text-gray-800">🎫 Votre Billet</h2>
                <p className="text-gray-500 mt-1 text-sm">Présentez ce QR Code à l'agent de contrôle</p>
            </div>

            {/* Ticket */}
            <div className="bg-white rounded-3xl shadow-2xl overflow-hidden border border-gray-100 animate-fade-in-up">

                {/* Header bleu */}
                <div className="bg-gradient-to-br from-blue-900 to-blue-600 p-7 text-white">
                    <div className="flex items-center justify-between mb-5">
                        <div className="flex items-center gap-2 text-blue-200 text-sm font-semibold">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2"><rect x="2" y="7" width="20" height="12" rx="2"/><line x1="2" y1="12" x2="22" y2="12"/></svg>
                            Ticketeer
                        </div>
                        <span className={`px-3 py-1 rounded-full text-xs font-bold border ${etatStyle}`}>
                            {billet.etat}
                        </span>
                    </div>

                    <div className="flex items-center justify-between">
                        <div>
                            <div className="text-4xl font-black">{hDep}</div>
                            <div className="text-blue-200 text-sm mt-1">{villeD}</div>
                        </div>
                        <div className="flex flex-col items-center px-4">
                            <span className="text-3xl">✈</span>
                            <div className={`mt-1 text-xs font-bold px-2 py-0.5 rounded-full ${isDirect ? 'bg-green-400/20 text-green-200' : 'bg-orange-400/20 text-orange-200'}`}>
                                {isDirect ? 'Direct' : `${segs.length} correspondances`}
                            </div>
                        </div>
                        <div className="text-right">
                            <div className="text-4xl font-black">{hArr}</div>
                            <div className="text-blue-200 text-sm mt-1">{villeA}</div>
                        </div>
                    </div>

                    {date && (
                        <div className="mt-4 pt-4 border-t border-blue-400/30 flex items-center justify-between text-sm">
                            <span className="text-blue-200">
                                📅 {new Date(date).toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })}
                            </span>
                            <span className="text-blue-300 text-xs">{train}</span>
                        </div>
                    )}
                </div>

                {/* Séparateur ticket */}
                <div className="relative h-6 bg-white flex items-center">
                    <div className="absolute -left-3 w-6 h-6 bg-slate-50 rounded-full border border-gray-200" />
                    <div className="flex-1 mx-4 border-t-2 border-dashed border-gray-200" />
                    <div className="absolute -right-3 w-6 h-6 bg-slate-50 rounded-full border border-gray-200" />
                </div>

                {/* QR + infos */}
                <div className="px-8 pb-8 pt-2 flex flex-col items-center">
                    <div className="bg-white p-4 rounded-2xl shadow-inner border-2 border-blue-100 mb-5">
                        <QRCodeSVG value={billet.uuid} size={190} level="H" />
                    </div>

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
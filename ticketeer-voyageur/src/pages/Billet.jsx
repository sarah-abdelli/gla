import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { QRCodeSVG } from 'qrcode.react'
import { getBillet } from '../api/api'

function Billet() {
    const { uuid } = useParams()
    const [billet, setBillet] = useState(null)
    const [loading, setLoading] = useState(true)
    const [erreur, setErreur] = useState('')

    useEffect(() => {
        const charger = async () => {
            try {
                const res = await getBillet(uuid)
                setBillet(res.data)
            } catch (err) {
                setErreur('Billet introuvable.')
            } finally {
                setLoading(false)
            }
        }
        charger()
    }, [uuid])

    if (loading) return <div className="text-center py-20 text-gray-500">Chargement...</div>
    if (erreur) return <div className="text-center py-20 text-red-500">{erreur}</div>

    const villeDepart = billet.itineraire?.segments[0]?.villeDepart?.nom
    const villeArrivee = billet.itineraire?.segments[billet.itineraire.segments.length - 1]?.villeArrivee?.nom
    const heureDepart = billet.itineraire?.segments[0]?.heureDepart?.substring(0, 5)
    const heureArrivee = billet.itineraire?.segments[billet.itineraire.segments.length - 1]?.heureArrivee?.substring(0, 5)

    return (
        <div className="max-w-md mx-auto">
            <h2 className="text-2xl font-bold text-blue-700 mb-6 text-center">🎫 Votre Billet</h2>

            <div className="bg-white rounded-2xl shadow-lg p-8">
                {/* Infos trajet */}
                <div className="text-center mb-6">
                    <p className="text-2xl font-bold text-gray-800">
                        {villeDepart} → {villeArrivee}
                    </p>
                    <p className="text-gray-500 mt-1">{heureDepart} → {heureArrivee}</p>
                    <span className={`inline-block mt-2 px-3 py-1 rounded-full text-sm font-semibold ${
                        billet.etat === 'VALIDE' ? 'bg-green-100 text-green-700' :
                            billet.etat === 'UTILISE' ? 'bg-gray-100 text-gray-600' :
                                'bg-red-100 text-red-600'
                    }`}>
            {billet.etat}
          </span>
                </div>

                {/* QR Code */}
                <div className="flex justify-center mb-6">
                    <div className="p-4 border-2 border-blue-200 rounded-xl">
                        <QRCodeSVG value={billet.uuid} size={200} />
                    </div>
                </div>

                {/* UUID */}
                <div className="bg-gray-50 rounded-lg p-3 text-center">
                    <p className="text-xs text-gray-400 mb-1">Référence billet</p>
                    <p className="text-xs font-mono text-gray-600 break-all">{billet.uuid}</p>
                </div>
            </div>
        </div>
    )
}

export default Billet
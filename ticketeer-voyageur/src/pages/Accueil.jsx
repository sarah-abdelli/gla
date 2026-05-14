import { Link } from 'react-router-dom'
import { useState, useEffect } from 'react'
import { getVilles, getTrains } from '../api/api'

const features = [
    { icon: '🔍', title: 'Recherchez', desc: 'Trouvez le trajet idéal entre les grandes villes de France.' },
    { icon: '💳', title: 'Réservez',   desc: 'Paiement simulé sécurisé. Billet généré instantanément.' },
    { icon: '📱', title: 'Voyagez',    desc: "Présentez votre QR Code à l'agent de contrôle à bord." }
]

function Accueil() {
    const [villes, setVilles]   = useState([])
    const [nbTrains, setNbTrains] = useState(0)

    useEffect(() => {
        getVilles()
            .then(res => setVilles(res.data.map(v => v.nom).sort()))
            .catch(() => setVilles(['Paris', 'Lyon', 'Marseille', 'Bordeaux', 'Lille',
                'Strasbourg', 'Nantes', 'Toulouse', 'Nice', 'Montpellier']))

        getTrains()
            .then(res => setNbTrains(res.data.length))
            .catch(() => setNbTrains(0))
    }, [])

    return (
        <div>
            {/* Hero */}
            <div className="bg-gradient-to-br from-blue-950 via-blue-800 to-blue-600 text-white">
                <div className="container mx-auto px-6 py-24 flex flex-col items-center text-center">
                    <div className="text-8xl mb-6 animate-bounce">🚄</div>
                    <h1 className="text-5xl font-extrabold mb-4 tracking-tight leading-tight">
                        Voyagez en France<br/>
                        <span className="text-yellow-300">simplement et vite.</span>
                    </h1>
                    <p className="text-blue-100 text-xl mb-10 max-w-lg">
                        Recherchez votre trajet, achetez votre billet et voyagez avec votre QR Code.
                    </p>
                    <Link
                        to="/recherche"
                        className="bg-yellow-400 hover:bg-yellow-300 text-blue-900 px-10 py-4 rounded-2xl text-xl font-black shadow-2xl transition-all hover:scale-105 flex items-center gap-3"
                    >
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                            <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
                        </svg>
                        Rechercher un trajet
                    </Link>
                </div>
            </div>

            {/* Stats dynamiques */}
            <div className="bg-white border-b border-gray-100 shadow-sm">
                <div className="container mx-auto px-6 py-8 grid grid-cols-3 gap-6 max-w-2xl">
                    {[
                        { val: villes.length || '—', label: 'Villes desservies' },
                        { val: nbTrains || '—',       label: 'Trains disponibles' },
                        { val: '24/7',                label: 'Service disponible' },
                    ].map(s => (
                        <div key={s.label} className="text-center">
                            <div className="text-4xl font-black text-blue-700">{s.val}</div>
                            <div className="text-gray-500 text-sm mt-1">{s.label}</div>
                        </div>
                    ))}
                </div>
            </div>

            {/* How it works */}
            <div className="container mx-auto px-6 py-16 max-w-5xl">
                <h2 className="text-3xl font-extrabold text-center text-gray-800 mb-12">Comment ça marche ?</h2>
                <div className="grid md:grid-cols-3 gap-6">
                    {features.map((f, i) => (
                        <div key={i}
                             className="bg-white rounded-2xl shadow-md p-8 text-center hover:shadow-xl transition-all border border-gray-100 animate-fade-in-up"
                             style={{ animationDelay: `${i * 0.1}s` }}>
                            <div className="text-5xl mb-4">{f.icon}</div>
                            <div className="w-8 h-8 bg-blue-700 text-white rounded-full flex items-center justify-center font-black text-sm mx-auto mb-3">{i + 1}</div>
                            <h3 className="text-lg font-bold text-gray-800 mb-2">{f.title}</h3>
                            <p className="text-gray-500 text-sm leading-relaxed">{f.desc}</p>
                        </div>
                    ))}
                </div>
            </div>

            {/* Villes dynamiques */}
            <div className="bg-gradient-to-br from-slate-100 to-blue-50 py-14">
                <div className="container mx-auto px-6 max-w-4xl">
                    <h2 className="text-2xl font-extrabold text-center text-gray-800 mb-8">
                        Villes desservies
                    </h2>
                    <div className="flex flex-wrap justify-center gap-3">
                        {villes.length === 0 ? (
                            <p className="text-gray-400 text-sm">Chargement des villes...</p>
                        ) : (
                            villes.map(v => (
                                <Link key={v} to="/recherche"
                                      className="bg-white border border-blue-200 text-blue-700 px-5 py-2 rounded-full font-semibold hover:bg-blue-700 hover:text-white transition-all shadow-sm text-sm flex items-center gap-1.5"
                                >
                                    <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
                                        <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
                                    </svg>
                                    {v}
                                </Link>
                            ))
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Accueil
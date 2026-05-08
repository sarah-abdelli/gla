import { Link, useLocation } from 'react-router-dom'

function Navbar() {
    const location = useLocation()
    const isActive = (path) => location.pathname === path

    return (
        <nav className="bg-gradient-to-r from-blue-900 to-blue-700 text-white shadow-xl">
            <div className="container mx-auto px-6 flex items-center justify-between">
                <Link to="/" className="flex items-center gap-3 py-4">
                    <div className="bg-white/15 rounded-xl p-2">
                        <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <rect x="2" y="7" width="20" height="12" rx="2"/>
                            <path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/>
                            <line x1="2" y1="12" x2="22" y2="12"/>
                            <path d="M6 19v1a1 1 0 0 0 1 1h1a1 1 0 0 0 1-1v-1"/>
                            <path d="M15 19v1a1 1 0 0 0 1 1h1a1 1 0 0 0 1-1v-1"/>
                        </svg>
                    </div>
                    <div>
                        <div className="text-xl font-extrabold tracking-wide leading-none">Ticketeer</div>
                        <div className="text-blue-200 text-xs mt-0.5">Votre billet, votre voyage</div>
                    </div>
                </Link>

                <div className="flex items-center gap-2">
                    <Link to="/" className={`px-5 py-2 rounded-xl font-semibold text-sm transition-all ${isActive('/') ? 'bg-white text-blue-800 shadow' : 'text-white hover:bg-white/10'}`}>
                        Accueil
                    </Link>
                    <Link to="/recherche" className={`px-5 py-2 rounded-xl font-semibold text-sm transition-all flex items-center gap-1.5 ${isActive('/recherche') ? 'bg-white text-blue-800 shadow' : 'text-white hover:bg-white/10'}`}>
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                        Rechercher
                    </Link>
                </div>
            </div>
        </nav>
    )
}

export default Navbar
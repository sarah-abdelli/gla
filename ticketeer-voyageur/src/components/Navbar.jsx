import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function Navbar() {
    const location = useLocation()
    const navigate = useNavigate()
    const { user, logout } = useAuth()
    const isActive = (path) => location.pathname === path

    const handleLogout = () => {
        logout()
        navigate('/')
    }

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
                    <Link to="/" className={`px-4 py-2 rounded-xl font-semibold text-sm transition-all ${isActive('/') ? 'bg-white text-blue-800 shadow' : 'text-white hover:bg-white/10'}`}>
                        Accueil
                    </Link>
                    <Link to="/recherche" className={`px-4 py-2 rounded-xl font-semibold text-sm transition-all flex items-center gap-1.5 ${isActive('/recherche') ? 'bg-white text-blue-800 shadow' : 'text-white hover:bg-white/10'}`}>
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                        Rechercher
                    </Link>

                    {user ? (
                        <>
                            <Link to="/mes-billets" className={`px-4 py-2 rounded-xl font-semibold text-sm transition-all flex items-center gap-1.5 ${isActive('/mes-billets') ? 'bg-white text-blue-800 shadow' : 'text-white hover:bg-white/10'}`}>
                                🎫 Mes billets
                            </Link>
                            <div className="flex items-center gap-2 ml-2 pl-2 border-l border-blue-500">
                                <div className="bg-white/20 rounded-xl px-3 py-1.5 flex items-center gap-2">
                                    <div className="w-6 h-6 bg-yellow-400 rounded-full flex items-center justify-center text-blue-900 font-black text-xs">
                                        {user.nom.charAt(0).toUpperCase()}
                                    </div>
                                    <span className="text-sm font-semibold text-white">{user.nom.split(' ')[0]}</span>
                                </div>
                                <button onClick={handleLogout}
                                        className="text-blue-200 hover:text-white hover:bg-white/10 px-3 py-1.5 rounded-xl text-sm font-semibold transition-all">
                                    Déconnexion
                                </button>
                            </div>
                        </>
                    ) : (
                        <Link to="/login" className={`px-4 py-2 rounded-xl font-semibold text-sm transition-all flex items-center gap-1.5 ${isActive('/login') ? 'bg-white text-blue-800 shadow' : 'bg-yellow-400 text-blue-900 hover:bg-yellow-300'}`}>
                            🔐 Connexion
                        </Link>
                    )}
                </div>
            </div>
        </nav>
    )
}

export default Navbar
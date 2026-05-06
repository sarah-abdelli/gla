import { Link } from 'react-router-dom'

function Navbar() {
    return (
        <nav className="bg-blue-700 text-white shadow-md">
            <div className="container mx-auto px-4 py-4 flex items-center justify-between">
                <Link to="/" className="text-2xl font-bold tracking-wide">
                    🚆 Ticketeer
                </Link>
                <div className="flex gap-6">
                    <Link to="/" className="hover:text-blue-200 transition">Accueil</Link>
                    <Link to="/recherche" className="hover:text-blue-200 transition">Rechercher</Link>
                </div>
            </div>
        </nav>
    )
}

export default Navbar
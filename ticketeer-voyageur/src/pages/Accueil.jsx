import { Link } from 'react-router-dom'

function Accueil() {
    return (
        <div className="flex flex-col items-center justify-center py-20">
            <h1 className="text-4xl font-bold text-blue-700 mb-4">
                Bienvenue sur Ticketeer 🚆
            </h1>
            <p className="text-gray-600 text-lg mb-8 text-center max-w-md">
                Recherchez votre trajet, achetez votre billet et voyagez avec votre QR Code.
            </p>
            <Link
                to="/recherche"
                className="bg-blue-700 text-white px-8 py-3 rounded-lg text-lg font-semibold hover:bg-blue-800 transition"
            >
                Rechercher un trajet
            </Link>
        </div>
    )
}

export default Accueil
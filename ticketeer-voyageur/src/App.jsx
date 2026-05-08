import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Accueil from './pages/Accueil'
import Recherche from './pages/Recherche'
import Billet from './pages/Billet'
import Paiement from './pages/Paiement'

function App() {
    return (
        <div className="min-h-screen bg-slate-50">
            <Navbar />
            <Routes>
                <Route path="/" element={<Accueil />} />
                <Route path="/recherche" element={
                    <div className="container mx-auto px-4 py-10 max-w-4xl">
                        <Recherche />
                    </div>
                } />
                <Route path="/paiement" element={
                    <div className="container mx-auto px-4 py-10 max-w-2xl">
                        <Paiement />
                    </div>
                } />
                <Route path="/billet/:uuid" element={
                    <div className="container mx-auto px-4 py-10">
                        <Billet />
                    </div>
                } />
            </Routes>
        </div>
    )
}

export default App
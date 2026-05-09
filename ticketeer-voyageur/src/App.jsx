import { Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import Navbar from './components/Navbar'
import ProtectedRoute from './components/ProtectedRoute'
import Accueil from './pages/Accueil'
import Recherche from './pages/Recherche'
import Billet from './pages/Billet'
import Paiement from './pages/Paiement'
import Login from './pages/Login'
import MesBillets from './pages/MesBillets'

function App() {
    return (
        <AuthProvider>
            <div className="min-h-screen bg-slate-50">
                <Navbar />
                <Routes>
                    <Route path="/" element={<Accueil />} />
                    <Route path="/login" element={<Login />} />
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
                    <Route path="/mes-billets" element={
                        <ProtectedRoute>
                            <div className="container mx-auto px-4 py-10 max-w-4xl">
                                <MesBillets />
                            </div>
                        </ProtectedRoute>
                    } />
                </Routes>
            </div>
        </AuthProvider>
    )
}

export default App
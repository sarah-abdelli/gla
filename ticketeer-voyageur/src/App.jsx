import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Accueil from './pages/Accueil'
import Recherche from './pages/Recherche'
import Billet from './pages/Billet'
import Paiement from './pages/Paiement'

function App() {
  return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="container mx-auto px-4 py-8">
          <Routes>
            <Route path="/" element={<Accueil />} />
            <Route path="/recherche" element={<Recherche />} />
            <Route path="/paiement" element={<Paiement />} />
            <Route path="/billet/:uuid" element={<Billet />} />
          </Routes>
        </div>
      </div>
  )
}

export default App
import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { loginVoyageur } from '../api/api'
import { useAuth } from '../context/AuthContext'

function Login() {
    const [email, setEmail]     = useState('')
    const [mdp, setMdp]         = useState('')
    const [loading, setLoading] = useState(false)
    const [erreur, setErreur]   = useState('')
    const { login }             = useAuth()
    const navigate              = useNavigate()

    const handleSubmit = async (e) => {
        e.preventDefault()
        if (!email || !mdp) { setErreur('Veuillez remplir tous les champs.'); return }
        setErreur('')
        setLoading(true)
        try {
            const res = await loginVoyageur(email, mdp)
            login(res.data)
            navigate('/')
        } catch {
            setErreur('Email ou mot de passe incorrect.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="min-h-[80vh] flex items-center justify-center px-4">
            <div className="w-full max-w-md">

                <div className="text-center mb-8">
                    <div className="text-6xl mb-4">🚄</div>
                    <h2 className="text-3xl font-extrabold text-gray-800">Connexion</h2>
                    <p className="text-gray-500 text-sm mt-2">Accédez à vos billets et réservations</p>
                </div>

                <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div>
                            <label className="block text-sm font-semibold text-gray-600 mb-2">Adresse email</label>
                            <input
                                type="email"
                                value={email}
                                onChange={e => setEmail(e.target.value)}
                                placeholder="alice@test.com"
                                className="w-full border-2 border-gray-200 rounded-xl px-4 py-3 focus:outline-none focus:border-blue-500 bg-gray-50 transition-colors"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-gray-600 mb-2">Mot de passe</label>
                            <input
                                type="password"
                                value={mdp}
                                onChange={e => setMdp(e.target.value)}
                                placeholder="••••••••"
                                className="w-full border-2 border-gray-200 rounded-xl px-4 py-3 focus:outline-none focus:border-blue-500 bg-gray-50 transition-colors"
                            />
                        </div>

                        {erreur && (
                            <div className="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-xl text-sm flex items-center gap-2">
                                ⚠️ {erreur}
                            </div>
                        )}

                        <button type="submit" disabled={loading}
                                className="w-full bg-blue-700 hover:bg-blue-800 text-white py-3.5 rounded-xl font-black text-base shadow-lg transition-all hover:scale-[1.01] disabled:opacity-50 flex items-center justify-center gap-2 mt-2">
                            {loading
                                ? <><span className="animate-spin">⏳</span> Connexion...</>
                                : <>🔐 Se connecter</>
                            }
                        </button>
                    </form>

                    <div className="mt-6 pt-6 border-t border-gray-100">
                        <p className="text-xs text-gray-400 text-center font-semibold uppercase tracking-widest mb-3">Comptes de test</p>
                        <div className="grid grid-cols-2 gap-2 text-xs">
                            {[
                                { email: 'alice@test.com',  mdp: 'pass123' },
                                { email: 'bob@test.com',    mdp: 'pass123' },
                                { email: 'clara@test.com',  mdp: 'pass123' },
                                { email: 'david@test.com',  mdp: 'pass123' },
                            ].map(c => (
                                <button key={c.email}
                                        onClick={() => { setEmail(c.email); setMdp(c.mdp) }}
                                        className="bg-blue-50 hover:bg-blue-100 text-blue-700 rounded-lg px-3 py-2 text-left transition-colors">
                                    <div className="font-bold">{c.email.split('@')[0]}</div>
                                    <div className="text-blue-400">{c.email}</div>
                                </button>
                            ))}
                        </div>
                    </div>
                </div>

                <div className="mt-4 text-center">
                    <Link to="/" className="text-blue-600 hover:underline text-sm font-medium">
                        ← Retour à l'accueil
                    </Link>
                </div>
            </div>
        </div>
    )
}

export default Login
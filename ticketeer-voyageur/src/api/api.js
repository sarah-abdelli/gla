import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
})

export const rechercherItineraires = (depart, arrivee, date) =>
    api.get(`/itineraires/search?depart=${depart}&arrivee=${arrivee}&date=${date}`)

export const creerBillet = (voyageurId, itineraireId) =>
    api.post('/billets/create', { voyageurId, itineraireId })

export const getBillet = (uuid) =>
    api.get(`/billets/${uuid}`)

export const getMesBillets = (voyageurId) =>
    api.get(`/billets/voyageur/${voyageurId}`)

export const loginVoyageur = (email, motDePasse) =>
    api.post('/auth/login/voyageur', { email, motDePasse })

export const getVilles = () =>
    api.get('/admin/villes')

export const getTrains = () =>
    api.get('/admin/trains')

export default api
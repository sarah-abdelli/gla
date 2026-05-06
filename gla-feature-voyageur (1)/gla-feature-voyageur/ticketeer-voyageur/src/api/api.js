import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
})

export const rechercherItineraires = (depart, arrivee) =>
    api.get(`/itineraires/search?depart=${depart}&arrivee=${arrivee}`)

export const creerBillet = (voyageurId, itineraireId) =>
    api.post('/billets/create', { voyageurId, itineraireId })

export const getBillet = (uuid) =>
    api.get(`/billets/${uuid}`)

export default api
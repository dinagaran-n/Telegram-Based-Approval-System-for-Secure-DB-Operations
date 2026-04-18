import axios from 'axios';

const API = import.meta.env.VITE_API_URL;

const api = axios.create({
  baseURL: API,
  headers: {
    'Content-Type': 'application/json'
  }
});

export const getRequests = () => api.get('/requests').then(res => res.data);
export const getUsers = () => api.get('/users').then(res => res.data);
export const createUser = (data) => axios.post(`${API}/users`, { name: data.name, email: data.email }, { headers: { 'Content-Type': 'application/json' } }).then(res => res.data);
export const updateUser = (id, data) => api.put(`/users/${id}`, data).then(res => res.data);
export const deleteUser = (id) => api.delete(`/users/${id}`).then(res => res.data);

export default api;

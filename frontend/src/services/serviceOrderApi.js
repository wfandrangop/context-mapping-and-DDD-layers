import axios from 'axios';

const BASE = 'http://localhost:8080/api/service-orders';

export const createOrder = (clientId, workerId) =>
  axios.post(BASE, { clientId, workerId }).then((r) => r.data);

export const getOrder = (id) => axios.get(`${BASE}/${id}`).then((r) => r.data);

export const beginOrder = (id) => axios.put(`${BASE}/${id}/begin`).then((r) => r.data);

export const addPhoto = (id, file) => {
  const form = new FormData();
  form.append('file', file);
  return axios.post(`${BASE}/${id}/photos`, form).then((r) => r.data);
};

export const completeOrder = (id) => axios.put(`${BASE}/${id}/complete`).then((r) => r.data);

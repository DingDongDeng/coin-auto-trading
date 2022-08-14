function api() {
  const api = axios.create({
    timeout: 5000,
  });
  api.interceptors.response.use((response) => response, (error) => {
    alert(error);
    throw error;
  });
  return api;
}

export default {
  axios: api()
}
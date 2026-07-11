import axios from "axios";

export const authApi = axios.create({
  baseURL: "http://localhost:8080",
});

export const walletApi = axios.create({
  baseURL: "http://localhost:8080",
});

walletApi.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem("accessToken");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

walletApi.interceptors.response.use(
  (response) => response,

  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const response = await authApi.post("/api/auth/refreshToken", {
          accessToken: sessionStorage.getItem("accessToken"),

          refreshToken: sessionStorage.getItem("refreshToken"),
        });

        const newAccessToken = response.data.data.accessToken;

        sessionStorage.setItem("accessToken", newAccessToken);

        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

        return walletApi(originalRequest);
      } catch (err) {
        sessionStorage.clear();

        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  },
);

import { authApi } from "./axiosInstance";

export const signup = (payload) => {
  return authApi.post("/api/auth/signup", payload);
};

export const login = (payload) => {
  return authApi.post("/api/auth/login", payload);
};

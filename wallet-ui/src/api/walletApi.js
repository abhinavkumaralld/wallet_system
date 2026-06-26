import { walletApi } from "./axiosInstance";

export const getWallet = () => walletApi.get("/api/wallet");

export const getTransactions = () => walletApi.get("/api/wallet/transactions");

export const deposit = (payload) =>
  walletApi.post("/api/wallet/deposit", payload);

export const withdraw = (payload) =>
  walletApi.post("/api/wallet/withdraw", payload);

export const transfer = (payload) =>
  walletApi.post("/api/wallet/transfer", payload);

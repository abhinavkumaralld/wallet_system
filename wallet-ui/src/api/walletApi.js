import { walletApi } from "./axiosInstance";

export const getWallet = () => walletApi.get("/api/wallet");

export const createWallet = () => walletApi.post("/api/wallet/create");

export const getTransactions = (page, size) => {
  return walletApi.get("/api/wallet/transactions", {
    params: {
      page,
      size,
      sortBy: "createdAt",
    },
  });
};

export const deposit = (payload) =>
  walletApi.post("/api/wallet/deposit", payload);

export const withdraw = (payload) =>
  walletApi.post("/api/wallet/withdraw", payload);

export const transfer = (payload) =>
  walletApi.post("/api/wallet/transfer", payload);

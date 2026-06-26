import { createContext, useContext, useState } from "react";
import { useNavigate } from "react-router-dom";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const navigate = useNavigate();

  const [accessToken, setAccessToken] = useState(
    sessionStorage.getItem("accessToken"),
  );

  const [refreshToken, setRefreshToken] = useState(
    sessionStorage.getItem("refreshToken"),
  );

  const login = (accessToken, refreshToken) => {
    sessionStorage.setItem("accessToken", accessToken);

    sessionStorage.setItem("refreshToken", refreshToken);

    setAccessToken(accessToken);
    setRefreshToken(refreshToken);
  };

  const logout = () => {
    sessionStorage.clear();

    setAccessToken(null);
    setRefreshToken(null);

    navigate("/login");
  };

  return (
    <AuthContext.Provider
      value={{
        accessToken,
        refreshToken,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);

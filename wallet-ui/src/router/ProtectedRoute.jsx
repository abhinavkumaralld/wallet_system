import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ children }) => {
  const token = sessionStorage.getItem("accessToken");

  if (!token) {
    return <Navigate to="/login" />;
  }

  return children;
};

export default ProtectedRoute;

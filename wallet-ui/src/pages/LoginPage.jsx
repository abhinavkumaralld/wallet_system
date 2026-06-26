import {
  Box,
  Button,
  Container,
  Paper,
  TextField,
  Typography,
} from "@mui/material";

import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { login } from "../api/authApi";
import { useAuth } from "../context/AuthContext";

const LoginPage = () => {
  const navigate = useNavigate();

  const { login: saveTokens } = useAuth();

  const [form, setForm] = useState({
    email: "",
    password: "",
  });

  const handleLogin = async () => {
    try {
      const response = await login(form);

      saveTokens(
        response.data.data.accessToken,
        response.data.data.refreshToken,
      );

      navigate("/");
    } catch (err) {
      alert(err.response?.data?.message || "Login failed");
    }
  };

  return (
    <Container maxWidth="sm">
      <Paper sx={{ p: 4, mt: 10 }}>
        <Typography variant="h4" gutterBottom>
          Login
        </Typography>

        <TextField
          fullWidth
          margin="normal"
          label="Email"
          value={form.email}
          onChange={(e) =>
            setForm({
              ...form,
              email: e.target.value,
            })
          }
        />

        <TextField
          fullWidth
          type="password"
          margin="normal"
          label="Password"
          value={form.password}
          onChange={(e) =>
            setForm({
              ...form,
              password: e.target.value,
            })
          }
        />

        <Button
          fullWidth
          variant="contained"
          sx={{ mt: 2 }}
          onClick={handleLogin}
        >
          Login
        </Button>

        <Box mt={2}>
          <Link to="/signup">Create Account</Link>
        </Box>
      </Paper>
    </Container>
  );
};

export default LoginPage;

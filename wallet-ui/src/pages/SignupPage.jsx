import { Button, Container, Paper, TextField, Typography } from "@mui/material";

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { signup } from "../api/authApi";

const SignupPage = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
  });

  const handleSignup = async () => {
    try {
      await signup(form);

      alert("Signup successful. Please login.");

      navigate("/login");
    } catch (err) {
      alert(err.response?.data?.message || "Signup failed");
    }
  };

  return (
    <Container maxWidth="sm">
      <Paper sx={{ p: 4, mt: 10 }}>
        <Typography variant="h4" gutterBottom>
          Signup
        </Typography>

        <TextField
          fullWidth
          margin="normal"
          label="Name"
          value={form.name}
          onChange={(e) =>
            setForm({
              ...form,
              name: e.target.value,
            })
          }
        />

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
          onClick={handleSignup}
        >
          Signup
        </Button>
      </Paper>
    </Container>
  );
};

export default SignupPage;

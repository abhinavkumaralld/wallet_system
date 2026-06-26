import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from "@mui/material";

import { useState } from "react";
import { deposit } from "../api/walletApi";

const DepositDialog = ({ open, onClose, onSuccess }) => {
  const [amount, setAmount] = useState("");

  const handleSubmit = async () => {
    await deposit({
      amount: Number(amount),
      referenceId: "DEP-" + Date.now(),
      description: "Wallet Deposit",
    });

    onSuccess();
    onClose();
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Deposit Money</DialogTitle>

      <DialogContent>
        <TextField
          fullWidth
          margin="normal"
          label="Amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>

        <Button variant="contained" onClick={handleSubmit}>
          Deposit
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default DepositDialog;

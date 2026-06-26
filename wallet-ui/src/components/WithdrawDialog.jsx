import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from "@mui/material";

import { useState } from "react";
import { withdraw } from "../api/walletApi";

const WithdrawDialog = ({ open, onClose, onSuccess }) => {
  const [amount, setAmount] = useState("");

  const handleSubmit = async () => {
    await withdraw({
      amount: Number(amount),
      referenceId: "WTH-" + Date.now(),
      description: "Wallet Withdraw",
    });

    onSuccess();
    onClose();
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Withdraw Money</DialogTitle>

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
          Withdraw
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default WithdrawDialog;

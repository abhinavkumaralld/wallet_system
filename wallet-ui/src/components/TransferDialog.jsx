import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from "@mui/material";

import { useState } from "react";
import { transfer } from "../api/walletApi";

const TransferDialog = ({ open, onClose, onSuccess }) => {
  const [amount, setAmount] = useState("");

  const [receiverUserId, setReceiverUserId] = useState("");

  const handleSubmit = async () => {
    await transfer({
      amount: Number(amount),
      receiverUserId: Number(receiverUserId),
      referenceId: "TRF-" + Date.now(),
      description: "Wallet Transfer",
    });

    onSuccess();
    onClose();
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Transfer Money</DialogTitle>

      <DialogContent>
        <TextField
          fullWidth
          margin="normal"
          label="Receiver User Id"
          value={receiverUserId}
          onChange={(e) => setReceiverUserId(e.target.value)}
        />

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
          Transfer
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default TransferDialog;

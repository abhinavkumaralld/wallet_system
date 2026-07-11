import { Box, Button, Grid, Typography } from "@mui/material";

import { useEffect, useState } from "react";

import { createWallet, getWallet, getTransactions } from "../api/walletApi";

import WalletCard from "../components/WalletCard";
import DepositDialog from "../components/DepositDialog";
import WithdrawDialog from "../components/WithdrawDialog";
import TransferDialog from "../components/TransferDialog";
import TransactionGrid from "../components/TransactionGrid";

import { useAuth } from "../context/AuthContext";

const DashboardPage = () => {
  const [wallet, setWallet] = useState(null);

  const [transactions, setTransactions] = useState([]);

  const [depositOpen, setDepositOpen] = useState(false);

  const [withdrawOpen, setWithdrawOpen] = useState(false);

  const [transferOpen, setTransferOpen] = useState(false);

  const [walletError, setWalletError] = useState("");

  const [creatingWallet, setCreatingWallet] = useState(false);

  const { logout } = useAuth();

  const loadData = async () => {
    try {
      const walletResponse = await getWallet();

      const transactionResponse = await getTransactions();

      setWallet(walletResponse.data.data);

      setTransactions(
        transactionResponse.data.data.map((v, i) => ({ ...v, id: i })),
      );

      setWalletError("");
    } catch (err) {
      const status = err.response?.status;
      const message = err.response?.data?.message || err.message;

      if (status === 400 && /wallet not exist/i.test(message)) {
        setWallet(null);
        setTransactions([]);
        setWalletError("No wallet exists yet. Create one to continue.");
      } else {
        console.error(err);
        setWalletError("Unable to load wallet data right now.");
      }
    }
  };

  const handleCreateWallet = async () => {
    try {
      setCreatingWallet(true);
      setWalletError("");

      await createWallet();

      await loadData();
    } catch (err) {
      console.error(err);
      setWalletError("Wallet creation failed. Please try again.");
    } finally {
      setCreatingWallet(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  return (
    <Box p={3}>
      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        mb={3}
      >
        <Typography variant="h4">Wallet Dashboard</Typography>

        <Button
          variant="contained"
          color="error"
          onClick={logout}
          sx={{ px: 2.5, py: 1.2, ml: 2 }}
        >
          Logout
        </Button>
      </Box>

      <WalletCard wallet={wallet} />

      {walletError && (
        <Typography color="error" mt={2}>
          {walletError}
        </Typography>
      )}

      {!wallet && (
        <Button
          variant="contained"
          color="primary"
          onClick={handleCreateWallet}
          disabled={creatingWallet}
          sx={{ mt: 2, px: 2.5, py: 1.2 }}
        >
          {creatingWallet ? "Creating Wallet..." : "Create Wallet"}
        </Button>
      )}

      <Grid container spacing={2} mt={2}>
        <Grid item>
          <Button
            variant="contained"
            onClick={() => setDepositOpen(true)}
            sx={{ px: 2.5, py: 1.2 }}
          >
            Deposit
          </Button>
        </Grid>

        <Grid item>
          <Button
            variant="contained"
            onClick={() => setWithdrawOpen(true)}
            sx={{ px: 2.5, py: 1.2 }}
          >
            Withdraw
          </Button>
        </Grid>

        <Grid item>
          <Button
            variant="contained"
            onClick={() => setTransferOpen(true)}
            sx={{ px: 2.5, py: 1.2 }}
          >
            Transfer
          </Button>
        </Grid>
      </Grid>

      <Box mt={4}>
        <Typography variant="h5" gutterBottom>
          Transactions
        </Typography>

        <TransactionGrid rows={transactions} />
      </Box>

      <DepositDialog
        open={depositOpen}
        onClose={() => setDepositOpen(false)}
        onSuccess={loadData}
      />

      <WithdrawDialog
        open={withdrawOpen}
        onClose={() => setWithdrawOpen(false)}
        onSuccess={loadData}
      />

      <TransferDialog
        open={transferOpen}
        onClose={() => setTransferOpen(false)}
        onSuccess={loadData}
      />
    </Box>
  );
};

export default DashboardPage;

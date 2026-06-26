import { Box, Button, Grid, Typography } from "@mui/material";

import { useEffect, useState } from "react";

import { getWallet, getTransactions } from "../api/walletApi";

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

  const { logout } = useAuth();

  const loadData = async () => {
    try {
      const walletResponse = await getWallet();

      const transactionResponse = await getTransactions();

      setWallet(walletResponse.data.data);

      setTransactions(
        transactionResponse.data.data.map((v, i) => ({ ...v, id: i })),
      );
    } catch (err) {
      console.error(err);
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

        <Button variant="contained" color="error" onClick={logout}>
          Logout
        </Button>
      </Box>

      <WalletCard wallet={wallet} />

      <Grid container spacing={2} mt={2}>
        <Grid item>
          <Button variant="contained" onClick={() => setDepositOpen(true)}>
            Deposit
          </Button>
        </Grid>

        <Grid item>
          <Button variant="contained" onClick={() => setWithdrawOpen(true)}>
            Withdraw
          </Button>
        </Grid>

        <Grid item>
          <Button variant="contained" onClick={() => setTransferOpen(true)}>
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

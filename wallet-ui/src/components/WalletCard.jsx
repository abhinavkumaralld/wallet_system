import { Card, CardContent, Typography } from "@mui/material";

const WalletCard = ({ wallet }) => {
  return (
    <Card>
      <CardContent>
        <Typography variant="h6">Wallet Balance</Typography>

        {wallet ? (
          <>
            <Typography variant="h3">₹ {wallet.balance || 0}</Typography>
            <Typography color="text.secondary">
              Wallet Id: {wallet.id}
            </Typography>
          </>
        ) : (
          <Typography color="text.secondary" mt={1}>
            No wallet exists yet. Create one to get started.
          </Typography>
        )}
      </CardContent>
    </Card>
  );
};

export default WalletCard;

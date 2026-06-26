import { Card, CardContent, Typography } from "@mui/material";

const WalletCard = ({ wallet }) => {
  return (
    <Card>
      <CardContent>
        <Typography variant="h6">Wallet Balance</Typography>

        <Typography variant="h3">₹ {wallet?.balance || 0}</Typography>

        <Typography color="text.secondary">Wallet Id: {wallet?.id}</Typography>
      </CardContent>
    </Card>
  );
};

export default WalletCard;

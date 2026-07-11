import { DataGrid } from "@mui/x-data-grid";

const columns = [
  {
    field: "id",
    headerName: "ID",
    width: 100,
  },
  {
    field: "type",
    headerName: "Type",
    width: 150,
  },
  {
    field: "amount",
    headerName: "Amount",
    width: 150,
  },
  {
    field: "referenceId",
    headerName: "Reference",
    width: 220,
  },
  {
    field: "description",
    headerName: "Description",
    width: 250,
  },
  {
    field: "createdAt",
    headerName: "Date",
    width: 220,
  },
];

const TransactionGrid = ({
  rows,
  loading,
  page,
  pageSize,
  rowCount,
  onPageChange,
  onPageSizeChange,
}) => {
  return (
    <div
      style={{
        height: 500,
        width: "100%",
      }}
    >
      <DataGrid
        rows={rows}
        columns={columns}
        loading={loading}
        pagination
        paginationMode="server"
        rowCount={rowCount}
        pageSizeOptions={[5, 10, 20]}
        paginationModel={{
          page,
          pageSize,
        }}
        onPaginationModelChange={(model) => {
          if (model.page !== page) {
            onPageChange(model.page);
          }

          if (model.pageSize !== pageSize) {
            onPageSizeChange(model.pageSize);
          }
        }}
      />
    </div>
  );
};

export default TransactionGrid;

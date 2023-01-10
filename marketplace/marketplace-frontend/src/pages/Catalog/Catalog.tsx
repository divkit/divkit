import { CatalogGrid } from '../../components/CatalogGrid/CatalogGrid';
import { SearchBar } from '../../components/SearchBar/SearchBar';
import { Box } from '@mui/material';

export const Catalog = () => {
  return (
    <Box
      sx={{
        paddingTop: 6,
        margin: '0 auto',
        display: 'flex',
        flexDirection: 'column',
        gap: '1rem',
        justifyContent: 'center',
        maxWidth: { xs: '45rem', sm: '96%', lg: '80%' },
      }}
    >
      <SearchBar />
      <CatalogGrid />
    </Box>
  );
};

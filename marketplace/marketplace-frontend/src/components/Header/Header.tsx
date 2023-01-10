import style from './header.module.scss';
import { DivkitLogo } from '../DivkitLogo/DivkitLogo';
import cn from 'classnames';
import { Box } from '@mui/material';
import { BtnNewTemplate } from '../BtnNewTemplate/BtnNewTemplate';

export const Header = () => {
  return (
    <Box
      component='header'
      className={style.header}
      sx={{ bgcolor: 'white.main', display: 'flex' }}
    >
      <DivkitLogo className={cn(style.divkitLogo)} />
      <BtnNewTemplate sx={{ justifySelf: 'flex-end', fontSize: '1.4rem' }} />
    </Box>
  );
};

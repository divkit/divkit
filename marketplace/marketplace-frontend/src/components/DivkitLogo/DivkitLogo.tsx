import { FC } from 'react';
import { Link } from 'react-router-dom';
import style from './DivkitLogo.module.scss';

import logo from '../../assets/orig.svg';
import logoShort from '../../assets/short-logo.svg';

import { useAppDispatch } from '../../store/hooks/hooks';
import { setPage } from '../../store/slices/querySlice';
import { useWindowSize } from '../../hooks/useWindowSize';

type Props = {
  className: string;
};

export const DivkitLogo: FC<Props> = ({ className = '' }) => {
  const dispatch = useAppDispatch();

  const { width: browserWidth } = useWindowSize();

  const handleHomeNavigate = () => {
    dispatch(setPage(1));
  };

  return (
    <Link to={'/'} className={style.logoBox} onClick={handleHomeNavigate}>
      <img
        src={browserWidth > 600 ? logo : logoShort}
        alt='divkit logo'
        loading='lazy'
        className={className}
      />
      <p className={style.text}>Market</p>
    </Link>
  );
};

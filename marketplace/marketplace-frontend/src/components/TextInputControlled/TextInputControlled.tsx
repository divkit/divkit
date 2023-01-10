import { FC } from 'react';
import { TextField, SxProps } from '@mui/material';
import { TagType } from '../../types';

interface Props {
  id: string;
  label?: string;
  variant?: 'standard' | 'filled' | 'outlined';
  sx?: SxProps;
  multiline?: boolean;
  name?: string;
  value?: string | TagType[];
}
const inputCustomization = {
  InputLabelProps: {
    sx: {
      fontSize: '1.6rem',
    },
  },
  InputProps: {
    sx: {
      fontSize: '1.6rem',
      maxLength: 10,
    },
  },
};

export const TextInputControlled: FC<Props> = ({
  id,
  label = '',
  variant,
  multiline = false,
  sx,
}) => {
  return (
    <TextField
      id={id}
      label={label}
      variant={variant}
      multiline={multiline}
      {...inputCustomization}
      sx={sx}
    />
  );
};

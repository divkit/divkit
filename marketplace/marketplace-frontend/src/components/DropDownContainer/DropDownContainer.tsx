import { FC, useState } from 'react';
import { Paper, Collapse, Fab, Typography } from '@mui/material';

import { ExpandMore } from '@mui/icons-material';

interface Props {
  className?: string;
  children?: React.ReactNode;
  title?: string;
}

export const DropDownContainer: FC<Props> = ({
  className = '',
  title = 'New component',
  children,
}) => {
  const [expand, setExpand] = useState(false);

  const expandHandler = () => {
    setExpand((prevValue) => !prevValue);
  };

  return (
    <Paper sx={{ width: 'max-content', margin: '0 auto', position: 'relative' }}>
      <Collapse in={!expand}>
        <Typography variant='titleCard' component={'h3'}>
          {title}
        </Typography>
      </Collapse>
      <Collapse in={expand}>{children}</Collapse>

      <Fab
        onClick={expandHandler}
        size='small'
        aria-expanded={expand}
        aria-label='show information'
        sx={{
          position: 'absolute',
          bottom: 0,
          left: '50%',
          transform: 'translate(-50%, 50%)',
        }}
      >
        <ExpandMore
          fontSize='large'
          sx={{
            transform: expand ? 'rotate(180deg)' : '',
          }}
        />
      </Fab>
    </Paper>
  );
};

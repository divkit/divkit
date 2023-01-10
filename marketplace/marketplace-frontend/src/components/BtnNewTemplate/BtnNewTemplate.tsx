import { Link } from 'react-router-dom'
import { Button, SxProps } from '@mui/material'

interface IProps {
  sx?: SxProps
}

export const BtnNewTemplate = ({ sx }: IProps) => {
  return (
    <Link to='create-template/'>
      <Button
        data-testid='create-template'
        color='primary'
        variant='outlined'
        sx={sx}>
        New Template
      </Button>
    </Link>
  )
}

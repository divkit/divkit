import { FC } from 'react'
import { Button, SxProps } from '@mui/material'

interface Props {
  sx?: SxProps
  disabled?: boolean
}

export const SubmitButton: FC<Props> = ({ sx, disabled }) => {
  return (
    <Button
      type='submit'
      sx={{ ...sx }}
      variant='contained'
      color='primary'
      disabled={disabled}>
      Save
    </Button>
  )
}

import React from 'react'
import cn from 'classnames'
import styles from './Tag.module.scss'
import { Chip, SxProps } from '@mui/material/'

interface ITagProps {
  text: string
  onDelete?: (tag: string) => void
  sx?: SxProps
}

export const Tag = ({ text, onDelete, sx }: ITagProps) => {
  const handleDelete = () => {
    if (onDelete) {
      onDelete(text)
    }
  }
  return (
    <Chip
      data-testid='tag'
      className={cn(styles.tag)}
      variant='outlined'
      label={text}
      size='medium'
      sx={{
        fontSize: 16,
        borderRadius: 0,
        bgcolor: 'white.main',
        svg: {
          fill: 'primary.main',
        },
        ...sx,
      }}
      onDelete={onDelete ? handleDelete : undefined}
    />
  )
}

import React from 'react'
import { Pagination } from '@mui/material'
import { useAppDispatch, useAppSelector } from '../../store/hooks/hooks'
import { getPage } from '../../store/selectors'
import { setPage } from '../../store/slices/querySlice'
export const PaginationControlled = ({
  totalPages,
}: {
  totalPages: number
}) => {
  const dispatch = useAppDispatch()
  const page = useAppSelector(getPage)
  const handleChange = (_: React.ChangeEvent<unknown>, value: number) => {
    dispatch(setPage(value))
  }
  return (
    <Pagination
      sx={{
        button: {
          fontSize: 14,
        },
        margin: '0 auto',
        userSelect: 'none',
      }}
      data-testid='pagination'
      count={totalPages}
      page={page}
      onChange={handleChange}
    />
  )
}

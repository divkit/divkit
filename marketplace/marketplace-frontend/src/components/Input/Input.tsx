import React from 'react'
import { TextField } from '@mui/material'
import { FormikProps } from 'formik'
import { ISearchInitialValues } from '../SearchBar/SearchBar'

interface IInputProps {
  name: string
  formik: FormikProps<ISearchInitialValues>
  placeholder?: string
}

export const Input = ({ name, formik, placeholder = '' }: IInputProps) => {
  return (
    <TextField
      data-testid='find-by-search'
      InputProps={{
        sx: {
          fontSize: 14,
          input: {
            padding: 2,
          },
        },
      }}
      name={name}
      placeholder={placeholder}
      value={formik.values.title}
      onChange={formik.handleChange}
    />
  )
}

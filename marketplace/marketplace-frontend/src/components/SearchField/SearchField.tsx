import { useState, useEffect } from 'react'
import { Autocomplete, Stack, Box, TextField, Typography } from '@mui/material'
import { TagType } from '../../types'
import { FormikProps } from 'formik'
import { ISearchInitialValues } from '../SearchBar/SearchBar'
import { Tags } from '../Tags/Tags'
import { useLazyGetSimilarTagsQuery } from '../../services/api'
import { CreateTag } from '../CreateTag/CreateTag'

interface ISearchFieldProps {
  addTag: (tag: TagType) => void
  usedTags?: TagType[]
  canCreateTag?: boolean
  formik?: FormikProps<ISearchInitialValues>
}

export const SearchField = ({
  formik,
  addTag,
  canCreateTag = false,
}: ISearchFieldProps) => {
  const [inputValue, setInputValue] = useState<string>('')
  const [fetchTags, { data }] = useLazyGetSimilarTagsQuery()
  console.log(data)
  const LIMIT: number = 5
  const deleteTag = (text: string) => {
    const filtered = formik?.values.tags.filter(tag => tag.tag !== text)
    if (formik) {
      formik.setFieldValue('tags', filtered)
    }
  }

  const clearField = (text: string) => {
    if (data?.tags.length) {
      const currentTag = data.tags!.find(tag => tag.tag === text)
      if (currentTag || (formik && formik.values.tags.length < LIMIT)) {
        if (currentTag) {
          setInputValue('')
          addTag(currentTag)
        }
      }
    }
  }

  useEffect(() => {
    if (inputValue.length > 3) {
      fetchTags({
        text: inputValue,
        size: 5,
        page: 1,
      })
    }
  }, [inputValue])

  const clearAddTag = (newInputValue: string) => {
    setInputValue(newInputValue)
    clearField(newInputValue)
  }

  return (
    <>
      <Stack spacing={2} sx={{ minWidth: 180, maxWidth: 300 }}>
        <Autocomplete
          data-testid='autocomplete'
          disableClearable
          onChange={(_, text: string) => clearField(text)}
          value={inputValue}
          onInputChange={(_, newInputValue) => clearAddTag(newInputValue)}
          id='tags'
          inputValue={inputValue}
          noOptionsText={
            canCreateTag ? (
              <CreateTag addTag={addTag} text={inputValue} />
            ) : (
              <Typography>No options</Typography>
            )
          }
          options={(data?.tags || []).map(option => option.tag)}
          renderOption={(props, option) => (
            <Box
              component='li'
              data-testid='autocomplete-option'
              sx={{ fontSize: 16 }}
              {...props}>
              {option}
            </Box>
          )}
          renderInput={params => (
            <TextField
              {...params}
              fullWidth
              placeholder='Find by tags'
              data-testid='autocomplete-input'
              InputProps={{
                ...params.InputProps,
                type: 'search',
                style: {
                  fontSize: 14,
                  padding: 0,
                },
              }}
            />
          )}
        />
      </Stack>
      {formik?.values.tags && (
        <Tags deleteTag={deleteTag} tags={formik!.values.tags} />
      )}
    </>
  )
}

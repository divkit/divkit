import { useEffect } from 'react'
import * as Yup from 'yup'
import { SearchField } from '../SearchField/SearchField'
import { Box, Button } from '@mui/material'
import { useFormik } from 'formik'
import { TagType } from '../../types'
import { Input } from '../Input/Input'
import { useAppDispatch, useAppSelector } from '../../store/hooks/hooks'
import { setFilters, setPage } from '../../store/slices/querySlice'
import { useSearchParams } from 'react-router-dom'
import { getPage } from '../../store/selectors'

export interface ISearchInitialValues {
  title: string
  tags: TagType[]
}

export const SearchBar = () => {
  const dispatch = useAppDispatch()
  const [searchParams, setSearchParams] = useSearchParams()

  let search = searchParams.get('template') || ''
  let page = useAppSelector(getPage)

  useEffect(() => {
    const index = search.indexOf('&page')
    const newSearch = index !== -1 ? search.slice(0, index) : search
    dispatch(setFilters(newSearch))
  }, [search])

  useEffect(() => {
    const index = search.indexOf('&page')
    const newSearch = index !== -1 ? search.slice(0, index) : search
    setSearchParams({ template: `${newSearch}&page=${page}` })
  }, [page])

  useEffect(() => {
    const index = search.indexOf('&page')
    let currPage = 1
    if (index !== -1) {
      currPage = parseFloat(search.slice(index + 6)) || 1
    }
    dispatch(setPage(currPage))
  }, [search])

  const ValidationSchema = Yup.object({
    title: Yup.string().max(30, 'Too long title!'),
  })
  const formik = useFormik<ISearchInitialValues>({
    initialValues: {
      title: '',
      tags: [],
    },
    enableReinitialize: true,
    validationSchema: ValidationSchema,
    onSubmit: values => {
      let tags_ids = values.tags.map(tag => tag.id).join(',')
      let request = {
        title: values.title,
        tags_ids,
        page,
      }

      formik.resetForm()
      formik.setFieldValue('title', '')

      setSearchParams({
        template: `${request.title && `title=${request.title}&`}${
          request.tags_ids && `tag_ids=${request.tags_ids}`
        }`,
      })
    },
  })

  const addTag = (newTag: TagType) => {
    if (!formik.values.tags.includes(newTag)) {
      formik.setFieldValue('tags', [...formik.values.tags, newTag])
    }
  }

  return (
    <form onSubmit={formik.handleSubmit}>
      <Box
        sx={{
          display: 'flex',
          alignItems: 'flex-start',
          gap: 3,
          justifyContent: 'space-between',
        }}>
        <Box
          sx={{
            display: 'flex',
            flex: '0 1 70%',
            flexDirection: 'column',
            gap: 3,
          }}>
          <Input name='title' formik={formik} placeholder={'Find by title'} />
          <SearchField formik={formik} addTag={addTag} />
        </Box>
        <Button
          sx={{
            flex: '1 1 10%',
            fontSize: 12,
            maxWidth: '10rem',
            textTransform: 'uppercase',
            marginRight: { xs: 0, sm: '1rem' },
          }}
          color='primary'
          variant='contained'
          type='submit'>
          Send
        </Button>
      </Box>
    </form>
  )
}

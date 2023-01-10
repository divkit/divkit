import React, { useEffect } from 'react'
import { CatalogItem } from '../CatalogItem/CatalogItem'

import { Rings } from 'react-loader-spinner'
import { ringLoaderConfig } from '../../utils/loaderConfig'

import Masonry from '@mui/lab/Masonry'
import { Typography } from '@mui/material'
import { useLazyGetTemplatesByNameAndTagsQuery } from '../../services/api'
import { useAppSelector } from '../../store/hooks/hooks'
import { getFilters, getPage } from '../../store/selectors'
import { PaginationControlled } from '../PaginationControlled/PaginationControlled'
import { Link } from 'react-router-dom'

export const CatalogGrid = () => {
  const page = useAppSelector(getPage)
  const filters = useAppSelector(getFilters)
  let [fetchTemplates, { data }] = useLazyGetTemplatesByNameAndTagsQuery()

  useEffect(() => {
    fetchTemplates({
      searchParams: filters,
      page,
    })
    // window.scrollTo(0, 0)
  }, [filters, page])

  data = JSON.parse(JSON.stringify(data) || '{}')
  let templates
  let total_pages = 1
  if (data) {
    templates = data.templates
    total_pages = data.total_pages
  }

  return templates ? (
    templates.length > 0 ? (
      <>
        <Masonry columns={{ sm: 2, md: 3 }} spacing={3}>
          {templates.map(item => (
            <CatalogItem item={item} key={item.id} />
          ))}
        </Masonry>
        {total_pages > 1 && <PaginationControlled totalPages={total_pages} />}
      </>
    ) : (
      <>
        <Typography data-testid='nothing-found' variant='h2'>
          Nothing found for your request
        </Typography>
        <Link to='/' data-testid='back-to-catalog'>
          <Typography
            variant='h4'
            sx={{
              '&:hover': {
                textDecoration: 'underline',
              },
            }}>
            Back to catalog
          </Typography>
        </Link>
      </>
    )
  ) : (
    <Rings {...ringLoaderConfig} />
  )
}

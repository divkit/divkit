import React, { useRef, useEffect, useState } from 'react';
import cn from 'classnames';
import style from './CatalogItem.module.scss';
import { ITemplate } from '../../types';
import { Link } from 'react-router-dom';

import { DivKit } from '@divkitframework/react';
import '@divkitframework/divkit/dist/client.css';

import { CardContent, Card, Typography, Box, Tooltip } from '@mui/material/';
import { Tags } from '../Tags/Tags';
import { CollapseElement } from '../CollapseElement/CollapseElement';

interface ICatalogItem {
  item: ITemplate;
}

const TEMPLATE_PAGE = 'template/';

export const CatalogItem = ({ item }: ICatalogItem) => {
  const content = useRef<HTMLDivElement | null>(null);
  const description = useRef<HTMLDivElement | null>(null);

  const [contentHeight, setContentHeight] = useState(0);
  const [descriptionHeight, setDescriptionHeight] = useState(0);

  useEffect(() => {
    setContentHeight(content.current?.clientHeight || 0);
    setDescriptionHeight(description.current?.clientHeight || 0);
  }, [content.current, description.current]);

  return (
    <Card data-testid='card' className={cn(style.card)}>
      <Tooltip title={item.title} componentsProps={{ tooltip: { sx: { fontSize: '1.5rem' } } }}>
        <Typography
          variant='titleCard'
          component='h4'
          sx={{
            whiteSpace: 'nowrap',
            textOverflow: 'ellipsis',
            overflow: 'hidden',
          }}
        >
          {item.title}
        </Typography>
      </Tooltip>

      <CollapseElement innerElementHeight={contentHeight} collapsedHeight='20rem'>
        <CardContent
          ref={content}
          sx={{ minHeight: '20rem', padding: 0, ':last-child': { padding: 0 } }}
        >
          {item.preview_url ? (
            <Box
              sx={{
                height: '19.9rem',
                width: '100%',
                background: `center / cover no-repeat url(${item.preview_url}) `,
              }}
            ></Box>
          ) : (
            <Box sx={{ padding: '1.5rem' }}>
              <DivKit id={item.id} json={item.template} />
            </Box>
          )}
        </CardContent>
      </CollapseElement>

      <CollapseElement innerElementHeight={descriptionHeight} collapsedHeight='7rem'>
        <Box ref={description} sx={{ padding: '0 1.5rem' }}>
          {item.tags && item.tags.length > 0 ? (
            <Tags tags={item.tags} />
          ) : (
            item.description && (
              <Typography variant='text' component='p'>
                <Typography variant='subtitle' component='span'>
                  Description:
                </Typography>
                {item.description}
              </Typography>
            )
          )}
        </Box>
      </CollapseElement>

      <Typography variant='caption' component='p'>
        {new Date(item.dt_updated).toLocaleDateString()}
      </Typography>
      <Link data-testid='open-template' className={cn(style.link)} to={TEMPLATE_PAGE + item.id}>
        Open
      </Link>
    </Card>
  );
};

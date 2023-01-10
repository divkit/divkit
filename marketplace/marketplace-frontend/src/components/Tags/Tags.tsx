import React from 'react';
import cn from 'classnames';
import styles from './Tags.module.scss';
import { Tag } from '../Tag/Tag';
import { TagType } from '../../types';
import { Box, SxProps } from '@mui/material';

interface ITagsProps {
  tags: TagType[];
  deleteTag?: (tag: string) => void;
  sx?: SxProps;
  children?: React.ReactNode;
}

const sxStyle: SxProps = {
  display: 'flex',
  flexWrap: 'wrap',
  gap: '1rem',
  padding: '1rem 0',
};

export const Tags = ({ tags = [], deleteTag, sx, children }: ITagsProps) => {
  return (
    <Box className={cn(styles.tags)} sx={{ ...sxStyle, ...sx }}>
      {tags.map((tag) => (
        <Tag key={tag.id} onDelete={deleteTag} text={tag.tag} />
      ))}
      {children}
    </Box>
  );
};

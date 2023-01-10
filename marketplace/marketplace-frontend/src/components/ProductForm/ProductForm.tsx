import { useState } from 'react';
import {
  Box,
  FormGroup,
  TextField,
  Divider,
  Paper,
  Collapse,
  Fab,
  Typography,
} from '@mui/material';

import { Tags } from '../Tags/Tags';
import { TagType } from '../../types';

import { ExpandMore, Add } from '@mui/icons-material';

import { FormikProps } from 'formik';
import { SearchField } from '../SearchField/SearchField';
import { IProductFormValues } from '../../pages/Product/Product';

const inputCustomization = {
  InputLabelProps: {
    sx: {
      fontSize: '1.6rem',
    },
  },
  InputProps: {
    sx: {
      fontSize: '1.6rem',
      maxLength: 10,
    },
  },
};

interface FormValues {
  formik: FormikProps<IProductFormValues>;
}

export const ProductForm = ({ formik }: FormValues) => {
  const [expand, setExpand] = useState(false);

  const [isInputShow, setInputShow] = useState(false);

  const expandHandler = () => {
    setExpand((prevValue) => !prevValue);
  };

  const handleDeleteTag = (tagTitle: string) => {
    const newTags = formik.values.tags.filter((valuesTag) => valuesTag.tag !== tagTitle);
    formik.setFieldValue('tags', newTags);
  };

  const handleClickOnAddButton = () => {
    setInputShow(true);
  };

  const handleAddTag = (tag: TagType) => {
    if (formik.values.tags.find((valuesTag) => valuesTag.id === tag.id)) {
      return;
    }
    const newTags = formik.values.tags.concat(tag);
    formik.setFieldValue('tags', newTags);
    setInputShow(false);
  };

  return (
    <Paper sx={{ width: '100%', margin: '0 auto', position: 'relative' }}>
      <Collapse in={!expand}>
        <Typography variant='titleCard' component={'h3'}>
          {formik.values.title}
        </Typography>
      </Collapse>
      <Collapse in={expand}>
        <Box sx={{ padding: [6, 7], display: 'flex' }}>
          <FormGroup
            sx={{
              display: 'flex',
              flexDirection: 'column',
              gap: 6,
              maxWidth: '50%',
              width: '100%',
              position: 'relative',
            }}
          >
            {formik.errors.title && (
              <Typography
                variant='errorMessage'
                component='p'
                sx={{ position: 'absolute', top: 0, left: '4rem' }}
              >
                {formik.errors.title}
              </Typography>
            )}
            <TextField
              data-testid='title'
              id='product_title'
              label='Title'
              variant='standard'
              {...inputCustomization}
              sx={{
                width: { xs: '95%', sm: '60%' },
              }}
              name='title'
              value={formik.values.title}
              onChange={formik.handleChange}
            />
            <TextField
              data-testid='description'
              id='product_description'
              label='Description'
              variant='standard'
              {...inputCustomization}
              sx={{
                width: { xs: '95%', sm: '80%' },
              }}
              name='description'
              value={formik.values.description}
              onChange={formik.handleChange}
              multiline
            />
          </FormGroup>
          <Divider
            orientation='vertical'
            flexItem
            sx={{ margin: { xs: '0 1.3rem', sm: '0 2rem' } }}
          />
          <Box sx={{ maxWidth: '50%', width: '100%' }}>
            <Typography variant='title' component='h4'>
              Tags:
            </Typography>

            <Tags
              tags={formik.values.tags}
              deleteTag={handleDeleteTag}
              sx={{ display: 'inline-flex', alignItems: 'center' }}
            >
              {formik.values.tags.length === 0 && (
                <Typography variant='hintText' component='span' sx={{ display: 'inline-block' }}>
                  No tags yet, you can add some
                </Typography>
              )}
              {isInputShow ? (
                <SearchField canCreateTag addTag={handleAddTag} />
              ) : (
                <Fab
                  size='small'
                  aria-label='add'
                  data-testid='add'
                  sx={{ flexShrink: 0 }}
                  onClick={handleClickOnAddButton}
                >
                  <Add fontSize='large' />
                </Fab>
              )}
            </Tags>
          </Box>
        </Box>
      </Collapse>

      <Fab
        onClick={expandHandler}
        size='small'
        aria-expanded={expand}
        aria-label='show information'
        data-testid='show-info'
        sx={{
          position: 'absolute',
          bottom: 0,
          left: '50%',
          transform: 'translate(-50%, 50%)',
        }}
      >
        <ExpandMore
          fontSize='large'
          sx={{
            transform: expand ? 'rotate(180deg)' : '',
          }}
        />
      </Fab>
    </Paper>
  );
};

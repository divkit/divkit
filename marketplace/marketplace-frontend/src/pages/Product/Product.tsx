import { useState, useMemo } from 'react';
import style from './Product.module.scss';
import { useParams } from 'react-router-dom';

import { Rings } from 'react-loader-spinner';
import { ringLoaderConfig } from '../../utils/loaderConfig';
import { Box, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

import { ProductForm } from '../../components/ProductForm/ProductForm';
import { useCreateTemplateMutation, useUpdateTemplateMutation } from '../../services/api';

import { useFormik } from 'formik';

import { ITemplate, TagType } from '../../types';
import { ReflexJsonEditor } from '../../components/ReflexJsonEditor/ReflexJsonEditor';
import { SubmitButton } from '../../components/SubmitButton/SubmitButton';

import * as Yup from 'yup';
import { GetTemplateByIdQuery } from '../../components/GetTemplateByIdQuery/GetTemplateByIdQuery';
import { EMPTY_CARD } from '../../utils/emptyCard';

export interface IProductFormValues {
  title: string;
  description: string;
  tags: TagType[];
  json: string;
  file?: Blob;
  previewUrl?: string;
  previewFilename?: string;
}

const NEW_TEMPLATE: IProductFormValues = {
  title: 'New Template',
  description: '',
  tags: [],
  json: JSON.stringify(EMPTY_CARD),
  previewUrl: '',
};

const ValidationSchema = Yup.object({
  title: Yup.string().max(30, 'Too long title!').required('Title required'),
  json: Yup.string().min(15, 'Too short template').required('Template required'),
});

export const Product = () => {
  const { id } = useParams();
  let navigate = useNavigate();

  const [updateTemplate] = useUpdateTemplateMutation();
  const [createTemplate] = useCreateTemplateMutation();
  const [jsonError, setJsonError] = useState(false);
  const [itemLoaded, setItemLoaded] = useState(false);

  let fetchedId = '';

  const initialValues = useMemo<IProductFormValues>(
    // () => global.structuredClone(NEW_TEMPLATE), -----------------
    () => JSON.parse(JSON.stringify(NEW_TEMPLATE)),
    [id],
  );

  const reinitializeInitialValue = (data: IProductFormValues) => {
    initialValues.title = data.title;
    initialValues.description = data.description;
    initialValues.tags = data.tags;
    initialValues.json = data.json;
    initialValues.file = data.file;
    initialValues.previewUrl = data.previewUrl;
    initialValues.previewFilename = data.previewFilename;
  };

  const formik = useFormik<IProductFormValues>({
    validationSchema: ValidationSchema,
    initialValues,
    enableReinitialize: true,
    onSubmit: async (values) => {
      let request = {
        title: values.title,
        description: values.description,
        tags_ids: values.tags,
        template: JSON.parse(values.json),
        file: values.file,
        preview_filename: values.previewFilename,
      };

      reinitializeInitialValue(formik.values);
      formik.resetForm();
      if (id) {
        updateTemplate({ id, ...request });
        navigate(`/template/${id}`);
      } else {
        createTemplate(request)
          .unwrap()
          .then((response) => {
            fetchedId = response.id;
            navigate(`/template/${fetchedId}`);
          })
          .catch((error) => {
            console.warn(error);
          });
      }
    },
  });

  const handleGetTemplate = (item: ITemplate) => {
    reinitializeInitialValue({
      ...item,
      json: JSON.stringify(item.template),
      tags: item.tags || [],
      previewUrl: item.preview_url || '',
      previewFilename: item.preview_filename || '',
    });
    setItemLoaded(true);
  };

  const handleResetForm = () => {
    formik.resetForm();
  };

  const handleError = (error: boolean | string) => {
    if (error) {
      setJsonError(true);
    } else {
      setJsonError(false);
    }
  };

  return (
    <form data-testid='card-form' onSubmit={formik.handleSubmit}>
      {id && <GetTemplateByIdQuery id={id} handleGetTemplate={handleGetTemplate} />}
      {(id && itemLoaded) || !id ? (
        <Box
          className={style.wrapper}
          sx={{
            paddingTop: 6,
            display: 'flex',
            flexDirection: 'column',
            gap: 8,
            position: 'relative',
          }}
        >
          {formik.dirty && (
            <Box
              sx={{
                zIndex: 2,
                position: 'absolute',
                top: 0,
                left: '50%',
                transform: 'translate(-50%, -25%)',
                display: 'flex',
                gap: 3,
              }}
            >
              <Button
                color='error'
                sx={{
                  fontSize: '2rem',
                }}
                variant='contained'
                onClick={handleResetForm}
              >
                Reset
              </Button>
              <SubmitButton
                sx={{
                  fontSize: '2rem',
                }}
                disabled={jsonError || !formik.isValid}
              />
            </Box>
          )}
          <ProductForm formik={formik} />

          <ReflexJsonEditor
            itemId={id || 'new-template'}
            handleError={handleError}
            formik={formik}
          />
        </Box>
      ) : (
        <Rings {...ringLoaderConfig} />
      )}
    </form>
  );
};

import { useState, useEffect } from 'react'
import { ReflexContainer, ReflexSplitter, ReflexElement } from 'react-reflex'
import 'react-reflex/styles.css'
import { DivKit } from '@divkitframework/react'
import '@divkitframework/divkit/dist/client.css'
import { DivJson } from '@divkitframework/divkit/typings/common'
import JSONInput from 'react-json-editor-ajrm'
import { LOCALE } from '../../utils/localeForJSONEditor'
import { FormikProps } from 'formik'
import style from './ReflexJsonEditor.module.scss'
import { IProductFormValues } from '../../pages/Product/Product'
import { Typography, Fab, Box } from '@mui/material'
import { FileDownload } from '@mui/icons-material'
import { downloadJsonFile } from '../../utils/downloadFile'
import { DropZone, IFile } from '../DropZone/DropZone'
import { useWindowSize } from '../../hooks/useWindowSize'

const editorTheme = {
  light: 'light_mitsuketa_tribute',
  dark: 'dark_vscode_tribute',
}

export interface EditorProps {
  itemId: string
  handleError: (error: boolean | string) => void
  formik: FormikProps<IProductFormValues>
}

export const ReflexJsonEditor = ({
  itemId,
  handleError,
  formik,
}: EditorProps) => {
  const [currentJSON, setCurrentJSON] = useState<DivJson>()
  const [errorJson, setErrorJson] = useState(false)

  const { width: browserWidth } = useWindowSize()

  useEffect(() => {
    if (formik.values.json) {
      let newValue
      try {
        newValue = JSON.parse(formik.values.json)
      } catch (e) {
        console.log("Can't parse that string", e)
      }
      if (newValue) {
        setCurrentJSON(newValue)
      }
    }
  }, [formik.values.json])

  const changeHandler = ({
    json,
    error,
  }: {
    json: string
    error: boolean | string
    plainText: string
  }) => {
    if (error) {
      setErrorJson(true)
    }
    handleError(error)
    formik.setFieldValue('json', json)
    if (!error) {
      setErrorJson(false)
      setCurrentJSON(JSON.parse(formik.values.json))
    }
  }

  const handleDropJson = (content: string | IFile) => {
    if (typeof content === 'string') {
      formik.setFieldValue('json', content)
    }
  }

  const handleDropPreview = (file: string | IFile) => {
    if (typeof file !== 'string') {
      formik.setFieldValue('file', file.value)
      formik.setFieldValue('previewUrl', file.src)
    }
  }

  const handleDownload = () => {
    downloadJsonFile(formik.values.title, formik.values.json)
  }

  const handleDeletePreview = () => {
    formik.setFieldValue('previewFilename', '');
    formik.setFieldValue('previewUrl', '');
  };

  return (
    <ReflexContainer
      orientation={browserWidth > 600 ? 'vertical' : 'horizontal'}>
      <ReflexElement className={style.panel}>
        <Box
          sx={{
            marginBottom: '1.7rem',
            display: 'flex',
            gap: 2,
            alignItems: 'center',
          }}>
          <DropZone
            handleDrop={handleDropJson}
            placeholder={`Drag 'n' drop *.json file here`}
            acceptFile={{ 'application/json': ['.json'] }}
          />

          <Fab
            color='secondary'
            aria-label='download-json'
            sx={{ flexShrink: 0 }}
            onClick={handleDownload}>
            <FileDownload />
          </Fab>
        </Box>

        {formik.errors.json && (
          <Typography
            variant='errorMessage'
            component='p'
            sx={{ position: 'absolute', top: 0, left: '4rem' }}>
            {formik.errors.json}
          </Typography>
        )}
        <JSONInput
          locale={LOCALE}
          placeholder={currentJSON}
          theme={editorTheme.dark}
          height='75vh'
          width='100%'
          style={{
            body: { fontSize: '1.6rem' },
          }}
          onChange={changeHandler}
          confirmGood={false}></JSONInput>
      </ReflexElement>

      <ReflexSplitter className={style.splitter} />

      <ReflexElement className={style.panel}>
        <Box sx={{ display: 'flex', gap: 2, flexDirection: 'column' }}>
          <DropZone
            sx={{ order: { xs: 2, sm: 1 } }}
            handleDrop={handleDropPreview}
            placeholder={`Drag 'n' drop preview image file here`}
            acceptFile={{
              'image/*': [],
            }}
            withPreview
            previewUrl={formik.values.previewUrl}
            onDelete={handleDeletePreview}
          />

          {currentJSON && (
            <Box sx={{ order: { xs: 1, sm: 2 } }}>
              <DivKit id={itemId} json={currentJSON} />
            </Box>
          )}
        </Box>
      </ReflexElement>
    </ReflexContainer>
  )
}

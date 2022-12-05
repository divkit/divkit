import { useState } from 'react';
import { useDropzone } from 'react-dropzone';
import { Paper, SxProps, Typography, Box, Button } from '@mui/material';

const FILE_TYPES = {
  json: 'application/json',
  img: /image\/*/,
};

export interface IFile {
  value: Blob;
  src: string;
}

interface IProps {
  handleDrop: (content: string | IFile) => void;
  placeholder: string;
  acceptFile: Record<string, string[]>;
  sx?: SxProps;
  withPreview?: boolean;
  previewUrl?: string;
  onDelete?: () => void;
}

export const DropZone = ({
  handleDrop,
  placeholder,
  acceptFile,
  sx,
  withPreview = false,
  onDelete,
  previewUrl = '',
}: IProps) => {
  const [file, setFile] = useState<IFile>();
  const { acceptedFiles, getRootProps, getInputProps } = useDropzone({
    accept: acceptFile,
    onDrop: (acceptedFiles) => {
      const currentFile = acceptedFiles.at(-1);
      if (currentFile && currentFile.type === FILE_TYPES.json) {
        currentFile?.text().then((content) => {
          handleDrop(content);
        });
      }
      if (currentFile && FILE_TYPES.img.test(currentFile.type)) {
        setFile({ value: currentFile, src: URL.createObjectURL(currentFile) });

        handleDrop({ value: currentFile, src: URL.createObjectURL(currentFile) });
      }
    },
  });

  return (
    <Box sx={{ ...sx }}>
      <Paper
        {...getRootProps()}
        sx={{
          padding: '3rem 1rem',
          transition: 'all .3s',
          userSelect: 'none',
          flexGrow: 1,
          overflow: 'hidden',
          cursor: 'pointer',
          ':hover': {
            backgroundColor: 'secondary.light',
          },
        }}
      >
        <input {...getInputProps()} />
        <Typography variant='hintText' component='p' align='center'>
          {placeholder}
          {Boolean(acceptedFiles.at(-1)?.name) && '  : ' + acceptedFiles.at(-1)?.name}
        </Typography>
        {withPreview && previewUrl && (
          <Box
            onLoad={() => {
              URL.revokeObjectURL(previewUrl);
            }}
            sx={{
              margin: '0 auto',
              width: '10rem',
              height: '10rem',
              backgroundImage: `url(${previewUrl})`,
              backgroundSize: 'contain',
              backgroundRepeat: 'no-repeat',
              backgroundPosition: 'center',
            }}
          ></Box>
        )}
      </Paper>
      {onDelete && (
        <Button
          variant='outlined'
          color='secondary'
          onClick={onDelete}
          sx={{ maxWidth: '17rem', margin: '1rem auto', display: 'block', fontSize: '1.4rem' }}
        >
          Delete preview
        </Button>
      )}
    </Box>
  );
};

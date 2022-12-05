import React from 'react'
import { Button, Box } from '@mui/material'
import { useCreateTagMutation } from '../../services/api'
import { TagType } from '../../types'

interface ICreateTagProps {
  text: string
  addTag: (s: TagType) => void
}

export const CreateTag = ({ text, addTag }: ICreateTagProps) => {
  const [postTag] = useCreateTagMutation()
  const createTag = async () => {
    if (text.trim().length > 3) {
      const result = await postTag(text)
      if ('data' in result) {
        const id = result.data.id
        addTag({ id, tag: text })
      }
    }
  }
  return (
    <Box sx={{ fontSize: 12 }}>
      <Button data-testid='create-tag' onClick={createTag}>
        Create Tag
      </Button>
    </Box>
  )
}

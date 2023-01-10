import React from 'react'
import cn from 'classnames'
import styles from './NotFound.module.scss'
import { Link } from 'react-router-dom'
import { Typography, useTheme } from '@mui/material'
import { Container } from '@mui/system'
import SentimentDissatisfiedRoundedIcon from '@mui/icons-material/SentimentDissatisfiedRounded'

export const NotFound = () => {
  return (
    <Container
      sx={{
        color: 'gray.dark',
      }}
      className={cn(styles.notFound)}>
      <Typography>
        <SentimentDissatisfiedRoundedIcon
          sx={{
            fontSize: 150,
          }}
        />
      </Typography>
      <Typography
        sx={{
          fontSize: 50,
        }}
        className={cn(styles.title)}>
        404
      </Typography>
      <Typography gutterBottom variant='h3'>
        Sorry, page not found
      </Typography>
      <Typography gutterBottom variant='h4' className={cn(styles.text)}>
        The page you requested could not be found
      </Typography>

      <Link to='/'>
        <Typography variant='h6'>Back To Home</Typography>
      </Link>
    </Container>
  )
}

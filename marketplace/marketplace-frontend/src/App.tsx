import React from 'react'
import { Route, Routes } from 'react-router-dom'
import { Catalog } from './pages/Catalog/Catalog'
import { Product } from './pages/Product/Product'
import { NotFound } from './pages/NotFound/NotFound'
import { Container } from '@mui/material'
import { Header } from './components/Header/Header'
import { Footer } from './components/Footer/Footer'

import cn from 'classnames'
import styles from './App.module.scss'

export const App = () => {
  return (
    <div className={cn(styles.container)}>
      <Header />
      <Container component='main' className={cn(styles.content)} maxWidth='lg'>
        <Routes>
          <Route path='/' element={<Catalog />} />
          <Route path='/template/:id' element={<Product />} />
          <Route path='/create-template' element={<Product />} />
          <Route path='*' element={<NotFound />} />
        </Routes>
      </Container>
      <Footer />
    </div>
  )
}
export default App

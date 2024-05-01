import axios from 'axios'
import type { AxiosResponse } from 'axios'

const ENDPOINT_STATISTICS: string = 'statistics'
const ENDPOINT_USERS: string = 'users'

type Handler<T = unknown> = (event: T) => Promise<void | AxiosResponse<any, any>>

export function isAuthenticated() {
  const token = localStorage.getItem('user')
  if (token) {
    const user = JSON.parse(token)
    console.log('[Api] Checking Authentication...')
    if (user && user.token) {
      console.log('[Api] User is authenticated.')
      return true
    } else {
      console.log('[Api] User is not authenticated.')
      return false
    }
  }
  return false
}

export function authHeader() {
  // return authorization header with jwt token
  const token = localStorage.getItem('user')
  if (token) {
    const user = JSON.parse(token)
    if (user && user.token) {
      const header = { Authorization: 'Bearer ' + user.token }
      return header
    } else {
      return {}
    }
  } else {
    return {}
  }
}

export class AppAPI {
  #url: string
  #endpoints = new Map<string, Map<string, Handler<any>>>()

  constructor(url: string) {
    this.#url = url
  }

  /**
   * Create and store a single entity's endpoints
   * @param {A entity Object} entity
   */
  createEntity(entity: { name: string }) {
    this.#endpoints.set(entity.name, this.createBasicCRUDEndpoints(entity))
  }

  login(username: string, password: string) {
    return axios.request({
      url: '/api/v1/auth/signin',
      method: 'post',
      withCredentials: true,
      baseURL: this.#url,
      data: {
        username: username,
        password: password
      }
    })
  }

  resetPassword(email: string) {
    return axios.request({
      url: '/users/resetpassword',
      method: 'post',
      withCredentials: false,
      baseURL: this.#url,
      data: { email: email }
    })
  }

  register(username: string, password: string, email: string) {
    return axios.request({
      url: '/api/v1/auth/signup',
      method: 'post',
      withCredentials: true,
      baseURL: this.#url,
      data: {
        username: username,
        password: password,
        email: email
      }
    })
  }

  /**
   * Create the basic endpoints handlers for CRUD operations
   * @param {A entity Object} entity
   */
  createBasicCRUDEndpoints(entity: { name: string }) {
    const handlers =
      /** @type {Map<string, Handler<any>>} */ this.#endpoints.get(entity.name) ??
      new Map<string, Handler<any>>()

    const resourceURL = `${this.#url}/${entity.name}`

    // const resourceURL = `${this.#url}`
    // handlers.set('getById', (id: any, config = { headers: authHeader() }) =>
    //   axios.get(`${resourceURL}/api/v1/resource`, config).catch(function (error: any) {
    //     console.log(`[Api.js - getById - ${resourceURL}] Error: `, error.response)
    //   })
    // )

    handlers.set('getById', (id: any, config = { headers: authHeader() }) =>
      axios.get(`${resourceURL}/api/v1/resource`, config).catch(function (error: any) {
        console.log(`[Api.js - getById - ${resourceURL}] Error: `, error.response)
      })
    )

    handlers.set('getUsername', (data = {}, config = { headers: authHeader() }) =>
      axios.get(`${resourceURL}/username`, config).catch(function (error) {
        console.log(`[Api.js - getUsername - ${resourceURL}] Error: `, error.response)
      })
    )

    handlers.set('getEmail', (data = {}, config = { headers: authHeader() }) =>
      axios.get(`${resourceURL}/email`, config).catch(function (error) {
        console.log(`[Api.js - getEmail - ${resourceURL}] Error: `, error.response)
      })
    )

    handlers.set('getPicture', (data = {}, config = { headers: authHeader() }) =>
      axios.get(`${resourceURL}/picture`, config).catch(function (error) {
        console.log(`[Api.js - getPicture - ${resourceURL}] Error: `, error.response)
      })
    )

    handlers.set('getPictureResource', ({ fileName }, config = { headers: authHeader() }) =>
      axios.get(`${resourceURL}/picture/${fileName}`, config).catch(function (error) {
        console.log(`[Api.js - getPictureResource - ${resourceURL}] Error: `, error.response)
      })
    )

    handlers.set('changePassword', (credentials, config = { headers: authHeader() }) =>
      axios.post(`${resourceURL}/password`, credentials, config)
    )

    handlers.set('changeEmail', (credentials, config = { headers: authHeader() }) =>
      axios.post(`${resourceURL}/email`, credentials, config)
    )

    handlers.set('changePicture', (formData, config = { headers: authHeader() }) =>
      axios.post(`${resourceURL}/picture`, formData, config)
    )

    handlers.set('changeLostPassword', (credentials, config = {}) =>
      axios.post(`${resourceURL}/changelostpassword`, credentials, config)
    )

    handlers.set('logout', (credentials, config = { headers: authHeader() }) =>
      axios.post(`${resourceURL}/logout`, credentials, config)
    )

    handlers.set('create', (data, config = { headers: authHeader() }) =>
      axios.post(resourceURL, data, config)
    )

    handlers.set('capture', (data, config = {}) =>
      axios.post(`${resourceURL}/capture`, data, config)
    )

    handlers.set('captures', (data = {}, config = { headers: authHeader() }) =>
      axios.get(`${resourceURL}/captures`, config)
    )

    return handlers
  }

  getEndpoints(name: string): Map<string, Handler<any>> | undefined {
    return this.#endpoints.get(name)
  }
}

// let API = new AppAPI(process.env.VUE_APP_API_URL)
const API = new AppAPI('http://127.0.0.1:8080')
API.createEntity({ name: ENDPOINT_STATISTICS })
API.createEntity({ name: ENDPOINT_USERS })

export { API }

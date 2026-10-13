export function apiBaseUrl() {
  const configuredBase =
    import.meta.env.VITE_API_BASE_URL || 'https://student-management-api-sple.onrender.com/api'
  const withProtocol = /^https?:\/\//i.test(configuredBase) ? configuredBase : `https://${configuredBase}`
  const withoutTrailingSlashes = withProtocol.replace(/\/+$/, '')
  return /\/api$/i.test(withoutTrailingSlashes) ? withoutTrailingSlashes : `${withoutTrailingSlashes}/api`
}

export function apiOrigin() {
  return apiBaseUrl().replace(/\/api$/i, '')
}

export function absolutePhotoUrl(photoUrl) {
  if (!photoUrl) return null
  if (photoUrl.startsWith('http')) return photoUrl
  return `${apiOrigin()}${photoUrl.startsWith('/') ? '' : '/'}${photoUrl}`
}

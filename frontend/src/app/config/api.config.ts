function getBaseUrl(): string {
  if (typeof window !== 'undefined' && (window as any).__env?.apiBaseUrl) {
    return (window as any).__env.apiBaseUrl;
  }
  return 'http://localhost:5001';
}

export const API_CONFIG = {
  baseUrl: getBaseUrl(),
  endpoints: {
    clientes: '/clientes',
    cuentas: '/cuentas',
    movimientos: '/movimientos',
    reportes: '/reportes'
  }
};

export const getApiUrl = (endpoint: string): string => {
  return `${API_CONFIG.baseUrl}${endpoint}`;
};

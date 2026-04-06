import http from '../request'
import type {
  DeliveryNoticeH,
  OrderExecutionSummary,
  PushNoticeReq,
  SalesAuditReq,
  SalesOutboundH,
  SalesOrderH,
  SalesPage,
  SalesReturnH,
} from '../../types/sales'

const get = <T>(url: string, params?: Record<string, any>) => http.get<T>(url, { params })
const post = <T>(url: string, data?: any) => http.post<T>(url, data)

export const salesOrderApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<SalesPage<SalesOrderH>>('/sales/orders', params),
  detail: (id: number) => get<{ header: SalesOrderH; lines: any[] }>(`/sales/orders/${id}`),
  save: (data: any) => post<{ header: SalesOrderH; lines: any[] }>('/sales/orders', data),
  submit: (id: number) => post<SalesOrderH>(`/sales/orders/${id}/submit`),
  audit: (id: number, data: SalesAuditReq) => post<SalesOrderH>(`/sales/orders/${id}/audit`, data),
  unAudit: (id: number) => post<SalesOrderH>(`/sales/orders/${id}/unaudit`),
  close: (id: number) => post<SalesOrderH>(`/sales/orders/${id}/close`),
  void: (id: number) => post<SalesOrderH>(`/sales/orders/${id}/void`),
  pushDeliveryNotice: (data: PushNoticeReq) => post<{ header: DeliveryNoticeH; lines: any[] }>('/sales/orders/push-delivery-notice', data),
}

export const deliveryNoticeApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<SalesPage<DeliveryNoticeH>>('/sales/delivery-notices', params),
  detail: (id: number) => get<{ header: DeliveryNoticeH; lines: any[] }>(`/sales/delivery-notices/${id}`),
  save: (data: any) => post<{ header: DeliveryNoticeH; lines: any[] }>('/sales/delivery-notices', data),
  audit: (id: number) => post<DeliveryNoticeH>(`/sales/delivery-notices/${id}/audit`),
  close: (id: number) => post<DeliveryNoticeH>(`/sales/delivery-notices/${id}/close`),
}

export const salesOutboundApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<SalesPage<SalesOutboundH>>('/sales/outbounds', params),
  detail: (id: number) => get<{ header: SalesOutboundH; lines: any[] }>(`/sales/outbounds/${id}`),
  save: (data: any) => post<{ header: SalesOutboundH; lines: any[] }>('/sales/outbounds', data),
  audit: (id: number) => post<SalesOutboundH>(`/sales/outbounds/${id}/audit`),
  pdaRecords: (id: number) => get<string>(`/sales/outbounds/${id}/pda-records`),
}

export const salesReturnApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<SalesPage<SalesReturnH>>('/sales/returns', params),
  detail: (id: number) => get<{ header: SalesReturnH; lines: any[] }>(`/sales/returns/${id}`),
  save: (data: any) => post<{ header: SalesReturnH; lines: any[] }>('/sales/returns', data),
  audit: (id: number, data: SalesAuditReq) => post<SalesReturnH>(`/sales/returns/${id}/audit`, data),
}

export const orderExecutionApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<SalesPage<OrderExecutionSummary>>('/sales/order-executions', params),
}

import http from '../request'
import type {
  PurchaseAuditReq,
  PurchaseInboundH,
  PurchaseOrderH,
  PurchasePage,
  PurchaseReceiptH,
  PurchaseReqH,
  PurchaseReturnH,
  PushOrderReq,
} from '../../types/purchase'

const get = <T>(url: string, params?: Record<string, any>) => http.get<T>(url, { params })
const post = <T>(url: string, data?: any) => http.post<T>(url, data)

export const purchaseReqApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<PurchasePage<PurchaseReqH>>('/purchase/reqs', params),
  detail: (id: number) => get<{ header: PurchaseReqH; lines: any[] }>(`/purchase/reqs/${id}`),
  save: (data: any) => post<{ header: PurchaseReqH; lines: any[] }>('/purchase/reqs', data),
  submit: (id: number) => post<PurchaseReqH>(`/purchase/reqs/${id}/submit`),
  audit: (id: number, data: PurchaseAuditReq) => post<PurchaseReqH>(`/purchase/reqs/${id}/audit`, data),
  pushOrder: (data: PushOrderReq) => post<{ header: PurchaseOrderH; lines: any[] }>('/purchase/reqs/push-order', data),
}

export const purchaseOrderApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<PurchasePage<PurchaseOrderH>>('/purchase/orders', params),
  detail: (id: number) => get<{ header: PurchaseOrderH; lines: any[] }>(`/purchase/orders/${id}`),
  save: (data: any) => post<{ header: PurchaseOrderH; lines: any[] }>('/purchase/orders', data),
  audit: (id: number, data: PurchaseAuditReq) => post<PurchaseOrderH>(`/purchase/orders/${id}/audit`, data),
  close: (id: number) => post<PurchaseOrderH>(`/purchase/orders/${id}/close`),
}

export const purchaseReceiptApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<PurchasePage<PurchaseReceiptH>>('/purchase/receipts', params),
  detail: (id: number) => get<{ header: PurchaseReceiptH; lines: any[] }>(`/purchase/receipts/${id}`),
  confirm: (data: any) => post<{ header: PurchaseReceiptH; lines: any[] }>('/purchase/receipts/confirm', data),
}

export const purchaseInboundApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<PurchasePage<PurchaseInboundH>>('/purchase/inbounds', params),
  detail: (id: number) => get<{ header: PurchaseInboundH; lines: any[] }>(`/purchase/inbounds/${id}`),
  save: (data: any) => post<{ header: PurchaseInboundH; lines: any[] }>('/purchase/inbounds', data),
  audit: (id: number, data: PurchaseAuditReq) => post<PurchaseInboundH>(`/purchase/inbounds/${id}/audit`, data),
}

export const purchaseReturnApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<PurchasePage<PurchaseReturnH>>('/purchase/returns', params),
  detail: (id: number) => get<{ header: PurchaseReturnH; lines: any[] }>(`/purchase/returns/${id}`),
  save: (data: any) => post<{ header: PurchaseReturnH; lines: any[] }>('/purchase/returns', data),
  audit: (id: number, data: PurchaseAuditReq) => post<PurchaseReturnH>(`/purchase/returns/${id}/audit`, data),
}

import type { PageResult } from './common'

export interface SalesOrderH {
  id?: number
  orderNo?: string
  customerId: number
  bizDate?: string
  status?: string
  approvalStatus?: string
  totalQty?: number
  deliveredQty?: number
  remark?: string
}

export interface SalesOrderD {
  id?: number
  orderId?: number
  lineNo?: number
  materialId: number
  orderQty: number
  deliveredQty?: number
  status?: string
  remark?: string
}

export interface DeliveryNoticeH {
  id?: number
  noticeNo?: string
  orderId: number
  bizDate?: string
  status?: string
  totalQty?: number
  outboundQty?: number
  remark?: string
}

export interface DeliveryNoticeD {
  id?: number
  noticeId?: number
  lineNo?: number
  orderLineId?: number
  materialId: number
  noticeQty: number
  outboundQty?: number
  status?: string
}

export interface SalesOutboundH {
  id?: number
  outboundNo?: string
  noticeId: number
  bizDate?: string
  status?: string
  totalQty?: number
  pdaRecordJson?: string
  remark?: string
}

export interface SalesOutboundD {
  id?: number
  outboundId?: number
  lineNo?: number
  noticeLineId: number
  materialId?: number
  outboundQty: number
  status?: string
}

export interface SalesReturnH {
  id?: number
  returnNo?: string
  customerId: number
  bizDate?: string
  status?: string
  approvalStatus?: string
  totalQty?: number
  remark?: string
}

export interface SalesReturnD {
  id?: number
  returnId?: number
  lineNo?: number
  materialId: number
  returnQty: number
}

export interface SalesAuditReq {
  approved: boolean
  remark?: string
}

export interface PushLine {
  sourceLineId: number
  qty: number
}

export interface PushNoticeReq {
  orderId: number
  bizDate?: string
  lines: PushLine[]
}

export interface OrderExecutionSummary {
  orderId: number
  orderNo: string
  orderTotalQty: number
  deliveredQty: number
  undeliveredQty: number
  orderStatus: string
}

export type SalesPage<T> = PageResult<T>

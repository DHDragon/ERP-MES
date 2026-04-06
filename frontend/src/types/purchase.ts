import type { PageResult } from './common'

export interface PurchaseReqH {
  id?: number
  reqNo?: string
  sourceType?: string
  supplierId?: number
  bizDate?: string
  status?: string
  approvalStatus?: string
  totalQty?: number
  orderedQty?: number
  remark?: string
}

export interface PurchaseReqD {
  id?: number
  reqId?: number
  lineNo?: number
  materialId: number
  reqQty: number
  orderedQty?: number
  status?: string
  remark?: string
}

export interface PurchaseOrderH {
  id?: number
  orderNo?: string
  reqId?: number
  supplierId?: number
  bizDate?: string
  status?: string
  approvalStatus?: string
  totalQty?: number
  receivedQty?: number
  inboundQty?: number
  remark?: string
}

export interface PurchaseOrderD {
  id?: number
  orderId?: number
  lineNo?: number
  reqLineId?: number
  materialId: number
  orderQty: number
  receivedQty?: number
  inboundQty?: number
  status?: string
}

export interface PurchaseReceiptH {
  id?: number
  receiptNo?: string
  orderId: number
  bizDate?: string
  status?: string
  totalQty?: number
  inboundQty?: number
  qcEnabled?: number
  remark?: string
}

export interface PurchaseReceiptD {
  id?: number
  receiptId?: number
  lineNo?: number
  orderLineId: number
  materialId?: number
  receiptQty: number
  inboundQty?: number
  status?: string
}

export interface PurchaseInboundH {
  id?: number
  inboundNo?: string
  receiptId: number
  bizDate?: string
  status?: string
  totalQty?: number
  qcRequired?: number
  qcPassed?: number
  inspectionHookStatus?: string
  remark?: string
}

export interface PurchaseInboundD {
  id?: number
  inboundId?: number
  lineNo?: number
  receiptLineId: number
  materialId?: number
  inboundQty: number
  status?: string
}

export interface PurchaseReturnH {
  id?: number
  returnNo?: string
  supplierId: number
  bizDate?: string
  status?: string
  approvalStatus?: string
  totalQty?: number
  remark?: string
}

export interface PurchaseReturnD {
  id?: number
  returnId?: number
  lineNo?: number
  materialId: number
  returnQty: number
  remark?: string
}

export interface PurchaseAuditReq {
  approved: boolean
  remark?: string
}

export interface PushLine {
  sourceLineId: number
  qty: number
}

export interface PushOrderReq {
  reqId: number
  bizDate?: string
  lines: PushLine[]
}

export type PurchasePage<T> = PageResult<T>

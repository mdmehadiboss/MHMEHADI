package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.entity.SaleWithItems
import com.example.data.model.AppSettings
import com.example.data.model.ReportSummary
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object ReceiptPdfGenerator {

    fun generateReceiptPdfFile(
        context: Context,
        saleWithItems: SaleWithItems,
        settings: AppSettings
    ): File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(400, 750, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        drawReceiptOnCanvas(canvas, 400, 750, saleWithItems, settings)

        document.finishPage(page)

        val outputDir = context.cacheDir
        val outputFile = File(outputDir, "${saleWithItems.sale.invoiceNumber}.pdf")
        try {
            val fos = FileOutputStream(outputFile)
            document.writeTo(fos)
            fos.close()
            document.close()
            return outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            return null
        }
    }

    private fun drawReceiptOnCanvas(
        canvas: Canvas,
        width: Int,
        height: Int,
        saleWithItems: SaleWithItems,
        settings: AppSettings
    ) {
        val sale = saleWithItems.sale
        val items = saleWithItems.items

        // Background
        val bgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val textPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
        }

        var y = 40f
        val margin = 24f
        val contentWidth = width - (margin * 2)

        // Shop Title
        textPaint.textSize = 20f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(settings.shopName, width / 2f, y, textPaint)
        y += 20f

        // Shop Address & Phone
        textPaint.textSize = 10f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        if (settings.shopAddress.isNotBlank()) {
            canvas.drawText(settings.shopAddress, width / 2f, y, textPaint)
            y += 14f
        }
        if (settings.shopPhone.isNotBlank()) {
            canvas.drawText("Tel: ${settings.shopPhone}", width / 2f, y, textPaint)
            y += 18f
        }

        // Divider
        val linePaint = Paint().apply {
            color = Color.DKGRAY
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }
        canvas.drawLine(margin, y, width - margin, y, linePaint)
        y += 18f

        // Invoice & Date
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = 10f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("INVOICE: ${sale.invoiceNumber}", margin, y, textPaint)
        textPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(FormatUtils.formatDateTime(sale.timestamp), width - margin, y, textPaint)
        y += 16f

        // Customer Info
        val custJson = try {
            JSONObject(sale.customerInfoJson)
        } catch (e: Exception) {
            JSONObject()
        }

        if (custJson.length() > 0) {
            textPaint.textAlign = Paint.Align.LEFT
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            val keys = custJson.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val value = custJson.optString(key)
                if (value.isNotBlank()) {
                    val fieldLabel = settings.customerFields.firstOrNull { it.id == key }?.getLabel(settings.language) ?: key
                    canvas.drawText("$fieldLabel: $value", margin, y, textPaint)
                    y += 13f
                }
            }
            y += 4f
        }

        // Table Header
        canvas.drawLine(margin, y, width - margin, y, linePaint)
        y += 14f

        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textAlign = Paint.Align.LEFT
        canvas.drawText("ITEM", margin, y, textPaint)
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("QTY", margin + 200f, y, textPaint)
        textPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("PRICE", margin + 270f, y, textPaint)
        canvas.drawText("TOTAL", width - margin, y, textPaint)
        y += 8f

        canvas.drawLine(margin, y, width - margin, y, linePaint)
        y += 16f

        // Table Items
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        for (item in items) {
            textPaint.textAlign = Paint.Align.LEFT
            val itemName = if (item.productName.length > 20) item.productName.take(18) + ".." else item.productName
            canvas.drawText(itemName, margin, y, textPaint)

            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText("${item.quantity}", margin + 200f, y, textPaint)

            textPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText(FormatUtils.formatMoneyNoSymbol(item.unitPrice), margin + 270f, y, textPaint)
            canvas.drawText(FormatUtils.formatMoneyNoSymbol(item.totalPrice), width - margin, y, textPaint)
            y += 15f
        }

        canvas.drawLine(margin, y, width - margin, y, linePaint)
        y += 16f

        // Totals summary
        textPaint.textAlign = Paint.Align.RIGHT
        textPaint.textSize = 10f
        canvas.drawText("Subtotal: ${FormatUtils.formatMoney(sale.subtotal, settings.currencySymbol)}", width - margin, y, textPaint)
        y += 14f

        if (sale.discountAmount > 0) {
            val discLabel = if (sale.isPercentageDiscount) "Discount (${sale.discountValue}%): -" else "Discount: -"
            canvas.drawText("$discLabel ${FormatUtils.formatMoney(sale.discountAmount, settings.currencySymbol)}", width - margin, y, textPaint)
            y += 14f
        }

        // Grand Total (Bold & Larger)
        textPaint.textSize = 13f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("GRAND TOTAL: ${FormatUtils.formatMoney(sale.grandTotal, settings.currencySymbol)}", width - margin, y, textPaint)
        y += 24f

        // Owner Signature
        if (settings.ownerSignature.isNotBlank()) {
            textPaint.textSize = 9f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            textPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Authorized: ${settings.ownerSignature}", width - margin, y, textPaint)
            y += 16f
        }

        // Tagline / Footer
        textPaint.textSize = 9f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textAlign = Paint.Align.CENTER
        if (settings.receiptTagline.isNotBlank()) {
            canvas.drawText(settings.receiptTagline, width / 2f, y, textPaint)
            y += 14f
        }
        canvas.drawText("*** Powered by GlassPOS (Offline) ***", width / 2f, y, textPaint)
    }

    fun printReceipt(context: Context, saleWithItems: SaleWithItems, settings: AppSettings) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
        if (printManager == null) {
            Toast.makeText(context, "Print service is unavailable", Toast.LENGTH_SHORT).show()
            return
        }

        val jobName = "Receipt_${saleWithItems.sale.invoiceNumber}"
        val printAdapter = object : PrintDocumentAdapter() {
            private var pdfFile: File? = null

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }

                pdfFile = generateReceiptPdfFile(context, saleWithItems, settings)
                if (pdfFile != null) {
                    val info = PrintDocumentInfo.Builder(jobName)
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(1)
                        .build()
                    callback?.onLayoutFinished(info, true)
                } else {
                    callback?.onLayoutFailed("Failed to generate receipt PDF")
                }
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                val file = pdfFile
                if (file == null || destination == null) {
                    callback?.onWriteFailed("File descriptor or file is null")
                    return
                }

                try {
                    val input = FileInputStream(file)
                    val output = FileOutputStream(destination.fileDescriptor)
                    val buf = ByteArray(1024)
                    var bytesRead: Int
                    while (input.read(buf).also { bytesRead = it } > 0) {
                        output.write(buf, 0, bytesRead)
                    }
                    input.close()
                    output.close()
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                }
            }
        }

        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    }

    fun shareReceiptPdf(context: Context, saleWithItems: SaleWithItems, settings: AppSettings) {
        val file = generateReceiptPdfFile(context, saleWithItems, settings)
        if (file == null) {
            Toast.makeText(context, "Could not generate PDF", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Receipt ${saleWithItems.sale.invoiceNumber}")
                putExtra(Intent.EXTRA_TEXT, "${settings.shopName} - Invoice ${saleWithItems.sale.invoiceNumber}\nTotal: ${FormatUtils.formatMoney(saleWithItems.sale.grandTotal, settings.currencySymbol)}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Receipt via"))
        } catch (e: Exception) {
            Toast.makeText(context, "Error sharing: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun printReport(context: Context, report: ReportSummary, settings: AppSettings) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "Sales_Report_${report.reportType}"
        val printAdapter = object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                val info = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()
                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                if (destination == null) return
                val doc = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(500, 700, 1).create()
                val page = doc.startPage(pageInfo)
                val canvas = page.canvas

                // Background
                val bgPaint = Paint().apply { color = Color.WHITE; style = Paint.Style.FILL }
                canvas.drawRect(0f, 0f, 500f, 700f, bgPaint)

                val paint = Paint().apply { color = Color.BLACK; isAntiAlias = true }
                var y = 50f

                paint.textSize = 20f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("${settings.shopName} - Sales Report", 250f, y, paint)
                y += 24f

                paint.textSize = 12f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText("Generated: ${FormatUtils.formatDateTime(System.currentTimeMillis())} (${report.reportType.uppercase()})", 250f, y, paint)
                y += 30f

                paint.textAlign = Paint.Align.LEFT
                paint.textSize = 14f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("Summary Statistics", 40f, y, paint)
                y += 20f

                paint.textSize = 12f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText("Total Transactions: ${report.totalTransactions}", 40f, y, paint)
                y += 18f
                canvas.drawText("Total Sales: ${FormatUtils.formatMoney(report.totalSales, settings.currencySymbol)}", 40f, y, paint)
                y += 18f
                canvas.drawText("Total Discount Given: ${FormatUtils.formatMoney(report.totalDiscount, settings.currencySymbol)}", 40f, y, paint)
                y += 18f
                canvas.drawText("Average Sale Amount: ${FormatUtils.formatMoney(report.averageSale, settings.currencySymbol)}", 40f, y, paint)
                y += 30f

                if (report.topProducts.isNotEmpty()) {
                    paint.textSize = 14f
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    canvas.drawText("Top Selling Products", 40f, y, paint)
                    y += 20f

                    paint.textSize = 11f
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    for (top in report.topProducts) {
                        canvas.drawText("• ${top.productName}: ${top.quantitySold} pcs (${FormatUtils.formatMoney(top.totalRevenue, settings.currencySymbol)})", 40f, y, paint)
                        y += 16f
                    }
                }

                doc.finishPage(page)
                try {
                    val out = FileOutputStream(destination.fileDescriptor)
                    doc.writeTo(out)
                    doc.close()
                    out.close()
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                }
            }
        }
        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    }
}

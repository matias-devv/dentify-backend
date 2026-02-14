package com.dentify.domain.receipt.service;

import com.dentify.domain.pay.enums.PaymentMethod;
import com.dentify.domain.receipt.enums.ReceiptType;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.mercadopagopayment.model.MercadoPagoPayment;
import com.dentify.domain.pay.model.Pay;
import com.dentify.domain.receipt.model.Receipt;
import com.dentify.domain.receipt.repository.IReceiptRepository;
import com.dentify.integration.cloudinary.ICloudinaryService;
import com.dentify.domain.pay.service.IPayService;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptService implements IReceiptService {

    private final ICloudinaryService cloudinaryService;

    private final IReceiptRepository receiptRepository;
    private final IPayService payService;

    /**
     * Generate a receipt PDF and save it to the database
     *
     * @param pay         Payment entity
     * @param appointment Appointment entity
     * @param payMP       MercadoPago payment data (nullable for cash)
     * @return Receipt entity with filename and URL
     */
    @Transactional
    @Override
    public Receipt generateAndSaveReceipt(Pay pay, Appointment appointment, MercadoPagoPayment payMP) {

        // 1. Determine receipt type
        ReceiptType type = determineReceiptType(pay, payMP);

        // 2. Generate PDF based on payment method
        String filename = generateUniqueFilename(pay);
        byte[] pdfBytes;

        if ( pay.getPayment_method() == PaymentMethod.CASH ) {

            pdfBytes = generateCashReceiptPDF(pay, appointment);
        }
        else {

            pdfBytes = generateMercadoPagoReceiptPDF(pay, appointment, payMP);
        }

        // 3. Save file to disk/cloud storage
        String fileUrl = cloudinaryService.uploadPDF( filename, pdfBytes);

        // 4. Create Receipt entity
        Receipt receipt = Receipt   .builder()
                                    .pay(pay)
                                    .url_pdf(fileUrl)
                                    .filename(filename)
                                    .issueDate(LocalDateTime.now())
                                    .receiptType(type)
                                    .build();

        log.info("CloudinaryService class = {}", cloudinaryService.getClass());

        return receiptRepository.save(receipt);
    }

    private ReceiptType determineReceiptType(Pay pay, MercadoPagoPayment payMP) {

        if (pay.getPayment_method() == PaymentMethod.CASH) {
            return ReceiptType.CASH_RECEIPT;
        }
        // Check if it's wallet or card installments
        if (payMP != null && payMP.getInstallments() != null && payMP.getInstallments() > 1) {
            return ReceiptType.CARD_INSTALLMENT_RECEIPT;
        }

        return ReceiptType.WALLET_RECEIPT;
    }

    /**
     * Generate professional CASH receipt PDF
     */
    private byte[] generateCashReceiptPDF(Pay pay, Appointment appointment) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(40, 40, 40, 40);

            // ==================== HEADER ====================

            // Logo/Title section
            Paragraph title = new Paragraph("COMPROBANTE DE PAGO")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(new DeviceRgb(22, 160, 133)) // Verde profesional
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(title);

            Paragraph subtitle = new Paragraph("Pago en Efectivo")
                    .setFontSize(14)
                    .setFontColor(new DeviceRgb(127, 140, 141))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(subtitle);

            // Status badge
            Paragraph status = new Paragraph("✓ PAGO APROBADO")
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(new DeviceRgb(39, 174, 96))
                    .setBackgroundColor(new DeviceRgb(232, 245, 233))
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(status);

            // Separator line
            addSeparatorLine(document);

            // ==================== RECEIPT NUMBER & DATE ====================

            Table headerInfoTable = new Table(2);
            headerInfoTable.setWidth(UnitValue.createPercentValue(100));
            headerInfoTable.setMarginTop(15);
            headerInfoTable.setMarginBottom(15);

            // Receipt number
            Cell receiptCell = new Cell()
                    .add(new Paragraph("N° de Comprobante")
                            .setFontSize(10)
                            .setFontColor(new DeviceRgb(127, 140, 141))
                            .setMarginBottom(3))
                    .add(new Paragraph(String.format("COMP-%06d", pay.getId_pay()))
                            .setFontSize(14)
                            .setBold()
                            .setFontColor(new DeviceRgb(44, 62, 80)))
                    .setBorder(Border.NO_BORDER);
            headerInfoTable.addCell(receiptCell);

            // Date
            Cell dateCell = new Cell()
                    .add(new Paragraph("Fecha de Emisión")
                            .setFontSize(10)
                            .setFontColor(new DeviceRgb(127, 140, 141))
                            .setMarginBottom(3))
                    .add(new Paragraph(pay.getDate_generation().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")))
                            .setFontSize(14)
                            .setBold()
                            .setFontColor(new DeviceRgb(44, 62, 80)))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT);
            headerInfoTable.addCell(dateCell);

            document.add(headerInfoTable);

            // ==================== AMOUNT HIGHLIGHT BOX ====================

            Div amountBox = new Div()
                    .setBackgroundColor(new DeviceRgb(232, 245, 233))
                    .setBorder(new SolidBorder(new DeviceRgb(22, 160, 133), 3, Border.SOLID))
                    .setPadding(20)
                    .setMarginTop(15)
                    .setMarginBottom(20);

            Paragraph amountLabel = new Paragraph("MONTO TOTAL")
                    .setFontSize(11)
                    .setFontColor(new DeviceRgb(127, 140, 141))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            amountBox.add(amountLabel);

            Paragraph amountValue = new Paragraph(String.format("$%.2f", pay.getAmount()))
                    .setFontSize(36)
                    .setBold()
                    .setFontColor(new DeviceRgb(22, 160, 133))
                    .setTextAlignment(TextAlignment.CENTER);
            amountBox.add(amountValue);

            Paragraph paymentMethodBadge = new Paragraph("💵 Efectivo")
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(new DeviceRgb(22, 160, 133))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(8);
            amountBox.add(paymentMethodBadge);

            document.add(amountBox);

            // ==================== PATIENT INFORMATION ====================

            addSectionTitle(document, "INFORMACIÓN DEL PACIENTE");

            Table patientTable = createDetailsTable();
            addDetailRow(patientTable, "Nombre Completo",
                    appointment.getPatient().getName() + " " + appointment.getPatient().getSurname());
            addDetailRow(patientTable, "DNI", appointment.getPatient().getDni());
            if (appointment.getPatient().getEmail() != null) {
                addDetailRow(patientTable, "Email", appointment.getPatient().getEmail());
            }
            document.add(patientTable);

            // ==================== TREATMENT INFORMATION ====================

            addSectionTitle(document, "INFORMACIÓN DEL SERVICIO");

            Table treatmentTable = createDetailsTable();

            String treatmentName = (pay.getTreatment() != null && pay.getTreatment().getProduct() != null)
                    ? pay.getTreatment().getProduct().getName_product()
                    : "Consulta general";
            addDetailRow(treatmentTable, "Servicio", treatmentName);

            if (appointment.getDate() != null) {
                addDetailRow(treatmentTable, "Fecha del Turno",
                        appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }

            if (appointment.getStartTime() != null) {
                addDetailRow(treatmentTable, "Hora",
                        appointment.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }

            addDetailRow(treatmentTable, "Profesional",
                    appointment.getApp_user().getName() + " " + appointment.getApp_user().getSurname());

            document.add(treatmentTable);

            // ==================== PAYMENT DETAILS ====================

            addSectionTitle(document, "DETALLES DEL PAGO");

            Table paymentTable = createDetailsTable();
            addDetailRow(paymentTable, "Método de Pago", "Efectivo");
            addDetailRow(paymentTable, "Lugar de Pago", "Consultorio");
            addDetailRow(paymentTable, "Estado", "✓ Aprobado");
            document.add(paymentTable);

            // ==================== FOOTER ====================

            addSeparatorLine(document);

            Paragraph footerNote = new Paragraph(
                    "Este comprobante certifica el pago recibido y es válido para cualquier consulta o reclamo.")
                    .setFontSize(10)
                    .setFontColor(new DeviceRgb(127, 140, 141))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20)
                    .setMarginBottom(10);
            document.add(footerNote);

            Paragraph generatedBy = new Paragraph("Comprobante generado automáticamente")
                    .setFontSize(9)
                    .setItalic()
                    .setFontColor(new DeviceRgb(149, 165, 166))
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(generatedBy);

            // Clinic info
            if (appointment.getApp_user().getClinic_name() != null) {
                Paragraph clinicInfo = new Paragraph(appointment.getApp_user().getClinic_name())
                        .setFontSize(9)
                        .setFontColor(new DeviceRgb(149, 165, 166))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(5);
                document.add(clinicInfo);
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating professional cash receipt PDF", e);
            throw new RuntimeException("Error generating cash receipt", e);
        }
    }

    // ==================== HELPER METHODS FOR PDF ====================

    private void addSeparatorLine(Document document) {
        LineSeparator separator = new LineSeparator(new SolidLine(1f));
        separator.setMarginTop(10);
        separator.setMarginBottom(10);
        separator.setStrokeColor(new DeviceRgb(236, 240, 241));
        document.add(separator);
    }

    private void addSectionTitle(Document document, String title) {
        Paragraph sectionTitle = new Paragraph(title)
                .setFontSize(12)
                .setBold()
                .setFontColor(new DeviceRgb(44, 62, 80))
                .setMarginTop(15)
                .setMarginBottom(10);
        document.add(sectionTitle);
    }

    private Table createDetailsTable() {
        Table table = new Table(new float[]{2, 3});
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(15);
        return table;
    }

    private void addDetailRow(Table table, String label, String value) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label)
                        .setFontSize(10)
                        .setFontColor(new DeviceRgb(127, 140, 141)))
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(8)
                .setPaddingBottom(8)
                .setPaddingLeft(0);

        Cell valueCell = new Cell()
                .add(new Paragraph(value)
                        .setFontSize(10)
                        .setBold()
                        .setFontColor(new DeviceRgb(44, 62, 80)))
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(8)
                .setPaddingBottom(8)
                .setTextAlignment(TextAlignment.RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    /**
     * Generate MercadoPago-style receipt PDF
     * Este intenta replicar el formato de MP
     */
    private byte[] generateMercadoPagoReceiptPDF(Pay pay, Appointment appointment, MercadoPagoPayment payMP) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            // MercadoPago style header (azul)
            document.add(new Paragraph("COMPROBANTE DE PAGO")
                    .setFontSize(22)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Mercado Pago")
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Payment Status (verde si approved)
            Paragraph status = new Paragraph(String.format("Estado: %s",
                    payMP.getStatus() != null ? payMP.getStatus().toUpperCase() : "APROBADO"))
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(ColorConstants.GREEN)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(status);

            // Separator
            document.add(new Paragraph("─────────────────────────────────────")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            // Payment details
            document.add(new Paragraph("Detalles de la Transacción")
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(15));

            if (payMP.getPaymentId() != null) {
                document.add(new Paragraph(String.format("ID de Pago: %s", payMP.getPaymentId())));
            }

            document.add(new Paragraph(String.format("Referencia Externa: %s", payMP.getExternalReference())));

            if (payMP.getDateApproved() != null) {
                document.add(new Paragraph(String.format("Fecha de Aprobación: %s",
                        payMP.getDateApproved().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))));
            }

            // Amount (destacado)
            document.add(new Paragraph(String.format("Monto Total: $%.2f", payMP.getTransactionAmount() != null ? payMP.getTransactionAmount() : pay.getAmount()))
                    .setFontSize(18)
                    .setBold()
                    .setMarginTop(15)
                    .setMarginBottom(15));

            // Payment method details
            document.add(new Paragraph("Método de Pago")
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(10));

            if (payMP.getPaymentTypeId() != null) {
                String paymentType = translatePaymentType(payMP.getPaymentTypeId());
                document.add(new Paragraph(String.format("Tipo: %s", paymentType)));
            }

            if (payMP.getInstallments() != null && payMP.getInstallments() > 1) {
                document.add(new Paragraph(String.format("Cuotas: %d", payMP.getInstallments())));
            }

            if (payMP.getPayerEmail() != null) {
                document.add(new Paragraph(String.format("Email del pagador: %s", payMP.getPayerEmail())));
            }

            // Patient info
            document.add(new Paragraph("Información del Paciente")
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(15));

            document.add(new Paragraph(String.format("Nombre: %s %s",
                    appointment.getPatient().getName(),
                    appointment.getPatient().getSurname())));
            document.add(new Paragraph(String.format("DNI: %s", appointment.getPatient().getDni())));

            // Treatment info
            if (pay.getTreatment() != null && pay.getTreatment().getProduct() != null) {
                document.add(new Paragraph(String.format("Servicio: %s",
                        pay.getTreatment().getProduct().getName_product()))
                        .setMarginTop(10));
            }

            // Footer
            document.add(new Paragraph("─────────────────────────────────────")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30));

            document.add(new Paragraph("Comprobante generado automáticamente")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setItalic());

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating MercadoPago receipt PDF", e);
            throw new RuntimeException("Error generating receipt", e);
        }
    }

    private String translatePaymentType(String paymentTypeId) {
        return switch (paymentTypeId) {
            case "credit_card" -> "Tarjeta de Crédito";
            case "debit_card" -> "Tarjeta de Débito";
            case "ticket" -> "Efectivo (RapiPago/PagoFácil)";
            case "account_money" -> "Dinero en cuenta";
            default -> paymentTypeId;
        };
    }

    private String generateUniqueFilename(Pay pay) {
        return String.format("comprobante_%d_%s.pdf", pay.getId_pay(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
    }

    @Override
    public List<Receipt> findByGenerationDateBefore(LocalDateTime cutoff) {
        return receiptRepository.findByIssueDateBefore(cutoff);
    }

    @Transactional
    public void deleteReceipt(Long receiptId) {

        Receipt receipt = receiptRepository.findById(receiptId).orElseThrow(() -> new RuntimeException("Receipt not found"));

        // Delete from Cloudinary
        cloudinaryService.delete( receipt.getFilename() );

        // Delete from database
        receiptRepository.delete(receipt);

        log.info("✅ Receipt deleted: {}", receiptId);
    }

}
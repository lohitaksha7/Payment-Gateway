package com.payment.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailTemplateBuilder {
    public static String buildPaymentSuccessTemplate(Long paymentId, double amount,
                                                     String currency, Long merchantId,
                                                     LocalDateTime succeededAt){
        String formattedDate = succeededAt!=null ?
                succeededAt.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")) : "N/A";

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <style>" +
                "        body { font-family: 'Inter', sans-serif; background-color: #f4f6f8; margin: 0; padding: 0; color: #333333; }" +
                "        .container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }" +
                "        .header { background: linear-gradient(135deg, #6366f1, #4f46e5); padding: 35px; text-align: center; color: #ffffff; }" +
                "        .header h1 { margin: 0; font-size: 24px; font-weight: 700; letter-spacing: -0.5px; }" +
                "        .content { padding: 40px 30px; }" +
                "        .success-badge { display: inline-block; background-color: #ecfdf5; color: #059669; font-weight: 600; font-size: 12px; padding: 6px 12px; border-radius: 50px; text-transform: uppercase; margin-bottom: 20px; }" +
                "        .amount-card { background-color: #f9fafb; border: 1px solid #f3f4f6; border-radius: 8px; padding: 20px; text-align: center; margin-bottom: 30px; }" +
                "        .amount-card h2 { margin: 0 0 5px 0; font-size: 14px; color: #6b7280; text-transform: uppercase; font-weight: 500; }" +
                "        .amount-val { font-size: 32px; font-weight: 800; color: #111827; }" +
                "        .details-table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }" +
                "        .details-table td { padding: 12px 0; border-bottom: 1px solid #f3f4f6; font-size: 14px; }" +
                "        .details-table td.label { color: #6b7280; font-weight: 500; }" +
                "        .details-table td.value { color: #111827; text-align: right; font-weight: 600; }" +
                "        .footer { background-color: #f9fafb; padding: 20px; text-align: center; border-top: 1px solid #f3f4f6; font-size: 12px; color: #9ca3af; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <h1>Payment Receipt</h1>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <div class='success-badge'>Payment Succeeded</div>" +
                "            <div class='amount-card'>" +
                "                <h2>Total Paid</h2>" +
                "                <div class='amount-val'>" + currency + " " + String.format("%.2f", amount) + "</div>" +
                "            </div>" +
                "            <table class='details-table'>" +
                "                <tr>" +
                "                    <td class='label'>Payment ID</td>" +
                "                    <td class='value'>#" + paymentId + "</td>" +
                "                </tr>" +
                "                <tr>" +
                "                    <td class='label'>Merchant ID</td>" +
                "                    <td class='value'>" + merchantId + "</td>" +
                "                </tr>" +
                "                <tr>" +
                "                    <td class='label'>Date & Time</td>" +
                "                    <td class='value'>" + formattedDate + "</td>" +
                "                </tr>" +
                "                <tr>" +
                "                    <td class='label'>Method</td>" +
                "                    <td class='value'>Card Payment</td>" +
                "                </tr>" +
                "            </table>" +
                "            <p style='font-size: 14px; color: #4b5563; line-height: 1.5; margin: 0;'>Thank you for your business. If you have any questions, please contact our support team.</p>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            &copy; 2026 Antigravity Payment Solutions. All rights reserved." +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
    public static String buildPaymentFailureTemplate(Long paymentId, double amount,
                                                     String currency, Long merchantId,
                                                     String errorMessage, LocalDateTime failedAt) {

        String formattedDate = failedAt != null ? failedAt.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")) : "N/A";

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <style>" +
                "        body { font-family: 'Inter', sans-serif; background-color: #f4f6f8; margin: 0; padding: 0; color: #333333; }" +
                "        .container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }" +
                "        .header { background: linear-gradient(135deg, #ef4444, #dc2626); padding: 35px; text-align: center; color: #ffffff; }" +
                "        .header h1 { margin: 0; font-size: 24px; font-weight: 700; letter-spacing: -0.5px; }" +
                "        .content { padding: 40px 30px; }" +
                "        .failure-badge { display: inline-block; background-color: #fef2f2; color: #b91c1c; font-weight: 600; font-size: 12px; padding: 6px 12px; border-radius: 50px; text-transform: uppercase; margin-bottom: 20px; }" +
                "        .amount-card { background-color: #f9fafb; border: 1px solid #f3f4f6; border-radius: 8px; padding: 20px; text-align: center; margin-bottom: 30px; }" +
                "        .amount-card h2 { margin: 0 0 5px 0; font-size: 14px; color: #6b7280; text-transform: uppercase; font-weight: 500; }" +
                "        .amount-val { font-size: 32px; font-weight: 800; color: #111827; }" +
                "        .details-table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }" +
                "        .details-table td { padding: 12px 0; border-bottom: 1px solid #f3f4f6; font-size: 14px; }" +
                "        .details-table td.label { color: #6b7280; font-weight: 500; }" +
                "        .details-table td.value { color: #111827; text-align: right; font-weight: 600; }" +
                "        .footer { background-color: #f9fafb; padding: 20px; text-align: center; border-top: 1px solid #f3f4f6; font-size: 12px; color: #9ca3af; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <h1>Declined Transaction Alert</h1>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <div class='failure-badge'>Payment Failed</div>" +
                "            <div class='amount-card'>" +
                "                <h2>Transaction Value</h2>" +
                "                <div class='amount-val'>" + currency + " " + String.format("%.2f", amount) + "</div>" +
                "            </div>" +
                "            <table class='details-table'>" +
                "                <tr>" +
                "                    <td class='label'>Payment ID</td>" +
                "                    <td class='value'>#" + paymentId + "</td>" +
                "                </tr>" +
                "                <tr>" +
                "                    <td class='label'>Merchant ID</td>" +
                "                    <td class='value'>" + merchantId + "</td>" +
                "                </tr>" +
                "                <tr>" +
                "                    <td class='label'>Decline Reason</td>" +
                "                    <td class='value' style='color: #dc2626;'>" + errorMessage + "</td>" +
                "                </tr>" +
                "                <tr>" +
                "                    <td class='label'>Date & Time</td>" +
                "                    <td class='value'>" + formattedDate + "</td>" +
                "                </tr>" +
                "            </table>" +
                "            <p style='font-size: 14px; color: #4b5563; line-height: 1.5; margin: 0;'>The transaction failed due to the reason specified above. Please log into your merchant console to retry or review failed checkout sessions.</p>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            &copy; 2026 Antigravity Payment Solutions. All rights reserved." +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}
